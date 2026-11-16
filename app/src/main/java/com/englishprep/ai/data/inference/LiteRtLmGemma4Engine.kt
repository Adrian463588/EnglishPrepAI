package com.englishprep.ai.data.inference

import android.content.Context
import com.englishprep.ai.domain.engines.LlmCapabilities
import com.englishprep.ai.domain.engines.LlmModelState
import com.englishprep.ai.domain.engines.LocalLlmEngine
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.Message
import com.google.ai.edge.litertlm.MessageCallback
import com.google.ai.edge.litertlm.SamplerConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiteRtLmGemma4Engine @Inject constructor(
    @ApplicationContext private val context: Context,
) : LocalLlmEngine {

    private val lock = Mutex()
    private var engine: Engine? = null
    private var activeConversation: Conversation? = null

    private val _state = MutableStateFlow<LlmModelState>(LlmModelState.Unloaded)
    override val state: StateFlow<LlmModelState> = _state.asStateFlow()

    override val capabilities = LlmCapabilities(
        engineName = "Google AI Edge LiteRT-LM 0.13.1 (Gemma 4 .litertlm)",
        supportsGemma4LitertLm = true,
        supportsStreaming = true,
    )

    override suspend fun load(modelPath: String, modelName: String, variant: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            lock.withLock {
                val file = File(modelPath)
                if (!file.exists() || file.length() <= 0L) {
                    val msg = "Berkas model Gemma 4 tidak ditemukan atau kosong: ${file.name}"
                    _state.value = LlmModelState.Error(msg)
                    return@withLock Result.failure(IllegalStateException(msg))
                }
                if (!file.name.endsWith(".litertlm", ignoreCase = true)) {
                    val msg = "Format tidak kompatibel: LiteRT-LM Gemma 4 memerlukan berkas .litertlm."
                    _state.value = LlmModelState.Error(msg)
                    return@withLock Result.failure(IllegalArgumentException(msg))
                }

                runCatching {
                    unloadLocked()
                    _state.value = LlmModelState.Loading(modelName)
                    val cacheDir = File(context.cacheDir, "inference").apply { mkdirs() }.absolutePath
                    val config = EngineConfig(
                        modelPath = file.absolutePath,
                        backend = Backend.CPU(),
                        maxNumTokens = 1024,
                        cacheDir = cacheDir,
                    )
                    val instance = Engine(config)
                    instance.initialize()
                    engine = instance
                    _state.value = LlmModelState.Ready(modelName = modelName, variant = variant)
                }.onFailure { err ->
                    unloadLocked()
                    _state.value = LlmModelState.Error(
                        "Gagal memuat Gemma 4 via LiteRT-LM: ${err.message ?: "RAM/ABI tidak mencukupi"}",
                    )
                }
            }
        }

    override fun generateStream(prompt: String, systemPrompt: String?): Flow<String> = callbackFlow {
        val loadedEngine = engine
        if (loadedEngine == null || !loadedEngine.isInitialized()) {
            close(IllegalStateException("Model Gemma 4 belum dimuat."))
            return@callbackFlow
        }

        val convConfig = ConversationConfig(
            systemInstruction = systemPrompt?.takeIf { it.isNotBlank() }?.let { Contents.of(it) },
            samplerConfig = SamplerConfig(
                topK = 40,
                topP = 0.92,
                temperature = 0.35,
                seed = 42,
            ),
        )
        val conversation = loadedEngine.createConversation(convConfig)
        activeConversation = conversation

        conversation.sendMessageAsync(
            prompt,
            object : MessageCallback {
                override fun onMessage(message: Message) {
                    val text = message.contents.contents
                        .filterIsInstance<com.google.ai.edge.litertlm.Content.Text>()
                        .joinToString("") { it.text }
                    if (text.isNotEmpty()) {
                        trySend(text)
                    }
                }

                override fun onDone() {
                    runCatching { conversation.close() }
                    if (activeConversation === conversation) activeConversation = null
                    close()
                }

                override fun onError(throwable: Throwable) {
                    runCatching { conversation.close() }
                    if (activeConversation === conversation) activeConversation = null
                    close(throwable)
                }
            },
        )

        awaitClose {
            runCatching { conversation.cancelProcess() }
            runCatching { conversation.close() }
            if (activeConversation === conversation) activeConversation = null
        }
    }.flowOn(Dispatchers.IO)

    override fun cancel() {
        runCatching { activeConversation?.cancelProcess() }
    }

    override fun unload() {
        unloadLocked()
        _state.value = LlmModelState.Unloaded
    }

    private fun unloadLocked() {
        runCatching { activeConversation?.cancelProcess() }
        runCatching { activeConversation?.close() }
        activeConversation = null
        runCatching { engine?.close() }
        engine = null
    }
}
