package com.englishprep.ai.ui.feature.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import com.englishprep.ai.ui.components.InlineFeedback
import com.englishprep.ai.ui.components.PreferenceRow
import com.englishprep.ai.ui.components.ResponsiveScreenContainer
import com.englishprep.ai.ui.components.SectionHeading
import com.englishprep.ai.ui.theme.AppSpacing

@Composable
fun ProfileRoute(
    onOpenModelManager: () -> Unit = {},
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
        onOpenModelManager = onOpenModelManager,
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
    onOpenModelManager: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var showOssLicenses by remember { mutableStateOf(false) }

    ResponsiveScreenContainer(maxWidth = 760.dp, modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            item {
                Text(
                    text = "Profil",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.semantics { heading() },
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

            item {
                SectionHeading(text = "Preferensi")
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                PreferenceRow(
                    title = "Bahasa Antarmuka",
                    subtitle = state.settings.language.label,
                    onClick = { showLanguageDialog = true },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                PreferenceRow(
                    title = "Tampilan",
                    subtitle = state.settings.themeMode.label,
                    onClick = { showThemeDialog = true },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                Column(modifier = Modifier.padding(vertical = AppSpacing.sm)) {
                    Text("Kecepatan Audio Default (Latihan)", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(AppSpacing.xs))
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
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                ToggleRow("Perbesar Skala Teks Bacaan", state.settings.fontScaleBoost, onToggleFontBoost)
                ToggleRow("Kurangi Animasi (Reduce Motion)", state.settings.reduceMotion, onToggleReduceMotion)
                ToggleRow("Getar Haptik Saat Selesai", state.settings.hapticsEnabled, onToggleHaptics)
                ToggleRow("Pengingat Belajar Harian (Lokal)", state.settings.reminderEnabled, onToggleReminder)
            }

            item {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                SectionHeading(text = "Konten Offline")
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                PreferenceRow(
                    title = "Paket Soal & Audio",
                    subtitle = "15 paket siap (1.725 butir) • Impor JSON/ZIP via SAF",
                    onClick = onImportPack,
                    trailing = {
                        Text(
                            text = "Impor",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    },
                )
            }

            item {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                SectionHeading(text = "AI Offline")
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                val aiSummary = when (val llm = state.llmState) {
                    is LlmModelState.Ready -> "Gemma 4 aktif: ${llm.modelName}"
                    is LlmModelState.Loading -> "Memuat ${llm.modelName}..."
                    is LlmModelState.Error -> llm.message
                    LlmModelState.Unloaded -> if (state.sttState is SttModelState.Ready) {
                        "Whisper STT aktif • LLM belum dimuat"
                    } else {
                        "Belum ada model terpasang • FTS offline aktif"
                    }
                }
                PreferenceRow(
                    title = "Kelola Model AI",
                    subtitle = aiSummary,
                    onClick = onOpenModelManager,
                )
                state.securityPosture?.let { posture ->
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                    val modeLabel = if (posture.r8ExpectedOnRelease) {
                        "Mode Release (R8/ProGuard Aktif)"
                    } else {
                        "Mode Debug (QA Lokal)"
                    }
                    PreferenceRow(
                        title = "Keamanan & Integritas R8",
                        subtitle = modeLabel,
                        onClick = { showSecurityDialog = true },
                    )
                }
            }

            item {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                SectionHeading(text = "Privasi & Data")
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                PreferenceRow(
                    title = "Ekspor Cadangan Data",
                    subtitle = "Simpan riwayat & progres belajar lokal (JSON)",
                    onClick = onExportBackup,
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                PreferenceRow(
                    title = "Pulihkan Cadangan Data",
                    subtitle = "Muat ulang dari berkas cadangan JSON",
                    onClick = onRestoreBackup,
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                PreferenceRow(
                    title = "Hapus Semua Riwayat Belajar",
                    subtitle = "Hapus sesi ujian, skor, progres, & riwayat chat dari perangkat",
                    onClick = { showDeleteConfirm = true },
                    trailing = {
                        Text(
                            text = "Hapus",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.error,
                        )
                    },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                PreferenceRow(
                    title = "Lihat Lisensi Open-Source & Pihak Ketiga",
                    subtitle = "1.725 butir READY, OER PDF & audio orisinal • Skor non-resmi",
                    onClick = { showOssLicenses = true },
                )
            }
        }
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Bahasa Antarmuka") },
            text = {
                Column {
                    AppLanguage.entries.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
                                .clickable {
                                    onSelectLanguage(lang)
                                    showLanguageDialog = false
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(selected = state.settings.language == lang, onClick = null)
                            Spacer(modifier = Modifier.width(AppSpacing.sm))
                            Text(text = lang.label, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text("Tutup") }
            },
        )
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Tema Warna") },
            text = {
                Column {
                    ThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
                                .clickable {
                                    onSelectTheme(mode)
                                    showThemeDialog = false
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(selected = state.settings.themeMode == mode, onClick = null)
                            Spacer(modifier = Modifier.width(AppSpacing.sm))
                            Text(text = mode.label, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) { Text("Tutup") }
            },
        )
    }

    if (showSecurityDialog) {
        val posture = state.securityPosture
        if (posture != null) {
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
            AlertDialog(
                onDismissRequest = { showSecurityDialog = false },
                title = { Text("Keamanan & Integritas R8") },
                text = {
                    Text(
                        text = (if (posture.r8ExpectedOnRelease) "Mode Release (R8/ProGuard Aktif)\n" else "Mode Debug (QA Lokal)\n") +
                            "Signature SHA-256: $sigStatus\n" +
                            "Backup OS (allowBackup): ${if (posture.allowBackupEnabled) "Aktif" else "Nonaktif"}\n" +
                            "Debugger: ${if (posture.isDebuggerAttached) "Terpasang" else "Tidak"}" +
                            if (posture.advisoryWarnings.isNotEmpty()) "\nCatatan: ${posture.advisoryWarnings.joinToString(" • ")}" else "",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showSecurityDialog = false }) { Text("Tutup") }
                },
            )
        }
    }

    if (showOssLicenses) {
        AlertDialog(
            onDismissRequest = { showOssLicenses = false },
            title = { Text("Lisensi Open-Source & Atribusi") },
            text = {
                Text(
                    text = "Seluruh 30 modul, 15 paket tryout penuh (1.725 butir READY), kutipan OER Bank Materi PDF, dan 390 audio WAV IMA-ADPCM adalah konten belajar orisinal atau OER CC-BY/Public Domain. Skor mentah & umpan balik AI lokal bukan nilai resmi.\n\n" +
                        "• AndroidX Jetpack Compose, Material 3, Room, DataStore, WorkManager, Navigation, Hilt (Apache-2.0)\n" +
                        "• AndroidX Media3 ExoPlayer 1.5.1 (Apache-2.0)\n" +
                        "• Kotlin Standard Library, Coroutines, Serialization (Apache-2.0)\n" +
                        "• Google LiteRT-LM Android 0.13.1 (Apache-2.0)\n" +
                        "• Microsoft ONNX Runtime Android 1.22.0 (MIT)\n" +
                        "• JUnit 4 (EPL-1.0), Robolectric (MIT), Turbine (Apache-2.0)\n\n" +
                        "TOEFL, TOEFL ITP, dan TOEFL iBT adalah merek dagang ETS. IELTS adalah merek dagang Cambridge Assessment English / British Council / IDP. Aplikasi ini independen dan tidak berafiliasi dengan pemilik merek tersebut.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                )
            },
            confirmButton = {
                TextButton(onClick = { showOssLicenses = false }) { Text("Tutup") }
            },
        )
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
