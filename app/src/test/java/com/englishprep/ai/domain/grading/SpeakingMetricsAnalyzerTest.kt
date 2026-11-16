package com.englishprep.ai.domain.grading

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sin

class SpeakingMetricsAnalyzerTest {

    private val analyzer = SpeakingMetricsAnalyzer()

    @Test
    fun `computes WPM filler counts and Listen-Repeat overlap ratio`() {
        val metrics = analyzer.analyzeTranscript(
            transcript = "Please um submit your revised field report before noon on Friday.",
            durationSeconds = 6.0,
            referenceText = "Please submit your revised field report before noon on Friday.",
        )
        assertEquals(11, metrics.wordCount)
        assertEquals(110, metrics.wordsPerMinute)
        assertEquals(1, metrics.fillerCount)
        assertTrue((metrics.overlapRatioWithReference ?: 0.0) > 0.90)
    }

    @Test
    fun `whisper log mel spectrogram outputs expected 80 by 3000 tensor shape`() {
        // 1 second 440Hz sine wave at 16 kHz
        val pcm = FloatArray(16_000) { i -> sin(2.0 * Math.PI * 440.0 * i / 16_000.0).toFloat() * 0.5f }
        val mel = WhisperAudioFeatures.computeLogMelSpectrogram(pcm)
        assertEquals(80 * 3000, mel.size)
        assertTrue(mel.any { it != 0f })
    }
}
