package com.englishprep.ai.domain.grading

import com.englishprep.ai.domain.models.ExamId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExamScoreEstimatorTest {

    private val estimator = ExamScoreEstimator()

    @Test
    fun `itp estimate remains within 310 to 677 and marks non-official`() {
        val zero = estimator.estimate(ExamId.ITP_L1, rawCorrect = 0, rawTotal = 12)
        val full = estimator.estimate(ExamId.ITP_L1, rawCorrect = 12, rawTotal = 12)

        assertFalse(zero.officialEquivalent)
        assertFalse(full.officialEquivalent)
        assertTrue(zero.primaryEstimateLabel.contains("310"))
        assertTrue(full.primaryEstimateLabel.contains("677"))
        assertTrue(zero.secondaryNote.contains("non-resmi", ignoreCase = true) || zero.secondaryNote.contains("non-official", ignoreCase = true))
    }

    @Test
    fun `ibt 2026 uses 1 to 6 scale and disclaims adaptive non-linearity`() {
        val est = estimator.estimate(ExamId.IBT_2026, rawCorrect = 7, rawTotal = 9)
        assertFalse(est.officialEquivalent)
        assertEquals(78, est.accuracyPercent)
        assertTrue(est.primaryEstimateLabel.contains("/ 6"))
        assertTrue(est.secondaryNote.contains("adaptif", ignoreCase = true) || est.secondaryNote.contains("adaptive", ignoreCase = true))
    }

    @Test
    fun `ielts estimate produces half-band steps from 0 to 9`() {
        val mid = estimator.estimate(ExamId.IELTS_ACADEMIC, rawCorrect = 5, rawTotal = 7)
        assertFalse(mid.officialEquivalent)
        assertTrue(mid.primaryEstimateLabel.startsWith("Est. Band"))
    }
}
