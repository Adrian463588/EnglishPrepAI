package com.englishprep.ai.ui.feature.results

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.englishprep.ai.domain.models.GradingOutcome
import com.englishprep.ai.ui.components.AppButton
import com.englishprep.ai.ui.components.ResponsiveScreenContainer
import com.englishprep.ai.ui.components.SectionHeading
import com.englishprep.ai.ui.components.tabular
import com.englishprep.ai.ui.theme.AppSpacing
import com.englishprep.ai.ui.theme.LocalSemanticStatusColors

@Composable
fun ResultsRoute(
    onBackHome: () -> Unit,
    onOpenModule: (String) -> Unit,
    onOpenRetrySession: (String) -> Unit,
    viewModel: ResultsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ResultsScreen(
        state = state,
        onBackHome = onBackHome,
        onOpenModule = onOpenModule,
        onRetryMistakes = { viewModel.retryMistakes(onOpenRetrySession) },
    )
}

private enum class ResultFilter(val label: String) {
    ALL("Semua"),
    WRONG("Salah"),
    SKIPPED("Dilewati"),
    CORRECT("Benar"),
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ResultsScreen(
    state: ResultsUiState,
    onBackHome: () -> Unit,
    onOpenModule: (String) -> Unit,
    onRetryMistakes: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var activeFilter by remember { mutableStateOf(ResultFilter.ALL) }
    val statusColors = LocalSemanticStatusColors.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hasil & Pembahasan") },
                navigationIcon = {
                    IconButton(
                        onClick = onBackHome,
                        modifier = Modifier.defaultMinSize(minWidth = AppSpacing.minTouchTarget, minHeight = AppSpacing.minTouchTarget),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tutup hasil")
                    }
                },
            )
        },
        modifier = modifier,
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val filteredRows = remember(state.rows, activeFilter) {
            state.rows.filter { row ->
                when (activeFilter) {
                    ResultFilter.ALL -> true
                    ResultFilter.WRONG -> row.outcome is GradingOutcome.Objective && !row.outcome.isCorrect && !row.outcome.isSkipped
                    ResultFilter.SKIPPED -> (row.outcome is GradingOutcome.Objective && row.outcome.isSkipped) || row.userAnswer.isBlank()
                    ResultFilter.CORRECT -> when (row.outcome) {
                        is GradingOutcome.Objective -> row.outcome.isCorrect
                        is GradingOutcome.SubjectivePending -> row.outcome.meetsMinimumWords
                    }
                }
            }
        }

        ResponsiveScreenContainer(maxWidth = 840.dp, modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(AppSpacing.md),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
            ) {
                item {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(AppSpacing.md)) {
                            Text(
                                text = state.sessionTitle.replace(" (PRACTICE)", "").replace(" (STRICT)", ""),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.semantics { heading() },
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            val overall = state.overallEstimate
                            if (overall != null) {
                                Text(
                                    text = overall.primaryEstimateLabel,
                                    style = MaterialTheme.typography.headlineSmall.tabular(),
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(modifier = Modifier.height(AppSpacing.xs))
                                val answeredCount = overall.rawTotal - overall.skippedCount
                                Text(
                                    text = "Dijawab: $answeredCount/${overall.rawTotal} • Dilewati: ${overall.skippedCount}\n${overall.secondaryNote}",
                                    style = MaterialTheme.typography.bodyMedium.tabular(),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            } else {
                                Text(
                                    text = "Sesi tugas subyektif selesai (estimasi non-resmi).",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                            if (state.sectionSummaries.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(AppSpacing.sm))
                                state.sectionSummaries.forEach { (sec, txt) ->
                                    Text(
                                        text = "$sec: $txt",
                                        style = MaterialTheme.typography.bodyMedium.tabular(),
                                    )
                                }
                            }
                            if (state.wrongQuestionIds.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(AppSpacing.md))
                                AppButton(
                                    text = "Ulangi ${state.wrongQuestionIds.size} Soal Salah",
                                    onClick = onRetryMistakes,
                                )
                            }
                        }
                    }
                }

                item {
                    SectionHeading(text = "Pembahasan Per Butir (${filteredRows.size}/${state.rows.size})")
                    Spacer(modifier = Modifier.height(AppSpacing.sm))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                    ) {
                        ResultFilter.entries.forEach { f ->
                            FilterChip(
                                selected = activeFilter == f,
                                onClick = { activeFilter = f },
                                label = { Text(f.label) },
                                modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                            )
                        }
                    }
                }

                if (filteredRows.isEmpty()) {
                    item {
                        Text(
                            text = "Tidak ada butir soal pada kategori '${activeFilter.label}'.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                items(filteredRows, key = { it.question.id }) { row ->
                    val q = row.question
                    val isCorrect = when (val out = row.outcome) {
                        is GradingOutcome.Objective -> out.isCorrect
                        is GradingOutcome.SubjectivePending -> out.meetsMinimumWords
                    }
                    val statusMsg = when (val out = row.outcome) {
                        is GradingOutcome.Objective -> out.feedbackMessage
                        is GradingOutcome.SubjectivePending -> out.feedbackMessage
                    }
                    val tintColor = if (isCorrect) statusColors.success else MaterialTheme.colorScheme.error
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = tintColor,
                            )
                            Spacer(modifier = Modifier.width(AppSpacing.xs))
                            Text(
                                text = statusMsg,
                                style = MaterialTheme.typography.labelLarge,
                                color = tintColor,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = q.section.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(text = q.prompt, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "Jawaban Anda: ${row.userAnswer.ifBlank { "(kosong)" }.replace("|", ", ")}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = q.explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        if (q.rationaleSource.isNotBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                TextButton(
                                    onClick = { onOpenModule(q.rationaleSource) },
                                    modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                                ) {
                                    Text("Pelajari Materi")
                                }
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    }
                }
            }
        }
    }
}
