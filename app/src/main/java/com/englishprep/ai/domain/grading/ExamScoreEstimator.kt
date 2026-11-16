package com.englishprep.ai.domain.grading

import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.ScoreEstimate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Computes deterministic raw objective accuracy metrics without fabricating
 * uncalibrated scaled scores (310-677, 1-6/0-120, or official IELTS bands).
 */
@Singleton
class ExamScoreEstimator @Inject constructor() {

    fun estimate(
        examId: ExamId,
        rawCorrect: Int,
        rawTotal: Int,
        skippedCount: Int = 0,
    ): ScoreEstimate {
        val safeTotal = rawTotal.coerceAtLeast(1)
        val ratio = (rawCorrect.toFloat() / safeTotal.toFloat()).coerceIn(0f, 1f)
        val pct = (ratio * 100f).roundToInt()

        val primaryLabel = "Skor Mentah: $rawCorrect/$rawTotal ($pct%)"
        val note = when (examId) {
            ExamId.ITP_L1 -> "Skor latihan mentah non-resmi ($rawCorrect/$rawTotal benar, $skippedCount dilewati); tabel konversi ETS ITP 310-677 berlisensi tidak disertakan."
            ExamId.IBT_2026 -> "Ringkasan akurasi latihan non-resmi ($rawCorrect/$rawTotal benar, $skippedCount dilewati); ujian iBT 2026 bersifat adaptif dan aplikasi tidak mereplikasi skala 1-6/0-120 resmi ETS."
            ExamId.IELTS_ACADEMIC -> "Skor latihan objektif non-resmi ($rawCorrect/$rawTotal benar, $skippedCount dilewati); penilaian Writing/Speaking menggunakan komentar rubrik terpisah, bukan Band resmi."
        }

        return ScoreEstimate(
            examId = examId,
            rawCorrect = rawCorrect,
            rawTotal = rawTotal,
            skippedCount = skippedCount,
            accuracyPercent = pct,
            primaryEstimateLabel = primaryLabel,
            secondaryNote = note,
            officialEquivalent = false,
            policyVersion = POLICY_VERSION,
        )
    }

    companion object {
        const val POLICY_VERSION = "v1.1-raw-deterministic"
    }
}
