package com.englishprep.ai.ui.feature.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.englishprep.ai.data.datastore.AppLanguage
import com.englishprep.ai.data.datastore.ThemeMode
import com.englishprep.ai.data.room.ModelInstallEntity
import com.englishprep.ai.domain.engines.LlmModelState
import com.englishprep.ai.domain.engines.SttModelState
import com.englishprep.ai.ui.components.AppButton
import com.englishprep.ai.ui.components.InlineFeedback
import com.englishprep.ai.ui.components.OfflineModelState
import com.englishprep.ai.ui.components.ResponsiveScreenContainer
import com.englishprep.ai.ui.components.SectionHeading
import com.englishprep.ai.ui.theme.AppSpacing

@Composable
fun ProfileRoute(
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
    val packLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let(viewModel::importStudyPackSaf)
    }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
        uri?.let(viewModel::exportBackupToUri)
    }
    val restoreLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let(viewModel::importBackupFromUri)
    }
    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        viewModel.setReminder(granted)
    }

    ProfileScreen(
        state = state,
        onSelectLanguage = viewModel::setLanguage,
        onSelectTheme = viewModel::setThemeMode,
        onSelectSpeed = viewModel::setAudioSpeed,
        onToggleFontBoost = viewModel::setFontScaleBoost,
        onToggleReduceMotion = viewModel::setReduceMotion,
        onToggleHaptics = viewModel::setHaptics,
        onToggleReminder = { enabled ->
            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val ok = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
                if (ok) viewModel.setReminder(true) else notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.setReminder(enabled)
            }
        },
        onImportGemma4 = { gemmaLauncher.launch(arrayOf("*/*")) },
        onImportWhisper = { whisperLauncher.launch(null) },
        onImportPack = { packLauncher.launch(arrayOf("application/json", "application/zip")) },
        onActivateModel = viewModel::activateModel,
        onDeleteModel = viewModel::deleteModel,
        onOpenExternalUrl = { url ->
            runCatching {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        },
        onExportBackup = { exportLauncher.launch("english_prep_backup.json") },
        onRestoreBackup = { restoreLauncher.launch(arrayOf("application/json")) },
        onDeleteAllData = viewModel::deleteAllUserData,
        onDismissBanner = viewModel::dismissBanner,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onSelectLanguage: (AppLanguage) -> Unit,
    onSelectTheme: (ThemeMode) -> Unit,
    onSelectSpeed: (Float) -> Unit,
    onToggleFontBoost: (Boolean) -> Unit,
    onToggleReduceMotion: (Boolean) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onToggleReminder: (Boolean) -> Unit,
    onImportGemma4: () -> Unit,
    onImportWhisper: () -> Unit,
    onImportPack: () -> Unit,
    onActivateModel: (ModelInstallEntity) -> Unit,
    onDeleteModel: (String) -> Unit,
    onOpenExternalUrl: (String) -> Unit,
    onExportBackup: () -> Unit,
    onRestoreBackup: () -> Unit,
    onDeleteAllData: () -> Unit,
    onDismissBanner: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    ResponsiveScreenContainer(maxWidth = 760.dp, modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
        item {
            Text(
                text = "Profil, AI Offline & Pengaturan",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.semantics { heading() },
            )
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(
                text = "Tanpa akun atau login. Seluruh data & model AI disimpan lokal di perangkat.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (state.isBusy) {
            item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
        }

        if (state.statusBanner != null || state.errorBanner != null) {
            item {
                InlineFeedback(
                    visible = true,
                    isSuccess = state.errorBanner == null,
                    title = if (state.errorBanner == null) "Informasi" else "Peringatan SAF / Model",
                    body = state.errorBanner ?: state.statusBanner.orEmpty(),
                    actionLabel = "Tutup",
                    onAction = onDismissBanner,
                )
            }
        }

        // Section: Offline AI (Gemma 4 + Whisper ONNX + SAF)
        item {
            SectionHeading(text = "AI Offline (SAF Folder & Hugging Face)")
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            OfflineModelState(
                modelLabel = "Gemma 4 Chatbot & Coach (LiteRT-LM)",
                isReady = state.llmState is LlmModelState.Ready,
                detail = when (val s = state.llmState) {
                    is LlmModelState.Ready -> "Aktif: ${s.modelName}"
                    is LlmModelState.Loading -> "Memuat ${s.modelName}..."
                    is LlmModelState.Error -> s.message
                    LlmModelState.Unloaded -> "Belum dimuat — unduh .litertlm lalu impor lewat SAF."
                },
            )
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            OfflineModelState(
                modelLabel = "Whisper ONNX STT (Speaking Offline)",
                isReady = state.sttState is SttModelState.Ready,
                detail = when (val s = state.sttState) {
                    is SttModelState.Ready -> "Aktif: ${s.modelName}"
                    is SttModelState.Error -> s.message
                    SttModelState.Unloaded -> "Belum dimuat — pilih folder encoder/decoder/vocab via SAF."
                },
            )
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                AppButton(text = "Impor Gemma 4 (.litertlm)", onClick = onImportGemma4)
                AppButton(text = "Impor Folder Whisper ONNX", onClick = onImportWhisper, outlined = true)
                AppButton(text = "Impor Paket Soal (SAF)", onClick = onImportPack, outlined = true)
            }
        }

        if (state.installedModels.isNotEmpty()) {
            item {
                SectionHeading(text = "Model Terpasang di Perangkat")
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    state.installedModels.forEach { mdl ->
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.padding(AppSpacing.md)) {
                                Text(text = mdl.displayName, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = "${mdl.family} • ${mdl.runtime} • ${mdl.sizeBytes / (1024 * 1024)} MB",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(modifier = Modifier.height(AppSpacing.sm))
                                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                                    OutlinedButton(
                                        onClick = { onActivateModel(mdl) },
                                        modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                                    ) { Text("Aktifkan") }
                                    TextButton(
                                        onClick = { onDeleteModel(mdl.id) },
                                        modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                                    ) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            SectionHeading(text = "Katalog Rujukan Model Hugging Face")
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(
                text = "Aplikasi tetap bekerja 100% offline. Jika ingin mengunduh model, buka tautan berikut di browser eksternal lalu pilih berkas/folder lewat tombol Impor SAF di atas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                HuggingFaceCatalog.recommendedModels.forEach { entry ->
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(AppSpacing.md)) {
                            Text(text = entry.displayName, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "${entry.runtime} • ${entry.minDiskGb} • ${entry.minRamGb}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            Text(
                                text = "Berkas: ${entry.expectedFiles}\n${entry.notes}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            TextButton(
                                onClick = { onOpenExternalUrl(entry.huggingFaceUrl) },
                                modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                            ) {
                                Text("Buka di Browser Eksternal")
                            }
                        }
                    }
                }
            }
        }

        // Section: Language, Theme, Audio & Accessibility
        item {
            SectionHeading(text = "Tampilan, Audio & Aksesibilitas")
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Text("Bahasa Antarmuka", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                AppLanguage.entries.forEach { lang ->
                    FilterChip(
                        selected = state.settings.language == lang,
                        onClick = { onSelectLanguage(lang) },
                        label = { Text(lang.label) },
                        modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                    )
                }
            }
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Text("Tema Warna", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                ThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = state.settings.themeMode == mode,
                        onClick = { onSelectTheme(mode) },
                        label = { Text(mode.label) },
                        modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                    )
                }
            }
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Text("Kecepatan Audio Default (Mode Latihan)", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                listOf(0.85f, 1.0f, 1.25f).forEach { sp ->
                    FilterChip(
                        selected = kotlin.math.abs(state.settings.audioSpeedPractice - sp) < 0.05f,
                        onClick = { onSelectSpeed(sp) },
                        label = { Text("${sp}x") },
                        modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                    )
                }
            }
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            ToggleRow("Perbesar Skala Teks Bacaan", state.settings.fontScaleBoost, onToggleFontBoost)
            ToggleRow("Kurangi Animasi (Reduce Motion)", state.settings.reduceMotion, onToggleReduceMotion)
            ToggleRow("Getar Haptik Saat Selesai", state.settings.hapticsEnabled, onToggleHaptics)
            ToggleRow("Pengingat Belajar Harian (Lokal)", state.settings.reminderEnabled, onToggleReminder)
        }

        // Section: DevSecOps & Local Integrity
        state.securityPosture?.let { posture ->
            item {
                SectionHeading(text = "Keamanan & Integritas R8")
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(AppSpacing.md)) {
                        val modeLabel = if (posture.r8ExpectedOnRelease) {
                            "Mode Release (R8/ProGuard Aktif)"
                        } else {
                            "Mode Debug (QA Lokal)"
                        }
                        val sigShort = if (posture.signingSha256.length >= 16) {
                            "${posture.signingSha256.take(8)}…${posture.signingSha256.takeLast(8)}"
                        } else {
                            posture.signingSha256.ifEmpty { "Tidak tersedia" }
                        }
                        val sigStatus = when (posture.signatureMatchesExpected) {
                            true -> "Cocok dengan release key"
                            false -> "Peringatan: tidak cocok"
                            null -> "Sertifikat lokal aktif ($sigShort)"
                        }
                        Text(text = modeLabel, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(AppSpacing.xs))
                        Text(
                            text = "Signature SHA-256: $sigStatus\n" +
                                "Backup OS (allowBackup): ${if (posture.allowBackupEnabled) "Aktif" else "Nonaktif"} • " +
                                "Debugger: ${if (posture.isDebuggerAttached) "Terpasang" else "Tidak"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (posture.advisoryWarnings.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            Text(
                                text = posture.advisoryWarnings.joinToString(" • "),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.tertiary,
                            )
                        }
                    }
                }
            }
        }

        // Section: Backup & Privacy
        item {
            SectionHeading(text = "Cadangan Data & Privasi")
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                AppButton(text = "Ekspor Cadangan JSON", onClick = onExportBackup, outlined = true)
                AppButton(text = "Pulihkan Cadangan JSON", onClick = onRestoreBackup, outlined = true)
            }
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            TextButton(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
            ) {
                Text("Hapus Semua Riwayat Belajar", color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Text(
                text = "Lisensi & Kepatuhan: Seluruh 30 modul, 15 paket tryout penuh (1.725 butir READY), kutipan OER Bank Materi PDF, dan 390 audio WAV IMA-ADPCM adalah konten belajar orisinal atau OER CC-BY/Public Domain (bukan soal resmi ETS/British Council/IELTS). Skor mentah & umpan balik AI lokal bukan nilai resmi.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            var showOssLicenses by remember { mutableStateOf(false) }
            TextButton(
                onClick = { showOssLicenses = true },
                modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
            ) {
                Text("Lihat Lisensi Open-Source & Pihak Ketiga")
            }
            if (showOssLicenses) {
                AlertDialog(
                    onDismissRequest = { showOssLicenses = false },
                    title = { Text("Lisensi Open-Source & Atribusi") },
                    text = {
                        Text(
                            text = "• AndroidX Jetpack Compose, Material 3, Room, DataStore, WorkManager, Navigation, Hilt (Apache-2.0)\n" +
                                "• AndroidX Media3 ExoPlayer 1.5.1 (Apache-2.0)\n" +
                                "• Kotlin Standard Library, Coroutines, Serialization (Apache-2.0)\n" +
                                "• Google LiteRT-LM Android 0.13.1 (Apache-2.0)\n" +
                                "• Microsoft ONNX Runtime Android 1.22.0 (MIT)\n" +
                                "• JUnit 4 (EPL-1.0), Robolectric (MIT), Turbine (Apache-2.0)\n\n" +
                                "TOEFL, TOEFL ITP, dan TOEFL iBT adalah merek dagang ETS. IELTS adalah merek dagang Cambridge Assessment English / British Council / IDP. Aplikasi ini independen dan tidak berafiliasi dengan pemilik merek tersebut.",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { showOssLicenses = false }) { Text("Tutup") }
                    },
                )
            }
        }
    }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Hapus seluruh riwayat belajar?") },
            text = { Text("Riwayat sesi ujian, skor, progres modul, dan percakapan Tutor AI akan dihapus permanen dari perangkat.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    onDeleteAllData()
                }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Batal") }
            },
        )
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
            .toggleable(
                value = checked,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            )
            .padding(vertical = AppSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(AppSpacing.md))
        Switch(checked = checked, onCheckedChange = null)
    }
}
