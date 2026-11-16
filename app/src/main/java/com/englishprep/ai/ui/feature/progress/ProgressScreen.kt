package com.englishprep.ai.ui.feature.progress

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.englishprep.ai.ui.components.AppButton
import com.englishprep.ai.ui.components.PreferenceRow
import com.englishprep.ai.ui.components.ProgressSummary
import com.englishprep.ai.ui.components.ResponsiveScreenContainer
import com.englishprep.ai.ui.components.SectionHeading
import com.englishprep.ai.ui.components.rememberIsExpandedWidth
import com.englishprep.ai.ui.components.tabular
import com.englishprep.ai.ui.theme.AppSpacing
import com.englishprep.ai.ui.theme.LocalSemanticStatusColors
import com.englishprep.ai.ui.theme.MotionTokens

@Composable
fun ProgressRoute(
    onOpenResults: (String) -> Unit,
    onOpenModule: (String) -> Unit,
    onOpenPractice: () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ProgressScreen(
        state = state,
        onOpenResults = onOpenResults,
        onOpenModule = onOpenModule,
        onOpenPractice = onOpenPractice,
    )
}

@Composable
fun ProgressScreen(
    state: ProgressUiState,
    onOpenResults: (String) -> Unit,
    onOpenModule: (String) -> Unit,
    onOpenPractice: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val isExpanded = rememberIsExpandedWidth()
    val reduceMotion = LocalSemanticStatusColors.current.reduceMotion

    ResponsiveScreenContainer(maxWidth = if (isExpanded) 960.dp else 720.dp, modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            item {
                Text(
                    text = "Progres ${state.selectedExam.title}",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.semantics { heading() },
                )
            }

            item {
                val summaryCard: @Composable (Modifier) -> Unit = { cardMod ->
                    ProgressSummary(
                        title = "Ringkasan Latihan",
                        accuracyPercent = state.overallAccuracyPercent,
                        completedSessions = state.recentSessions.size,
                        modifier = cardMod,
                    )
                }
                if (isExpanded && state.recentTrendPercents.size >= 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
                        verticalAlignment = Alignment.Top,
                    ) {
                        summaryCard(Modifier.weight(1f))
                        TrendChartCard(
                            points = state.recentTrendPercents,
                            modifier = Modifier.weight(1f),
                        )
                    }
                } else {
                    summaryCard(Modifier)
                }
            }

            if (state.recentSessions.isEmpty() && state.weakModules.isEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                        Text(
                            text = "Belum Ada Riwayat Tryout",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "Selesaikan satu paket latihan atau simulasi agar statistik akurasi per seksi dan modul lemah tampil di sini.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        AppButton(text = "Mulai Paket Pertama", onClick = onOpenPractice)
                    }
                }
            }

            if (state.recentTrendPercents.size >= 2 && !isExpanded) {
                item {
                    SectionHeading(text = "Tren Sesi Terbaru")
                    Spacer(modifier = Modifier.height(AppSpacing.sm))
                    TrendChartCard(points = state.recentTrendPercents)
                }
            }

            if (state.sectionRows.isNotEmpty()) {
                item {
                    SectionHeading(text = "Performa per Seksi")
                    Spacer(modifier = Modifier.height(AppSpacing.sm))
                    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
                        state.sectionRows.forEach { row ->
                            val animProg by animateFloatAsState(
                                targetValue = (row.percent / 100f).coerceIn(0f, 1f),
                                animationSpec = tween(if (reduceMotion) 0 else MotionTokens.AnswerCheckMs),
                                label = "secBar_${row.section}",
                            )
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(text = row.section, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = "${row.correct}/${row.total} (${row.percent}%)",
                                        style = MaterialTheme.typography.bodyMedium.tabular(),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Spacer(modifier = Modifier.height(AppSpacing.xs))
                                LinearProgressIndicator(
                                    progress = { animProg },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
            }

            if (state.recentSessions.isNotEmpty()) {
                item {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(AppSpacing.sm))
                    SectionHeading(text = "Percobaan Terbaru")
                }
                items(state.recentSessions, key = { it.id }) { ses ->
                    val modeText = if (ses.mode == "STRICT") "Simulasi Strict" else "Mode Latihan"
                    PreferenceRow(
                        title = ses.title.replace(" (PRACTICE)", "").replace(" (STRICT)", ""),
                        subtitle = modeText,
                        onClick = { onOpenResults(ses.id) },
                        trailing = {
                            Text("Hasil", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        },
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                }
            }

            if (state.weakModules.isNotEmpty()) {
                item {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(AppSpacing.sm))
                    SectionHeading(text = "Perlu Diperkuat (<80%)")
                }
                items(state.weakModules, key = { it.id }) { prog ->
                    val pct = (prog.masteryEstimate * 100).toInt()
                    PreferenceRow(
                        title = prog.moduleId,
                        subtitle = "${prog.correct}/${prog.attempts} benar ($pct%)",
                        onClick = { onOpenModule(prog.moduleId) },
                        trailing = {
                            Text("Pelajari", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        },
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                }
            }
        }
    }
}

@Composable
private fun TrendChartCard(
    points: List<Int>,
    modifier: Modifier = Modifier,
) {
    val lineColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
            ) {
                val stepX = size.width / (points.size - 1).coerceAtLeast(1)
                drawLine(gridColor, Offset(0f, size.height * 0.5f), Offset(size.width, size.height * 0.5f), 1.dp.toPx())
                for (i in 0 until points.size - 1) {
                    val x1 = i * stepX
                    val y1 = size.height * (1f - points[i] / 100f)
                    val x2 = (i + 1) * stepX
                    val y2 = size.height * (1f - points[i + 1] / 100f)
                    drawLine(lineColor, Offset(x1, y1), Offset(x2, y2), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
                }
                points.forEachIndexed { idx, pct ->
                    val x = idx * stepX
                    val y = size.height * (1f - pct / 100f)
                    drawCircle(lineColor, radius = 4.dp.toPx(), center = Offset(x, y))
                }
            }
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(
                text = "Urutan sesi: ${points.joinToString(" → ") { "$it%" }}",
                style = MaterialTheme.typography.labelMedium.tabular(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
