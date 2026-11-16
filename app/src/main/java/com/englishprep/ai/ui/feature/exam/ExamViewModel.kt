package com.englishprep.ai.ui.feature.exam

import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.englishprep.ai.data.datastore.UserPreferencesRepository
import com.englishprep.ai.data.media.LocalTtsSpeaker
import com.englishprep.ai.data.room.EvaluationEntity
import com.englishprep.ai.data.room.ExamSessionEntity
import com.englishprep.ai.data.stt.RecordingState
import com.englishprep.ai.data.stt.WavAudioRecorder
import com.englishprep.ai.domain.ai.RubricFeedbackAnalyzer
import com.englishprep.ai.domain.engines.AudioPlaybackState
import com.englishprep.ai.domain.engines.AudioPlayer
import com.englishprep.ai.domain.engines.LlmModelState
import com.englishprep.ai.domain.engines.LocalLlmEngine
import com.englishprep.ai.domain.engines.SpeechToTextEngine
import com.englishprep.ai.domain.engines.SttModelState
import com.englishprep.ai.domain.grading.ExamScoreEstimator
import com.englishprep.ai.domain.grading.QuizGradingEngine
import com.englishprep.ai.domain.grading.SpeakingMetricsAnalyzer
import com.englishprep.ai.domain.models.AudioSource
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.ExamMode
import com.englishprep.ai.domain.models.GradingOutcome
import com.englishprep.ai.domain.models.MediaAssetItem
import com.englishprep.ai.domain.models.PassageItem
import com.englishprep.ai.domain.models.QuestionItem
import com.englishprep.ai.domain.models.SpeakingMetrics
import com.englishprep.ai.domain.repositories.ExamCatalogRepository
import com.englishprep.ai.domain.repositories.SessionProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ExamUiState(
    val isLoading: Boolean = true,
    val session: ExamSessionEntity? = null,
    val examId: ExamId = ExamId.ITP_L1,
    val mode: ExamMode = ExamMode.PRACTICE,
    val questions: List<QuestionItem> = emptyList(),
    val passages: Map<String, PassageItem> = emptyMap(),
    val assets: Map<String, MediaAssetItem> = emptyMap(),
    val currentIndex: Int = 0,
    val answers: Map<String, String> = emptyMap(),
    val flaggedIds: Set<String> = emptySet(),
    val checkedOutcomes: Map<String, GradingOutcome> = emptyMap(),
    val aiFeedbackByQuestion: Map<String, String> = emptyMap(),
    val speakingMetricsByQuestion: Map<String, SpeakingMetrics> = emptyMap(),
    val recordingPaths: Map<String, String> = emptyMap(),
    val isEvaluatingAi: Boolean = false,
    val isTranscribing: Boolean = false,
    val savedBadgeText: String = "",
    val remainingMs: Long? = null,
    val elapsedMs: Long = 0L,
    val prepCountdownSec: Int? = null,
    val showNavigator: Boolean = false,
    val showPassageSheet: Boolean = false,
    val errorMessage: String? = null,
    val audioState: AudioPlaybackState = AudioPlaybackState(),
    val recordingState: RecordingState = RecordingState(),
    val llmState: LlmModelState = LlmModelState.Unloaded,
    val sttState: SttModelState = SttModelState.Unloaded,
) {
    val currentQuestion: QuestionItem? get() = questions.getOrNull(currentIndex)
    val currentPassage: PassageItem? get() = currentQuestion?.passageId?.let { passages[it] }
    val currentAsset: MediaAssetItem? get() = currentQuestion?.audioAssetId?.let { assets[it] }
}

