package com.englishprep.ai.data.repository

import com.englishprep.ai.data.room.CatalogDao
import com.englishprep.ai.data.room.CourseModuleEntity
import com.englishprep.ai.data.room.CurriculumDrillDto
import com.englishprep.ai.data.room.QuestionEntity
import com.englishprep.ai.data.room.QuestionResponseSchemaDto
import com.englishprep.ai.domain.models.ContentPackSummary
import com.englishprep.ai.domain.models.CourseModuleItem
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.ExamSection
import com.englishprep.ai.domain.models.MediaAssetItem
import com.englishprep.ai.domain.models.PackStatus
import com.englishprep.ai.domain.models.PassageItem
import com.englishprep.ai.domain.models.QuestionItem
import com.englishprep.ai.domain.models.RagSnippet
import com.englishprep.ai.domain.models.TaskType
import com.englishprep.ai.domain.repositories.ExamCatalogRepository
import com.englishprep.ai.domain.repositories.PackPlayBundle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamCatalogRepositoryImpl @Inject constructor(
    private val catalogDao: CatalogDao,
    private val json: Json,
) : ExamCatalogRepository {

    override fun observeModules(examId: ExamId): Flow<List<CourseModuleItem>> =
        catalogDao.observeModulesByExam(examId.name).map { list -> list.map(::toDomainModule) }

    override suspend fun getModule(moduleId: String): CourseModuleItem? =
        catalogDao.getModule(moduleId)?.let(::toDomainModule)

    override fun observePacks(examId: ExamId): Flow<List<ContentPackSummary>> =
        catalogDao.observePacksByExam(examId.name).map { packs ->
            packs.map { p ->
                ContentPackSummary(
                    packId = p.id,
                    examId = ExamId.valueOf(p.examId),
                    edition = p.edition,
                    title = p.title,
                    theme = p.theme,
                    status = runCatching { PackStatus.valueOf(p.status) }.getOrDefault(PackStatus.SAMPLE),
                    blueprintCount = p.blueprintCount,
                    availableCount = p.availableCount,
                    durationMinutes = p.durationMinutes,
                    blueprintDurationMinutes = p.blueprintDurationMinutes,
                    license = p.license,
                    provenance = p.provenance,
                    audioBytes = p.audioBytes,
                )
            }
        }

    override suspend fun loadPackBundle(packId: String): PackPlayBundle? {
        val pack = catalogDao.getPack(packId) ?: return null
        val passages = catalogDao.getPassagesForPack(packId).associate { p ->
            p.id to PassageItem(p.id, p.packId, p.title, p.body, p.sourceLicense, p.readingOrder)
        }
        val assets = catalogDao.getMediaAssetsForPack(packId).associate { a ->
            a.id to MediaAssetItem(a.id, a.packId, a.relativePathOrUri, a.kind, a.sha256, a.byteSize, a.durationMs, a.caption, a.license)
        }
        val questions = catalogDao.getQuestionsForPack(packId).map { q -> toDomainQuestion(q) }
        return PackPlayBundle(
            pack = ContentPackSummary(
                packId = pack.id,
                examId = ExamId.valueOf(pack.examId),
                edition = pack.edition,
                title = pack.title,
                theme = pack.theme,
                status = runCatching { PackStatus.valueOf(pack.status) }.getOrDefault(PackStatus.SAMPLE),
                blueprintCount = pack.blueprintCount,
                availableCount = pack.availableCount,
                durationMinutes = pack.durationMinutes,
                blueprintDurationMinutes = pack.blueprintDurationMinutes,
                license = pack.license,
                provenance = pack.provenance,
                audioBytes = pack.audioBytes,
            ),
            passages = passages,
            assets = assets,
            questions = questions,
        )
    }

    override suspend fun loadQuestionsByIds(questionIds: List<String>): PackPlayBundle? {
        if (questionIds.isEmpty()) return null
        val questionEntities = catalogDao.getQuestionsByIds(questionIds)
        if (questionEntities.isEmpty()) return null
        val packIds = questionEntities.map { it.packId }.distinct()
        val firstPack = catalogDao.getPack(packIds.first()) ?: return null
        val passages = packIds.flatMap { catalogDao.getPassagesForPack(it) }.associate { p ->
            p.id to PassageItem(p.id, p.packId, p.title, p.body, p.sourceLicense, p.readingOrder)
        }
        val assets = packIds.flatMap { catalogDao.getMediaAssetsForPack(it) }.associate { a ->
            a.id to MediaAssetItem(a.id, a.packId, a.relativePathOrUri, a.kind, a.sha256, a.byteSize, a.durationMs, a.caption, a.license)
        }
        return PackPlayBundle(
            pack = ContentPackSummary(
                packId = "RETRY",
                examId = ExamId.valueOf(firstPack.examId),
                edition = firstPack.edition,
                title = "Latihan Ulang Soal Salah",
                theme = "Focused mistake review session",
                status = PackStatus.SAMPLE,
                blueprintCount = questionEntities.size,
                availableCount = questionEntities.size,
                durationMinutes = 15,
                blueprintDurationMinutes = 15,
                license = "Original",
                provenance = "LocalMistakeReview",
                audioBytes = 0L,
            ),
            passages = passages,
            assets = assets,
            questions = questionEntities.map { toDomainQuestion(it) },
        )
    }

    override suspend fun searchLocalCurriculum(examId: ExamId?, query: String, limit: Int): List<RagSnippet> {
        val stopwords = setOf(
            "jelaskan", "ringkasan", "konsep", "utama", "modul", "beserta", "contoh", "tentang",
            "apa", "bagaimana", "mengapa", "yang", "dan", "atau", "untuk", "dengan", "dari", "pada",
            "the", "and", "for", "with", "what", "how", "why", "explain", "about", "module",
        )
        val tokens = query.lowercase()
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.length >= 3 && it !in stopwords }
            .distinct()
            .take(6)
        if (tokens.isEmpty()) return emptyList()
        val ftsQuery = tokens.joinToString(" OR ") { "$it*" }
        val hits = runCatching { catalogDao.searchCurriculumFts(ftsQuery, limit * 2) }.getOrDefault(emptyList())
        return hits
            .filter { examId == null || it.examId == examId.name }
            .take(limit)
            .map { hit ->
                RagSnippet(
                    sourceId = hit.moduleId,
                    title = hit.title,
                    excerpt = hit.body.take(420),
                )
            }
    }

    private fun toDomainModule(m: CourseModuleEntity): CourseModuleItem {
        val drills = runCatching {
            json.decodeFromString<List<CurriculumDrillDto>>(m.drillsJson)
        }.getOrDefault(emptyList()).mapIndexed { idx, d ->
            QuestionItem(
                id = d.id,
                packId = m.id,
                moduleId = m.id,
                section = ExamSection.valueOf(m.section),
                taskType = TaskType.valueOf(d.taskType),
                prompt = d.prompt,
                options = d.options,
                answer = d.answer,
                acceptedAnswers = d.acceptedAnswers.ifEmpty { listOfNotNull(d.answer.takeIf { it.isNotBlank() }) },
                explanation = d.explanation,
                rationaleSource = m.id,
                difficulty = m.difficulty,
                sortIndex = idx + 1,
                sourceLicense = m.license,
            )
        }
        return CourseModuleItem(
            id = m.id,
            examId = ExamId.valueOf(m.examId),
            section = ExamSection.valueOf(m.section),
            title = m.title,
            objective = m.objective,
            theory = m.theory,
            workedExample = m.workedExample,
            commonMistake = m.commonMistake,
            difficulty = m.difficulty,
            estimatedMinutes = m.estimatedMinutes,
            sortIndex = m.sortIndex,
            license = m.license,
            provenance = m.provenance,
            audioScript = m.audioScript,
            audioAssetPath = m.audioAssetPath,
            drills = drills,
        )
    }

    private suspend fun toDomainQuestion(q: QuestionEntity): QuestionItem {
        val opts = catalogDao.getOptionsForQuestion(q.id).map { it.text }
        val schema = runCatching {
            json.decodeFromString<QuestionResponseSchemaDto>(q.responseSchemaJson)
        }.getOrDefault(QuestionResponseSchemaDto())
        return QuestionItem(
            id = q.id,
            packId = q.packId,
            moduleId = q.moduleId,
            section = ExamSection.valueOf(q.section),
            taskType = TaskType.valueOf(q.taskType),
            prompt = q.prompt,
            options = opts,
            answer = q.correctAnswerJson ?: "",
            acceptedAnswers = schema.acceptedAnswers,
            explanation = q.explanation,
            rationaleSource = q.rationaleSource,
            difficulty = q.difficulty,
            sortIndex = q.sortIndex,
            points = q.points,
            sourceLicense = q.sourceLicense,
            passageId = q.passageId,
            audioAssetId = q.audioAssetId,
            maxWords = schema.maxWords,
            minWords = schema.minWords,
            recommendedWords = schema.recommendedWords,
            prepSeconds = schema.prepSeconds,
            speakSeconds = schema.speakSeconds,
            referenceText = schema.referenceText,
            pairs = schema.pairs,
            rubricCriteria = schema.rubricCriteria,
        )
    }
}
