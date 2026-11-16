package com.englishprep.ai.domain.grading

import com.englishprep.ai.domain.models.ExamId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExamScoreEstimatorTest {

    private val estimator = ExamScoreEstimator()

    @Test
    fun `itp defaults to deterministic raw score without uncalibrated 310 to 677 claims`() {
        val zero = estimator.estimate(ExamId.ITP_L1, rawCorrect = 0, rawTotal = 12)
        val full = estimator.estimate(ExamId.ITP_L1, rawCorrect = 12, rawTotal = 12)

        assertFalse(zero.officialEquivalent)
        assertFalse(full.officialEquivalent)
        assertEquals("v1.1-raw-deterministic", full.policyVersion)
        assertTrue(zero.primaryEstimateLabel.contains("0/12"))
        assertTrue(full.primaryEstimateLabel.contains("12/12"))
        assertFalse(full.primaryEstimateLabel.contains("Est. ITP"))
        assertTrue(zero.secondaryNote.contains("ETS", ignoreCase = true))
    }

    @Test
    fun `ibt 2026 reports raw practice accuracy without fabricating 1 to 6 or 0 to 120`() {
        val est = estimator.estimate(ExamId.IBT_2026, rawCorrect = 7, rawTotal = 9)
        assertFalse(est.officialEquivalent)
        assertEquals(78, est.accuracyPercent)
        assertTrue(est.primaryEstimateLabel.contains("7/9"))
        assertFalse(est.primaryEstimateLabel.contains("/ 6"))
        assertFalse(est.primaryEstimateLabel.contains("/120"))
        assertTrue(est.secondaryNote.contains("adaptif", ignoreCase = true) || est.secondaryNote.contains("adaptive", ignoreCase = true))
    }

    @Test
    fun `ielts reports deterministic raw score and rubric caveat without invented band`() {
        val mid = estimator.estimate(ExamId.IELTS_ACADEMIC, rawCorrect = 5, rawTotal = 7)
        assertFalse(mid.officialEquivalent)
        assertEquals(71, mid.accuracyPercent)
        assertTrue(mid.primaryEstimateLabel.contains("5/7"))
        assertFalse(mid.primaryEstimateLabel.startsWith("Est. Band"))
    }
}
