package com.englishprep.ai.ui.feature.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.englishprep.ai.domain.engines.LlmModelState
import com.englishprep.ai.ui.components.ResponsiveScreenContainer
import com.englishprep.ai.ui.theme.AppSpacing
import com.englishprep.ai.ui.theme.LocalSemanticStatusColors

@Composable
fun ChatRoute(
    onBack: () -> Unit,
    onOpenModelManager: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ChatScreen(
        state = state,
        onBack = onBack,
        onOpenModelManager = onOpenModelManager,
        onDraftChange = viewModel::updateDraft,
        onSend = viewModel::sendMessage,
        onCancel = viewModel::cancelGeneration,
        onClearChat = viewModel::clearConversation,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    state: ChatUiState,
    onBack: () -> Unit,
    onOpenModelManager: () -> Unit,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    onCancel: () -> Unit,
    onClearChat: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val llmUnloaded = state.llmState !is LlmModelState.Ready
    val modelStatusLabel = when (val llm = state.llmState) {
        is LlmModelState.Ready -> "Gemma 4: ${llm.modelName}"
        is LlmModelState.Loading -> "Memuat ${llm.modelName}..."
        is LlmModelState.Error -> "Model offline error • Kelola"
        LlmModelState.Unloaded -> "Mode FTS aktif • Kelola model"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
                            .let { base ->
                                if (llmUnloaded) base.clickable(onClick = onOpenModelManager) else base
                            },
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Tutor AI",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = modelStatusLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (llmUnloaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.defaultMinSize(minWidth = AppSpacing.minTouchTarget, minHeight = AppSpacing.minTouchTarget),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (state.messages.isNotEmpty()) {
                        TextButton(
                            onClick = onClearChat,
                            modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                        ) {
                            Text("Bersihkan")
                        }
                    }
                },
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding(),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 760.dp)
                            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = state.draftInput,
                            onValueChange = onDraftChange,
                            enabled = !state.strictMockActive,
                            placeholder = {
                                Text(
                                    if (state.strictMockActive) {
                                        "Tutor dikunci selama Simulasi Strict aktif"
                                    } else {
                                        "Tanya materi ${state.selectedExam.title}..."
                                    },
                                )
                            },
                            maxLines = 4,
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(modifier = Modifier.width(AppSpacing.sm))
                        if (state.isGenerating) {
                            IconButton(
                                onClick = onCancel,
                                modifier = Modifier.defaultMinSize(minWidth = AppSpacing.minTouchTarget, minHeight = AppSpacing.minTouchTarget),
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = "Hentikan jawaban AI", tint = MaterialTheme.colorScheme.error)
                            }
                        } else {
                            IconButton(
                                onClick = onSend,
                                enabled = state.draftInput.isNotBlank() && !state.strictMockActive,
                                modifier = Modifier.defaultMinSize(minWidth = AppSpacing.minTouchTarget, minHeight = AppSpacing.minTouchTarget),
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Kirim pertanyaan")
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier.testTag("chat_screen_root"),
    ) { padding ->
        val listState = rememberLazyListState()
        val reduceMotion = LocalSemanticStatusColors.current.reduceMotion
        val targetScrollCount = state.messages.size + if (state.streamingReply.isNotEmpty()) 1 else 0
        LaunchedEffect(state.messages.size, state.streamingReply.isNotEmpty()) {
            if (targetScrollCount > 0) {
                val lastIdx = (listState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0)
                if (reduceMotion) {
                    listState.scrollToItem(lastIdx)
                } else {
                    listState.animateScrollToItem(lastIdx)
                }
            }
        }

        // Deliberately zero suggestion chip bar above messages per DESIGN.md
        ResponsiveScreenContainer(maxWidth = 760.dp, modifier = Modifier.padding(padding)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(AppSpacing.md),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm + 2.dp),
            ) {
                if (state.strictMockActive) {
                    item {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.padding(AppSpacing.md)) {
                                Text(
                                    text = "Mode Simulasi Strict Sedang Aktif",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Spacer(modifier = Modifier.height(AppSpacing.xs))
                                Text(
                                    text = "Tutor AI dinonaktifkan sementara selama simulasi Strict berlangsung.",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                }
                if (state.messages.isEmpty() && state.streamingReply.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = AppSpacing.lg),
                        ) {
                            Text(
                                text = "Asisten Belajar Lokal (${state.selectedExam.title})",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            Text(
                                text = "Tanyakan aturan tata bahasa, struktur IELTS Writing, strategi Listening, atau konsep modul apa pun. Jawaban merujuk basis data materi lokal di perangkat.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                items(state.messages, key = { it.id }) { msg ->
                    val isUser = msg.role == "USER"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = if (isUser) AppSpacing.xl else 0.dp,
                                end = if (isUser) 0.dp else AppSpacing.lg,
                            ),
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = if (isUser) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerLow
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.padding(AppSpacing.md)) {
                                Text(
                                    text = if (isUser) "Anda" else "Tutor Lokal",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(modifier = Modifier.height(AppSpacing.xs))
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                                val cleanCitations = formatCitationLabels(msg.citationsJson)
                                if (cleanCitations.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(AppSpacing.xs))
                                    Text(
                                        text = "Sumber: $cleanCitations",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }

                if (state.streamingReply.isNotEmpty()) {
                    item {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = AppSpacing.lg),
                        ) {
                            Column(modifier = Modifier.padding(AppSpacing.md)) {
                                Text(
                                    text = "Tutor Lokal (menulis...)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(modifier = Modifier.height(AppSpacing.xs))
                                Text(text = state.streamingReply, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatCitationLabels(rawJson: String?): String {
    if (rawJson.isNullOrBlank()) return ""
    return rawJson
        .removePrefix("[")
        .removeSuffix("]")
        .split(",")
        .map { it.trim().trim('"', '\'') }
        .filter { it.isNotEmpty() }
        .joinToString(", ")
}
