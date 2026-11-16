package com.englishprep.ai.data.stt

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import com.englishprep.ai.domain.engines.SpeechToTextEngine
import com.englishprep.ai.domain.engines.SttModelState
import com.englishprep.ai.domain.engines.SttTranscriptionResult
import com.englishprep.ai.domain.grading.WhisperAudioFeatures
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.LongBuffer
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WhisperOnnxSpeechEngine @Inject constructor(
    private val json: Json,
) : SpeechToTextEngine {

    private val lock = Mutex()
    private val cancelled = AtomicBoolean(false)
    private var env: OrtEnvironment? = null
    private var encoderSession: OrtSession? = null
    private var decoderSession: OrtSession? = null
    private var idToToken: Map<Int, String> = emptyMap()
    private var sotTokenId: Int = 50257
    private var eotTokenId: Int = 50256
    private var noTimestampsId: Int = 50362

    private val _state = MutableStateFlow<SttModelState>(SttModelState.Unloaded)
    override val state: StateFlow<SttModelState> = _state.asStateFlow()

    override suspend fun load(modelDirectoryPath: String, modelName: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            lock.withLock {
                val dir = File(modelDirectoryPath)
                if (!dir.exists() || !dir.isDirectory) {
                    val msg = "Direktori model Whisper ONNX tidak ditemukan."
                    _state.value = SttModelState.Error(msg)
                    return@withLock Result.failure(IllegalStateException(msg))
                }
                val files = dir.listFiles().orEmpty()
                val encoderFile = files.firstOrNull {
                    it.name.contains("encoder", ignoreCase = true) && it.name.endsWith(".onnx", ignoreCase = true)
                }
                val decoderFile = files.firstOrNull {
                    it.name.contains("decoder", ignoreCase = true) && it.name.endsWith(".onnx", ignoreCase = true)
                }
                val vocabFile = files.firstOrNull { it.name.equals("vocab.json", ignoreCase = true) }
                val tokensFile = files.firstOrNull { it.name.endsWith("tokens.txt", ignoreCase = true) }

                if (encoderFile == null || decoderFile == null || (vocabFile == null && tokensFile == null)) {
                    val msg = "Folder harus berisi encoder*.onnx, decoder*.onnx, dan vocab.json (atau tokens.txt)."
                    _state.value = SttModelState.Error(msg)
                    return@withLock Result.failure(IllegalArgumentException(msg))
                }

                runCatching {
                    unloadLocked()
                    val ortEnv = OrtEnvironment.getEnvironment()
                    val opts = OrtSession.SessionOptions().apply {
                        setIntraOpNumThreads(2)
                        setOptimizationLevel(OrtSession.SessionOptions.OptLevel.BASIC_OPT)
                    }
                    val enc = ortEnv.createSession(encoderFile.absolutePath, opts)
                    val dec = ortEnv.createSession(decoderFile.absolutePath, opts)
                    val tokenMap = if (vocabFile != null) {
                        parseVocabJson(vocabFile)
                    } else {
                        parseTokensTxt(tokensFile!!)
                    }

                    env = ortEnv
                    encoderSession = enc
                    decoderSession = dec
                    idToToken = tokenMap
                    resolveSpecialIds(tokenMap)
                    _state.value = SttModelState.Ready(modelName)
                }.onFailure { err ->
                    unloadLocked()
                    _state.value = SttModelState.Error("Gagal memuat Whisper ONNX: ${err.message}")
                }
            }
        }

    override suspend fun transcribeWav(wavFilePath: String): Result<SttTranscriptionResult> =
        withContext(Dispatchers.IO) {
            lock.withLock {
                val ortEnv = env
                val enc = encoderSession
                val dec = decoderSession
                if (ortEnv == null || enc == null || dec == null) {
                    return@withLock Result.failure(IllegalStateException("Model Whisper ONNX belum dimuat."))
                }
                cancelled.set(false)

                runCatching {
                    val pcm = readWavPcm16Mono(File(wavFilePath))
                    if (pcm.isEmpty()) {
                        error("Berkas rekaman audio kosong.")
                    }
                    val mel = WhisperAudioFeatures.computeLogMelSpectrogram(pcm)
                    val encInputName = enc.inputNames.firstOrNull() ?: "input_features"
                    val encOutName = enc.outputNames.firstOrNull() ?: "last_hidden_state"

                    val melTensor = OnnxTensor.createTensor(ortEnv, FloatBuffer.wrap(mel), longArrayOf(1L, 80L, 3000L))
                    melTensor.use { mt ->
                        enc.run(mapOf(encInputName to mt)).use { encRes ->
                            val hiddenValue = encRes.get(encOutName).orElse(encRes.get(0)) as OnnxTensor
                            val hiddenShape = hiddenValue.info.shape
                            val hiddenBuf = hiddenValue.floatBuffer
                            val hiddenArr = FloatArray(hiddenBuf.remaining()).also { hiddenBuf.get(it) }

                            val decInputNames = dec.inputNames
                            if (!decInputNames.contains("input_ids") || decInputNames.any { it.contains("past_key_values") }) {
                                error("Varian decoder ONNX memerlukan KV-cache atau tensor khusus; gunakan varian decoder_model.onnx standar (tanpa merged KV-cache) dari onnx-community/whisper-tiny.en.")
                            }

                            val hiddenInputKey = decInputNames.firstOrNull { it != "input_ids" } ?: "encoder_hidden_states"
                            val generated = mutableListOf(sotTokenId, noTimestampsId)
                            val maxNewTokens = 96

                            val hiddenTensor = OnnxTensor.createTensor(ortEnv, FloatBuffer.wrap(hiddenArr), hiddenShape)
                            hiddenTensor.use { ht ->
                                for (step in 0 until maxNewTokens) {
                                    if (cancelled.get()) break
                                    val idsArr = generated.map { it.toLong() }.toLongArray()
                                    val idTensor = OnnxTensor.createTensor(ortEnv, LongBuffer.wrap(idsArr), longArrayOf(1L, idsArr.size.toLong()))
                                    val nextTokenId = idTensor.use { itTensor ->
                                        dec.run(mapOf("input_ids" to itTensor, hiddenInputKey to ht)).use { decRes ->
                                            val logitsTensor = decRes.get(0) as OnnxTensor
                                            val shape = logitsTensor.info.shape
                                            val vocabSize = shape.last().toInt()
                                            val seqLen = shape[1].toInt()
                                            val fb = logitsTensor.floatBuffer
                                            val offset = (seqLen - 1) * vocabSize
                                            var bestIdx = eotTokenId
                                            var bestScore = Float.NEGATIVE_INFINITY
                                            for (v in 0 until vocabSize) {
                                                val s = fb.get(offset + v)
                                                if (s > bestScore) {
                                                    bestScore = s
                                                    bestIdx = v
                                                }
                                            }
                                            bestIdx
                                        }
                                    }
                                    if (nextTokenId == eotTokenId) break
                                    generated.add(nextTokenId)
                                }
                            }

                            val text = decodeTokens(generated.drop(2))
                            SttTranscriptionResult(
                                transcript = text,
                                usedWhisperOnnx = true,
                                note = "Transkrip otomatis lokal (Whisper ONNX) — silakan edit bila diperlukan.",
                            )
                        }
                    }
                }
            }
        }

    override fun cancel() {
        cancelled.set(true)
    }

    override fun unload() {
        unloadLocked()
        _state.value = SttModelState.Unloaded
    }

    private fun unloadLocked() {
        runCatching { encoderSession?.close() }
        runCatching { decoderSession?.close() }
        encoderSession = null
        decoderSession = null
        idToToken = emptyMap()
    }

    private fun parseVocabJson(file: File): Map<Int, String> {
        val root = json.parseToJsonElement(file.readText()).jsonObject
        val out = HashMap<Int, String>(root.size)
        root.forEach { (tok, idEl) ->
            out[idEl.jsonPrimitive.int] = tok
        }
        return out
    }

    private fun parseTokensTxt(file: File): Map<Int, String> {
        val out = HashMap<Int, String>()
        file.forEachLine { line ->
            val parts = line.trim().split(Regex("\\s+"))
            if (parts.size >= 2) {
                val id = parts.last().toIntOrNull()
                if (id != null) {
                    out[id] = parts.dropLast(1).joinToString(" ")
                }
            }
        }
        return out
    }

    private fun resolveSpecialIds(map: Map<Int, String>) {
        val rev = map.entries.associate { (k, v) -> v to k }
        sotTokenId = rev["<|startoftranscript|>"] ?: 50257
        eotTokenId = rev["<|endoftext|>"] ?: 50256
        noTimestampsId = rev["<|notimestamps|>"] ?: 50362
    }

    private fun decodeTokens(tokenIds: List<Int>): String {
        val raw = tokenIds
            .mapNotNull { idToToken[it] }
            .filterNot { it.startsWith("<|") && it.endsWith("|>") }
            .joinToString("")
        return raw.replace('Ġ', ' ').replace('Ċ', '\n').trim()
    }

    private fun readWavPcm16Mono(file: File): FloatArray {
        if (!file.exists() || file.length() <= 44L) return FloatArray(0)
        val bytes = file.readBytes()
        val pcmStart = 44
        val sampleCount = (bytes.size - pcmStart) / 2
        if (sampleCount <= 0) return FloatArray(0)
        val bb = ByteBuffer.wrap(bytes, pcmStart, sampleCount * 2).order(ByteOrder.LITTLE_ENDIAN)
        return FloatArray(sampleCount) {
            (bb.short / 32768.0f).coerceIn(-1f, 1f)
        }
    }
}
