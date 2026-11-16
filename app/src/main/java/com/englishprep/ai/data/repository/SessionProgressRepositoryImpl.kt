package com.englishprep.ai.data.repository

import com.englishprep.ai.data.room.ChatAndModelDao
import com.englishprep.ai.data.room.EvaluationEntity
import com.englishprep.ai.data.room.ExamSessionEntity
import com.englishprep.ai.data.room.SessionDao
import com.englishprep.ai.data.room.SpeechRecordingEntity
import com.englishprep.ai.data.room.StudyProgressEntity
import com.englishprep.ai.data.room.UserAnswerEntity
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.ExamMode
import com.englishprep.ai.domain.models.SessionState
import com.englishprep.ai.domain.repositories.SessionProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class BackupPayloadDto(
    val schemaVersion: Int = 1,
    val exportedAt: Long,
    val sessions: List<BackupSessionDto> = emptyList(),
    val answers: List<BackupAnswerDto> = emptyList(),
    val evaluations: List<BackupEvaluationDto> = emptyList(),
    val progress: List<BackupProgressDto> = emptyList(),
)

@Serializable
data class BackupSessionDto(
    val id: String,
    val examId: String,
    val packId: String? = null,
    val mode: String,
    val title: String,
    val startAt: Long,
    val deadlineAt: Long? = null,
    val endAt: Long? = null,
    val lastSavedAt: Long,
    val state: String,
    val elapsedMonotonicCheckpointMs: Long,
    val currentQuestionIndex: Int,
    val scorePolicyVersion: String,
)

@Serializable
data class BackupAnswerDto(
    val id: String,
    val sessionId: String,
    val questionId: String,
    val answerJson: String,
    val updatedAt: Long,
    val isFlagged: Boolean,
    val submittedAt: Long? = null,
    val autoCheckResultJson: String? = null,
)

@Serializable
data class BackupEvaluationDto(
    val id: String,
    val sessionId: String,
    val section: String,
    val rawCorrect: Int,
    val rawTotal: Int,
    val scaledScore: String? = null,
    val officialEquivalent: Boolean = false,
    val estimateMethod: String? = null,
    val aiFeedbackJson: String? = null,
    val createdAt: Long,
)

@Serializable
data class BackupProgressDto(
    val id: String,
    val examId: String,
    val moduleId: String,
    val attempts: Int,
    val correct: Int,
    val reviewedAt: Long,
    val masteryEstimate: Float,
    val updatedAt: Long,
)

