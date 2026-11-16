package com.englishprep.ai.ui.feature.learn

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.englishprep.ai.data.datastore.UserPreferencesRepository
import com.englishprep.ai.domain.engines.AudioPlaybackState
import com.englishprep.ai.domain.engines.AudioPlayer
import com.englishprep.ai.domain.models.AudioSource
import com.englishprep.ai.domain.models.CourseModuleItem
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.ExamSection
import com.englishprep.ai.domain.repositories.ExamCatalogRepository
import com.englishprep.ai.domain.repositories.SessionProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LearnUiState(
    val isLoading: Boolean = true,
    val selectedExam: ExamId = ExamId.ITP_L1,
    val selectedSection: ExamSection? = null,
    val modules: List<CourseModuleItem> = emptyList(),
    val masteryByModuleId: Map<String, Int> = emptyMap(),
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LearnViewModel @Inject constructor(
    private val preferences: UserPreferencesRepository,
    private val catalogRepository: ExamCatalogRepository,
    private val sessionRepository: SessionProgressRepository,
) : ViewModel() {

    private val sectionFilter = MutableStateFlow<ExamSection?>(null)

    val uiState: StateFlow<LearnUiState> = preferences.settingsFlow.flatMapLatest { settings ->
        combine(
            catalogRepository.observeModules(settings.selectedExam),
            sessionRepository.observeStudyProgress(settings.selectedExam),
            sectionFilter,
        ) { modules, progress, filter ->
            val filtered = if (filter == null) modules else modules.filter { it.section == filter }
            val masteryMap = progress.associate { it.moduleId to (it.masteryEstimate * 100f).toInt() }
            LearnUiState(
                isLoading = false,
                selectedExam = settings.selectedExam,
                selectedSection = filter,
                modules = filtered,
                masteryByModuleId = masteryMap,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = LearnUiState(),
    )

    fun selectSection(section: ExamSection?) {
        sectionFilter.value = section
    }
}

data class ModuleDetailUiState(
    val isLoading: Boolean = true,
    val module: CourseModuleItem? = null,
    val drillAnswers: Map<String, String> = emptyMap(),
    val drillChecked: Map<String, Boolean> = emptyMap(),
    val drillSavedMessage: String? = null,
    val audioState: AudioPlaybackState = AudioPlaybackState(),
)

@HiltViewModel
class ModuleDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val catalogRepository: ExamCatalogRepository,
    private val sessionRepository: SessionProgressRepository,
    private val audioPlayer: AudioPlayer,
) : ViewModel() {

    private val moduleId: String = checkNotNull(savedStateHandle["moduleId"])
    private val _uiState = MutableStateFlow(ModuleDetailUiState())
    val uiState: StateFlow<ModuleDetailUiState> = combine(
        _uiState,
        audioPlayer.state,
    ) { state, audio ->
        state.copy(audioState = audio)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ModuleDetailUiState(),
    )

    init {
        viewModelScope.launch {
            val module = catalogRepository.getModule(moduleId)
            _uiState.update { it.copy(isLoading = false, module = module) }
            module?.audioAssetPath?.let { relPath ->
                audioPlayer.prepare(
                    source = AudioSource.BundledAsset(relPath),
                    title = "Contoh Audio • ${module.id}",
                    maxReplays = null,
                )
            }
        }
    }

    fun answerDrill(questionId: String, value: String) {
        _uiState.update { state ->
            state.copy(
                drillAnswers = state.drillAnswers + (questionId to value),
                drillSavedMessage = null,
            )
        }
    }

    fun checkDrill(questionId: String, expectedAnswer: String, acceptedVariants: List<String>) {
        val current = _uiState.value.drillAnswers[questionId].orEmpty().trim()
        val normalized = current.lowercase().replace(Regex("\\s+"), " ")
        val candidates = (acceptedVariants + expectedAnswer).map { it.trim().lowercase().replace(Regex("\\s+"), " ") }
        val ok = normalized.isNotEmpty() && candidates.any { it == normalized }
        _uiState.update { state ->
            val updatedChecks = state.drillChecked + (questionId to ok)
            state.copy(drillChecked = updatedChecks)
        }
        val mod = _uiState.value.module ?: return
        val checks = _uiState.value.drillChecked
        if (checks.size == mod.drills.size) {
            val correctCount = checks.values.count { it }
            viewModelScope.launch {
                sessionRepository.recordModuleDrill(
                    examId = mod.examId,
                    moduleId = mod.id,
                    attempts = mod.drills.size,
                    correct = correctCount,
                )
                _uiState.update {
                    it.copy(drillSavedMessage = "Progres latihan tersimpan ($correctCount/${mod.drills.size} benar).")
                }
            }
        }
    }

    fun toggleAudio() = audioPlayer.togglePlayPause()
    fun replayAudio() = audioPlayer.replay()
    fun seekAudio(posMs: Long) = audioPlayer.seekTo(posMs)
    fun setAudioSpeed(speed: Float) = audioPlayer.setSpeed(speed)

    override fun onCleared() {
        audioPlayer.stop()
        super.onCleared()
    }
}
