package com.englishprep.ai.ui.feature.results

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.englishprep.ai.data.room.SessionDao
import com.englishprep.ai.domain.grading.ExamScoreEstimator
import com.englishprep.ai.domain.grading.QuizGradingEngine
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.GradingOutcome
import com.englishprep.ai.domain.models.QuestionItem
import com.englishprep.ai.domain.models.ScoreEstimate
import com.englishprep.ai.domain.repositories.ExamCatalogRepository
import com.englishprep.ai.domain.repositories.SessionProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuestionReviewRow(
    val question: QuestionItem,
    val userAnswer: String,
    val outcome: GradingOutcome,
)

data class ResultsUiState(
    val isLoading: Boolean = true,
    val sessionTitle: String = "",
    val examId: ExamId = ExamId.ITP_L1,
    val overallEstimate: ScoreEstimate? = null,
    val sectionSummaries: List<Pair<String, String>> = emptyList(),
    val rows: List<QuestionReviewRow> = emptyList(),
    val wrongQuestionIds: List<String> = emptyList(),
    val errorMessage: String? = null,
)

@HiltViewModel
class ResultsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionDao: SessionDao,
    private val catalogRepository: ExamCatalogRepository,
    private val sessionRepository: SessionProgressRepository,
    private val gradingEngine: QuizGradingEngine,
    private val scoreEstimator: ExamScoreEstimator,
) : ViewModel() {

    private val sessionId: String = checkNotNull(savedStateHandle["sessionId"])
    private val _uiState = MutableStateFlow(ResultsUiState())
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val session = sessionRepository.getSession(sessionId)
            if (session == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Hasil sesi tidak ditemukan.") }
                return@launch
            }
            val packRef = session.packId.orEmpty()
            val bundle = if (packRef.startsWith("RETRY:")) {
                catalogRepository.loadQuestionsByIds(packRef.removePrefix("RETRY:").split(",").filter { it.isNotBlank() })
            } else {
                catalogRepository.loadPackBundle(packRef)
            }
            if (bundle == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Data paket soal tidak ditemukan.") }
                return@launch
            }
            val answers = sessionRepository.observeAnswers(sessionId).first().associateBy { it.questionId }
            val rows = mutableListOf<QuestionReviewRow>()
            val wrongIds = mutableListOf<String>()
            var objCorrect = 0
            var objTotal = 0
            var objSkipped = 0

            bundle.questions.forEach { q ->
                val ansText = answers[q.id]?.answerJson.orEmpty()
                val rec = sessionRepository.getLatestRecordingTranscript(sessionId, q.id)
                val outcome = gradingEngine.grade(q, ansText, hasAudioRecording = rec != null)
                if (outcome is GradingOutcome.Objective) {
                    objTotal++
                    if (outcome.isSkipped) objSkipped++
                    if (outcome.isCorrect) objCorrect++ else wrongIds.add(q.id)
                }
                rows += QuestionReviewRow(q, ansText, outcome)
            }

            val examId = ExamId.valueOf(session.examId)
            val evals = sessionDao.getEvaluationsForSession(sessionId)
            val overall = if (objTotal > 0) scoreEstimator.estimate(examId, objCorrect, objTotal, objSkipped) else null
            val sections = evals.filter { it.section != "OVERALL" }.map {
                val label = if (it.rawTotal > 0) "${it.rawCorrect}/${it.rawTotal} benar" else "Tugas subyektif"
                it.section to "$label ${it.scaledScore?.let { s -> "• $s" }.orEmpty()}"
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    sessionTitle = session.title,
                    examId = examId,
                    overallEstimate = overall,
                    sectionSummaries = sections,
                    rows = rows,
                    wrongQuestionIds = wrongIds,
                )
            }
        }
    }

    fun retryMistakes(onOpenSession: (String) -> Unit) {
        val st = _uiState.value
        if (st.wrongQuestionIds.isEmpty()) return
        viewModelScope.launch {
            val session = sessionRepository.createMistakeRetrySession(
                examId = st.examId,
                questionIds = st.wrongQuestionIds,
                title = "Ulangi Kesalahan • ${st.examId.title}",
            )
            onOpenSession(session.id)
        }
    }
}
