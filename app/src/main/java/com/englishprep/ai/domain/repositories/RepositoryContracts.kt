package com.englishprep.ai.domain.repositories

import com.englishprep.ai.data.room.ChatMessageEntity
import com.englishprep.ai.data.room.ChatThreadEntity
import com.englishprep.ai.data.room.EvaluationEntity
import com.englishprep.ai.data.room.ExamSessionEntity
import com.englishprep.ai.data.room.ImportJobEntity
import com.englishprep.ai.data.room.ModelInstallEntity
import com.englishprep.ai.data.room.StudyProgressEntity
import com.englishprep.ai.data.room.UserAnswerEntity
import com.englishprep.ai.domain.models.ContentPackSummary
import com.englishprep.ai.domain.models.CourseModuleItem
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.ExamMode
import com.englishprep.ai.domain.models.MediaAssetItem
import com.englishprep.ai.domain.models.PassageItem
import com.englishprep.ai.domain.models.QuestionItem
import com.englishprep.ai.domain.models.RagSnippet
import kotlinx.coroutines.flow.Flow

data class PackPlayBundle(
    val pack: ContentPackSummary,
    val passages: Map<String, PassageItem>,
    val assets: Map<String, MediaAssetItem>,
    val questions: List<QuestionItem>,
)

interface ExamCatalogRepository {
    fun observeModules(examId: ExamId): Flow<List<CourseModuleItem>>
    suspend fun getModule(moduleId: String): CourseModuleItem?
    fun observePacks(examId: ExamId): Flow<List<ContentPackSummary>>
    suspend fun loadPackBundle(packId: String): PackPlayBundle?
    suspend fun loadQuestionsByIds(questionIds: List<String>): PackPlayBundle?
    suspend fun searchLocalCurriculum(examId: ExamId?, query: String, limit: Int = 5): List<RagSnippet>
}

interface SessionProgressRepository {
    fun observeSessions(examId: ExamId): Flow<List<ExamSessionEntity>>
    fun observeLatestActiveSession(): Flow<ExamSessionEntity?>
    fun observeEvaluations(examId: ExamId): Flow<List<EvaluationEntity>>
    fun observeStudyProgress(examId: ExamId): Flow<List<StudyProgressEntity>>
    fun observeAnswers(sessionId: String): Flow<List<UserAnswerEntity>>

    suspend fun getSession(sessionId: String): ExamSessionEntity?
    suspend fun createOrResumePackSession(
        examId: ExamId,
        packId: String,
        title: String,
        mode: ExamMode,
        durationMinutes: Int,
    ): ExamSessionEntity

    suspend fun createMistakeRetrySession(
        examId: ExamId,
        questionIds: List<String>,
        title: String,
    ): ExamSessionEntity

    suspend fun saveAnswer(
        sessionId: String,
        questionId: String,
        answerJson: String,
        isFlagged: Boolean,
        autoCheckResultJson: String? = null,
    )

    suspend fun updateSessionCheckpoint(
        sessionId: String,
        currentIndex: Int,
        elapsedMonotonicMs: Long,
    )

    suspend fun submitSession(
        sessionId: String,
        evaluations: List<EvaluationEntity>,
        submittedAt: Long,
    )

    suspend fun recordModuleDrill(examId: ExamId, moduleId: String, attempts: Int, correct: Int)

    suspend fun saveSpeechRecording(
        sessionId: String,
        questionId: String,
        filePath: String,
        durationMs: Long,
        transcript: String?,
        feedbackJson: String?,
    )

    suspend fun getLatestRecordingTranscript(sessionId: String, questionId: String): Pair<String, String?>?
    suspend fun exportBackupJson(): String
    suspend fun restoreBackupJson(jsonText: String): Result<Int>
    suspend fun clearAllUserData()
}

interface AiAndImportRepository {
    fun observeThreads(): Flow<List<ChatThreadEntity>>
    fun observeMessages(threadId: String): Flow<List<ChatMessageEntity>>
    fun observeInstalledModels(): Flow<List<ModelInstallEntity>>
    fun observeImportJobs(): Flow<List<ImportJobEntity>>

    suspend fun getOrCreateThread(examId: ExamId?): ChatThreadEntity
    suspend fun appendMessage(
        threadId: String,
        role: String,
        text: String,
        citations: List<String> = emptyList(),
        modelId: String? = null,
    )
    suspend fun clearThreadMessages(threadId: String)
    suspend fun importModelFileViaSaf(uriString: String, expectedFamily: String): Result<ModelInstallEntity>
    suspend fun importWhisperFolderViaSaf(treeUriString: String): Result<ModelInstallEntity>
    suspend fun importStudyPackViaSaf(uriString: String): Result<String>
    suspend fun activateModel(model: ModelInstallEntity): Result<Unit>
    suspend fun removeModel(modelId: String)
    suspend fun verifyInstalledModelIntegrity(model: ModelInstallEntity): Boolean
}
