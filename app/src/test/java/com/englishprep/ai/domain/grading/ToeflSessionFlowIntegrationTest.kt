package com.englishprep.ai.domain.grading

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.englishprep.ai.data.repository.ExamCatalogRepositoryImpl
import com.englishprep.ai.data.repository.SessionProgressRepositoryImpl
import com.englishprep.ai.data.room.AssetSeeder
import com.englishprep.ai.data.room.EnglishPrepDatabase
import com.englishprep.ai.data.room.EvaluationEntity
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.ExamMode
import com.englishprep.ai.domain.models.ExamSection
import com.englishprep.ai.domain.models.GradingOutcome
import com.englishprep.ai.domain.models.SessionState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class ToeflSessionFlowIntegrationTest {

    private lateinit var context: Context
    private lateinit var db: EnglishPrepDatabase
    private lateinit var catalogRepo: ExamCatalogRepositoryImpl
    private lateinit var sessionRepo: SessionProgressRepositoryImpl
    private val gradingEngine = QuizGradingEngine()
    private val scoreEstimator = ExamScoreEstimator()

    @Before
    fun setup() = runTest {
        context = ApplicationProvider.getApplicationContext()
        val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
        db = Room.inMemoryDatabaseBuilder(context, EnglishPrepDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        AssetSeeder(context, db.catalogDao(), json).seedIfNeeded()
        catalogRepo = ExamCatalogRepositoryImpl(db.catalogDao(), json, context)
        sessionRepo = SessionProgressRepositoryImpl(db.sessionDao(), db.chatAndModelDao(), json)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `toefl itp session loads valid listening wavs grades deterministically updates progress and spawns mistake retry`() = runTest {
        val bundle = catalogRepo.loadPackBundle("ITP-L1-01")!!
        assertEquals(140, bundle.questions.size)
        assertEquals(50, bundle.questions.count { it.section == ExamSection.LISTENING })
        assertEquals(40, bundle.questions.count { it.section == ExamSection.STRUCTURE })
        assertEquals(50, bundle.questions.count { it.section == ExamSection.READING })

        // Verify every distinct Listening audio asset references a valid RIFF WAVE asset.
        bundle.assets.values.forEach { asset ->
            val relPath = asset.relativePathOrUri.removePrefix("asset://")
            context.assets.open(relPath).use { stream ->
                val header = ByteArray(12)
                assertEquals(12, stream.read(header))
                assertEquals("RIFF", String(header, 0, 4, Charsets.US_ASCII))
                assertEquals("WAVE", String(header, 8, 4, Charsets.US_ASCII))
            }
        }

        val session = sessionRepo.createOrResumePackSession(
            examId = ExamId.ITP_L1,
            packId = "ITP-L1-01",
            title = bundle.pack.title,
            mode = ExamMode.PRACTICE,
            durationMinutes = bundle.pack.durationMinutes,
        )

        val wrongIds = mutableListOf<String>()
        val evaluations = mutableListOf<EvaluationEntity>()
        val moduleCounts = mutableMapOf<String, Pair<Int, Int>>()
        var totalCorrect = 0
        var totalObj = 0

        bundle.questions.groupBy { it.section }.forEach { (section, secQuestions) ->
            var secCorrect = 0
            var secTotal = 0
            secQuestions.forEachIndexed { idx, q ->
                val userAns = if (idx == secQuestions.lastIndex) "__WRONG__" else q.answer
                val outcome = gradingEngine.grade(q, userAns)
                assertTrue(outcome is GradingOutcome.Objective)
                outcome as GradingOutcome.Objective
                secTotal++
                totalObj++
                if (outcome.isCorrect) {
                    secCorrect++
                    totalCorrect++
                } else {
                    wrongIds += q.id
                }
                val (pa, pc) = moduleCounts[q.rationaleSource] ?: (0 to 0)
                moduleCounts[q.rationaleSource] = (pa + 1) to (pc + if (outcome.isCorrect) 1 else 0)
                sessionRepo.saveAnswer(
                    sessionId = session.id,
                    questionId = q.id,
                    answerJson = userAns,
                    isFlagged = false,
                    autoCheckResultJson = "${outcome.isCorrect}|${outcome.feedbackMessage}",
                )
            }
            val secEst = scoreEstimator.estimate(ExamId.ITP_L1, secCorrect, secTotal)
            assertFalse(secEst.officialEquivalent)
            evaluations += EvaluationEntity(
                id = "EVAL_${UUID.randomUUID()}",
                sessionId = session.id,
                section = section.name,
                rawCorrect = secCorrect,
                rawTotal = secTotal,
                scaledScore = secEst.primaryEstimateLabel,
                officialEquivalent = false,
                estimateMethod = secEst.secondaryNote,
                aiFeedbackJson = null,
                createdAt = System.currentTimeMillis(),
            )
        }

        val overallEst = scoreEstimator.estimate(ExamId.ITP_L1, totalCorrect, totalObj)
        assertTrue(overallEst.primaryEstimateLabel.contains("137/140"))
        assertEquals(137, totalCorrect)
        assertEquals(140, totalObj)
        assertEquals(98, overallEst.accuracyPercent)

        evaluations += EvaluationEntity(
            id = "EVAL_OVERALL_${UUID.randomUUID()}",
            sessionId = session.id,
            section = "OVERALL",
            rawCorrect = totalCorrect,
            rawTotal = totalObj,
            scaledScore = overallEst.primaryEstimateLabel,
            officialEquivalent = false,
            estimateMethod = overallEst.secondaryNote,
            aiFeedbackJson = null,
            createdAt = System.currentTimeMillis(),
        )
        sessionRepo.updateSessionCheckpoint(session.id, 139, 45_000L)
        sessionRepo.submitSession(session.id, evaluations, System.currentTimeMillis())
        moduleCounts.forEach { (modId, pair) ->
            sessionRepo.recordModuleDrill(ExamId.ITP_L1, modId, pair.first, pair.second)
        }

        val storedSession = sessionRepo.getSession(session.id)!!
        assertEquals(SessionState.SUBMITTED.name, storedSession.state)

        val storedAnswers = sessionRepo.observeAnswers(session.id).first()
        assertEquals(140, storedAnswers.size)
        assertTrue(storedAnswers.all { it.submittedAt != null && it.answerJson.isNotBlank() })

        val storedEvals = sessionRepo.observeEvaluations(ExamId.ITP_L1).first()
        assertEquals(4, storedEvals.size) // 3 sections + OVERALL

        val progressRows = sessionRepo.observeStudyProgress(ExamId.ITP_L1).first()
        assertTrue(progressRows.isNotEmpty())

        val retrySession = sessionRepo.createMistakeRetrySession(
            examId = ExamId.ITP_L1,
            questionIds = wrongIds,
            title = "Ulangi Salah",
        )
        val retryBundle = catalogRepo.loadQuestionsByIds(wrongIds)!!
        assertEquals(3, retryBundle.questions.size)
        assertEquals(SessionState.ACTIVE.name, retrySession.state)
    }

    @Test
    fun `toefl ibt 2026 and ielts academic full packs execute objective and subjective tasks cleanly`() = runTest {
        val ibtBundle = catalogRepo.loadPackBundle("IBT26-01")!!
        assertEquals(120, ibtBundle.questions.size)
        assertTrue(ibtBundle.questions.any { it.section == ExamSection.WRITING })
        assertTrue(ibtBundle.questions.any { it.section == ExamSection.SPEAKING })
        ibtBundle.assets.values.forEach { asset ->
            context.assets.open(asset.relativePathOrUri.removePrefix("asset://")).use { stream ->
                val header = ByteArray(4)
                assertEquals(4, stream.read(header))
                assertEquals("RIFF", String(header, Charsets.US_ASCII))
            }
        }
        val ibtOutcomes = ibtBundle.questions.map { q ->
            val draft = if (q.answer.isNotBlank()) q.answer else "Student draft response addressing every prompt detail."
            gradingEngine.grade(q, draft, hasAudioRecording = false)
        }
        assertTrue(ibtOutcomes.any { it is GradingOutcome.Objective && it.isCorrect })
        assertTrue(ibtOutcomes.any { it is GradingOutcome.SubjectivePending })

        val ieltsBundle = catalogRepo.loadPackBundle("IELTS-A-01")!!
        assertEquals(85, ieltsBundle.questions.size)
        assertEquals(40, ieltsBundle.questions.count { it.section == ExamSection.LISTENING })
        assertEquals(40, ieltsBundle.questions.count { it.section == ExamSection.READING })
        assertEquals(2, ieltsBundle.questions.count { it.section == ExamSection.WRITING })
        assertEquals(3, ieltsBundle.questions.count { it.section == ExamSection.SPEAKING })
        val ieltsOutcomes = ieltsBundle.questions.map { q ->
            val draft = if (q.answer.isNotBlank()) q.answer else "Structured academic response exploring key evidence clearly."
            gradingEngine.grade(q, draft, hasAudioRecording = false)
        }
        assertEquals(80, ieltsOutcomes.count { it is GradingOutcome.Objective && it.isCorrect })
        assertEquals(5, ieltsOutcomes.count { it is GradingOutcome.SubjectivePending })
    }
}
