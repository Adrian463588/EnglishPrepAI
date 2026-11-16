package com.englishprep.ai.ui.feature.results

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.englishprep.ai.domain.models.GradingOutcome
import com.englishprep.ai.ui.components.AppButton
import com.englishprep.ai.ui.components.InlineFeedback
import com.englishprep.ai.ui.components.SectionHeading
import com.englishprep.ai.ui.theme.AppSpacing

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    state: ResultsUiState,
    onBackHome: () -> Unit,
    onOpenModule: (String) -> Unit,
    onRetryMistakes: () -> Unit,
    modifier: Modifier = Modifier,
) {
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            item {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(AppSpacing.md)) {
                        Text(
                            text = state.sessionTitle,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.semantics { heading() },
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.xs))
                        val overall = state.overallEstimate
                        if (overall != null) {
                            Text(
                                text = overall.primaryEstimateLabel,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            val answeredCount = overall.rawTotal - overall.skippedCount
                            Text(
                                text = "Dijawab: $answeredCount/${overall.rawTotal} • Dilewati: ${overall.skippedCount} • Kebijakan ${overall.policyVersion}\n${overall.secondaryNote}",
                                style = MaterialTheme.typography.bodyMedium,
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
                                    text = "• $sec: $txt",
                                    style = MaterialTheme.typography.bodyMedium,
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
                SectionHeading(text = "Pembahasan Per Butir")
            }

            items(state.rows, key = { it.question.id }) { row ->
                val q = row.question
                val isCorrect = when (val out = row.outcome) {
                    is GradingOutcome.Objective -> out.isCorrect
                    is GradingOutcome.SubjectivePending -> out.meetsMinimumWords
                }
                val statusMsg = when (val out = row.outcome) {
                    is GradingOutcome.Objective -> out.feedbackMessage
                    is GradingOutcome.SubjectivePending -> out.feedbackMessage
                }
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(AppSpacing.md)) {
                        Text(
                            text = "${q.id} • ${q.section.label}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.xs))
                        Text(text = q.prompt, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(AppSpacing.sm))
                        Text(
                            text = "Jawaban Anda: ${row.userAnswer.ifBlank { "(kosong)" }.replace("|", ", ")}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.sm))
                        InlineFeedback(
                            visible = true,
                            isSuccess = isCorrect,
                            title = statusMsg,
                            body = q.explanation,
                        )
                        if (q.rationaleSource.isNotBlank()) {
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                TextButton(
                                    onClick = { onOpenModule(q.rationaleSource) },
                                    modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                                ) {
                                    Text("Buka Materi ${q.rationaleSource}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
