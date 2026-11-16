package com.englishprep.ai.domain.models

import kotlinx.serialization.Serializable

enum class ExamId(
    val title: String,
    val subtitle: String,
    val scaleLabel: String,
    val officialReferenceUrl: String,
    val supportsWriting: Boolean,
    val supportsSpeaking: Boolean,
) {
    ITP_L1(
        title = "TOEFL ITP Level 1",
        subtitle = "Listening, Structure & Written Expression, Reading",
        scaleLabel = "Format 310-677 (Latihan memakai skor mentah)",
        officialReferenceUrl = "https://www.ets.org/toefl/itp/test-content.html",
        supportsWriting = false,
        supportsSpeaking = false,
    ),
    IBT_2026(
        title = "TOEFL iBT (2026)",
        subtitle = "Post-Jan 2026: Reading, Listening, Writing, Speaking",
        scaleLabel = "Format 1-6 (Latihan non-adaptif, skor mentah)",
        officialReferenceUrl = "https://www.ets.org/toefl/test-takers/ibt/about/content.html?version=after-jan-2026",
        supportsWriting = true,
        supportsSpeaking = true,
    ),
    IELTS_ACADEMIC(
        title = "IELTS Academic",
        subtitle = "Listening, Reading, Writing Tasks 1-2, Speaking Parts 1-3",
        scaleLabel = "Format Band 0-9 (Latihan memakai skor mentah)",
        officialReferenceUrl = "https://ielts.org/take-a-test/test-types/ielts-academic-test",
        supportsWriting = true,
        supportsSpeaking = true,
    ),
}

enum class ExamSection(val label: String) {
    LISTENING("Listening"),
    STRUCTURE("Structure & Written Expression"),
    READING("Reading"),
    WRITING("Writing"),
    SPEAKING("Speaking"),
}

enum class TaskType {
    CHOICE_SINGLE,
    CHOICE_MULTI,
    FILL_BLANK,
    MATCHING,
    REORDER,
    ERROR_IDENTIFICATION,
    SENTENCE_BUILD,
    WRITING_RESPONSE,
    SPEAKING_RESPONSE,
    LISTEN_REPEAT,
}

enum class PackStatus { SAMPLE, INCOMPLETE, READY }

enum class ExamMode { PRACTICE, STRICT }

enum class SessionState { ACTIVE, SUBMITTED, ABANDONED }

@Serializable
data class MatchingPairSpec(
    val left: String,
    val rightOptions: List<String>,
)

@Serializable
data class QuestionItem(
    val id: String,
    val packId: String,
    val moduleId: String? = null,
    val section: ExamSection,
    val taskType: TaskType,
    val prompt: String,
    val options: List<String> = emptyList(),
    val answer: String = "",
    val acceptedAnswers: List<String> = emptyList(),
    val explanation: String,
    val rationaleSource: String,
    val difficulty: String = "B2",
    val sortIndex: Int = 0,
    val points: Int = 1,
    val sourceLicense: String = "Original",
    val passageId: String? = null,
    val audioAssetId: String? = null,
    val maxWords: Int? = null,
    val minWords: Int? = null,
    val recommendedWords: Int? = null,
    val prepSeconds: Int = 0,
    val speakSeconds: Int = 45,
    val referenceText: String? = null,
    val pairs: List<MatchingPairSpec> = emptyList(),
    val rubricCriteria: List<String> = emptyList(),
)

@Serializable
data class PassageItem(
    val id: String,
    val packId: String,
    val title: String,
    val body: String,
    val sourceLicense: String = "Original",
    val readingOrder: Int = 1,
)

@Serializable
data class MediaAssetItem(
    val id: String,
    val packId: String,
    val relativePathOrUri: String,
    val kind: String,
    val sha256: String,
    val byteSize: Long,
    val durationMs: Long? = null,
    val caption: String,
    val license: String = "Original",
)

data class ContentPackSummary(
    val packId: String,
    val examId: ExamId,
    val edition: String,
    val title: String,
    val theme: String,
    val status: PackStatus,
    val blueprintCount: Int,
    val availableCount: Int,
    val durationMinutes: Int,
    val blueprintDurationMinutes: Int,
    val license: String,
    val provenance: String,
    val audioBytes: Long,
)

data class CourseModuleItem(
    val id: String,
    val examId: ExamId,
    val section: ExamSection,
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
    val drills: List<QuestionItem> = emptyList(),
)

sealed interface GradingOutcome {
    data class Objective(
        val isCorrect: Boolean,
        val isSkipped: Boolean,
        val earnedPoints: Int,
        val maxPoints: Int,
        val normalizedUserAnswer: String,
        val feedbackMessage: String,
    ) : GradingOutcome

    data class SubjectivePending(
        val wordCount: Int,
        val meetsMinimumWords: Boolean,
        val hasAudioRecording: Boolean,
        val feedbackMessage: String,
    ) : GradingOutcome
}

data class ScoreEstimate(
    val examId: ExamId,
    val rawCorrect: Int,
    val rawTotal: Int,
    val skippedCount: Int = 0,
    val accuracyPercent: Int,
    val primaryEstimateLabel: String,
    val secondaryNote: String,
    val officialEquivalent: Boolean = false,
    val policyVersion: String = "v1.1-raw-deterministic",
)

data class SpeakingMetrics(
    val durationSeconds: Double,
    val wordCount: Int,
    val wordsPerMinute: Int,
    val fillerCount: Int,
    val overlapRatioWithReference: Double?,
)

data class RagSnippet(
    val sourceId: String,
    val title: String,
    val excerpt: String,
)

data class MaterialBankEntry(
    val id: String,
    val sourceId: String,
    val examScope: String,
    val skill: String,
    val sourceTitle: String,
    val chapterTitle: String,
    val pageRange: String,
    val pdfFileName: String,
    val authorOrOrg: String,
    val license: String,
    val sourceUrl: String,
    val redistributable: Boolean,
    val excerpt: String,
)
