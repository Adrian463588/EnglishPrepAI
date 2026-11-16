package com.englishprep.ai.ui.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.englishprep.ai.domain.engines.LlmModelState
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.ui.components.AppButton
import com.englishprep.ai.ui.components.ExamSelector
import com.englishprep.ai.ui.components.ModuleRow
import com.englishprep.ai.ui.components.OfflineModelState
import com.englishprep.ai.ui.components.ProgressSummary
import com.englishprep.ai.ui.components.ResponsiveScreenContainer
import com.englishprep.ai.ui.components.SectionHeading
import com.englishprep.ai.ui.components.rememberIsExpandedWidth
import com.englishprep.ai.ui.theme.AppSpacing

@Composable
fun HomeRoute(
    onOpenSession: (String) -> Unit,
    onOpenModule: (String) -> Unit,
    onOpenPractice: () -> Unit,
    onOpenChat: (String?) -> Unit,
    onOpenProfileAi: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        onOpenSwitcher = { viewModel.setSwitcherVisible(true) },
        onDismissSwitcher = { viewModel.setSwitcherVisible(false) },
        onSelectExam = viewModel::selectExam,
        onPrimaryAction = { viewModel.startPrimaryAction(onOpenSession) },
        onOpenModule = onOpenModule,
        onOpenPractice = onOpenPractice,
        onOpenChat = { onOpenChat(state.recommendedModule?.id) },
        onOpenProfileAi = onOpenProfileAi,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    onOpenSwitcher: () -> Unit,
    onDismissSwitcher: () -> Unit,
    onSelectExam: (ExamId) -> Unit,
    onPrimaryAction: () -> Unit,
    onOpenModule: (String) -> Unit,
    onOpenPractice: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenProfileAi: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val isExpanded = rememberIsExpandedWidth()

    ResponsiveScreenContainer(maxWidth = if (isExpanded) 960.dp else 720.dp, modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = AppSpacing.md, vertical = AppSpacing.sm + 4.dp),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm + 4.dp),
        ) {
            item {
                Text(
                    text = "English Prep AI",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(
                    text = "Mode offline penuh • Materi, tryout, audio & AI lokal di perangkat.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            item {
                ExamSelector(
                    activeExam = state.selectedExam,
                    onOpenSwitcher = onOpenSwitcher,
                )
            }

            item {
                val heroCard: @Composable (Modifier) -> Unit = { cardMod ->
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        modifier = cardMod.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(AppSpacing.md)) {
                            val heroTitle = state.activeSessionTitle
                                ?: state.recommendedPack?.title
                                ?: state.selectedExam.title
                            Text(
                                text = heroTitle,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.semantics { heading() },
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            Text(
                                text = state.selectedExam.subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.md))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                AppButton(
                                    text = if (state.activeSessionId != null) "Lanjutkan" else "Mulai Latihan",
                                    onClick = onPrimaryAction,
                                )
                                TextButton(
                                    onClick = onOpenPractice,
                                    modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                                ) {
                                    Text("Lihat 5 Paket")
                                }
                            }
                        }
                    }
                }
                val summaryCard: @Composable (Modifier) -> Unit = { sumMod ->
                    ProgressSummary(
                        title = "Ringkasan ${state.selectedExam.title}",
                        accuracyPercent = state.averageAccuracyPercent,
                        completedSessions = state.completedSessionCount,
                        subtitle = state.selectedExam.scaleLabel,
                        modifier = sumMod,
                    )
                }

                if (isExpanded) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
                        verticalAlignment = Alignment.Top,
                    ) {
                        heroCard(Modifier.weight(1.15f))
                        summaryCard(Modifier.weight(0.85f))
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm + 4.dp)) {
                        heroCard(Modifier)
                        summaryCard(Modifier)
                    }
                }
            }

            state.recommendedModule?.let { module ->
                item {
                    Spacer(modifier = Modifier.height(AppSpacing.xs))
                    SectionHeading(text = "Materi Berikutnya")
                    Spacer(modifier = Modifier.height(AppSpacing.xs))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ModuleRow(
                        module = module,
                        masteryPercent = null,
                        onClick = { onOpenModule(module.id) },
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                SectionHeading(text = "Tutor AI Lokal (Gemma 4 / FTS)")
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                val llmReady = state.llmState is LlmModelState.Ready
                val detail = when (val llm = state.llmState) {
                    is LlmModelState.Ready -> "Aktif offline: ${llm.modelName} (${llm.variant})"
                    is LlmModelState.Loading -> "Memuat model lokal: ${llm.modelName}..."
                    is LlmModelState.Error -> llm.message
                    LlmModelState.Unloaded -> "Model belum diimpor; pencarian materi FTS offline tetap aktif."
                }
                OfflineModelState(
                    modelLabel = "Asisten Belajar Offline",
                    isReady = llmReady,
                    detail = detail,
                    actionLabel = "Tanya AI",
                    onAction = onOpenChat,
                )
            }
        }
    }

    if (state.showExamSwitcher) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = onDismissSwitcher,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
            ) {
                SectionHeading(text = "Pilih Target Ujian")
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                ExamId.entries.forEach { exam ->
                    val selected = exam == state.selectedExam
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
                            .clickable { onSelectExam(exam) },
                    ) {
                        Row(
                            modifier = Modifier.padding(AppSpacing.md),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = exam.title, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = "${exam.subtitle} • ${exam.scaleLabel}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Aktif",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(AppSpacing.sm))
                }
                Spacer(modifier = Modifier.height(AppSpacing.md))
            }
        }
    }
}