@Singleton
class SessionProgressRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao,
    private val chatAndModelDao: ChatAndModelDao,
    private val json: Json,
) : SessionProgressRepository {

    override fun observeSessions(examId: ExamId): Flow<List<ExamSessionEntity>> =
        sessionDao.observeSessionsByExam(examId.name)

    override fun observeLatestActiveSession(): Flow<ExamSessionEntity?> =
        sessionDao.observeLatestActiveSession()

    override fun observeEvaluations(examId: ExamId): Flow<List<EvaluationEntity>> =
        sessionDao.observeEvaluationsByExam(examId.name)

    override fun observeStudyProgress(examId: ExamId): Flow<List<StudyProgressEntity>> =
        sessionDao.observeStudyProgress(examId.name)

    override fun observeAnswers(sessionId: String): Flow<List<UserAnswerEntity>> =
        sessionDao.observeAnswers(sessionId)

    override suspend fun getSession(sessionId: String): ExamSessionEntity? =
        sessionDao.getSession(sessionId)

    override suspend fun createOrResumePackSession(
        examId: ExamId,
        packId: String,
        title: String,
        mode: ExamMode,
        durationMinutes: Int,
    ): ExamSessionEntity {
        val now = System.currentTimeMillis()
        val existing = sessionDao.observeLatestActiveSession()
        val session = ExamSessionEntity(
            id = "SES_${UUID.randomUUID()}",
            examId = examId.name,
            packId = packId,
            mode = mode.name,
            title = title,
            startAt = now,
            deadlineAt = if (mode == ExamMode.STRICT) now + durationMinutes * 60_000L else null,
            endAt = null,
            lastSavedAt = now,
            state = SessionState.ACTIVE.name,
            elapsedMonotonicCheckpointMs = 0L,
            currentQuestionIndex = 0,
            scorePolicyVersion = "v1.0-non-official",
        )
        sessionDao.upsertSession(session)
        return session
    }

    override suspend fun createMistakeRetrySession(
        examId: ExamId,
        questionIds: List<String>,
        title: String,
    ): ExamSessionEntity {
        val now = System.currentTimeMillis()
        val encodedIds = questionIds.joinToString(",")
        val session = ExamSessionEntity(
            id = "SES_RETRY_${UUID.randomUUID()}",
            examId = examId.name,
            packId = "RETRY:$encodedIds",
            mode = ExamMode.PRACTICE.name,
            title = title,
            startAt = now,
            deadlineAt = null,
            endAt = null,
            lastSavedAt = now,
            state = SessionState.ACTIVE.name,
            elapsedMonotonicCheckpointMs = 0L,
            currentQuestionIndex = 0,
            scorePolicyVersion = "v1.0-non-official",
        )
        sessionDao.upsertSession(session)
        return session
    }

    override suspend fun saveAnswer(
        sessionId: String,
        questionId: String,
        answerJson: String,
        isFlagged: Boolean,
        autoCheckResultJson: String?,
    ) {
        val now = System.currentTimeMillis()
        val existing = sessionDao.getAnswers(sessionId).find { it.questionId == questionId }
        sessionDao.upsertAnswer(
            UserAnswerEntity(
                id = existing?.id ?: "${sessionId}_$questionId",
                sessionId = sessionId,
                questionId = questionId,
                answerJson = answerJson,
                updatedAt = now,
                isFlagged = isFlagged,
                submittedAt = existing?.submittedAt,
                autoCheckResultJson = autoCheckResultJson,
            ),
        )
    }

    override suspend fun updateSessionCheckpoint(
        sessionId: String,
        currentIndex: Int,
        elapsedMonotonicMs: Long,
    ) {
        val current = sessionDao.getSession(sessionId) ?: return
        sessionDao.upsertSession(
            current.copy(
                currentQuestionIndex = currentIndex,
                elapsedMonotonicCheckpointMs = elapsedMonotonicMs,
                lastSavedAt = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun submitSession(
        sessionId: String,
        evaluations: List<EvaluationEntity>,
        submittedAt: Long,
    ) {
        val current = sessionDao.getSession(sessionId) ?: return
        sessionDao.upsertSession(
            current.copy(
                state = SessionState.SUBMITTED.name,
                endAt = submittedAt,
                lastSavedAt = submittedAt,
            ),
        )
        sessionDao.upsertEvaluations(evaluations)
        val answers = sessionDao.getAnswers(sessionId)
        answers.forEach { a ->
            if (a.submittedAt == null) {
                sessionDao.upsertAnswer(a.copy(submittedAt = submittedAt))
            }
        }
    }

    override suspend fun recordModuleDrill(examId: ExamId, moduleId: String, attempts: Int, correct: Int) {
        val now = System.currentTimeMillis()
        val prev = sessionDao.getModuleProgress(examId.name, moduleId)
        val totalAttempts = (prev?.attempts ?: 0) + attempts
        val totalCorrect = (prev?.correct ?: 0) + correct
        val mastery = if (totalAttempts > 0) (totalCorrect.toFloat() / totalAttempts.toFloat()).coerceIn(0f, 1f) else 0f
        sessionDao.upsertStudyProgress(
            StudyProgressEntity(
                id = prev?.id ?: "${examId.name}_$moduleId",
                examId = examId.name,
                moduleId = moduleId,
                attempts = totalAttempts,
                correct = totalCorrect,
                reviewedAt = now,
                masteryEstimate = mastery,
                updatedAt = now,
            ),
        )
    }

    override suspend fun saveSpeechRecording(
        sessionId: String,
        questionId: String,
        filePath: String,
        durationMs: Long,
        transcript: String?,
        feedbackJson: String?,
    ) {
        sessionDao.upsertSpeechRecording(
            SpeechRecordingEntity(
                id = "REC_${UUID.randomUUID()}",
                sessionId = sessionId,
                questionId = questionId,
                privateFilePath = filePath,
                durationMs = durationMs,
                transcript = transcript,
                feedbackJson = feedbackJson,
                createdAt = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun getLatestRecordingTranscript(sessionId: String, questionId: String): Pair<String, String?>? {
        val rec = sessionDao.getLatestRecording(sessionId, questionId) ?: return null
        return rec.privateFilePath to rec.transcript
    }

    override suspend fun exportBackupJson(): String {
        val payload = BackupPayloadDto(
            schemaVersion = 1,
            exportedAt = System.currentTimeMillis(),
            sessions = sessionDao.getAllSessionsSnapshot().map {
                BackupSessionDto(
                    it.id, it.examId, it.packId, it.mode, it.title, it.startAt,
                    it.deadlineAt, it.endAt, it.lastSavedAt, it.state,
                    it.elapsedMonotonicCheckpointMs, it.currentQuestionIndex, it.scorePolicyVersion,
                )
            },
            answers = sessionDao.getAllAnswersSnapshot().map {
                BackupAnswerDto(it.id, it.sessionId, it.questionId, it.answerJson, it.updatedAt, it.isFlagged, it.submittedAt, it.autoCheckResultJson)
            },
            evaluations = sessionDao.getAllEvaluationsSnapshot().map {
                BackupEvaluationDto(it.id, it.sessionId, it.section, it.rawCorrect, it.rawTotal, it.scaledScore, it.officialEquivalent, it.estimateMethod, it.aiFeedbackJson, it.createdAt)
            },
            progress = sessionDao.getAllProgressSnapshot().map {
                BackupProgressDto(it.id, it.examId, it.moduleId, it.attempts, it.correct, it.reviewedAt, it.masteryEstimate, it.updatedAt)
            },
        )
        return json.encodeToString(payload)
    }

    override suspend fun restoreBackupJson(jsonText: String): Result<Int> = runCatching {
        val payload = json.decodeFromString<BackupPayloadDto>(jsonText)
        require(payload.schemaVersion == 1) { "Versi cadangan tidak didukung: ${payload.schemaVersion}" }
        payload.sessions.forEach { s ->
            sessionDao.upsertSession(
                ExamSessionEntity(
                    s.id, s.examId, s.packId, s.mode, s.title, s.startAt,
                    s.deadlineAt, s.endAt, s.lastSavedAt, s.state,
                    s.elapsedMonotonicCheckpointMs, s.currentQuestionIndex, s.scorePolicyVersion,
                ),
            )
        }
        payload.answers.forEach { a ->
            sessionDao.upsertAnswer(
                UserAnswerEntity(a.id, a.sessionId, a.questionId, a.answerJson, a.updatedAt, a.isFlagged, a.submittedAt, a.autoCheckResultJson),
            )
        }
        if (payload.evaluations.isNotEmpty()) {
            sessionDao.upsertEvaluations(
                payload.evaluations.map { e ->
                    EvaluationEntity(e.id, e.sessionId, e.section, e.rawCorrect, e.rawTotal, e.scaledScore, e.officialEquivalent, e.estimateMethod, e.aiFeedbackJson, e.createdAt)
                },
            )
        }
        payload.progress.forEach { p ->
            sessionDao.upsertStudyProgress(
                StudyProgressEntity(p.id, p.examId, p.moduleId, p.attempts, p.correct, p.reviewedAt, p.masteryEstimate, p.updatedAt),
            )
        }
        payload.sessions.size + payload.progress.size
    }

    override suspend fun clearAllUserData() {
        sessionDao.clearAllSessions()
        sessionDao.clearAllStudyProgress()
        chatAndModelDao.clearAllChatThreads()
    }
}
