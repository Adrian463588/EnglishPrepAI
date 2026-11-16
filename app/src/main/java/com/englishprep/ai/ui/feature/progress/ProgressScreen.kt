package com.englishprep.ai.ui.feature.progress

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import com.englishprep.ai.ui.components.ProgressSummary
import com.englishprep.ai.ui.components.SectionHeading
import com.englishprep.ai.ui.theme.AppSpacing

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

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        item {
            Text(
                text = "Analitik Progres • ${state.selectedExam.title}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.semantics { heading() },
            )
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(
                text = "Seluruh statistik dihitung lokal dari riwayat latihan nyata di perangkat.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        item {
            ProgressSummary(
                title = "Ringkasan Objektif",
                accuracyPercent = state.overallAccuracyPercent,
                completedSessions = state.recentSessions.size,
                subtitle = state.selectedExam.scaleLabel,
            )
        }

        if (state.recentSessions.isEmpty() && state.weakModules.isEmpty()) {
            item {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(AppSpacing.md)) {
                        Text(
                            text = "Belum Ada Riwayat Tryout",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.xs))
                        Text(
                            text = "Selesaikan satu paket latihan atau simulasi agar grafik tren akurasi dan pemetaan kelemahan per seksi tampil di sini.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.md))
                        AppButton(text = "Mulai Paket Pertama", onClick = onOpenPractice)
                    }
                }
            }
        }

        if (state.recentTrendPercents.size >= 2) {
            item {
                SectionHeading(text = "Tren Akurasi Sesi Terakhir")
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                val lineColor = MaterialTheme.colorScheme.primary
                val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(AppSpacing.md)) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                        ) {
                            val pts = state.recentTrendPercents
                            val stepX = size.width / (pts.size - 1).coerceAtLeast(1)
                            drawLine(gridColor, Offset(0f, size.height * 0.5f), Offset(size.width, size.height * 0.5f), 1.dp.toPx())
                            for (i in 0 until pts.size - 1) {
                                val x1 = i * stepX
                                val y1 = size.height * (1f - pts[i] / 100f)
                                val x2 = (i + 1) * stepX
                                val y2 = size.height * (1f - pts[i + 1] / 100f)
                                drawLine(lineColor, Offset(x1, y1), Offset(x2, y2), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
                            }
                            pts.forEachIndexed { idx, pct ->
                                val x = idx * stepX
                                val y = size.height * (1f - pct / 100f)
                                drawCircle(lineColor, radius = 4.dp.toPx(), center = Offset(x, y))
                            }
                        }
                        Spacer(modifier = Modifier.height(AppSpacing.xs))
                        Text(
                            text = "Urutan kronologis: ${state.recentTrendPercents.joinToString(" → ") { "$it%" }}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        if (state.sectionRows.isNotEmpty()) {
            item {
                SectionHeading(text = "Akurasi per Seksi")
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                ) {
                    Column(modifier = Modifier.padding(AppSpacing.md)) {
                        state.sectionRows.forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(text = row.section, style = MaterialTheme.typography.titleMedium)
                                Text(text = "${row.correct}/${row.total} (${row.percent}%)", style = MaterialTheme.typography.bodyMedium)
                            }
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            LinearProgressIndicator(
                                progress = { (row.percent / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.md))
                        }
                    }
                }
            }
        }

        if (state.weakModules.isNotEmpty()) {
            item {
                SectionHeading(text = "Modul Perlu Penguatan (<80%)")
            }
            items(state.weakModules, key = { it.id }) { prog ->
                val pct = (prog.masteryEstimate * 100).toInt()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
                        .clickable { onOpenModule(prog.moduleId) }
                        .padding(vertical = AppSpacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = prog.moduleId, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${prog.correct}/${prog.attempts} latihan benar ($pct%)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text("Buka Materi", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
                HorizontalDivider()
            }
        }

        if (state.recentSessions.isNotEmpty()) {
            item {
                SectionHeading(text = "Riwayat Sesi Selesai")
            }
            items(state.recentSessions, key = { it.id }) { ses ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
                        .clickable { onOpenResults(ses.id) }
                        .padding(vertical = AppSpacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = ses.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Mode ${ses.mode}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text("Lihat Hasil", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
                HorizontalDivider()
            }
        }
    }
}
