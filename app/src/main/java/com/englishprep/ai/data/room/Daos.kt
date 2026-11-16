package com.englishprep.ai.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogDao {
    @Query("SELECT * FROM exam_profiles")
    fun observeExamProfiles(): Flow<List<ExamProfileEntity>>

    @Query("SELECT COUNT(*) FROM exam_profiles")
    suspend fun countExamProfiles(): Int

    @Upsert
    suspend fun upsertExamProfiles(profiles: List<ExamProfileEntity>)

    @Query("SELECT * FROM course_modules WHERE examId = :examId ORDER BY sortIndex ASC")
    fun observeModulesByExam(examId: String): Flow<List<CourseModuleEntity>>

    @Query("SELECT * FROM course_modules WHERE id = :moduleId")
    suspend fun getModule(moduleId: String): CourseModuleEntity?

    @Query("SELECT COUNT(*) FROM course_modules")
    suspend fun countModules(): Int

    @Query("SELECT COUNT(*) FROM content_packs WHERE status = 'READY' AND availableCount >= blueprintCount")
    suspend fun countReadyFullPacks(): Int

    @Upsert
    suspend fun upsertModules(modules: List<CourseModuleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurriculumFts(entries: List<CurriculumFtsEntity>)

    @Query("DELETE FROM curriculum_fts")
    suspend fun clearCurriculumFts()

    @Query(
        "SELECT moduleId, examId, title, body FROM curriculum_fts " +
            "WHERE curriculum_fts MATCH :ftsQuery LIMIT :limit",
    )
    suspend fun searchCurriculumFts(ftsQuery: String, limit: Int = 5): List<CurriculumFtsEntity>

    @Query("SELECT * FROM content_packs WHERE examId = :examId ORDER BY id ASC")
    fun observePacksByExam(examId: String): Flow<List<ContentPackEntity>>

    @Query("SELECT * FROM content_packs WHERE id = :packId")
    suspend fun getPack(packId: String): ContentPackEntity?

    @Upsert
    suspend fun upsertPack(pack: ContentPackEntity)

    @Upsert
    suspend fun upsertPassages(passages: List<PassageEntity>)

    @Upsert
    suspend fun upsertMediaAssets(assets: List<MediaAssetEntity>)

    @Upsert
    suspend fun upsertQuestions(questions: List<QuestionEntity>)

    @Upsert
    suspend fun upsertOptions(options: List<QuestionOptionEntity>)

    @Query("SELECT * FROM questions WHERE packId = :packId ORDER BY sortIndex ASC")
    suspend fun getQuestionsForPack(packId: String): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE id IN (:questionIds)")
    suspend fun getQuestionsByIds(questionIds: List<String>): List<QuestionEntity>

    @Query("SELECT * FROM passages WHERE packId = :packId ORDER BY readingOrder ASC")
    suspend fun getPassagesForPack(packId: String): List<PassageEntity>

    @Query("SELECT * FROM media_assets WHERE packId = :packId")
    suspend fun getMediaAssetsForPack(packId: String): List<MediaAssetEntity>

    @Query("SELECT * FROM media_assets WHERE id = :assetId")
    suspend fun getMediaAsset(assetId: String): MediaAssetEntity?

    @Query("SELECT * FROM question_options WHERE questionId = :questionId ORDER BY sortIndex ASC")
    suspend fun getOptionsForQuestion(questionId: String): List<QuestionOptionEntity>

    @Query("DELETE FROM questions WHERE packId = :packId")
    suspend fun deleteQuestionsForPack(packId: String)

    @Query("DELETE FROM passages WHERE packId = :packId")
    suspend fun deletePassagesForPack(packId: String)

    @Query("DELETE FROM media_assets WHERE packId = :packId")
    suspend fun deleteMediaAssetsForPack(packId: String)

    @Transaction
    suspend fun installPackAtomic(
        pack: ContentPackEntity,
        passages: List<PassageEntity>,
        assets: List<MediaAssetEntity>,
        questions: List<QuestionEntity>,
        options: List<QuestionOptionEntity>,
    ) {
        upsertPack(pack)
        deleteQuestionsForPack(pack.id)
        deletePassagesForPack(pack.id)
        deleteMediaAssetsForPack(pack.id)
        upsertPassages(passages)
        upsertMediaAssets(assets)
        upsertQuestions(questions)
        upsertOptions(options)
    }
}

@Dao
interface SessionDao {
    @Query("SELECT * FROM exam_sessions WHERE examId = :examId ORDER BY startAt DESC")
    fun observeSessionsByExam(examId: String): Flow<List<ExamSessionEntity>>

    @Query("SELECT * FROM exam_sessions WHERE state = 'ACTIVE' ORDER BY lastSavedAt DESC LIMIT 1")
    fun observeLatestActiveSession(): Flow<ExamSessionEntity?>

    @Query("SELECT * FROM exam_sessions WHERE id = :sessionId")
    suspend fun getSession(sessionId: String): ExamSessionEntity?

    @Upsert
    suspend fun upsertSession(session: ExamSessionEntity)

    @Query("SELECT * FROM user_answers WHERE sessionId = :sessionId")
    fun observeAnswers(sessionId: String): Flow<List<UserAnswerEntity>>

    @Query("SELECT * FROM user_answers WHERE sessionId = :sessionId")
    suspend fun getAnswers(sessionId: String): List<UserAnswerEntity>

    @Upsert
    suspend fun upsertAnswer(answer: UserAnswerEntity)

    @Upsert
    suspend fun upsertEvaluations(evaluations: List<EvaluationEntity>)

    @Query("SELECT * FROM evaluations WHERE sessionId = :sessionId")
    suspend fun getEvaluationsForSession(sessionId: String): List<EvaluationEntity>

    @Query(
        "SELECT e.* FROM evaluations e INNER JOIN exam_sessions s ON e.sessionId = s.id " +
            "WHERE s.examId = :examId ORDER BY e.createdAt DESC",
    )
    fun observeEvaluationsByExam(examId: String): Flow<List<EvaluationEntity>>

    @Query("SELECT * FROM study_progress WHERE examId = :examId ORDER BY updatedAt DESC")
    fun observeStudyProgress(examId: String): Flow<List<StudyProgressEntity>>

    @Query("SELECT * FROM study_progress WHERE examId = :examId AND moduleId = :moduleId LIMIT 1")
    suspend fun getModuleProgress(examId: String, moduleId: String): StudyProgressEntity?

    @Upsert
    suspend fun upsertStudyProgress(progress: StudyProgressEntity)

    @Upsert
    suspend fun upsertSpeechRecording(recording: SpeechRecordingEntity)

    @Query("SELECT * FROM speech_recordings WHERE sessionId = :sessionId AND questionId = :questionId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestRecording(sessionId: String, questionId: String): SpeechRecordingEntity?

    @Query("SELECT * FROM exam_sessions")
    suspend fun getAllSessionsSnapshot(): List<ExamSessionEntity>

    @Query("SELECT * FROM user_answers")
    suspend fun getAllAnswersSnapshot(): List<UserAnswerEntity>

    @Query("SELECT * FROM evaluations")
    suspend fun getAllEvaluationsSnapshot(): List<EvaluationEntity>

    @Query("SELECT * FROM study_progress")
    suspend fun getAllProgressSnapshot(): List<StudyProgressEntity>

    @Query("DELETE FROM exam_sessions")
    suspend fun clearAllSessions()

    @Query("DELETE FROM study_progress")
    suspend fun clearAllStudyProgress()
}

@Dao
interface ChatAndModelDao {
    @Query("SELECT * FROM chat_threads ORDER BY updatedAt DESC")
    fun observeThreads(): Flow<List<ChatThreadEntity>>

    @Upsert
    suspend fun upsertThread(thread: ChatThreadEntity)

    @Query("SELECT * FROM chat_messages WHERE threadId = :threadId ORDER BY createdAt ASC")
    fun observeMessages(threadId: String): Flow<List<ChatMessageEntity>>

    @Upsert
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE threadId = :threadId")
    suspend fun clearThreadMessages(threadId: String)

    @Query("DELETE FROM chat_threads")
    suspend fun clearAllChatThreads()

    @Query("SELECT * FROM model_installs ORDER BY installedAt DESC")
    fun observeInstalledModels(): Flow<List<ModelInstallEntity>>

    @Query("SELECT * FROM model_installs WHERE id = :id")
    suspend fun getModel(id: String): ModelInstallEntity?

    @Upsert
    suspend fun upsertModel(model: ModelInstallEntity)

    @Query("DELETE FROM model_installs WHERE id = :id")
    suspend fun deleteModel(id: String)

    @Query("SELECT * FROM import_jobs ORDER BY createdAt DESC LIMIT 20")
    fun observeImportJobs(): Flow<List<ImportJobEntity>>

    @Upsert
    suspend fun upsertImportJob(job: ImportJobEntity)
}
