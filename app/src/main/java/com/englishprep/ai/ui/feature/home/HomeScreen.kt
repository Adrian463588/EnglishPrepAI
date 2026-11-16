package com.englishprep.ai.ui.feature.home

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.englishprep.ai.ui.components.PreferenceRow
import com.englishprep.ai.ui.components.ResponsiveScreenContainer
import com.englishprep.ai.ui.components.SectionHeading
import com.englishprep.ai.ui.components.rememberIsExpandedWidth
import com.englishprep.ai.ui.components.tabular
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
        onOpenChat = { onOpenChat(null) },
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
            contentPadding = PaddingValues(horizontal = AppSpacing.md, vertical = AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    Text(
                        text = "English Prep",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.semantics { heading() },
                    )
                    ExamSelector(
                        activeExam = state.selectedExam,
                        onOpenSwitcher = onOpenSwitcher,
                    )
                }
            }

            item {
                val cleanHeroTitle = (state.activeSessionTitle ?: state.recommendedPack?.title ?: state.selectedExam.title)
                    .replace(" (PRACTICE)", "")
                    .replace(" (STRICT)", "")
                val progressLine = when {
                    state.activeSessionId != null && state.activeSessionTotalCount > 0 ->
                        "${state.activeSessionAnsweredCount} dari ${state.activeSessionTotalCount} soal"
                    state.recommendedPack != null ->
                        "${state.recommendedPack.availableCount} soal • ${state.recommendedPack.durationMinutes} mnt"
                    else -> null
                }

                val heroBlock: @Composable (Modifier) -> Unit = { blockMod ->
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        modifier = blockMod.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(AppSpacing.md)) {
                            Text(
                                text = if (state.activeSessionId != null) "Lanjutkan belajar" else "Mulai latihan",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            Text(
                                text = cleanHeroTitle,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.semantics { heading() },
                            )
                            if (progressLine != null) {
                                Spacer(modifier = Modifier.height(AppSpacing.xs))
                                Text(
                                    text = progressLine,
                                    style = MaterialTheme.typography.bodyMedium.tabular(),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
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
                                    Text("Semua paket")
                                }
                            }
                        }
                    }
                }

                val progressSummaryRow: @Composable (Modifier) -> Unit = { sumMod ->
                    Column(modifier = sumMod.fillMaxWidth()) {
                        SectionHeading(text = "Progres belajar")
                        Spacer(modifier = Modifier.height(AppSpacing.xs))
                        Text(
                            text = if (state.averageAccuracyPercent != null && state.completedSessionCount > 0) {
                                "Akurasi: ${state.averageAccuracyPercent}% • ${state.completedSessionCount} sesi selesai"
                            } else {
                                "Belum ada sesi selesai"
                            },
                            style = MaterialTheme.typography.bodyLarge.tabular(),
                            color = if (state.averageAccuracyPercent != null) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                }

                if (isExpanded) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.lg),
                        verticalAlignment = Alignment.Top,
                    ) {
                        heroBlock(Modifier.weight(1.25f))
                        progressSummaryRow(Modifier.weight(0.75f).padding(top = AppSpacing.sm))
                    }
                } else {
                    heroBlock(Modifier)
                    Spacer(modifier = Modifier.height(AppSpacing.md))
                    progressSummaryRow(Modifier)
                }
            }

            state.recommendedModule?.let { module ->
                item {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(AppSpacing.md))
                    SectionHeading(text = "Pelajaran berikutnya")
                    Spacer(modifier = Modifier.height(AppSpacing.xs))
                    ModuleRow(
                        module = module,
                        masteryPercent = null,
                        onClick = { onOpenModule(module.id) },
                    )
                }
            }

            item {
                val aiSubtitle = when (val llm = state.llmState) {
                    is LlmModelState.Ready -> "${llm.modelName} siap offline"
                    is LlmModelState.Loading -> "Memuat ${llm.modelName}..."
                    is LlmModelState.Error -> "Pencarian materi offline aktif"
                    LlmModelState.Unloaded -> "Pencarian materi offline aktif"
                }
                PreferenceRow(
                    title = "Tutor AI",
                    subtitle = aiSubtitle,
                    onClick = onOpenChat,
                    trailing = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                        ) {
                            Text(
                                text = "Tanya AI",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    },
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
                ExamId.entries.forEachIndexed { idx, exam ->
                    val selected = exam == state.selectedExam
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surface,
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
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = exam.subtitle,
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
                    if (idx < ExamId.entries.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    }
                }
                Spacer(modifier = Modifier.height(AppSpacing.lg))
            }
        }
    }
}
