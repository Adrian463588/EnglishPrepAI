package com.englishprep.ai.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Fts4
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "exam_profiles")
data class ExamProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val edition: String,
    val scoringPolicyVersion: String,
    val formatReferenceUrl: String,
)

@Entity(
    tableName = "course_modules",
    foreignKeys = [
        ForeignKey(
            entity = ExamProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["examId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("examId"), Index("section")],
)
data class CourseModuleEntity(
    @PrimaryKey val id: String,
    val examId: String,
    val section: String,
    val parentId: String? = null,
    val title: String,
    val objective: String,
    val theory: String,
    val workedExample: String,
    val commonMistake: String,
    val difficulty: String,
    val estimatedMinutes: Int,
    val sortIndex: Int,
    val license: String,
    val provenance: String,
    val audioScript: String? = null,
    val audioAssetPath: String? = null,
    val drillsJson: String = "[]",
)

@Fts4
@Entity(tableName = "curriculum_fts")
data class CurriculumFtsEntity(
    val moduleId: String,
    val examId: String,
    val title: String,
    val body: String,
)

@Entity(
    tableName = "content_packs",
    foreignKeys = [
        ForeignKey(
            entity = ExamProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["examId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["examId", "id"], unique = true)],
)
data class ContentPackEntity(
    @PrimaryKey val id: String,
    val examId: String,
    val edition: String,
    val title: String,
    val theme: String,
    val manifestVersion: Int,
    val status: String,
    val blueprintCount: Int,
    val availableCount: Int,
    val durationMinutes: Int,
    val blueprintDurationMinutes: Int,
    val license: String,
    val provenance: String,
    val importedAt: Long,
    val installedPath: String,
    val checksum: String,
    val audioBytes: Long,
)

@Entity(
    tableName = "passages",
    foreignKeys = [
        ForeignKey(
            entity = ContentPackEntity::class,
            parentColumns = ["id"],
            childColumns = ["packId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("packId")],
)
data class PassageEntity(
    @PrimaryKey val id: String,
    val packId: String,
    val title: String,
    val body: String,
    val sourceLicense: String,
    val readingOrder: Int,
)

@Entity(
    tableName = "media_assets",
    foreignKeys = [
        ForeignKey(
            entity = ContentPackEntity::class,
            parentColumns = ["id"],
            childColumns = ["packId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("packId")],
)
data class MediaAssetEntity(
    @PrimaryKey val id: String,
    val packId: String,
    val relativePathOrUri: String,
    val kind: String,
    val sha256: String,
    val byteSize: Long,
    val durationMs: Long?,
    val caption: String,
    val license: String,
)

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = ContentPackEntity::class,
            parentColumns = ["id"],
            childColumns = ["packId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["packId", "section", "sortIndex"])],
)
data class QuestionEntity(
    @PrimaryKey val id: String,
    val packId: String,
    val moduleId: String?,
    val section: String,
    val taskType: String,
    val prompt: String,
    val passageId: String?,
    val audioAssetId: String?,
    val responseSchemaJson: String,
    val correctAnswerJson: String?,
    val explanation: String,
    val rationaleSource: String,
    val difficulty: String,
    val sortIndex: Int,
    val points: Int,
    val sourceLicense: String,
)

@Entity(
    tableName = "question_options",
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("questionId")],
)
data class QuestionOptionEntity(
    @PrimaryKey val id: String,
    val questionId: String,
    val label: String,
    val text: String,
    val sortIndex: Int,
)

@Entity(
    tableName = "exam_sessions",
    indices = [Index(value = ["examId", "startAt"])],
)
data class ExamSessionEntity(
    @PrimaryKey val id: String,
    val examId: String,
    val packId: String?,
    val mode: String,
    val title: String,
    val startAt: Long,
    val deadlineAt: Long?,
    val endAt: Long?,
    val lastSavedAt: Long,
    val state: String,
    val elapsedMonotonicCheckpointMs: Long,
    val currentQuestionIndex: Int,
    val scorePolicyVersion: String,
)

@Entity(
    tableName = "user_answers",
    foreignKeys = [
        ForeignKey(
            entity = ExamSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["sessionId", "questionId"], unique = true)],
)
data class UserAnswerEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val questionId: String,
    val answerJson: String,
    val updatedAt: Long,
    val isFlagged: Boolean,
    val submittedAt: Long?,
    val autoCheckResultJson: String?,
)

@Entity(
    tableName = "evaluations",
    foreignKeys = [
        ForeignKey(
            entity = ExamSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sessionId")],
)
data class EvaluationEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val section: String,
    val rawCorrect: Int,
    val rawTotal: Int,
    val scaledScore: String?,
    val officialEquivalent: Boolean,
    val estimateMethod: String?,
    val aiFeedbackJson: String?,
    val createdAt: Long,
)

@Entity(
    tableName = "study_progress",
    indices = [Index(value = ["examId", "moduleId"], unique = true)],
)
data class StudyProgressEntity(
    @PrimaryKey val id: String,
    val examId: String,
    val moduleId: String,
    val attempts: Int,
    val correct: Int,
    val reviewedAt: Long,
    val masteryEstimate: Float,
    val updatedAt: Long,
)

@Entity(tableName = "chat_threads")
data class ChatThreadEntity(
    @PrimaryKey val id: String,
    val examId: String?,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatThreadEntity::class,
            parentColumns = ["id"],
            childColumns = ["threadId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["threadId", "createdAt"])],
)
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val threadId: String,
    val role: String,
    val text: String,
    val citationsJson: String?,
    val modelId: String?,
    val createdAt: Long,
)

@Entity(tableName = "model_installs")
data class ModelInstallEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val family: String,
    val modelVariant: String,
    val runtime: String,
    val format: String,
    val quantization: String,
    val sha256: String,
    val sizeBytes: Long,
    val privateStoragePath: String,
    val sourceDocumentUri: String?,
    val status: String,
    val installedAt: Long,
    val licenseId: String,
)

@Entity(
    tableName = "speech_recordings",
    foreignKeys = [
        ForeignKey(
            entity = ExamSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sessionId"), Index("questionId")],
)
data class SpeechRecordingEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val questionId: String,
    val privateFilePath: String,
    val durationMs: Long,
    val transcript: String?,
    val feedbackJson: String?,
    val createdAt: Long,
)

@Entity(tableName = "import_jobs")
data class ImportJobEntity(
    @PrimaryKey val id: String,
    val kind: String,
    val sourceUri: String,
    val state: String,
    val errorCode: String?,
    val createdAt: Long,
    val completedAt: Long?,
)
