package com.englishprep.ai.ui.feature.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.englishprep.ai.data.datastore.UserPreferencesRepository
import com.englishprep.ai.data.room.ChatMessageEntity
import com.englishprep.ai.domain.ai.GroundedTutorPromptBuilder
import com.englishprep.ai.domain.engines.LlmModelState
import com.englishprep.ai.domain.engines.LocalLlmEngine
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.ExamMode
import com.englishprep.ai.domain.repositories.AiAndImportRepository
import com.englishprep.ai.domain.repositories.ExamCatalogRepository
import com.englishprep.ai.domain.repositories.SessionProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val selectedExam: ExamId = ExamId.ITP_L1,
    val threadId: String? = null,
    val messages: List<ChatMessageEntity> = emptyList(),
    val streamingReply: String = "",
    val isGenerating: Boolean = false,
    val llmState: LlmModelState = LlmModelState.Unloaded,
    val draftInput: String = "",
    val contextSourceId: String? = null,
    val strictMockActive: Boolean = false,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val preferences: UserPreferencesRepository,
    private val catalogRepository: ExamCatalogRepository,
    private val sessionRepository: SessionProgressRepository,
    private val aiRepository: AiAndImportRepository,
    private val llmEngine: LocalLlmEngine,
    private val promptBuilder: GroundedTutorPromptBuilder,
) : ViewModel() {

    private val initialContextId: String? = savedStateHandle["contextSourceId"]
    private val activeThreadId = MutableStateFlow<String?>(null)
    private val draft = MutableStateFlow(
        initialContextId?.let { "Jelaskan ringkasan konsep utama modul $it beserta contoh jebakannya." }.orEmpty(),
    )
    private val streamingText = MutableStateFlow("")
    private val generating = MutableStateFlow(false)
    private var generationJob: Job? = null

    val uiState: StateFlow<ChatUiState> = combine(
        preferences.settingsFlow,
        activeThreadId.flatMapLatest { tid ->
            if (tid == null) flowOf(emptyList()) else aiRepository.observeMessages(tid)
        },
        llmEngine.state,
        draft,
        combine(streamingText, generating, sessionRepository.observeLatestActiveSession()) { s, g, active ->
            Triple(s, g, active?.mode == ExamMode.STRICT.name && active.state == "ACTIVE")
        },
    ) { settings, msgs, llm, inputText, (stream, isGen, isStrict) ->
        ChatUiState(
            selectedExam = settings.selectedExam,
            threadId = activeThreadId.value,
            messages = msgs,
            streamingReply = stream,
            isGenerating = isGen,
            llmState = llm,
            draftInput = inputText,
            contextSourceId = initialContextId,
            strictMockActive = isStrict,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ChatUiState(),
    )

    init {
        viewModelScope.launch {
            val exam = preferences.settingsFlow.first().selectedExam
            val thread = aiRepository.getOrCreateThread(exam)
            activeThreadId.value = thread.id
        }
    }

    fun updateDraft(text: String) {
        draft.value = text
    }

    fun clearConversation() {
        cancelGeneration()
        val tid = activeThreadId.value ?: return
        viewModelScope.launch {
            aiRepository.clearThreadMessages(tid)
        }
    }

    fun sendMessage() {
        val query = draft.value.trim()
        if (query.isEmpty() || generating.value || uiState.value.strictMockActive) return
        draft.value = ""

        generationJob = viewModelScope.launch {
            val exam = preferences.settingsFlow.first().selectedExam
            val thread = aiRepository.getOrCreateThread(exam)
            activeThreadId.value = thread.id
            aiRepository.appendMessage(thread.id, role = "USER", text = query)

            val directMod = initialContextId?.let { catalogRepository.getModule(it) }?.let { mod ->
                com.englishprep.ai.domain.models.RagSnippet(
                    sourceId = mod.id,
                    title = mod.title,
                    excerpt = "${mod.objective} ${mod.theory} Contoh: ${mod.workedExample} Jebakan: ${mod.commonMistake}".take(460),
                )
            }
            val ftsHits = catalogRepository.searchLocalCurriculum(exam, query, limit = 4)
            val snippets = (listOfNotNull(directMod) + ftsHits).distinctBy { it.sourceId }.take(4)
            val citations = snippets.map { it.sourceId }

            if (llmEngine.state.value is LlmModelState.Ready) {
                generating.value = true
                streamingText.value = ""
                val prompt = promptBuilder.buildPrompt(exam.title, query, snippets)
                val full = StringBuilder()
                runCatching {
                    llmEngine.generateStream(
                        prompt = prompt,
                        systemPrompt = "You are a concise, accurate bilingual TOEFL/IELTS study tutor. Ground explanations in provided local context and cite [ID].",
                    ).collect { chunk ->
                        full.append(chunk)
                        streamingText.value = full.toString()
                    }
                }.onFailure { err ->
                    if (full.isEmpty()) {
                        full.append(promptBuilder.buildFallbackOfflineReply(exam, query, snippets))
                        full.append("\n(Info runtime: ${err.message})")
                    }
                }
                val finalAnswer = full.toString().ifBlank {
                    promptBuilder.buildFallbackOfflineReply(exam, query, snippets)
                }
                aiRepository.appendMessage(
                    threadId = thread.id,
                    role = "ASSISTANT",
                    text = finalAnswer,
                    citations = citations,
                    modelId = (llmEngine.state.value as? LlmModelState.Ready)?.modelName,
                )
                streamingText.value = ""
                generating.value = false
            } else {
                val fallback = promptBuilder.buildFallbackOfflineReply(exam, query, snippets)
                aiRepository.appendMessage(
                    threadId = thread.id,
                    role = "ASSISTANT",
                    text = fallback,
                    citations = citations,
                    modelId = "local-fts-fallback",
                )
            }
        }
    }

    fun cancelGeneration() {
        llmEngine.cancel()
        generationJob?.cancel()
        generating.value = false
        val partial = streamingText.value.trim()
        streamingText.value = ""
        val tid = activeThreadId.value
        if (partial.isNotEmpty() && tid != null) {
            viewModelScope.launch {
                aiRepository.appendMessage(tid, role = "ASSISTANT", text = "$partial\n[Dihentikan pengguna]")
            }
        }
    }

    override fun onCleared() {
        llmEngine.cancel()
        super.onCleared()
    }
}
