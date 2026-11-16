package com.englishprep.ai.domain.engines

import com.englishprep.ai.domain.models.AudioSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

sealed interface LlmModelState {
    data object Unloaded : LlmModelState
    data class Loading(val modelName: String) : LlmModelState
    data class Ready(val modelName: String, val variant: String) : LlmModelState
    data class Error(val message: String) : LlmModelState
}

data class LlmCapabilities(
    val engineName: String,
    val supportsGemma4LitertLm: Boolean,
    val supportsStreaming: Boolean,
)

interface LocalLlmEngine {
    val state: StateFlow<LlmModelState>
    val capabilities: LlmCapabilities

    suspend fun load(modelPath: String, modelName: String, variant: String): Result<Unit>
    fun generateStream(prompt: String, systemPrompt: String? = null): Flow<String>
    fun cancel()
    fun unload()
}

sealed interface SttModelState {
    data object Unloaded : SttModelState
    data class Ready(val modelName: String) : SttModelState
    data class Error(val message: String) : SttModelState
}

data class SttTranscriptionResult(
    val transcript: String,
    val usedWhisperOnnx: Boolean,
    val note: String,
)

interface SpeechToTextEngine {
    val state: StateFlow<SttModelState>

    suspend fun load(modelDirectoryPath: String, modelName: String): Result<Unit>
    suspend fun transcribeWav(wavFilePath: String): Result<SttTranscriptionResult>
    fun cancel()
    fun unload()
}

data class AudioPlaybackState(
    val currentTitle: String = "",
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val speed: Float = 1.0f,
    val replaysUsed: Int = 0,
    val maxReplays: Int? = null,
    val errorMessage: String? = null,
    val assetPath: String? = null,
)

interface AudioPlayer {
    val state: StateFlow<AudioPlaybackState>

    fun prepare(source: AudioSource, title: String, maxReplays: Int?, initialSpeed: Float = 1.0f)
    fun togglePlayPause()
    fun replay()
    fun seekTo(positionMs: Long)
    fun setSpeed(speed: Float)
    fun stop()
    fun release()
}
