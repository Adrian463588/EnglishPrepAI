package com.englishprep.ai.ui.feature.profile

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.englishprep.ai.data.room.ModelInstallEntity
import com.englishprep.ai.domain.engines.LlmModelState
import com.englishprep.ai.domain.engines.SttModelState
import com.englishprep.ai.ui.components.AppButton
import com.englishprep.ai.ui.components.InlineFeedback
import com.englishprep.ai.ui.components.OfflineModelState
import com.englishprep.ai.ui.components.PreferenceRow
import com.englishprep.ai.ui.components.ResponsiveScreenContainer
import com.englishprep.ai.ui.components.SectionHeading
import com.englishprep.ai.ui.components.tabular
import com.englishprep.ai.ui.theme.AppSpacing

@Composable
fun ModelManagerRoute(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val gemmaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let(viewModel::importGemma4Saf)
    }
    val whisperLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        uri?.let(viewModel::importWhisperFolderSaf)
    }

    ModelManagerScreen(
        state = state,
        onBack = onBack,
        onImportGemma4 = { gemmaLauncher.launch(arrayOf("*/*")) },
        onImportWhisper = { whisperLauncher.launch(null) },
        onActivateModel = viewModel::activateModel,
        onDeleteModel = viewModel::deleteModel,
        onOpenExternalUrl = { url ->
            runCatching {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        },
        onDismissBanner = viewModel::dismissBanner,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelManagerScreen(
    state: ProfileUiState,
    onBack: () -> Unit,
    onImportGemma4: () -> Unit,
    onImportWhisper: () -> Unit,
    onActivateModel: (ModelInstallEntity) -> Unit,
    onDeleteModel: (String) -> Unit,
    onOpenExternalUrl: (String) -> Unit,
    onDismissBanner: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedSpecEntry by remember { mutableStateOf<RecommendedModelEntry?>(null) }
    var showImportPickerDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Model AI Offline") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.defaultMinSize(minWidth = AppSpacing.minTouchTarget, minHeight = AppSpacing.minTouchTarget),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
            )
        },
        modifier = modifier,
    ) { padding ->
        ResponsiveScreenContainer(maxWidth = 760.dp, modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(AppSpacing.md),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
            ) {
                if (state.isBusy) {
                    item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
                }

                if (state.statusBanner != null || state.errorBanner != null) {
                    item {
                        InlineFeedback(
                            visible = true,
                            isSuccess = state.errorBanner == null,
                            title = if (state.errorBanner == null) "Status Model" else "Peringatan Impor Model",
                            body = state.errorBanner ?: state.statusBanner.orEmpty(),
                            actionLabel = "Tutup",
                            onAction = onDismissBanner,
                        )
                    }
                }

                item {
                    SectionHeading(text = "Terpasang")
                    Spacer(modifier = Modifier.height(AppSpacing.sm))
                    val llmText = when (val l = state.llmState) {
                        is LlmModelState.Ready -> "Gemma 4 aktif: ${l.modelName}"
                        is LlmModelState.Loading -> "Memuat ${l.modelName}..."
                        is LlmModelState.Error -> l.message
                        LlmModelState.Unloaded -> "LLM belum dimuat (FTS offline aktif)"
                    }
                    val sttText = when (val s = state.sttState) {
                        is SttModelState.Ready -> "Whisper STT aktif: ${s.modelName}"
                        is SttModelState.Error -> s.message
                        SttModelState.Unloaded -> "Whisper STT belum dimuat"
                    }
                    OfflineModelState(
                        modelLabel = "Status Runtime Lokal",
                        isReady = state.llmState is LlmModelState.Ready || state.sttState is SttModelState.Ready,
                        detail = "$llmText\n$sttText",
                    )
                }

                if (state.installedModels.isNotEmpty()) {
                    items(state.installedModels, key = { it.id }) { mdl ->
                        InstalledModelRow(
                            model = mdl,
                            onActivate = { onActivateModel(mdl) },
                            onDelete = { onDeleteModel(mdl.id) },
                        )
                    }
                }

                item {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(AppSpacing.sm))
                    SectionHeading(text = "Model yang Tersedia")
                }

                items(HuggingFaceCatalog.recommendedModels, key = { it.id }) { entry ->
                    val shortHint = when (entry.id) {
                        "HF-GEMMA4-E2B" -> "Direkomendasikan untuk mayoritas perangkat • ${entry.minDiskGb}"
                        "HF-GEMMA4-E4B" -> "Untuk perangkat RAM besar • ${entry.minDiskGb}"
                        else -> "Transkripsi Speaking offline • ${entry.minDiskGb}"
                    }
                    PreferenceRow(
                        title = entry.displayName,
                        subtitle = shortHint,
                        onClick = { selectedSpecEntry = entry },
                        trailing = {
                            TextButton(
                                onClick = { selectedSpecEntry = entry },
                                modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                            ) {
                                Text("Detail")
                            }
                        },
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                }

                item {
                    Spacer(modifier = Modifier.height(AppSpacing.xs))
                    AppButton(
                        text = "Impor model dari perangkat",
                        onClick = { showImportPickerDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }

    selectedSpecEntry?.let { entry ->
        AlertDialog(
            onDismissRequest = { selectedSpecEntry = null },
            title = { Text(entry.displayName) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                    Text(text = entry.notes, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(AppSpacing.xs))
                    Text(text = "Runtime: ${entry.runtime}", style = MaterialTheme.typography.labelMedium)
                    Text(text = "Berkas: ${entry.expectedFiles}", style = MaterialTheme.typography.labelMedium)
                    Text(text = "Penyimpanan: ${entry.minDiskGb} • RAM: ${entry.minRamGb}", style = MaterialTheme.typography.labelMedium)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onOpenExternalUrl(entry.huggingFaceUrl)
                    selectedSpecEntry = null
                }) {
                    Text("Buka Halaman Unduhan")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedSpecEntry = null }) {
                    Text("Tutup")
                }
            },
        )
    }

    if (showImportPickerDialog) {
        AlertDialog(
            onDismissRequest = { showImportPickerDialog = false },
            title = { Text("Pilih Jenis Model (SAF)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
                            .clickable {
                                showImportPickerDialog = false
                                onImportGemma4()
                            },
                    ) {
                        Column(modifier = Modifier.padding(AppSpacing.md)) {
                            Text("Gemma 4 (.litertlm)", style = MaterialTheme.typography.titleSmall)
                            Text("Pilih satu berkas model Chatbot & Coach", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
                            .clickable {
                                showImportPickerDialog = false
                                onImportWhisper()
                            },
                    ) {
                        Column(modifier = Modifier.padding(AppSpacing.md)) {
                            Text("Folder Whisper ONNX", style = MaterialTheme.typography.titleSmall)
                            Text("Pilih folder encoder/decoder/vocab untuk Speaking", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showImportPickerDialog = false }) { Text("Batal") }
            },
        )
    }
}

@Composable
private fun InstalledModelRow(
    model: ModelInstallEntity,
    onActivate: () -> Unit,
    onDelete: () -> Unit,
) {
    var showTechDetails by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = AppSpacing.xs)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = model.displayName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Siap • ${model.sizeBytes / (1024 * 1024)} MB",
                    style = MaterialTheme.typography.bodySmall.tabular(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                OutlinedButton(
                    onClick = onActivate,
                    modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                ) { Text("Aktifkan") }
                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                ) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
                .clickable { showTechDetails = !showTechDetails },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (showTechDetails) "Sembunyikan detail teknis" else "Detail teknis",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(AppSpacing.xs))
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        AnimatedVisibility(visible = showTechDetails) {
            Column(
                modifier = Modifier.padding(bottom = AppSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text("Runtime: ${model.runtime} • Format: ${model.format}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Keluarga: ${model.family} (${model.modelVariant})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (model.sha256.isNotBlank()) {
                    Text("SHA-256: ${model.sha256.take(16)}…", style = MaterialTheme.typography.labelSmall.tabular(), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    }
}
