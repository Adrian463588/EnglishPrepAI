package com.englishprep.ai.data.media

import android.content.Context
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.englishprep.ai.domain.engines.AudioPlaybackState
import com.englishprep.ai.domain.engines.AudioPlayer
import com.englishprep.ai.domain.models.AudioSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Media3AudioEngine @Inject constructor(
    @ApplicationContext private val context: Context,
) : AudioPlayer {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var progressJob: Job? = null
    private var player: ExoPlayer? = null
    private var hasStartedCurrentLoad = false

    private val _state = MutableStateFlow(AudioPlaybackState())
    override val state: StateFlow<AudioPlaybackState> = _state.asStateFlow()

    private fun ensurePlayer(): ExoPlayer {
        player?.let { return it }
        val audioAttrs = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
            .build()
        return ExoPlayer.Builder(context)
            .build()
            .apply {
                setAudioAttributes(audioAttrs, true)
                setHandleAudioBecomingNoisy(true)
                addListener(playerListener)
                player = this
            }
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _state.update { it.copy(isPlaying = isPlaying) }
            if (isPlaying) startProgressLoop() else progressJob?.cancel()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            val exo = player ?: return
            if (playbackState == Player.STATE_READY) {
                _state.update { it.copy(durationMs = exo.duration.coerceAtLeast(0L), errorMessage = null) }
            } else if (playbackState == Player.STATE_ENDED) {
                progressJob?.cancel()
                exo.pause()
                exo.seekTo(0L)
                _state.update { it.copy(isPlaying = false, positionMs = 0L) }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            progressJob?.cancel()
            _state.update {
                it.copy(
                    isPlaying = false,
                    errorMessage = "Audio gagal diputar (${error.errorCodeName}). Pastikan aset atau izin SAF masih valid.",
                )
            }
        }
    }

    override fun prepare(source: AudioSource, title: String, maxReplays: Int?, initialSpeed: Float) {
        scope.launch {
            val uri = when (source) {
                is AudioSource.BundledAsset -> {
                    val clean = source.relativeAssetPath.removePrefix("asset:///").removePrefix("asset://").trimStart('/')
                    Uri.parse("asset:///$clean")
                }
                is AudioSource.LocalFile -> {
                    val file = File(source.absolutePath)
                    if (!file.exists() || file.length() == 0L) {
                        _state.value = AudioPlaybackState(
                            currentTitle = title,
                            maxReplays = maxReplays,
                            errorMessage = "File audio tidak ditemukan atau kosong: ${file.name}",
                        )
                        return@launch
                    }
                    Uri.fromFile(file)
                }
                is AudioSource.SafUri -> Uri.parse(source.uriString)
            }

            hasStartedCurrentLoad = false
            val exo = ensurePlayer()
            exo.stop()
            exo.setMediaItem(MediaItem.fromUri(uri))
            exo.setPlaybackSpeed(initialSpeed.coerceIn(0.75f, 1.5f))
            exo.prepare()
            _state.value = AudioPlaybackState(
                currentTitle = title,
                isPlaying = false,
                positionMs = 0L,
                durationMs = 0L,
                speed = initialSpeed.coerceIn(0.75f, 1.5f),
                replaysUsed = 0,
                maxReplays = maxReplays,
                errorMessage = null,
                assetPath = uri.toString(),
            )
        }
    }

    override fun togglePlayPause() {
        scope.launch {
            val exo = player ?: return@launch
            val current = _state.value
            if (exo.isPlaying) {
                exo.pause()
                return@launch
            }
            if (!hasStartedCurrentLoad) {
                if (current.maxReplays != null && current.replaysUsed >= current.maxReplays) {
                    return@launch
                }
                hasStartedCurrentLoad = true
                _state.update { it.copy(replaysUsed = it.replaysUsed + 1) }
            } else if (exo.playbackState == Player.STATE_ENDED || exo.currentPosition == 0L && current.replaysUsed > 0 && current.positionMs == 0L) {
                if (current.maxReplays != null && current.replaysUsed >= current.maxReplays) {
                    return@launch
                }
                _state.update { it.copy(replaysUsed = it.replaysUsed + 1) }
            }
            exo.play()
        }
    }

    override fun replay() {
        scope.launch {
            val exo = player ?: return@launch
            val current = _state.value
            if (current.maxReplays != null && current.replaysUsed >= current.maxReplays) {
                return@launch
            }
            hasStartedCurrentLoad = true
            _state.update { it.copy(replaysUsed = it.replaysUsed + 1, positionMs = 0L) }
            exo.seekTo(0L)
            exo.play()
        }
    }

    override fun seekTo(positionMs: Long) {
        scope.launch {
            val exo = player ?: return@launch
            val clamped = positionMs.coerceIn(0L, exo.duration.coerceAtLeast(0L))
            exo.seekTo(clamped)
            _state.update { it.copy(positionMs = clamped) }
        }
    }

    override fun setSpeed(speed: Float) {
        scope.launch {
            val clamped = speed.coerceIn(0.75f, 1.5f)
            player?.setPlaybackSpeed(clamped)
            _state.update { it.copy(speed = clamped) }
        }
    }

    override fun stop() {
        scope.launch {
            progressJob?.cancel()
            player?.pause()
            player?.seekTo(0L)
            _state.update { it.copy(isPlaying = false, positionMs = 0L) }
        }
    }

    override fun release() {
        scope.launch {
            progressJob?.cancel()
            player?.removeListener(playerListener)
            player?.release()
            player = null
            _state.value = AudioPlaybackState()
        }
    }

    private fun startProgressLoop() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                val exo = player ?: break
                _state.update {
                    it.copy(
                        positionMs = exo.currentPosition.coerceAtLeast(0L),
                        durationMs = exo.duration.coerceAtLeast(it.durationMs),
                    )
                }
                delay(200L)
            }
        }
    }
}
