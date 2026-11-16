package com.englishprep.ai.data.room

import android.content.Context
import com.englishprep.ai.domain.models.ExamId
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val catalogDao: CatalogDao,
    private val json: Json,
) {
    private val mutex = Mutex()

    suspend fun seedIfNeeded() = mutex.withLock {
        val alreadySeededFull = catalogDao.countExamProfiles() >= ExamId.entries.size &&
            catalogDao.countModules() >= 30 &&
            catalogDao.countReadyFullPacks() >= 15
        if (alreadySeededFull) {
            return@withLock
        }

        val now = System.currentTimeMillis()
        val profiles = ExamId.entries.map { exam ->
            ExamProfileEntity(
                id = exam.name,
                name = exam.title,
                edition = "2026.2",
                scoringPolicyVersion = "v1.0-non-official",
                formatReferenceUrl = exam.officialReferenceUrl,
            )
        }
        catalogDao.upsertExamProfiles(profiles)

        val currText = context.assets.open("curriculum/curriculum.json").bufferedReader().use { it.readText() }
        val curriculum = json.decodeFromString<CurriculumRootDto>(currText)
        val moduleEntities = curriculum.modules.map { m ->
            CourseModuleEntity(
                id = m.id,
                examId = m.examId,
                section = m.section,
                parentId = null,
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
                drillsJson = json.encodeToString(m.drills),
            )
        }
        catalogDao.upsertModules(moduleEntities)
        catalogDao.clearCurriculumFts()
        catalogDao.insertCurriculumFts(
            curriculum.modules.map { m ->
                CurriculumFtsEntity(
                    moduleId = m.id,
                    examId = m.examId,
                    title = m.title,
                    body = "${m.objective} ${m.theory} ${m.workedExample} ${m.commonMistake}",
                )
            },
        )

        runCatching {
            val mbText = context.assets.open("material_bank/material_bank.json").bufferedReader().use { it.readText() }
            val materialBank = json.decodeFromString<MaterialBankRootDto>(mbText)
            if (materialBank.entries.isNotEmpty()) {
                catalogDao.insertCurriculumFts(
                    materialBank.entries.map { e ->
                        CurriculumFtsEntity(
                            moduleId = e.id,
                            examId = e.examScope,
                            title = "${e.chapterTitle} — ${e.sourceTitle}",
                            body = "${e.excerpt} | Sumber: ${e.authorOrOrg} (${e.license}, hal. ${e.pageRange})",
                        )
                    },
                )
            }
        }

        val indexText = context.assets.open("packs/index.json").bufferedReader().use { it.readText() }
        val packIndex = json.decodeFromString<PackIndexRootDto>(indexText)
        packIndex.packs.forEach { entry ->
            val packJson = context.assets.open("packs/${entry.fileName}").bufferedReader().use { it.readText() }
            val doc = json.decodeFromString<PackDocumentDto>(packJson)
            installParsedPack(
                doc = doc,
                installedPath = "asset://packs/${entry.fileName}",
                importedAt = now,
                audioBytes = entry.audioBytes,
                catalogDao = catalogDao,
                json = json,
                assetUriPrefix = "asset://",
            )
        }
    }

    companion object {
        suspend fun installParsedPack(
            doc: PackDocumentDto,
            installedPath: String,
            importedAt: Long,
            audioBytes: Long,
            catalogDao: CatalogDao,
            json: Json,
            assetUriPrefix: String,
        ) {
            val computedStatus = if (doc.items.size < doc.blueprintCount) "SAMPLE" else "READY"
            val packEntity = ContentPackEntity(
                id = doc.packId,
                examId = doc.exam,
                edition = doc.edition,
                title = doc.title,
                theme = doc.theme,
                manifestVersion = doc.schemaVersion,
                status = computedStatus,
                blueprintCount = doc.blueprintCount,
                availableCount = doc.items.size,
                durationMinutes = doc.durationMinutes,
                blueprintDurationMinutes = doc.blueprintDurationMinutes,
                license = doc.license,
                provenance = doc.provenance,
                importedAt = importedAt,
                installedPath = installedPath,
                checksum = doc.checksum,
                audioBytes = audioBytes,
            )
            val passageEntities = doc.passages.map { p ->
                PassageEntity(
                    id = p.id,
                    packId = doc.packId,
                    title = p.title,
                    body = p.body,
                    sourceLicense = p.sourceLicense,
                    readingOrder = p.readingOrder,
                )
            }
            val assetEntities = doc.assets.map { a ->
                MediaAssetEntity(
                    id = a.id,
                    packId = doc.packId,
                    relativePathOrUri = "$assetUriPrefix${a.relativePath}",
                    kind = a.kind,
                    sha256 = a.sha256,
                    byteSize = a.byteSize,
                    durationMs = a.durationMs,
                    caption = a.caption,
                    license = a.license,
                )
            }
            val questionEntities = mutableListOf<QuestionEntity>()
            val optionEntities = mutableListOf<QuestionOptionEntity>()
            doc.items.forEachIndexed { index, q ->
                val schema = QuestionResponseSchemaDto(
                    acceptedAnswers = q.acceptedAnswers,
                    maxWords = q.maxWords,
                    minWords = q.minWords,
                    recommendedWords = q.recommendedWords,
                    prepSeconds = q.prepSeconds,
                    speakSeconds = q.speakSeconds,
                    referenceText = q.referenceText,
                    pairs = q.pairs,
                    rubricCriteria = q.rubricCriteria,
                )
                questionEntities += QuestionEntity(
                    id = q.id,
                    packId = doc.packId,
                    moduleId = q.rationaleSource.ifBlank { null },
                    section = q.section,
                    taskType = q.taskType,
                    prompt = q.prompt,
                    passageId = q.passageId,
                    audioAssetId = q.audioAssetId,
                    responseSchemaJson = json.encodeToString(schema),
                    correctAnswerJson = q.answer,
                    explanation = q.explanation,
                    rationaleSource = q.rationaleSource,
                    difficulty = q.difficulty,
                    sortIndex = if (q.sortIndex > 0) q.sortIndex else index + 1,
                    points = q.points,
                    sourceLicense = q.license,
                )
                q.options.forEachIndexed { optIdx, text ->
                    optionEntities += QuestionOptionEntity(
                        id = "${q.id}_OPT_$optIdx",
                        questionId = q.id,
                        label = ('A' + optIdx).toString(),
                        text = text,
                        sortIndex = optIdx,
                    )
                }
            }
            catalogDao.installPackAtomic(
                pack = packEntity,
                passages = passageEntities,
                assets = assetEntities,
                questions = questionEntities,
                options = optionEntities,
            )
            // Also index passages into FTS for source-grounded tutoring
            if (passageEntities.isNotEmpty()) {
                catalogDao.insertCurriculumFts(
                    passageEntities.map { p ->
                        CurriculumFtsEntity(
                            moduleId = p.id,
                            examId = doc.exam,
                            title = p.title,
                            body = p.body,
                        )
                    },
                )
            }
        }
    }
}
