package com.englishprep.ai.domain.grading

import com.englishprep.ai.domain.models.PackStatus
import com.englishprep.ai.domain.models.TaskType
import com.englishprep.ai.domain.repositories.PackPlayBundle

/**
 * Pure Kotlin structural validator for offline exam bundles (E07-06, E07-08).
 * Ensures no duplicate question IDs, missing objective keys, unresolved media assets,
 * or false Full-Mock READY claims on incomplete packs.
 */
object PackIntegrityValidator {

    fun validateBundle(bundle: PackPlayBundle): List<String> {
        val issues = mutableListOf<String>()
        val pack = bundle.pack

        if (pack.license.isBlank() || pack.provenance.isBlank()) {
            issues += "Metadata lisensi atau provenansi paket ${pack.packId} kosong."
        }
        if (pack.status == PackStatus.READY && bundle.questions.size < pack.blueprintCount) {
            issues += "Paket ${pack.packId} berstatus READY padahal hanya tersedia ${bundle.questions.size}/${pack.blueprintCount} soal."
        }
        val seenIds = mutableSetOf<String>()
        bundle.questions.forEach { q ->
            if (!seenIds.add(q.id)) {
                issues += "ID soal duplikat ditemukan: ${q.id}"
            }
            if (q.audioAssetId != null && !bundle.assets.containsKey(q.audioAssetId)) {
                issues += "Referensi audio tidak ditemukan untuk ${q.id}: ${q.audioAssetId}"
            }
            if (q.passageId != null && !bundle.passages.containsKey(q.passageId)) {
                issues += "Referensi bacaan tidak ditemukan untuk ${q.id}: ${q.passageId}"
            }
            val requiresObjectiveKey = q.taskType !in setOf(
                TaskType.WRITING_RESPONSE,
                TaskType.SPEAKING_RESPONSE,
                TaskType.LISTEN_REPEAT,
            )
            if (requiresObjectiveKey && q.answer.isBlank()) {
                issues += "Kunci jawaban objektif kosong pada ${q.id}."
            }
            if (q.explanation.isBlank()) {
                issues += "Penjelasan soal kosong pada ${q.id}."
            }
        }
        return issues
    }
}
