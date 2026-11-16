package com.englishprep.ai.ui.feature.learn

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.ExamSection
import com.englishprep.ai.domain.models.MaterialBankEntry
import com.englishprep.ai.domain.models.TaskType
import com.englishprep.ai.ui.components.AppButton
import com.englishprep.ai.ui.components.AudioPlayerBar
import com.englishprep.ai.ui.components.InlineFeedback
import com.englishprep.ai.ui.components.ModuleRow
import com.englishprep.ai.ui.components.QuestionCard
import com.englishprep.ai.ui.components.ResponsiveScreenContainer
import com.englishprep.ai.ui.components.SectionHeading
import com.englishprep.ai.ui.components.SelectableOptionRow
import com.englishprep.ai.ui.theme.AppSpacing
import com.englishprep.ai.ui.theme.LocalSemanticStatusColors
import com.englishprep.ai.ui.theme.MotionTokens

@Composable
fun LearnRoute(
    onOpenModule: (String) -> Unit,
    onAskTutor: (String) -> Unit = {},
    viewModel: LearnViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LearnScreen(
        state = state,
        onSelectSection = viewModel::selectSection,
        onToggleBookmarked = viewModel::toggleBookmarkFilter,
        onToggleMaterialExpanded = viewModel::toggleMaterialExpanded,
        onOpenModule = onOpenModule,
        onAskTutor = onAskTutor,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LearnScreen(
    state: LearnUiState,
    onSelectSection: (ExamSection?) -> Unit,
    onOpenModule: (String) -> Unit,
    onToggleBookmarked: () -> Unit = {},
    onToggleMaterialExpanded: (String) -> Unit = {},
    onAskTutor: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val availableSections = when (state.selectedExam) {
        ExamId.ITP_L1 -> listOf(ExamSection.LISTENING, ExamSection.STRUCTURE, ExamSection.READING)
        ExamId.IBT_2026, ExamId.IELTS_ACADEMIC -> listOf(
            ExamSection.LISTENING,
            ExamSection.READING,
            ExamSection.WRITING,
            ExamSection.SPEAKING,
        )
    }

    ResponsiveScreenContainer(maxWidth = 760.dp, modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = AppSpacing.md),
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = AppSpacing.md)) {
                    Text(
                        text = "Kurikulum ${state.selectedExam.title}",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.semantics { heading() },
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.xs))
                    Text(
                        text = if (state.selectedExam == ExamId.ITP_L1) {
                            "TOEFL ITP Level 1 berfokus pada Listening, Structure/Written Expression, dan Reading (Speaking/Writing tidak diujikan pada tes inti)."
                        } else {
                            "Setiap modul berisi teori ringkas, contoh kerja, jebakan umum, dan latihan interaktif."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.md))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                    ) {
                        FilterChip(
                            selected = !state.bookmarkedOnly && state.selectedSection == null,
                            onClick = { onSelectSection(null) },
                            label = { Text("Semua") },
                            modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                        )
                        availableSections.forEach { sec ->
                            FilterChip(
                                selected = !state.bookmarkedOnly && state.selectedSection == sec,
                                onClick = { onSelectSection(sec) },
                                label = { Text(sec.label) },
                                modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                            )
                        }
                        FilterChip(
                            selected = state.bookmarkedOnly,
                            onClick = onToggleBookmarked,
                            label = { Text("Tersimpan (${state.bookmarkedIds.size})") },
                            modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                        )
                    }
                    state.lastReadModuleId?.let { lastId ->
                        Spacer(modifier = Modifier.height(AppSpacing.xs))
                        Text(
                            text = "Terakhir dibaca: $lastId",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Spacer(modifier = Modifier.height(AppSpacing.sm))
                }
            }

            if (state.modules.isEmpty()) {
                item {
                    Text(
                        text = if (state.bookmarkedOnly) {
                            "Belum ada modul yang ditandai. Ketuk ikon Bookmark di dalam modul untuk menyimpannya."
                        } else {
                            "Tidak ada modul pada kategori ini."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(AppSpacing.md),
                    )
                }
            }

            items(state.modules, key = { it.id }) { mod ->
                ModuleRow(
                    module = mod,
                    masteryPercent = state.masteryByModuleId[mod.id],
                    onClick = { onOpenModule(mod.id) },
                )
            }

            if (!state.bookmarkedOnly && state.materialBankEntries.isNotEmpty()) {
                item(key = "material_bank_header") {
                    Column(modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.md)) {
                        SectionHeading(text = "Bank Materi OER & Panduan PDF (${state.materialBankEntries.size})")
                        Spacer(modifier = Modifier.height(AppSpacing.xs))
                        Text(
                            text = "Kutipan terverifikasi dari repositori OER terbuka (CC BY/Public Domain) dan spesifikasi resmi publik untuk bahan baca offline & AI Tutor.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                items(state.materialBankEntries, key = { "mb_${it.id}" }) { entry ->
                    MaterialBankRow(
                        entry = entry,
                        expanded = state.expandedMaterialId == entry.id,
                        onToggleExpand = { onToggleMaterialExpanded(entry.id) },
                        onAskTutor = { onAskTutor(entry.id) },
                        modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.xs),
                    )
                }
            }
        }
    }
}

