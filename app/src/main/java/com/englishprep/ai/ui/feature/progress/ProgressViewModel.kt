package com.englishprep.ai.ui.feature.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.englishprep.ai.data.datastore.UserPreferencesRepository
import com.englishprep.ai.data.room.ExamSessionEntity
import com.englishprep.ai.data.room.StudyProgressEntity
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.repositories.SessionProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SectionAccuracyRow(
    val section: String,
    val correct: Int,
    val total: Int,
    val percent: Int,
)

data class ProgressUiState(
    val isLoading: Boolean = true,
    val selectedExam: ExamId = ExamId.ITP_L1,
    val overallAccuracyPercent: Int? = null,
    val sectionRows: List<SectionAccuracyRow> = emptyList(),
    val recentTrendPercents: List<Int> = emptyList(),
    val recentSessions: List<ExamSessionEntity> = emptyList(),
    val weakModules: List<StudyProgressEntity> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val preferences: UserPreferencesRepository,
    private val sessionRepository: SessionProgressRepository,
) : ViewModel() {

    val uiState: StateFlow<ProgressUiState> = preferences.settingsFlow.flatMapLatest { settings ->
        val exam = settings.selectedExam
        combine(
            sessionRepository.observeSessions(exam),
            sessionRepository.observeEvaluations(exam),
            sessionRepository.observeStudyProgress(exam),
        ) { sessions, evals, studyProgress ->
            val submitted = sessions.filter { it.state == "SUBMITTED" }
            val sectionGroups = evals.filter { it.section != "OVERALL" && it.rawTotal > 0 }
                .groupBy { it.section }
                .map { (sec, list) ->
                    val c = list.sumOf { it.rawCorrect }
                    val t = list.sumOf { it.rawTotal }
                    val pct = if (t > 0) (c * 100f / t).toInt() else 0
                    SectionAccuracyRow(sec, c, t, pct)
                }

            val overallEvals = evals.filter { it.section == "OVERALL" && it.rawTotal > 0 }
            val overallPct = if (overallEvals.isNotEmpty()) {
                val c = overallEvals.sumOf { it.rawCorrect }
                val t = overallEvals.sumOf { it.rawTotal }
                if (t > 0) (c * 100f / t).toInt() else null
            } else null

            val trend = overallEvals
                .sortedBy { it.createdAt }
                .takeLast(8)
                .map { if (it.rawTotal > 0) (it.rawCorrect * 100f / it.rawTotal).toInt() else 0 }

            val weakMods = studyProgress.filter { it.masteryEstimate < 0.8f }.sortedBy { it.masteryEstimate }

            ProgressUiState(
                isLoading = false,
                selectedExam = exam,
                overallAccuracyPercent = overallPct,
                sectionRows = sectionGroups,
                recentTrendPercents = trend,
                recentSessions = submitted.take(10),
                weakModules = weakMods,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ProgressUiState(),
    )
}
