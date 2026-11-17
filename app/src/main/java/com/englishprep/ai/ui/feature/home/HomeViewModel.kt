package com.englishprep.ai.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.englishprep.ai.data.datastore.UserPreferencesRepository
import com.englishprep.ai.domain.engines.LlmModelState
import com.englishprep.ai.domain.engines.LocalLlmEngine
import com.englishprep.ai.domain.models.ContentPackSummary
import com.englishprep.ai.domain.models.CourseModuleItem
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.ExamMode
import com.englishprep.ai.domain.repositories.ExamCatalogRepository
import com.englishprep.ai.domain.repositories.SessionProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val selectedExam: ExamId = ExamId.ITP_L1,
    val activeSessionId: String? = null,
    val activeSessionTitle: String? = null,
    val activeSessionAnsweredCount: Int = 0,
    val activeSessionTotalCount: Int = 0,
    val recommendedModule: CourseModuleItem? = null,
    val recommendedPack: ContentPackSummary? = null,
    val averageAccuracyPercent: Int? = null,
    val completedSessionCount: Int = 0,
    val llmState: LlmModelState = LlmModelState.Unloaded,
    val showExamSwitcher: Boolean = false,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferences: UserPreferencesRepository,
    private val catalogRepository: ExamCatalogRepository,
    private val sessionRepository: SessionProgressRepository,
    private val llmEngine: LocalLlmEngine,
) : ViewModel() {

    private val showSwitcher = MutableStateFlow(false)

    private val coreDataFlow = combine(
        preferences.settingsFlow,
        sessionRepository.observeLatestActiveSession(),
        llmEngine.state,
    ) { settings, activeSession, llmState ->
        Triple(settings.selectedExam, activeSession, llmState)
    }.flatMapLatest { (examId, activeSession, llmState) ->
        val matchingActive = activeSession?.takeIf { it.examId == examId.name }
        val activeAnswersFlow = if (matchingActive != null) {
            sessionRepository.observeAnswers(matchingActive.id)
        } else {
            flowOf(emptyList())
        }
        combine(
            catalogRepository.observeModules(examId),
            catalogRepository.observePacks(examId),
            sessionRepository.observeSessions(examId),
            sessionRepository.observeEvaluations(examId),
            combine(sessionRepository.observeStudyProgress(examId), activeAnswersFlow, ::Pair),
        ) { modules, packs, sessions, evaluations, (progressList, activeAnswers) ->
            val completedSessions = sessions.filter { it.state == "SUBMITTED" }
            val totalCorrect = evaluations.sumOf { it.rawCorrect }
            val totalQuestions = evaluations.sumOf { it.rawTotal }
            val avgAccuracy = if (totalQuestions > 0) ((totalCorrect * 100f) / totalQuestions).toInt() else null

            val progressByMod = progressList.associateBy { it.moduleId }
            val nextModule = modules.firstOrNull { (progressByMod[it.id]?.masteryEstimate ?: 0f) < 0.8f }
                ?: modules.firstOrNull()
            val nextPack = packs.firstOrNull()
            val activePackTotal = matchingActive?.packId?.let { pid ->
                packs.firstOrNull { it.packId == pid }?.availableCount
            } ?: 0
            val answeredCount = activeAnswers.count { it.answerJson.isNotBlank() }

            HomeUiState(
                isLoading = false,
                selectedExam = examId,
                activeSessionId = matchingActive?.id,
                activeSessionTitle = matchingActive?.title,
                activeSessionAnsweredCount = answeredCount,
                activeSessionTotalCount = activePackTotal,
                recommendedModule = nextModule,
                recommendedPack = nextPack,
                averageAccuracyPercent = avgAccuracy,
                completedSessionCount = completedSessions.size,
                llmState = llmState,
            )
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        coreDataFlow,
        showSwitcher,
    ) { core, switcherOpen ->
        core.copy(showExamSwitcher = switcherOpen)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = HomeUiState(),
    )

    fun setSwitcherVisible(visible: Boolean) {
        showSwitcher.value = visible
    }

    fun selectExam(examId: ExamId) {
        viewModelScope.launch {
            preferences.setSelectedExam(examId)
            showSwitcher.value = false
        }
    }

    fun startPrimaryAction(onOpenSession: (String) -> Unit) {
        val current = uiState.value
        val activeId = current.activeSessionId
        if (activeId != null) {
            onOpenSession(activeId)
            return
        }
        val pack = current.recommendedPack ?: return
        viewModelScope.launch {
            val session = sessionRepository.createOrResumePackSession(
                examId = current.selectedExam,
                packId = pack.packId,
                title = pack.title,
                mode = ExamMode.PRACTICE,
                durationMinutes = pack.durationMinutes,
            )
            onOpenSession(session.id)
        }
    }
}
