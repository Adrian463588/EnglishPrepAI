package com.englishprep.ai.ui.feature.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.englishprep.ai.data.datastore.UserPreferencesRepository
import com.englishprep.ai.domain.models.ContentPackSummary
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.ExamMode
import com.englishprep.ai.domain.repositories.AiAndImportRepository
import com.englishprep.ai.domain.repositories.ExamCatalogRepository
import com.englishprep.ai.domain.repositories.SessionProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PracticeUiState(
    val isLoading: Boolean = true,
    val selectedExam: ExamId = ExamId.ITP_L1,
    val packs: List<ContentPackSummary> = emptyList(),
    val statusMessage: String? = null,
    val errorMessage: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val preferences: UserPreferencesRepository,
    private val catalogRepository: ExamCatalogRepository,
    private val sessionRepository: SessionProgressRepository,
    private val aiAndImportRepository: AiAndImportRepository,
) : ViewModel() {

    private val feedback = MutableStateFlow<Pair<String?, String?>>(null to null)

    val uiState: StateFlow<PracticeUiState> = preferences.settingsFlow.flatMapLatest { settings ->
        combine(
            catalogRepository.observePacks(settings.selectedExam),
            feedback,
        ) { packs, (status, err) ->
            PracticeUiState(
                isLoading = false,
                selectedExam = settings.selectedExam,
                packs = packs,
                statusMessage = status,
                errorMessage = err,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = PracticeUiState(),
    )

    fun startPack(pack: ContentPackSummary, mode: ExamMode, onOpenSession: (String) -> Unit) {
        viewModelScope.launch {
            val session = sessionRepository.createOrResumePackSession(
                examId = pack.examId,
                packId = pack.packId,
                title = "${pack.title} (${mode.name})",
                mode = mode,
                durationMinutes = pack.durationMinutes,
            )
            onOpenSession(session.id)
        }
    }

    fun importPackSaf(uriString: String) {
        viewModelScope.launch {
            feedback.value = "Memvalidasi paket soal..." to null
            aiAndImportRepository.importStudyPackViaSaf(uriString)
                .onSuccess { packTitle ->
                    feedback.value = "Paket '$packTitle' berhasil diimpor offline." to null
                }
                .onFailure { err ->
                    feedback.value = null to (err.message ?: "Impor paket gagal.")
                }
        }
    }

    fun clearMessage() {
        feedback.value = null to null
    }
}
