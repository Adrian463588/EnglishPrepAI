package com.englishprep.ai.domain.grading

import com.englishprep.ai.domain.models.SpeakingMetrics
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@Singleton
class SpeakingMetricsAnalyzer @Inject constructor() {

    private val fillers = setOf("um", "uh", "erm", "ah", "like")

    fun analyzeTranscript(
        transcript: String,
        durationSeconds: Double,
        referenceText: String? = null,
    ): SpeakingMetrics {
        val words = transcript.trim()
            .lowercase()
            .replace(Regex("[^a-z0-9'\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }

        val count = words.size
        val mins = (durationSeconds / 60.0).coerceAtLeast(1.0 / 60.0)
        val wpm = (count / mins).roundToInt()
        val fillerCount = words.count { it in fillers }

        val overlap = referenceText?.takeIf { it.isNotBlank() }?.let { ref ->
            val refWords = ref.lowercase()
                .replace(Regex("[^a-z0-9'\\s]"), " ")
                .split(Regex("\\s+"))
                .filter { it.isNotEmpty() }
            if (refWords.isEmpty()) 0.0 else {
                val userCounts = words.groupingBy { it }.eachCount()
                var matched = 0
                val used = mutableMapOf<String, Int>()
                refWords.forEach { rw ->
                    val seen = used[rw] ?: 0
                    val avail = userCounts[rw] ?: 0
                    if (seen < avail) {
                        matched++
                        used[rw] = seen + 1
                    }
                }
                matched.toDouble() / refWords.size.toDouble()
            }
        }

        return SpeakingMetrics(
            durationSeconds = durationSeconds,
            wordCount = count,
            wordsPerMinute = wpm,
            fillerCount = fillerCount,
            overlapRatioWithReference = overlap,
        )
    }
}

/**
 * Pure Kotlin Whisper 80-mel log spectrogram feature extractor (16 kHz mono PCM -> [80 x 3000]).
 * Output layout is row-major [1, 80, 3000] matching Hugging Face Whisper ONNX encoder input.
 */
object WhisperAudioFeatures {
    private const val SAMPLE_RATE = 16_000
    private const val N_FFT = 400
    private const val HOP_LENGTH = 160
    private const val N_MELS = 80
    private const val N_FRAMES = 3_000
    private const val FFT_SIZE = 512 // Next power of 2 >= N_FFT for Radix-2 FFT

    private val hannWindow = FloatArray(N_FFT) { i ->
        (0.5 * (1.0 - cos(2.0 * PI * i / N_FFT))).toFloat()
    }

    private val melFilterBank: Array<FloatArray> by lazy { buildMelFilterBank() }

    fun computeLogMelSpectrogram(pcmSamples: FloatArray): FloatArray {
        val targetSamples = N_FRAMES * HOP_LENGTH
        val padded = FloatArray(targetSamples + N_FFT)
        val copyLen = minOf(pcmSamples.size, targetSamples)
        System.arraycopy(pcmSamples, 0, padded, 0, copyLen)

        val nFreqs = FFT_SIZE / 2 + 1
        val melSpec = FloatArray(N_MELS * N_FRAMES)
        val re = FloatArray(FFT_SIZE)
        val im = FloatArray(FFT_SIZE)
        val power = FloatArray(nFreqs)

        var maxLog = Float.NEGATIVE_INFINITY
        for (frame in 0 until N_FRAMES) {
            val offset = frame * HOP_LENGTH
            re.fill(0f)
            im.fill(0f)
            for (i in 0 until N_FFT) {
                re[i] = padded[offset + i] * hannWindow[i]
            }
            fftRadix2(re, im)
            for (k in 0 until nFreqs) {
                power[k] = re[k] * re[k] + im[k] * im[k]
            }
            for (m in 0 until N_MELS) {
                val filter = melFilterBank[m]
                var energy = 0f
                for (k in 0 until nFreqs) {
                    energy += filter[k] * power[k]
                }
                val logVal = log10(max(energy, 1e-10f))
                melSpec[m * N_FRAMES + frame] = logVal
                if (logVal > maxLog) maxLog = logVal
            }
        }

        val clampMin = maxLog - 8.0f
        for (i in melSpec.indices) {
            val clamped = max(melSpec[i], clampMin)
            melSpec[i] = (clamped + 4.0f) / 4.0f
        }
        return melSpec
    }

    private fun fftRadix2(re: FloatArray, im: FloatArray) {
        val n = re.size
        var j = 0
        for (i in 1 until n) {
            var bit = n shr 1
            while (j and bit != 0) {
                j = j xor bit
                bit = bit shr 1
            }
            j = j xor bit
            if (i < j) {
                val tr = re[i]; re[i] = re[j]; re[j] = tr
                val ti = im[i]; im[i] = im[j]; im[j] = ti
            }
        }
        var len = 2
        while (len <= n) {
            val ang = -2.0 * PI / len
            val wLenRe = cos(ang).toFloat()
            val wLenIm = sin(ang).toFloat()
            var i = 0
            while (i < n) {
                var wRe = 1f
                var wIm = 0f
                for (k in 0 until len / 2) {
                    val uRe = re[i + k]
                    val uIm = im[i + k]
                    val idx2 = i + k + len / 2
                    val vRe = re[idx2] * wRe - im[idx2] * wIm
                    val vIm = re[idx2] * wIm + im[idx2] * wRe
                    re[i + k] = uRe + vRe
                    im[i + k] = uIm + vIm
                    re[idx2] = uRe - vRe
                    im[idx2] = uIm - vIm
                    val nextWRe = wRe * wLenRe - wIm * wLenIm
                    wIm = wRe * wLenIm + wIm * wLenRe
                    wRe = nextWRe
                }
                i += len
            }
            len = len shl 1
        }
    }

    private fun hzToMel(hz: Double): Double = 2595.0 * log10(1.0 + hz / 700.0)
    private fun melToHz(mel: Double): Double = 700.0 * (10.0.pow(mel / 2595.0) - 1.0)

    private fun buildMelFilterBank(): Array<FloatArray> {
        val nFreqs = FFT_SIZE / 2 + 1
        val melMin = hzToMel(0.0)
        val melMax = hzToMel(SAMPLE_RATE / 2.0)
        val melPoints = DoubleArray(N_MELS + 2) { i ->
            melMin + (melMax - melMin) * i / (N_MELS + 1)
        }
        val hzPoints = DoubleArray(N_MELS + 2) { i -> melToHz(melPoints[i]) }
        val fftFreqs = DoubleArray(nFreqs) { k -> k.toDouble() * SAMPLE_RATE / FFT_SIZE }

        return Array(N_MELS) { m ->
            val fLeft = hzPoints[m]
            val fCenter = hzPoints[m + 1]
            val fRight = hzPoints[m + 2]
            val enorm = 2.0 / (fRight - fLeft)
            FloatArray(nFreqs) { k ->
                val f = fftFreqs[k]
                val w = when {
                    f >= fLeft && f <= fCenter -> (f - fLeft) / (fCenter - fLeft)
                    f > fCenter && f <= fRight -> (fRight - f) / (fRight - fCenter)
                    else -> 0.0
                }
                (w * enorm).toFloat()
            }
        }
    }
}
