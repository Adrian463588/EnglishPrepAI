package com.englishprep.ai.ui.feature.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.englishprep.ai.data.datastore.AppLanguage
import com.englishprep.ai.data.datastore.ThemeMode
import com.englishprep.ai.data.datastore.UserPreferencesRepository
import com.englishprep.ai.data.datastore.UserSettings
import com.englishprep.ai.data.room.ImportJobEntity
import com.englishprep.ai.data.room.ModelInstallEntity
import com.englishprep.ai.data.work.StudyReminderWorker
import com.englishprep.ai.domain.engines.LlmModelState
import com.englishprep.ai.domain.engines.LocalLlmEngine
import com.englishprep.ai.domain.engines.SpeechToTextEngine
import com.englishprep.ai.domain.engines.SttModelState
import com.englishprep.ai.domain.repositories.AiAndImportRepository
import com.englishprep.ai.domain.repositories.SessionProgressRepository
import com.englishprep.ai.domain.security.SecurityPosture
import com.englishprep.ai.domain.security.SecurityPostureInspector
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class ProfileUiState(
    val settings: UserSettings = UserSettings(),
    val installedModels: List<ModelInstallEntity> = emptyList(),
    val importJobs: List<ImportJobEntity> = emptyList(),
    val llmState: LlmModelState = LlmModelState.Unloaded,
    val sttState: SttModelState = SttModelState.Unloaded,
    val securityPosture: SecurityPosture? = null,
    val isBusy: Boolean = false,
    val statusBanner: String? = null,
    val errorBanner: String? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: UserPreferencesRepository,
    private val aiRepository: AiAndImportRepository,
    private val sessionRepository: SessionProgressRepository,
    private val llmEngine: LocalLlmEngine,
    private val sttEngine: SpeechToTextEngine,
    private val securityInspector: SecurityPostureInspector,
) : ViewModel() {

    private val busy = MutableStateFlow(false)
    private val banners = MutableStateFlow<Pair<String?, String?>>(null to null)
    private val posture = MutableStateFlow<SecurityPosture?>(null)

    init {
        viewModelScope.launch {
            posture.value = securityInspector.inspect()
        }
    }

    val uiState: StateFlow<ProfileUiState> = combine(
        preferences.settingsFlow,
        aiRepository.observeInstalledModels(),
        aiRepository.observeImportJobs(),
        combine(llmEngine.state, sttEngine.state) { l, s -> l to s },
        combine(busy, banners, posture) { b, msg, p -> Triple(b, msg, p) },
    ) { settings, models, jobs, (llm, stt), (isBusy, msg, sec) ->
        ProfileUiState(
            settings = settings,
            installedModels = models,
            importJobs = jobs,
            llmState = llm,
            sttState = stt,
            securityPosture = sec,
            isBusy = isBusy,
            statusBanner = msg.first,
            errorBanner = msg.second,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ProfileUiState(),
    )

    fun setLanguage(lang: AppLanguage) = viewModelScope.launch { preferences.setLanguage(lang) }
    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch { preferences.setThemeMode(mode) }
    fun setAudioSpeed(speed: Float) = viewModelScope.launch { preferences.setAudioSpeedPractice(speed) }
    fun setReduceMotion(enabled: Boolean) = viewModelScope.launch { preferences.setReduceMotion(enabled) }
    fun setHaptics(enabled: Boolean) = viewModelScope.launch { preferences.setHapticsEnabled(enabled) }
    fun setFontScaleBoost(enabled: Boolean) = viewModelScope.launch { preferences.setFontScaleBoost(enabled) }

    fun setReminder(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setReminder(enabled)
            StudyReminderWorker.schedule(context, enabled)
            banners.value = (if (enabled) "Pengingat belajar harian diaktifkan." else "Pengingat dimatikan.") to null
        }
    }

    fun importGemma4Saf(uri: Uri) {
        viewModelScope.launch {
            busy.value = true
            banners.value = "Menyalin & memvalidasi model Gemma 4 (.litertlm)..." to null
            aiRepository.importModelFileViaSaf(uri.toString(), expectedFamily = "GEMMA4")
                .onSuccess { model ->
                    banners.value = "Model ${model.displayName} siap offline (${model.sizeBytes / (1024 * 1024)} MB)." to null
                }
                .onFailure { err ->
                    banners.value = null to (err.message ?: "Impor Gemma 4 gagal.")
                }
            busy.value = false
        }
    }

    fun importWhisperFolderSaf(treeUri: Uri) {
        viewModelScope.launch {
            busy.value = true
            banners.value = "Memvalidasi folder Whisper ONNX..." to null
            aiRepository.importWhisperFolderViaSaf(treeUri.toString())
                .onSuccess { model ->
                    banners.value = "Whisper ONNX '${model.displayName}' aktif." to null
                }
                .onFailure { err ->
                    banners.value = null to (err.message ?: "Impor folder Whisper ONNX gagal.")
                }
            busy.value = false
        }
    }

    fun importStudyPackSaf(uri: Uri) {
        viewModelScope.launch {
            busy.value = true
            banners.value = "Memvalidasi paket soal SAF..." to null
            aiRepository.importStudyPackViaSaf(uri.toString())
                .onSuccess { title ->
                    banners.value = "Paket '$title' berhasil dipasang offline." to null
                }
                .onFailure { err ->
                    banners.value = null to (err.message ?: "Impor paket soal ditolak.")
                }
            busy.value = false
        }
    }

    fun activateModel(model: ModelInstallEntity) {
        viewModelScope.launch {
            busy.value = true
            aiRepository.activateModel(model)
                .onSuccess {
                    banners.value = "Model ${model.displayName} aktif." to null
                }
                .onFailure { err ->
                    banners.value = null to (err.message ?: "Aktivasi gagal.")
                }
            busy.value = false
        }
    }

    fun deleteModel(modelId: String) {
        viewModelScope.launch {
            aiRepository.removeModel(modelId)
            banners.value = "Model lokal dihapus dari penyimpanan privat." to null
        }
    }

    fun exportBackupToUri(uri: Uri) {
        viewModelScope.launch {
            busy.value = true
            runCatching {
                val jsonText = sessionRepository.exportBackupJson()
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use {
                        it.write(jsonText)
                    } ?: error("Gagal membuka target penyimpanan cadangan.")
                }
            }.onSuccess {
                banners.value = "Cadangan data belajar berhasil diekspor (JSON)." to null
            }.onFailure { err ->
                banners.value = null to (err.message ?: "Ekspor cadangan gagal.")
            }
            busy.value = false
        }
    }

    fun importBackupFromUri(uri: Uri) {
        viewModelScope.launch {
            busy.value = true
            runCatching {
                val jsonText = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                        ?: error("Berkas cadangan tidak dapat dibaca.")
                }
                sessionRepository.restoreBackupJson(jsonText).getOrThrow()
            }.onSuccess { count ->
                banners.value = "Pemulihan selesai ($count entri dipulihkan)." to null
            }.onFailure { err ->
                banners.value = null to (err.message ?: "Pemulihan cadangan gagal.")
            }
            busy.value = false
        }
    }

    fun deleteAllUserData() {
        viewModelScope.launch {
            sessionRepository.clearAllUserData()
            banners.value = "Seluruh riwayat ujian, progres, dan riwayat chat telah dihapus." to null
        }
    }

    fun dismissBanner() {
        banners.value = null to null
    }
}
