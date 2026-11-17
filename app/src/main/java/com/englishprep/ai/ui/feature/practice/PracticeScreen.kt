package com.englishprep.ai.ui.feature.practice

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.englishprep.ai.domain.models.ExamMode
import com.englishprep.ai.ui.components.AppButton
import com.englishprep.ai.ui.components.InlineFeedback
import com.englishprep.ai.ui.components.PackageRow
import com.englishprep.ai.ui.components.ResponsiveScreenContainer
import com.englishprep.ai.ui.components.rememberIsExpandedWidth
import com.englishprep.ai.ui.theme.AppSpacing

@Composable
fun PracticeRoute(
    onOpenSession: (String) -> Unit,
    viewModel: PracticeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val packLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        uri?.let { viewModel.importPackSaf(it.toString()) }
    }

    PracticeScreen(
        state = state,
        onStartPractice = { pack -> viewModel.startPack(pack, ExamMode.PRACTICE, onOpenSession) },
        onStartStrict = { pack -> viewModel.startPack(pack, ExamMode.STRICT, onOpenSession) },
        onImportPack = { packLauncher.launch(arrayOf("application/json", "application/zip")) },
        onDismissMessage = viewModel::clearMessage,
    )
}

@Composable
fun PracticeScreen(
    state: PracticeUiState,
    onStartPractice: (com.englishprep.ai.domain.models.ContentPackSummary) -> Unit,
    onStartStrict: (com.englishprep.ai.domain.models.ContentPackSummary) -> Unit,
    @Suppress("UNUSED_PARAMETER") onImportPack: () -> Unit = {},
    onDismissMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val isExpanded = rememberIsExpandedWidth()
    val pairedPacks = remember(state.packs) { state.packs.chunked(2) }

    ResponsiveScreenContainer(maxWidth = if (isExpanded) 1040.dp else 720.dp, modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Tryout ${state.selectedExam.title}",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .weight(1f)
                            .semantics { heading() },
                    )
                }
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(
                    text = "Pilih Mode Latihan (pembahasan langsung) atau Full Mock Strict (timer & batas putar ujian).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (state.statusMessage != null || state.errorMessage != null) {
                item {
                    InlineFeedback(
                        visible = true,
                        isSuccess = state.errorMessage == null,
                        title = if (state.errorMessage == null) "Status Impor" else "Impor Ditolak",
                        body = state.errorMessage ?: state.statusMessage.orEmpty(),
                        actionLabel = "Tutup",
                        onAction = onDismissMessage,
                    )
                }
            }

            if (isExpanded) {
                items(pairedPacks, key = { pair -> pair.first().packId }) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
                    ) {
                        pair.forEach { pack ->
                            PackageRow(
                                pack = pack,
                                onPracticeClick = { onStartPractice(pack) },
                                onStrictClick = { onStartStrict(pack) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            } else {
                items(state.packs, key = { it.packId }) { pack ->
                    PackageRow(
                        pack = pack,
                        onPracticeClick = { onStartPractice(pack) },
                        onStrictClick = { onStartStrict(pack) },
                    )
                }
            }
        }
    }
}
