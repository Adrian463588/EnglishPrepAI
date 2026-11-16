package com.englishprep.ai.ui.feature.practice

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.englishprep.ai.domain.models.ExamMode
import com.englishprep.ai.ui.components.AppButton
import com.englishprep.ai.ui.components.InlineFeedback
import com.englishprep.ai.ui.components.PackageRow
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
    onImportPack: () -> Unit,
    onDismissMessage: () -> Unit,
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
                text = "Tryout ${state.selectedExam.title}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.semantics { heading() },
            )
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(
                text = "5 paket soal orisinal per jenis tes. Mode Latihan menampilkan pembahasan cepat & pengatur kecepatan audio; Mode Strict meniru batas ulang & timer ujian.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            AppButton(
                text = "Impor Paket Soal Eksternal (SAF JSON/ZIP)",
                onClick = onImportPack,
                outlined = true,
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

        items(state.packs, key = { it.packId }) { pack ->
            PackageRow(
                pack = pack,
                onPracticeClick = { onStartPractice(pack) },
                onStrictClick = { onStartStrict(pack) },
            )
        }
    }
}
