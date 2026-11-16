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
        catalogRepo = ExamCatalogRepositoryImpl(db.catalogDao(), json)
        sessionRepo = SessionProgressRepositoryImpl(db.sessionDao(), db.chatAndModelDao(), json)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `toefl itp session loads valid listening wavs grades deterministically updates progress and spawns mistake retry`() = runTest {
        val bundle = catalogRepo.loadPackBundle("ITP-L1-01")!!
        assertEquals(12, bundle.questions.size)
        assertTrue(bundle.questions.any { it.section == ExamSection.LISTENING })
        assertTrue(bundle.questions.any { it.section == ExamSection.STRUCTURE })
        assertTrue(bundle.questions.any { it.section == ExamSection.READING })

        // Verify every Listening question references an existing non-empty RIFF WAV asset.
        bundle.questions.filter { it.audioAssetId != null }.forEach { q ->
            val asset = bundle.assets[q.audioAssetId]!!
            val relPath = asset.relativePathOrUri.removePrefix("asset://")
            context.assets.open(relPath).use { stream ->
                val header = ByteArray(4)
                assertEquals(4, stream.read(header))
                assertEquals("RIFF", String(header, Charsets.US_ASCII))
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
        assertTrue(overallEst.primaryEstimateLabel.startsWith("Est. ITP "))
        assertEquals(9, totalCorrect)
        assertEquals(12, totalObj)
        assertEquals(75, overallEst.accuracyPercent)

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
        sessionRepo.updateSessionCheckpoint(session.id, 11, 45_000L)
        sessionRepo.submitSession(session.id, evaluations, System.currentTimeMillis())
        moduleCounts.forEach { (modId, pair) ->
            sessionRepo.recordModuleDrill(ExamId.ITP_L1, modId, pair.first, pair.second)
        }

        val storedSession = sessionRepo.getSession(session.id)!!
        assertEquals(SessionState.SUBMITTED.name, storedSession.state)

        val storedAnswers = sessionRepo.observeAnswers(session.id).first()
        assertEquals(12, storedAnswers.size)
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
    fun `toefl ibt 2026 pack executes objective and subjective tasks cleanly`() = runTest {
        val bundle = catalogRepo.loadPackBundle("IBT26-01")!!
        assertEquals(12, bundle.questions.size)
        assertTrue(bundle.questions.any { it.section == ExamSection.WRITING })
        assertTrue(bundle.questions.any { it.section == ExamSection.SPEAKING })

        val outcomes = bundle.questions.map { q ->
            val draft = if (q.answer.isNotBlank()) q.answer else "Student draft response addressing every prompt detail."
            gradingEngine.grade(q, draft, hasAudioRecording = false)
        }
        assertTrue(outcomes.any { it is GradingOutcome.Objective && it.isCorrect })
        assertTrue(outcomes.any { it is GradingOutcome.SubjectivePending })
    }
}
