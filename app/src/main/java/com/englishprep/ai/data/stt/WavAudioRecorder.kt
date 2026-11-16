package com.englishprep.ai.data.stt

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

data class RecordingState(
    val isRecording: Boolean = false,
    val elapsedMs: Long = 0L,
    val amplitudeLevel: Float = 0f,
    val outputFilePath: String? = null,
    val error: String? = null,
)

@Singleton
class WavAudioRecorder @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        const val SAMPLE_RATE = 16_000
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var audioRecord: AudioRecord? = null
    private var captureJob: Job? = null
    private val pcmBuffer = ByteArrayOutputStream()
    private var currentFile: File? = null
    private var startedAtMs: Long = 0L

    private val _state = MutableStateFlow(RecordingState())
    val state: StateFlow<RecordingState> = _state.asStateFlow()

    @SuppressLint("MissingPermission")
    fun startRecording(sessionId: String, questionId: String): Boolean {
        if (_state.value.isRecording) return false
        val minBuf = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        ).coerceAtLeast(4096)

        val recorder = try {
            AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBuf * 2,
            )
        } catch (t: Throwable) {
            _state.value = RecordingState(error = "Gagal mengakses mikrofon: ${t.message}")
            return false
        }

        if (recorder.state != AudioRecord.STATE_INITIALIZED) {
            recorder.release()
            _state.value = RecordingState(error = "Inisialisasi AudioRecord gagal.")
            return false
        }

        val dir = File(context.filesDir, "recordings/$sessionId").apply { mkdirs() }
        val outFile = File(dir, "${questionId}_${System.currentTimeMillis()}.wav")
        currentFile = outFile
        pcmBuffer.reset()
        startedAtMs = System.currentTimeMillis()
        audioRecord = recorder

        recorder.startRecording()
        _state.value = RecordingState(isRecording = true, outputFilePath = outFile.absolutePath)

        captureJob = scope.launch {
            val chunk = ByteArray(minBuf)
            try {
                while (isActive && _state.value.isRecording) {
                    val read = recorder.read(chunk, 0, chunk.size)
                    if (read > 0) {
                        pcmBuffer.write(chunk, 0, read)
                        val peak = computeNormalizedPeak(chunk, read)
                        _state.value = _state.value.copy(
                            elapsedMs = System.currentTimeMillis() - startedAtMs,
                            amplitudeLevel = peak,
                        )
                    }
                }
            } catch (t: Throwable) {
                _state.value = _state.value.copy(isRecording = false, error = "Rekaman terhenti: ${t.message}")
            }
        }
        return true
    }

    fun stopRecording(): File? {
        if (!_state.value.isRecording && audioRecord == null) return currentFile
        _state.value = _state.value.copy(isRecording = false)
        captureJob?.cancel()
        runCatching { audioRecord?.stop() }
        runCatching { audioRecord?.release() }
        audioRecord = null

        val file = currentFile ?: return null
        val pcmBytes = pcmBuffer.toByteArray()
        if (pcmBytes.isNotEmpty()) {
            writeWavFile(file, pcmBytes, SAMPLE_RATE, channels = 1, bitsPerSample = 16)
        }
        val duration = (pcmBytes.size.toDouble() / (SAMPLE_RATE * 2.0) * 1000.0).toLong()
        _state.value = RecordingState(
            isRecording = false,
            elapsedMs = duration,
            amplitudeLevel = 0f,
            outputFilePath = file.absolutePath,
        )
        return file
    }

    fun release() {
        stopRecording()
        pcmBuffer.reset()
    }

    private fun computeNormalizedPeak(bytes: ByteArray, length: Int): Float {
        var maxSample = 0
        var i = 0
        while (i + 1 < length) {
            val low = bytes[i].toInt() and 0xFF
            val high = bytes[i + 1].toInt()
            val sample = abs((high shl 8) or low)
            if (sample > maxSample) maxSample = sample
            i += 2
        }
        return (maxSample / 32768f).coerceIn(0f, 1f)
    }

    private fun writeWavFile(file: File, pcmData: ByteArray, sampleRate: Int, channels: Int, bitsPerSample: Int) {
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("RIFF".toByteArray(Charsets.US_ASCII))
            putInt(36 + pcmData.size)
            put("WAVE".toByteArray(Charsets.US_ASCII))
            put("fmt ".toByteArray(Charsets.US_ASCII))
            putInt(16)
            putShort(1.toShort())
            putShort(channels.toShort())
            putInt(sampleRate)
            putInt(byteRate)
            putShort((channels * bitsPerSample / 8).toShort())
            putShort(bitsPerSample.toShort())
            put("data".toByteArray(Charsets.US_ASCII))
            putInt(pcmData.size)
        }.array()

        FileOutputStream(file).use { fos ->
            fos.write(header)
            fos.write(pcmData)
        }
    }
}
