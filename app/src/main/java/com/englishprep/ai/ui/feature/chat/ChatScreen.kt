package com.englishprep.ai.ui.feature.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.englishprep.ai.domain.engines.LlmModelState
import com.englishprep.ai.ui.theme.AppSpacing

@Composable
fun ChatRoute(
    onBack: () -> Unit,
    onOpenProfileAi: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ChatScreen(
        state = state,
        onBack = onBack,
        onOpenProfileAi = onOpenProfileAi,
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
    onOpenProfileAi: () -> Unit,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    onCancel: () -> Unit,
    onClearChat: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val modelStatusLabel = when (val llm = state.llmState) {
        is LlmModelState.Ready -> "Offline Gemma 4: ${llm.modelName}"
        is LlmModelState.Loading -> "Memuat ${llm.modelName}..."
        is LlmModelState.Error -> "Runtime error • Fallback FTS aktif"
        LlmModelState.Unloaded -> "Tanpa LLM • Mode ringkasan FTS lokal"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Tutor AI Offline", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = modelStatusLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    if (state.llmState !is LlmModelState.Ready) {
                        TextButton(
                            onClick = onOpenProfileAi,
                            modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                        ) {
                            Text("Impor AI")
                        }
                    }
                },
            )
        },
        bottomBar = {
            Surface(tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth().imePadding()) {
                Column {
                    HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
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
        // Deliberately zero suggestion chip bar above messages per DESIGN.md
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            if (state.strictMockActive) {
                item {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(AppSpacing.md)) {
                            Text(
                                text = "Mode Simulasi Strict Sedang Aktif",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            Text(
                                text = "Tutor AI dinonaktifkan sementara selama sesi ujian Strict berlangsung agar hasil latihan tetap jujur.",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
            if (state.messages.isEmpty() && state.streamingReply.isEmpty()) {
                item {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(AppSpacing.md)) {
                            Text(
                                text = "Asisten Belajar Lokal (${state.selectedExam.title})",
                                style = MaterialTheme.typography.titleMedium,
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
            }

            items(state.messages, key = { it.id }) { msg ->
                val isUser = msg.role == "USER"
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = if (isUser) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceContainerLow,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
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
                        msg.citationsJson?.takeIf { it.isNotBlank() }?.let { cites ->
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            Text(
                                text = "Referensi lokal: $cites",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            if (state.streamingReply.isNotEmpty()) {
                item {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth(),
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