@HiltViewModel
class ExamViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val catalogRepository: ExamCatalogRepository,
    private val sessionRepository: SessionProgressRepository,
    private val preferences: UserPreferencesRepository,
    private val gradingEngine: QuizGradingEngine,
    private val scoreEstimator: ExamScoreEstimator,
    private val speakingAnalyzer: SpeakingMetricsAnalyzer,
    private val rubricAnalyzer: RubricFeedbackAnalyzer,
    private val audioPlayer: AudioPlayer,
    private val recorder: WavAudioRecorder,
    private val llmEngine: LocalLlmEngine,
    private val sttEngine: SpeechToTextEngine,
    private val ttsSpeaker: LocalTtsSpeaker,
) : ViewModel() {

    private val sessionId: String = checkNotNull(savedStateHandle["sessionId"])
    private val _state = MutableStateFlow(ExamUiState())

    private var timerJob: Job? = null
    private var prepJob: Job? = null
    private var baseElapsedMs: Long = 0L
    private var processResumeAnchorMs: Long = SystemClock.elapsedRealtime()
    private var totalDurationLimitMs: Long? = null
    private var preparedAudioAssetId: String? = null

    val uiState: StateFlow<ExamUiState> = combine(
        _state,
        audioPlayer.state,
        recorder.state,
        llmEngine.state,
        sttEngine.state,
    ) { base, audio, rec, llm, stt ->
        base.copy(
            audioState = audio,
            recordingState = rec,
            llmState = llm,
            sttState = stt,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ExamUiState(),
    )

    init {
        loadSession()
    }

    private fun loadSession() {
        viewModelScope.launch {
            val session = sessionRepository.getSession(sessionId)
            if (session == null) {
                _state.update { it.copy(isLoading = false, errorMessage = "Sesi ujian tidak ditemukan.") }
                return@launch
            }
            val packRef = session.packId.orEmpty()
            val bundle = if (packRef.startsWith("RETRY:")) {
                val ids = packRef.removePrefix("RETRY:").split(",").filter { it.isNotBlank() }
                catalogRepository.loadQuestionsByIds(ids)
            } else {
                catalogRepository.loadPackBundle(packRef)
            }
            if (bundle == null || bundle.questions.isEmpty()) {
                _state.update { it.copy(isLoading = false, errorMessage = "Paket soal gagal dimuat atau kosong.") }
                return@launch
            }

            val savedAnswers = sessionRepository.observeAnswers(sessionId).first()
            val answerMap = savedAnswers.associate { it.questionId to it.answerJson }
            val flagged = savedAnswers.filter { it.isFlagged }.map { it.questionId }.toSet()

            val mode = runCatching { ExamMode.valueOf(session.mode) }.getOrDefault(ExamMode.PRACTICE)
            baseElapsedMs = session.elapsedMonotonicCheckpointMs
            processResumeAnchorMs = SystemClock.elapsedRealtime()
            totalDurationLimitMs = if (mode == ExamMode.STRICT) {
                bundle.pack.durationMinutes * 60_000L
            } else {
                null
            }

            val restoredIndex = session.currentQuestionIndex.coerceIn(0, bundle.questions.lastIndex)
            _state.update {
                it.copy(
                    isLoading = false,
                    session = session,
                    examId = ExamId.valueOf(session.examId),
                    mode = mode,
                    questions = bundle.questions,
                    passages = bundle.passages,
                    assets = bundle.assets,
                    currentIndex = restoredIndex,
                    answers = answerMap,
                    flaggedIds = flagged,
                    elapsedMs = baseElapsedMs,
                    remainingMs = totalDurationLimitMs?.let { lim -> (lim - baseElapsedMs).coerceAtLeast(0L) },
                )
            }
            prepareAudioForCurrentQuestion()
            startMonotonicTimer()
        }
    }

    private fun startMonotonicTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val speedPref = preferences.settingsFlow.first().audioSpeedPractice
            if (_state.value.mode == ExamMode.PRACTICE) {
                audioPlayer.setSpeed(speedPref)
            }
            while (isActive) {
                val nowElapsed = baseElapsedMs + (SystemClock.elapsedRealtime() - processResumeAnchorMs)
                val limit = totalDurationLimitMs
                val remaining = limit?.let { (it - nowElapsed).coerceAtLeast(0L) }
                _state.update { it.copy(elapsedMs = nowElapsed, remainingMs = remaining) }
                if (remaining != null && remaining <= 0L) {
                    submitExam(onCompleted = {})
                    break
                }
                delay(1_000L)
            }
        }
    }

    fun selectAnswer(questionId: String, value: String) {
        val current = _state.value
        val updatedAnswers = current.answers + (questionId to value)
        _state.update {
            it.copy(
                answers = updatedAnswers,
                savedBadgeText = "Tersimpan",
                checkedOutcomes = if (it.mode == ExamMode.PRACTICE) it.checkedOutcomes - questionId else it.checkedOutcomes,
            )
        }
        persistAnswer(questionId, value, questionId in current.flaggedIds)
    }

    fun toggleMultiChoice(questionId: String, option: String) {
        val prev = _state.value.answers[questionId].orEmpty()
            .split("|")
            .filter { it.isNotBlank() }
            .toMutableSet()
        if (!prev.add(option)) prev.remove(option)
        selectAnswer(questionId, prev.joinToString("|"))
    }

    fun setMatchingPair(questionId: String, leftKey: String, rightVal: String) {
        val existing = _state.value.answers[questionId].orEmpty()
            .split("|")
            .filter { it.contains("::") }
            .associate {
                val parts = it.split("::", limit = 2)
                parts[0] to parts.getOrElse(1) { "" }
            }.toMutableMap()
        existing[leftKey] = rightVal
        val encoded = existing.entries.joinToString("|") { "${it.key}::${it.value}" }
        selectAnswer(questionId, encoded)
    }

    fun appendSequenceItem(questionId: String, item: String) {
        val current = _state.value.answers[questionId].orEmpty()
            .split("|")
            .filter { it.isNotBlank() }
        if (item in current) return
        selectAnswer(questionId, (current + item).joinToString("|"))
    }

    fun removeLastSequenceItem(questionId: String) {
        val current = _state.value.answers[questionId].orEmpty()
            .split("|")
            .filter { it.isNotBlank() }
        if (current.isEmpty()) return
        selectAnswer(questionId, current.dropLast(1).joinToString("|"))
    }

    fun toggleFlag(questionId: String) {
        val current = _state.value
        val nextFlags = if (questionId in current.flaggedIds) {
            current.flaggedIds - questionId
        } else {
            current.flaggedIds + questionId
        }
        _state.update { it.copy(flaggedIds = nextFlags, savedBadgeText = "Ditandai") }
        persistAnswer(questionId, current.answers[questionId].orEmpty(), questionId in nextFlags)
    }

    fun checkCurrentInPractice() {
        val st = _state.value
        if (st.mode != ExamMode.PRACTICE) return
        val q = st.currentQuestion ?: return
        val ans = st.answers[q.id].orEmpty()
        val hasRec = st.recordingPaths.containsKey(q.id)
        val outcome = gradingEngine.grade(q, ans, hasRec)
        _state.update { it.copy(checkedOutcomes = it.checkedOutcomes + (q.id to outcome)) }
    }

    fun evaluateSubjectiveDraft() {
        val st = _state.value
        val q = st.currentQuestion ?: return
        val text = st.answers[q.id].orEmpty()
        if (text.isBlank()) return

        _state.update { it.copy(isEvaluatingAi = true) }
        viewModelScope.launch {
            val llmReady = st.llmState is LlmModelState.Ready
            if (llmReady) {
                val prompt = rubricAnalyzer.buildWritingPrompt(st.examId, q, text)
                val sb = StringBuilder()
                runCatching {
                    llmEngine.generateStream(prompt).collect { chunk ->
                        sb.append(chunk)
                        _state.update { curr ->
                            curr.copy(aiFeedbackByQuestion = curr.aiFeedbackByQuestion + (q.id to sb.toString()))
                        }
                    }
                }.onFailure {
                    val fallback = rubricAnalyzer.buildDeterministicWritingFeedback(st.examId, q, text)
                    _state.update { curr ->
                        curr.copy(aiFeedbackByQuestion = curr.aiFeedbackByQuestion + (q.id to fallback))
                    }
                }
            } else {
                val deterministic = rubricAnalyzer.buildDeterministicWritingFeedback(st.examId, q, text)
                _state.update { curr ->
                    curr.copy(aiFeedbackByQuestion = curr.aiFeedbackByQuestion + (q.id to deterministic))
                }
            }
            _state.update { it.copy(isEvaluatingAi = false) }
        }
    }

    fun startPrepCountdown(seconds: Int) {
        if (seconds <= 0) return
        prepJob?.cancel()
        prepJob = viewModelScope.launch {
            for (sec in seconds downTo 0) {
                _state.update { it.copy(prepCountdownSec = sec) }
                if (sec > 0) delay(1_000L)
            }
        }
    }

    fun startSpeakingRecording(): Boolean {
        val q = _state.value.currentQuestion ?: return false
        audioPlayer.stop()
        return recorder.startRecording(sessionId, q.id)
    }

    fun stopSpeakingRecordingAndTranscribe() {
        val q = _state.value.currentQuestion ?: return
        val file = recorder.stopRecording() ?: return
        val durationSec = (_state.value.recordingState.elapsedMs / 1000.0).coerceAtLeast(1.0)
        _state.update {
            it.copy(recordingPaths = it.recordingPaths + (q.id to file.absolutePath))
        }

        viewModelScope.launch {
            val sttReady = _state.value.sttState is SttModelState.Ready
            var transcript = _state.value.answers[q.id].orEmpty()
            if (sttReady) {
                _state.update { it.copy(isTranscribing = true) }
                sttEngine.transcribeWav(file.absolutePath).onSuccess { res ->
                    if (res.transcript.isNotBlank()) {
                        transcript = res.transcript
                        selectAnswer(q.id, transcript)
                    }
                }
                _state.update { it.copy(isTranscribing = false) }
            }
            val metrics = speakingAnalyzer.analyzeTranscript(
                transcript = transcript,
                durationSeconds = durationSec,
                referenceText = q.referenceText,
            )
            _state.update {
                it.copy(speakingMetricsByQuestion = it.speakingMetricsByQuestion + (q.id to metrics))
            }
            sessionRepository.saveSpeechRecording(
                sessionId = sessionId,
                questionId = q.id,
                filePath = file.absolutePath,
                durationMs = _state.value.recordingState.elapsedMs,
                transcript = transcript.ifBlank { null },
                feedbackJson = null,
            )
        }
    }

    fun playOwnRecording(questionId: String) {
        val path = _state.value.recordingPaths[questionId] ?: return
        audioPlayer.prepare(
            source = AudioSource.LocalFile(path),
            title = "Rekaman Anda",
            maxReplays = null,
        )
        audioPlayer.togglePlayPause()
    }

    fun deleteSpeakingRecording(questionId: String) {
        audioPlayer.stop()
        val path = _state.value.recordingPaths[questionId]
        if (!path.isNullOrBlank()) {
            runCatching { java.io.File(path).delete() }
        }
        _state.update { st ->
            st.copy(
                recordingPaths = st.recordingPaths - questionId,
                speakingMetricsByQuestion = st.speakingMetricsByQuestion - questionId,
            )
        }
    }

    fun speakPromptViaTts(text: String) {
        ttsSpeaker.speak(text)
    }

    fun goToQuestion(index: Int) {
        val st = _state.value
        val clamped = index.coerceIn(0, (st.questions.size - 1).coerceAtLeast(0))
        if (clamped == st.currentIndex) return
        val targetAssetId = st.questions.getOrNull(clamped)?.audioAssetId
        val audioChanged = targetAssetId != preparedAudioAssetId
        prepJob?.cancel()
        if (audioChanged) {
            audioPlayer.stop()
            preparedAudioAssetId = null
        }
        _state.update { it.copy(currentIndex = clamped, showNavigator = false, prepCountdownSec = null) }
        saveCheckpoint(clamped)
        if (audioChanged) {
            prepareAudioForCurrentQuestion()
        }
    }

    fun nextQuestion() = goToQuestion(_state.value.currentIndex + 1)
    fun prevQuestion() = goToQuestion(_state.value.currentIndex - 1)
    fun setNavigatorVisible(visible: Boolean) = _state.update { it.copy(showNavigator = visible) }
    fun setPassageSheetVisible(visible: Boolean) = _state.update { it.copy(showPassageSheet = visible) }

    fun toggleAudio() = audioPlayer.togglePlayPause()
    fun replayAudio() = audioPlayer.replay()
    fun seekAudio(ms: Long) = audioPlayer.seekTo(ms)
    fun setAudioSpeed(speed: Float) = audioPlayer.setSpeed(speed)

    fun submitExam(onCompleted: (String) -> Unit) {
        timerJob?.cancel()
        prepJob?.cancel()
        audioPlayer.stop()
        preparedAudioAssetId = null
        val st = _state.value
        val now = System.currentTimeMillis()
        val finalElapsed = baseElapsedMs + (SystemClock.elapsedRealtime() - processResumeAnchorMs)

        viewModelScope.launch {
            sessionRepository.updateSessionCheckpoint(sessionId, st.currentIndex, finalElapsed)

            val groupedBySection = st.questions.groupBy { it.section }
            val evaluations = mutableListOf<EvaluationEntity>()
            val moduleStats = mutableMapOf<String, Pair<Int, Int>>()
            var totalObjCorrect = 0
            var totalObjCount = 0
            var totalObjSkipped = 0

            groupedBySection.forEach { (section, secQuestions) ->
                var secCorrect = 0
                var secObjTotal = 0
                var secSkipped = 0
                secQuestions.forEach { q ->
                    val ans = st.answers[q.id].orEmpty()
                    val outcome = gradingEngine.grade(q, ans, st.recordingPaths.containsKey(q.id))
                    if (outcome is GradingOutcome.Objective) {
                        secObjTotal++
                        totalObjCount++
                        if (outcome.isSkipped) {
                            secSkipped++
                            totalObjSkipped++
                        }
                        if (outcome.isCorrect) {
                            secCorrect++
                            totalObjCorrect++
                        }
                        val modId = q.rationaleSource.trim()
                        if (modId.startsWith("ITP-MOD-") || modId.startsWith("IBT-MOD-") || modId.startsWith("IELTS-MOD-")) {
                            val (prevA, prevC) = moduleStats[modId] ?: (0 to 0)
                            moduleStats[modId] = (prevA + 1) to (prevC + if (outcome.isCorrect) 1 else 0)
                        }
                    }
                    sessionRepository.saveAnswer(
                        sessionId = sessionId,
                        questionId = q.id,
                        answerJson = ans,
                        isFlagged = q.id in st.flaggedIds,
                        autoCheckResultJson = when (outcome) {
                            is GradingOutcome.Objective -> "${outcome.isCorrect}|${outcome.feedbackMessage}"
                            is GradingOutcome.SubjectivePending -> outcome.feedbackMessage
                        },
                    )
                }
                val secEstimate = if (secObjTotal > 0) {
                    scoreEstimator.estimate(st.examId, secCorrect, secObjTotal, secSkipped)
                } else null
                evaluations += EvaluationEntity(
                    id = "EVAL_${UUID.randomUUID()}",
                    sessionId = sessionId,
                    section = section.name,
                    rawCorrect = secCorrect,
                    rawTotal = secObjTotal,
                    scaledScore = secEstimate?.primaryEstimateLabel,
                    officialEquivalent = false,
                    estimateMethod = secEstimate?.secondaryNote ?: "Rubrik subyektif non-resmi",
                    aiFeedbackJson = null,
                    createdAt = now,
                )
            }

            if (totalObjCount > 0) {
                val overall = scoreEstimator.estimate(st.examId, totalObjCorrect, totalObjCount, totalObjSkipped)
                evaluations += EvaluationEntity(
                    id = "EVAL_OVERALL_${UUID.randomUUID()}",
                    sessionId = sessionId,
                    section = "OVERALL",
                    rawCorrect = totalObjCorrect,
                    rawTotal = totalObjCount,
                    scaledScore = overall.primaryEstimateLabel,
                    officialEquivalent = false,
                    estimateMethod = overall.secondaryNote,
                    aiFeedbackJson = null,
                    createdAt = now,
                )
            }

            sessionRepository.submitSession(sessionId, evaluations, now)
            moduleStats.forEach { (modId, counts) ->
                sessionRepository.recordModuleDrill(st.examId, modId, counts.first, counts.second)
            }
            onCompleted(sessionId)
        }
    }

    private fun prepareAudioForCurrentQuestion() {
        val st = _state.value
        val q = st.currentQuestion ?: return
        val asset = q.audioAssetId?.let { st.assets[it] } ?: run {
            preparedAudioAssetId = null
            return
        }
        if (asset.id == preparedAudioAssetId) {
            return
        }
        preparedAudioAssetId = asset.id
        val maxReplays = if (st.mode == ExamMode.STRICT) 1 else null
        val source = if (asset.relativePathOrUri.startsWith("asset://")) {
            AudioSource.BundledAsset(asset.relativePathOrUri)
        } else {
            AudioSource.LocalFile(asset.relativePathOrUri)
        }
        audioPlayer.prepare(
            source = source,
            title = "Rekaman ${q.section.label} (${asset.id})",
            maxReplays = maxReplays,
        )
    }

    private fun persistAnswer(questionId: String, answer: String, isFlagged: Boolean) {
        viewModelScope.launch {
            sessionRepository.saveAnswer(sessionId, questionId, answer, isFlagged)
            saveCheckpoint(_state.value.currentIndex)
        }
    }

    private fun saveCheckpoint(index: Int) {
        val nowElapsed = baseElapsedMs + (SystemClock.elapsedRealtime() - processResumeAnchorMs)
        viewModelScope.launch {
            sessionRepository.updateSessionCheckpoint(sessionId, index, nowElapsed)
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        prepJob?.cancel()
        audioPlayer.stop()
        preparedAudioAssetId = null
        ttsSpeaker.stop()
        super.onCleared()
    }
}
