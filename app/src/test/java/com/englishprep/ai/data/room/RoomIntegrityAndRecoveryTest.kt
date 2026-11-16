package com.englishprep.ai.data.room

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.englishprep.ai.data.repository.SessionProgressRepositoryImpl
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.ExamMode
import com.englishprep.ai.domain.models.SessionState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class RoomIntegrityAndRecoveryTest {

    private lateinit var context: Context
    private lateinit var dbFile: File
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        dbFile = File(context.cacheDir, "recovery_test_${System.nanoTime()}.db")
    }

    @After
    fun teardown() {
        dbFile.delete()
        File("${dbFile.absolutePath}-wal").delete()
        File("${dbFile.absolutePath}-shm").delete()
    }

    private fun openDb(): EnglishPrepDatabase =
        Room.databaseBuilder(context, EnglishPrepDatabase::class.java, dbFile.absolutePath)
            .allowMainThreadQueries()
            .build()

    @Test
    fun `answers and elapsed checkpoint survive simulated process death across database reopen`() = runTest {
        val db1 = openDb()
        AssetSeeder(context, db1.catalogDao(), json).seedIfNeeded()
        val repo1 = SessionProgressRepositoryImpl(db1.sessionDao(), db1.chatAndModelDao(), json)

        val session = repo1.createOrResumePackSession(
            examId = ExamId.ITP_L1,
            packId = "ITP-L1-01",
            title = "ITP Paket 1",
            mode = ExamMode.STRICT,
            durationMinutes = 20,
        )
        repo1.saveAnswer(session.id, "ITP-L1-01-Q01", "She borrowed it", isFlagged = false)
        // Update same question twice to ensure unique (sessionId, questionId) without duplicates
        repo1.saveAnswer(session.id, "ITP-L1-01-Q01", "She cannot lend the recorder because another classmate has it.", isFlagged = true)
        repo1.updateSessionCheckpoint(session.id, currentIndex = 4, elapsedMonotonicMs = 123_456L)
        db1.close()

        // Simulate app process relaunch by reopening database from disk
        val db2 = openDb()
        val repo2 = SessionProgressRepositoryImpl(db2.sessionDao(), db2.chatAndModelDao(), json)
        val restoredSession = repo2.getSession(session.id)!!
        val restoredAnswers = repo2.observeAnswers(session.id).first()

        assertEquals(SessionState.ACTIVE.name, restoredSession.state)
        assertEquals(4, restoredSession.currentQuestionIndex)
        assertEquals(123_456L, restoredSession.elapsedMonotonicCheckpointMs)
        assertEquals(1, restoredAnswers.size)
        assertEquals("She cannot lend the recorder because another classmate has it.", restoredAnswers.first().answerJson)
        assertTrue(restoredAnswers.first().isFlagged)
        db2.close()
    }

    @Test
    fun `backup json export and restore preserve submitted sessions progress and avoid duplicates`() = runTest {
        val db = openDb()
        AssetSeeder(context, db.catalogDao(), json).seedIfNeeded()
        val repo = SessionProgressRepositoryImpl(db.sessionDao(), db.chatAndModelDao(), json)

        val session = repo.createOrResumePackSession(ExamId.IELTS_ACADEMIC, "IELTS-A-01", "IELTS 1", ExamMode.PRACTICE, 20)
        repo.saveAnswer(session.id, "IELTS-A-01-Q01", "Answer A", isFlagged = false)
        repo.submitSession(session.id, emptyList(), System.currentTimeMillis())
        repo.recordModuleDrill(ExamId.IELTS_ACADEMIC, "IELTS-MOD-01", attempts = 3, correct = 2)
        repo.recordModuleDrill(ExamId.IELTS_ACADEMIC, "IELTS-MOD-01", attempts = 3, correct = 3)

        val progressBefore = repo.observeStudyProgress(ExamId.IELTS_ACADEMIC).first()
        assertEquals(1, progressBefore.size)
        assertEquals(6, progressBefore.first().attempts)
        assertEquals(5, progressBefore.first().correct)

        val backup = repo.exportBackupJson()
        repo.clearAllUserData()
        assertEquals(0, repo.observeSessions(ExamId.IELTS_ACADEMIC).first().size)

        val restoreResult = repo.restoreBackupJson(backup)
        assertTrue(restoreResult.isSuccess)
        assertEquals(1, repo.observeSessions(ExamId.IELTS_ACADEMIC).first().size)
        assertEquals(1, repo.observeAnswers(session.id).first().size)
        assertEquals(1, repo.observeStudyProgress(ExamId.IELTS_ACADEMIC).first().size)
        db.close()
    }
}
