package com.englishprep.ai.domain.grading

import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.ScoreEstimate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class ExamScoreEstimator @Inject constructor() {

    fun estimate(examId: ExamId, rawCorrect: Int, rawTotal: Int): ScoreEstimate {
        val safeTotal = rawTotal.coerceAtLeast(1)
        val ratio = (rawCorrect.toFloat() / safeTotal.toFloat()).coerceIn(0f, 1f)
        val pct = (ratio * 100f).roundToInt()

        return when (examId) {
            ExamId.ITP_L1 -> {
                val estScore = (310f + ratio * (677f - 310f)).roundToInt().coerceIn(310, 677)
                ScoreEstimate(
                    examId = examId,
                    rawCorrect = rawCorrect,
                    rawTotal = rawTotal,
                    accuracyPercent = pct,
                    primaryEstimateLabel = "Est. ITP $estScore / 677",
                    secondaryNote = "Estimasi non-resmi ($rawCorrect/$rawTotal benar); konversi skor ITP resmi memerlukan tabel ETS berlisensi.",
                    officialEquivalent = false,
                )
            }
            ExamId.IBT_2026 -> {
                val bandRaw = (1.0 + ratio * 5.0).coerceIn(1.0, 6.0)
                val roundedHalf = (bandRaw * 2.0).roundToInt() / 2.0
                val legacyApprox = (roundedHalf / 6.0 * 120.0).roundToInt()
                ScoreEstimate(
                    examId = examId,
                    rawCorrect = rawCorrect,
                    rawTotal = rawTotal,
                    accuracyPercent = pct,
                    primaryEstimateLabel = "Est. $roundedHalf / 6 (≈$legacyApprox/120)",
                    secondaryNote = "Ringkasan akurasi latihan non-resmi; ujian iBT 2026 bersifat adaptif sehingga rumus lokal tidak mereplikasi skor resmi ETS.",
                    officialEquivalent = false,
                )
            }
            ExamId.IELTS_ACADEMIC -> {
                val band = ((ratio * 9.0) * 2.0).roundToInt() / 2.0
                ScoreEstimate(
                    examId = examId,
                    rawCorrect = rawCorrect,
                    rawTotal = rawTotal,
                    accuracyPercent = pct,
                    primaryEstimateLabel = "Est. Band ${"%.1f".format(band)} / 9.0",
                    secondaryNote = "Estimasi objektif non-resmi ($rawCorrect/$rawTotal benar); komponen Writing/Speaking dinilai terpisah dengan rubrik.",
                    officialEquivalent = false,
                )
            }
        }
    }
}
