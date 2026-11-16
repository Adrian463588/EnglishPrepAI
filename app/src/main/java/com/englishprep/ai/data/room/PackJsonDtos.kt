package com.englishprep.ai.data.room

import com.englishprep.ai.domain.models.MatchingPairSpec
import kotlinx.serialization.Serializable

@Serializable
data class CurriculumRootDto(
    val schemaVersion: Int = 1,
    val modules: List<CurriculumModuleDto> = emptyList(),
)

@Serializable
data class CurriculumDrillDto(
    val id: String,
    val taskType: String,
    val prompt: String,
    val options: List<String> = emptyList(),
    val answer: String = "",
    val acceptedAnswers: List<String> = emptyList(),
    val explanation: String = "",
)

@Serializable
data class CurriculumModuleDto(
    val id: String,
    val examId: String,
    val section: String,
    val title: String,
    val objective: String,
    val theory: String,
    val workedExample: String,
    val commonMistake: String,
    val difficulty: String,
    val estimatedMinutes: Int,
    val sortIndex: Int,
    val license: String = "Original",
    val provenance: String = "Original",
    val audioScript: String? = null,
    val audioAssetPath: String? = null,
    val drills: List<CurriculumDrillDto> = emptyList(),
)

@Serializable
data class PackIndexRootDto(
    val schemaVersion: Int = 1,
    val totalPacks: Int = 0,
    val packs: List<PackIndexEntryDto> = emptyList(),
)

@Serializable
data class PackIndexEntryDto(
    val packId: String,
    val exam: String,
    val edition: String = "2026.1",
    val title: String,
    val theme: String = "",
    val status: String = "SAMPLE",
    val blueprintCount: Int = 0,
    val availableCount: Int = 0,
    val durationMinutes: Int = 20,
    val blueprintDurationMinutes: Int = 90,
    val license: String = "Original",
    val provenance: String = "Original",
    val fileName: String,
    val checksum: String = "",
    val audioBytes: Long = 0L,
)

@Serializable
data class PackDocumentDto(
    val schemaVersion: Int = 1,
    val exam: String,
    val edition: String = "2026.1",
    val packId: String,
    val title: String,
    val theme: String = "",
    val status: String = "SAMPLE",
    val blueprintCount: Int = 0,
    val availableCount: Int = 0,
    val durationMinutes: Int = 20,
    val blueprintDurationMinutes: Int = 90,
    val license: String = "Original",
    val provenance: String = "Original",
    val checksum: String = "",
    val passages: List<PackPassageDto> = emptyList(),
    val assets: List<PackAssetDto> = emptyList(),
    val items: List<PackQuestionDto> = emptyList(),
)

@Serializable
data class PackPassageDto(
    val id: String,
    val title: String,
    val body: String,
    val readingOrder: Int = 1,
    val sourceLicense: String = "Original",
)

@Serializable
data class PackAssetDto(
    val id: String,
    val relativePath: String,
    val kind: String = "AUDIO",
    val caption: String = "",
    val license: String = "Original",
    val sha256: String = "",
    val byteSize: Long = 0L,
    val durationMs: Long? = null,
)

@Serializable
data class PackQuestionDto(
    val id: String,
    val section: String,
    val taskType: String,
    val prompt: String,
    val options: List<String> = emptyList(),
    val answer: String = "",
    val acceptedAnswers: List<String> = emptyList(),
    val explanation: String = "",
    val rationaleSource: String = "",
    val difficulty: String = "B2",
    val sortIndex: Int = 0,
    val points: Int = 1,
    val license: String = "Original",
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
data class QuestionResponseSchemaDto(
    val acceptedAnswers: List<String> = emptyList(),
    val maxWords: Int? = null,
    val minWords: Int? = null,
    val recommendedWords: Int? = null,
    val prepSeconds: Int = 0,
    val speakSeconds: Int = 45,
    val referenceText: String? = null,
    val pairs: List<MatchingPairSpec> = emptyList(),
    val rubricCriteria: List<String> = emptyList(),
)