@Composable
private fun MaterialBankRow(
    entry: MaterialBankEntry,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onAskTutor: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val reduceMotion = LocalSemanticStatusColors.current.reduceMotion
    val animMs = if (reduceMotion) 0 else MotionTokens.AnswerCheckMs
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(animMs),
        label = "materialChevron",
    )

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
                    .clickable(onClick = onToggleExpand),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${entry.skill} • ${entry.license} • Hal. ${entry.pageRange}",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (entry.redistributable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.xs))
                    Text(
                        text = entry.chapterTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "${entry.sourceTitle} (${entry.authorOrOrg})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.width(AppSpacing.sm))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (expanded) "Tutup" else "Baca",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.graphicsLayer { rotationZ = chevronRotation },
                    )
                }
            }
            AnimatedVisibility(
                visible = expanded,
                enter = if (reduceMotion) fadeIn(tween(0)) else fadeIn(tween(animMs)) + expandVertically(tween(animMs)),
                exit = if (reduceMotion) fadeOut(tween(0)) else fadeOut(tween(animMs / 2)) + shrinkVertically(tween(animMs)),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(AppSpacing.sm))
                    Text(
                        text = entry.excerpt,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.sm))
                    TextButton(
                        onClick = onAskTutor,
                        modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                        Spacer(modifier = Modifier.width(AppSpacing.xs))
                        Text("Tanya AI tentang materi ini")
                    }
                }
            }
        }
    }
}

@Composable
fun ModuleDetailRoute(
    onBack: () -> Unit,
    onAskTutor: (String) -> Unit,
    viewModel: ModuleDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ModuleDetailScreen(
        state = state,
        onBack = onBack,
        onToggleBookmark = viewModel::toggleBookmark,
        onAskTutor = { state.module?.id?.let(onAskTutor) },
        onAnswerDrill = viewModel::answerDrill,
        onCheckDrill = viewModel::checkDrill,
        onToggleAudio = viewModel::toggleAudio,
        onReplayAudio = viewModel::replayAudio,
        onSeekAudio = viewModel::seekAudio,
        onSetSpeed = viewModel::setAudioSpeed,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ModuleDetailScreen(
    state: ModuleDetailUiState,
    onBack: () -> Unit,
    onAskTutor: () -> Unit,
    onAnswerDrill: (String, String) -> Unit,
    onCheckDrill: (String, String, List<String>) -> Unit,
    onToggleAudio: () -> Unit,
    onReplayAudio: () -> Unit,
    onSeekAudio: (Long) -> Unit,
    onSetSpeed: (Float) -> Unit,
    onToggleBookmark: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.module?.id ?: "Materi",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.defaultMinSize(minWidth = AppSpacing.minTouchTarget, minHeight = AppSpacing.minTouchTarget),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.defaultMinSize(minWidth = AppSpacing.minTouchTarget, minHeight = AppSpacing.minTouchTarget),
                    ) {
                        Icon(
                            imageVector = if (state.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (state.isBookmarked) "Hapus bookmark modul" else "Simpan bookmark modul",
                            tint = if (state.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    TextButton(
                        onClick = onAskTutor,
                        modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                        Spacer(modifier = Modifier.width(AppSpacing.xs))
                        Text("Tanya AI")
                    }
                },
            )
        },
        modifier = modifier,
    ) { padding ->
        val mod = state.module
        if (state.isLoading || mod == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                if (state.isLoading) CircularProgressIndicator() else Text("Modul tidak ditemukan.")
            }
            return@Scaffold
        }

        ResponsiveScreenContainer(maxWidth = 760.dp, modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(AppSpacing.md),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
            ) {
            item {
                Text(
                    text = mod.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(
                    text = "${mod.section.label} • ${mod.difficulty} • ${mod.estimatedMinutes}m • v1.0 • Lisensi: ${mod.license} (${mod.provenance})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            item {
                LessonBlock(title = "Tujuan Pembelajaran", body = mod.objective)
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                LessonBlock(title = "Konsep & Aturan Inti", body = mod.theory)
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                LessonBlock(title = "Contoh Kerja", body = mod.workedExample)
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                LessonBlock(title = "Kesalahan Umum", body = mod.commonMistake)
            }

            if (mod.audioAssetPath != null) {
                item {
                    SectionHeading(text = "Contoh Audio Lokal")
                    Spacer(modifier = Modifier.height(AppSpacing.sm))
                    AudioPlayerBar(
                        title = state.audioState.currentTitle.ifBlank { "Contoh Dialog" },
                        isPlaying = state.audioState.isPlaying,
                        positionMs = state.audioState.positionMs,
                        durationMs = state.audioState.durationMs,
                        speed = state.audioState.speed,
                        allowSpeedControl = true,
                        allowSeek = true,
                        replaysUsed = state.audioState.replaysUsed,
                        maxReplays = null,
                        errorMessage = state.audioState.errorMessage,
                        onTogglePlay = onToggleAudio,
                        onReplay = onReplayAudio,
                        onSeekTo = onSeekAudio,
                        onChangeSpeed = onSetSpeed,
                    )
                    mod.audioScript?.let { script ->
                        Spacer(modifier = Modifier.height(AppSpacing.xs))
                        Text(
                            text = "Skrip: $script",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            item {
                SectionHeading(text = "Latihan Singkat (${mod.drills.size} Soal)")
            }

            items(mod.drills, key = { it.id }) { drill ->
                val userAnswer = state.drillAnswers[drill.id].orEmpty()
                val checked = state.drillChecked[drill.id]
                QuestionCard(
                    headerLabel = "${drill.taskType.name} • ${drill.id}",
                    prompt = drill.prompt,
                ) {
                    if (drill.taskType == TaskType.FILL_BLANK) {
                        OutlinedTextField(
                            value = userAnswer,
                            onValueChange = { onAnswerDrill(drill.id, it) },
                            label = { Text("Jawaban Anda") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else if (drill.taskType == TaskType.SENTENCE_BUILD) {
                        val selectedWords = if (userAnswer.isBlank()) emptyList() else userAnswer.split("|")
                        val remainingWords = drill.options.filter { !selectedWords.contains(it) }
                        Text(
                            text = if (selectedWords.isEmpty()) "Ketuk kata berurutan:" else selectedWords.joinToString(" "),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.sm))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                        ) {
                            remainingWords.forEach { word ->
                                FilterChip(
                                    selected = false,
                                    onClick = {
                                        val next = (selectedWords + word).joinToString("|")
                                        onAnswerDrill(drill.id, next)
                                    },
                                    label = { Text(word) },
                                    modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                                )
                            }
                            if (selectedWords.isNotEmpty()) {
                                TextButton(
                                    onClick = { onAnswerDrill(drill.id, "") },
                                    modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                                ) {
                                    Text("Reset")
                                }
                            }
                        }
                    } else {
                        drill.options.forEach { option ->
                            val isSelected = userAnswer == option
                            SelectableOptionRow(
                                selected = isSelected,
                                label = option,
                                role = Role.RadioButton,
                                onClick = { onAnswerDrill(drill.id, option) },
                            ) {
                                RadioButton(selected = isSelected, onClick = null)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(AppSpacing.sm))
                    AppButton(
                        text = "Periksa Jawaban",
                        enabled = userAnswer.isNotBlank(),
                        onClick = { onCheckDrill(drill.id, drill.answer, drill.acceptedAnswers) },
                    )
                    if (checked != null) {
                        Spacer(modifier = Modifier.height(AppSpacing.sm))
                        InlineFeedback(
                            visible = true,
                            isSuccess = checked,
                            title = if (checked) "Tepat" else "Belum tepat (Kunci: ${drill.answer.replace("|", " ")})",
                            body = drill.explanation,
                        )
                    }
                }
            }

            state.drillSavedMessage?.let { msg ->
                item {
                    InlineFeedback(
                        visible = true,
                        isSuccess = true,
                        title = "Latihan Modul Selesai",
                        body = msg,
                    )
                }
            }
        }
        }
    }
}

@Composable
private fun LessonBlock(title: String, body: String) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics { heading() },
            )
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
