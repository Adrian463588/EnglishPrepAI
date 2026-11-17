package com.englishprep.ai.ui.feature.exam

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.englishprep.ai.domain.engines.SttModelState
import com.englishprep.ai.domain.grading.QuizGradingEngine
import com.englishprep.ai.domain.models.ExamMode
import com.englishprep.ai.domain.models.ExamSection
import com.englishprep.ai.domain.models.GradingOutcome
import com.englishprep.ai.domain.models.PassageItem
import com.englishprep.ai.domain.models.QuestionItem
import com.englishprep.ai.domain.models.TaskType
import com.englishprep.ai.ui.components.AppButton
import com.englishprep.ai.ui.components.AudioPlayerBar
import com.englishprep.ai.ui.components.InlineFeedback
import com.englishprep.ai.ui.components.QuestionCard
import com.englishprep.ai.ui.components.ResponsiveScreenContainer
import com.englishprep.ai.ui.components.SectionHeading
import com.englishprep.ai.ui.components.SelectableOptionRow
import com.englishprep.ai.ui.components.formatTimeMs
import com.englishprep.ai.ui.components.tabular
import com.englishprep.ai.ui.theme.AppSpacing
import com.englishprep.ai.ui.theme.LocalSemanticStatusColors
import com.englishprep.ai.ui.theme.MotionTokens

@Composable
fun ExamRoute(
    onBack: () -> Unit,
    onCompleted: (String) -> Unit,
    viewModel: ExamViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ExamScreen(
        state = state,
        onBack = onBack,
        onSelectAnswer = viewModel::selectAnswer,
        onToggleMulti = viewModel::toggleMultiChoice,
        onSetMatching = viewModel::setMatchingPair,
        onAppendSequence = viewModel::appendSequenceItem,
        onRemoveLastSequence = viewModel::removeLastSequenceItem,
        onToggleFlag = viewModel::toggleFlag,
        onCheckPractice = viewModel::checkCurrentInPractice,
        onEvaluateSubjective = viewModel::evaluateSubjectiveDraft,
        onStartPrepCountdown = viewModel::startPrepCountdown,
        onStartRecording = viewModel::startSpeakingRecording,
        onStopRecording = viewModel::stopSpeakingRecordingAndTranscribe,
        onPlayOwnRecording = viewModel::playOwnRecording,
        onDeleteRecording = viewModel::deleteSpeakingRecording,
        onSpeakTts = viewModel::speakPromptViaTts,
        onNext = viewModel::nextQuestion,
        onPrev = viewModel::prevQuestion,
        onGoTo = viewModel::goToQuestion,
        onSetNavigator = viewModel::setNavigatorVisible,
        onSetPassageSheet = viewModel::setPassageSheetVisible,
        onToggleAudio = viewModel::toggleAudio,
        onReplayAudio = viewModel::replayAudio,
        onSeekAudio = viewModel::seekAudio,
        onSetAudioSpeed = viewModel::setAudioSpeed,
        onSubmit = { viewModel.submitExam(onCompleted) },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExamScreen(
    state: ExamUiState,
    onBack: () -> Unit,
    onSelectAnswer: (String, String) -> Unit,
    onToggleMulti: (String, String) -> Unit,
    onSetMatching: (String, String, String) -> Unit,
    onAppendSequence: (String, String) -> Unit,
    onRemoveLastSequence: (String) -> Unit,
    onToggleFlag: (String) -> Unit,
    onCheckPractice: () -> Unit,
    onEvaluateSubjective: () -> Unit,
    onStartPrepCountdown: (Int) -> Unit,
    onStartRecording: () -> Boolean,
    onStopRecording: () -> Unit,
    onPlayOwnRecording: (String) -> Unit,
    onDeleteRecording: (String) -> Unit = {},
    onSpeakTts: (String) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onGoTo: (Int) -> Unit,
    onSetNavigator: (Boolean) -> Unit,
    onSetPassageSheet: (Boolean) -> Unit,
    onToggleAudio: () -> Unit,
    onReplayAudio: () -> Unit,
    onSeekAudio: (Long) -> Unit,
    onSetAudioSpeed: (Float) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var showQuitDialog by remember { mutableStateOf(false) }
    var showSubmitConfirmDialog by remember { mutableStateOf(false) }
    var micDeniedNote by remember { mutableStateOf(false) }
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            micDeniedNote = false
            onStartRecording()
        } else {
            micDeniedNote = true
        }
    }

    val config = LocalConfiguration.current
    val isExpanded = config.screenWidthDp >= 840
    val compactWrapBottomBar = LocalDensity.current.fontScale >= 1.5f && config.screenWidthDp < 400
    val q = state.currentQuestion

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            val countLabel = if (state.questions.isNotEmpty()) {
                                "Soal ${state.currentIndex + 1} dari ${state.questions.size}"
                            } else {
                                "Ujian"
                            }
                            val timerText = when {
                                state.remainingMs != null -> formatTimeMs(state.remainingMs)
                                else -> formatTimeMs(state.elapsedMs)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = countLabel,
                                    style = MaterialTheme.typography.titleMedium.tabular(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f),
                                )
                                Spacer(modifier = Modifier.width(AppSpacing.sm))
                                Text(
                                    text = timerText,
                                    style = MaterialTheme.typography.titleMedium.tabular(),
                                    color = if (state.remainingMs != null) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                )
                            }
                            val modePrefix = if (state.mode == ExamMode.STRICT) "Simulasi Strict" else "Mode Latihan"
                            val statusLine = if (state.savedBadgeText.isNotEmpty()) {
                                "$modePrefix • ${state.savedBadgeText}"
                            } else {
                                modePrefix
                            }
                            Text(
                                text = statusLine,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { showQuitDialog = true },
                            modifier = Modifier.defaultMinSize(minWidth = AppSpacing.minTouchTarget, minHeight = AppSpacing.minTouchTarget),
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Keluar ujian")
                        }
                    },
                    actions = {
                        if (q != null) {
                            val isFlagged = q.id in state.flaggedIds
                            IconButton(
                                onClick = { onToggleFlag(q.id) },
                                modifier = Modifier.defaultMinSize(minWidth = AppSpacing.minTouchTarget, minHeight = AppSpacing.minTouchTarget),
                            ) {
                                Icon(
                                    imageVector = if (isFlagged) Icons.Default.Flag else Icons.Default.OutlinedFlag,
                                    contentDescription = if (isFlagged) "Hapus tanda ragu" else "Tandai soal",
                                    tint = if (isFlagged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        IconButton(
                            onClick = { onSetNavigator(true) },
                            modifier = Modifier.defaultMinSize(minWidth = AppSpacing.minTouchTarget, minHeight = AppSpacing.minTouchTarget),
                        ) {
                            Icon(Icons.Default.GridView, contentDescription = "Daftar nomor soal")
                        }
                    },
                )
                if (!state.isLoading && state.questions.isNotEmpty()) {
                    LinearProgressIndicator(
                        progress = { ((state.currentIndex + 1f) / state.questions.size.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                    )
                }
            }
        },
        bottomBar = {
            if (q != null) {
                Surface(
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding(),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        HorizontalDivider()
                        val showCheck = state.mode == ExamMode.PRACTICE &&
                            q.taskType !in listOf(TaskType.WRITING_RESPONSE, TaskType.SPEAKING_RESPONSE)
                        val isLast = state.currentIndex >= state.questions.lastIndex
                        val backButton: @Composable () -> Unit = {
                            OutlinedButton(
                                onClick = onPrev,
                                enabled = state.currentIndex > 0,
                                modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                            ) {
                                Text("Kembali", maxLines = 1)
                            }
                        }
                        val checkButton: @Composable () -> Unit = {
                            OutlinedButton(
                                onClick = onCheckPractice,
                                enabled = state.answers[q.id]?.isNotBlank() == true,
                                modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                            ) {
                                Text("Periksa", maxLines = 1)
                            }
                        }
                        val nextOrSubmitButton: @Composable () -> Unit = {
                            AppButton(
                                text = if (isLast) "Selesai" else "Lanjut",
                                hapticOnTap = isLast,
                                onClick = {
                                    if (isLast) showSubmitConfirmDialog = true else onNext()
                                },
                            )
                        }

                        if (compactWrapBottomBar) {
                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .widthIn(max = 840.dp)
                                    .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                            ) {
                                backButton()
                                if (showCheck) checkButton()
                                nextOrSubmitButton()
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .widthIn(max = 840.dp)
                                    .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                backButton()
                                if (showCheck) checkButton()
                                Spacer(modifier = Modifier.weight(1f))
                                nextOrSubmitButton()
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier,
    ) { padding ->
        if (state.isLoading || q == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(AppSpacing.lg)) {
                        Text(text = state.errorMessage ?: "Tidak ada soal tersedia.")
                        Spacer(modifier = Modifier.height(AppSpacing.md))
                        AppButton(text = "Kembali", onClick = onBack)
                    }
                }
            }
            return@Scaffold
        }

        val reduceMotion = LocalSemanticStatusColors.current.reduceMotion
        AnimatedContent(
            targetState = state.currentIndex,
            transitionSpec = {
                if (reduceMotion) {
                    ContentTransform(EnterTransition.None, ExitTransition.None)
                } else {
                    val ms = MotionTokens.ScreenTransitionMs
                    val forward = targetState >= initialState
                    (fadeIn(tween(ms)) + slideInHorizontally(tween(ms)) { if (forward) it / 8 else -it / 8 }) togetherWith
                        (fadeOut(tween(ms / 2)) + slideOutHorizontally(tween(ms)) { if (forward) -it / 8 else it / 8 })
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            label = "examQuestionTransition",
        ) { idx ->
            val displayedQ = state.questions.getOrNull(idx) ?: q
            val displayedPassage = displayedQ.passageId?.let { state.passages[it] }
            if (isExpanded && displayedPassage != null) {
                Row(modifier = Modifier.fillMaxSize()) {
                    PassagePane(
                        passage = displayedPassage,
                        modifier = Modifier
                            .weight(0.45f)
                            .fillMaxHeight()
                            .padding(AppSpacing.md),
                    )
                    QuestionBodyPane(
                        state = state,
                        q = displayedQ,
                        currentPassage = null,
                        micDeniedNote = micDeniedNote,
                        onOpenPassageSheet = {},
                        onSelectAnswer = onSelectAnswer,
                        onToggleMulti = onToggleMulti,
                        onSetMatching = onSetMatching,
                        onAppendSequence = onAppendSequence,
                        onRemoveLastSequence = onRemoveLastSequence,
                        onEvaluateSubjective = onEvaluateSubjective,
                        onStartPrepCountdown = onStartPrepCountdown,
                        onRequestMicAndRecord = {
                            val ok = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                                PackageManager.PERMISSION_GRANTED
                            if (ok) onStartRecording() else micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        },
                        onStopRecording = onStopRecording,
                        onPlayOwnRecording = onPlayOwnRecording,
                        onDeleteRecording = onDeleteRecording,
                        onSpeakTts = onSpeakTts,
                        onToggleAudio = onToggleAudio,
                        onReplayAudio = onReplayAudio,
                        onSeekAudio = onSeekAudio,
                        onSetAudioSpeed = onSetAudioSpeed,
                        modifier = Modifier.weight(0.55f).fillMaxHeight(),
                    )
                }
            } else {
                ResponsiveScreenContainer(maxWidth = 760.dp) {
                    QuestionBodyPane(
                        state = state,
                        q = displayedQ,
                        currentPassage = displayedPassage,
                        micDeniedNote = micDeniedNote,
                        onOpenPassageSheet = { onSetPassageSheet(true) },
                        onSelectAnswer = onSelectAnswer,
                        onToggleMulti = onToggleMulti,
                        onSetMatching = onSetMatching,
                        onAppendSequence = onAppendSequence,
                        onRemoveLastSequence = onRemoveLastSequence,
                        onEvaluateSubjective = onEvaluateSubjective,
                        onStartPrepCountdown = onStartPrepCountdown,
                        onRequestMicAndRecord = {
                            val ok = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                                PackageManager.PERMISSION_GRANTED
                            if (ok) onStartRecording() else micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        },
                        onStopRecording = onStopRecording,
                        onPlayOwnRecording = onPlayOwnRecording,
                        onDeleteRecording = onDeleteRecording,
                        onSpeakTts = onSpeakTts,
                        onToggleAudio = onToggleAudio,
                        onReplayAudio = onReplayAudio,
                        onSeekAudio = onSeekAudio,
                        onSetAudioSpeed = onSetAudioSpeed,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = { Text("Keluar dari sesi?") },
            text = { Text("Jawaban yang sudah dipilih tersimpan otomatis di perangkat dan dapat dilanjutkan kembali.") },
            confirmButton = {
                TextButton(onClick = {
                    showQuitDialog = false
                    onBack()
                }) { Text("Keluar") }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) { Text("Batal") }
            },
        )
    }

    if (showSubmitConfirmDialog) {
        val answeredCount = state.questions.count { !state.answers[it.id].isNullOrBlank() || it.id in state.recordingPaths }
        val unansweredCount = (state.questions.size - answeredCount).coerceAtLeast(0)
        AlertDialog(
            onDismissRequest = { showSubmitConfirmDialog = false },
            title = { Text("Kumpulkan jawaban sekarang?") },
            text = {
                Text(
                    if (unansweredCount > 0) {
                        "Soal terjawab: $answeredCount/${state.questions.size}. Masih ada $unansweredCount soal kosong yang akan dihitung dilewati."
                    } else {
                        "Semua ${state.questions.size} soal telah dijawab. Lanjut hitung skor non-resmi?"
                    },
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showSubmitConfirmDialog = false
                    onSubmit()
                }) { Text("Kumpulkan Sekarang") }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitConfirmDialog = false }) { Text("Periksa Lagi") }
            },
        )
    }

    if (state.showNavigator) {
        val navSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            sheetState = navSheetState,
            onDismissRequest = { onSetNavigator(false) },
        ) {
            QuestionNavigatorSheetContent(
                questions = state.questions,
                answers = state.answers,
                flaggedIds = state.flaggedIds,
                currentIndex = state.currentIndex,
                onSelectQuestion = onGoTo,
            )
        }
    }

    val passageModal = state.currentPassage
    if (state.showPassageSheet && passageModal != null) {
        val passageSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            sheetState = passageSheetState,
            onDismissRequest = { onSetPassageSheet(false) },
        ) {
            PassagePane(
                passage = passageModal,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .heightIn(max = 560.dp)
                    .padding(AppSpacing.md),
            )
        }
    }
}

@Composable
private fun PassagePane(passage: PassageItem, modifier: Modifier = Modifier) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .padding(AppSpacing.md)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = passage.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { heading() },
            )
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Text(
                text = passage.body,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuestionBodyPane(
    state: ExamUiState,
    q: QuestionItem,
    currentPassage: PassageItem?,
    micDeniedNote: Boolean,
    onOpenPassageSheet: () -> Unit,
    onSelectAnswer: (String, String) -> Unit,
    onToggleMulti: (String, String) -> Unit,
    onSetMatching: (String, String, String) -> Unit,
    onAppendSequence: (String, String) -> Unit,
    onRemoveLastSequence: (String) -> Unit,
    onEvaluateSubjective: () -> Unit,
    onStartPrepCountdown: (Int) -> Unit,
    onRequestMicAndRecord: () -> Unit,
    onStopRecording: () -> Unit,
    onPlayOwnRecording: (String) -> Unit,
    onDeleteRecording: (String) -> Unit,
    onSpeakTts: (String) -> Unit,
    onToggleAudio: () -> Unit,
    onReplayAudio: () -> Unit,
    onSeekAudio: (Long) -> Unit,
    onSetAudioSpeed: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val userAnswer = state.answers[q.id].orEmpty()
    val isPractice = state.mode == ExamMode.PRACTICE
    val displayedAsset = q.audioAssetId?.let { state.assets[it] }
    var showTranscript by remember(q.id) { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        if (currentPassage != null) {
            item {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
                        .clickable(onClick = onOpenPassageSheet),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm + 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "Baca teks: ${currentPassage.title}",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "Buka",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }

        if (displayedAsset != null) {
            item {
                AudioPlayerBar(
                    title = "Audio ${q.section.label}",
                    isPlaying = state.audioState.isPlaying,
                    positionMs = state.audioState.positionMs,
                    durationMs = state.audioState.durationMs,
                    speed = state.audioState.speed,
                    allowSpeedControl = isPractice,
                    allowSeek = isPractice,
                    replaysUsed = state.audioState.replaysUsed,
                    maxReplays = state.audioState.maxReplays,
                    errorMessage = state.audioState.errorMessage,
                    onTogglePlay = onToggleAudio,
                    onReplay = onReplayAudio,
                    onSeekTo = onSeekAudio,
                    onChangeSpeed = onSetAudioSpeed,
                )
                if (isPractice && displayedAsset.caption.isNotBlank()) {
                    TextButton(
                        onClick = { showTranscript = !showTranscript },
                        modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                    ) {
                        Text(if (showTranscript) "Sembunyikan Transkrip" else "Lihat Transkrip Audio (Mode Latihan)")
                    }
                    if (showTranscript) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = displayedAsset.caption,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(AppSpacing.md),
                            )
                        }
                    }
                }
            }
        }

        item {
            val sectionHeader = if (q.section == ExamSection.STRUCTURE) "Structure" else q.section.label
            QuestionCard(
                headerLabel = sectionHeader,
                prompt = q.prompt,
            ) {
                when (q.taskType) {
                    TaskType.CHOICE_SINGLE,
                    TaskType.ERROR_IDENTIFICATION -> {
                        q.options.forEach { opt ->
                            val selected = userAnswer == opt
                            SelectableOptionRow(
                                selected = selected,
                                label = opt,
                                role = Role.RadioButton,
                                onClick = { onSelectAnswer(q.id, opt) },
                            ) {
                                RadioButton(selected = selected, onClick = null)
                            }
                        }
                    }

                    TaskType.CHOICE_MULTI -> {
                        val chosen = userAnswer.split("|").filter { it.isNotBlank() }.toSet()
                        q.options.forEach { opt ->
                            val checked = opt in chosen
                            SelectableOptionRow(
                                selected = checked,
                                label = opt,
                                role = Role.Checkbox,
                                onClick = { onToggleMulti(q.id, opt) },
                            ) {
                                Checkbox(checked = checked, onCheckedChange = null)
                            }
                        }
                    }

                    TaskType.FILL_BLANK -> {
                        OutlinedTextField(
                            value = userAnswer,
                            onValueChange = { onSelectAnswer(q.id, it) },
                            label = {
                                Text(q.maxWords?.let { "Ketik jawaban (maks. $it kata)" } ?: "Ketik jawaban Anda")
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    TaskType.MATCHING -> {
                        val currentMap = userAnswer.split("|")
                            .filter { it.contains("::") }
                            .associate {
                                val p = it.split("::", limit = 2)
                                p[0] to p.getOrElse(1) { "" }
                            }
                        q.pairs.forEach { pair ->
                            Text(
                                text = pair.left,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            pair.rightOptions.forEach { right ->
                                val selected = currentMap[pair.left] == right
                                SelectableOptionRow(
                                    selected = selected,
                                    label = right,
                                    role = Role.RadioButton,
                                    onClick = { onSetMatching(q.id, pair.left, right) },
                                ) {
                                    RadioButton(selected = selected, onClick = null)
                                }
                            }
                            Spacer(modifier = Modifier.height(AppSpacing.sm))
                        }
                    }

                    TaskType.REORDER,
                    TaskType.SENTENCE_BUILD -> {
                        val chosen = userAnswer.split("|").filter { it.isNotBlank() }
                        val remaining = q.options.filterNot { it in chosen }
                        Text(
                            text = if (chosen.isEmpty()) "Susunan Anda akan muncul di sini." else chosen.joinToString(" "),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.sm))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                        ) {
                            remaining.forEach { item ->
                                FilterChip(
                                    selected = false,
                                    onClick = { onAppendSequence(q.id, item) },
                                    label = { Text(item) },
                                    modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                                )
                            }
                        }
                        if (chosen.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(AppSpacing.sm))
                            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                                TextButton(
                                    onClick = { onRemoveLastSequence(q.id) },
                                    modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                                ) { Text("Hapus Terakhir") }
                                TextButton(
                                    onClick = { onSelectAnswer(q.id, "") },
                                    modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                                ) { Text("Reset") }
                            }
                        }
                    }

                    TaskType.WRITING_RESPONSE -> {
                        val wc = QuizGradingEngine.countWords(userAnswer)
                        val minW = q.minWords ?: 50
                        var showRubric by remember(q.id) { mutableStateOf(false) }
                        val criteria = remember(q.id) {
                            q.rubricCriteria.ifEmpty {
                                listOf("Task response", "Coherence & cohesion", "Lexical precision", "Grammatical accuracy")
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Jumlah kata: $wc / min. $minW kata • Disimpan otomatis",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (wc >= minW) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f),
                            )
                            TextButton(
                                onClick = { showRubric = !showRubric },
                                modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                            ) {
                                Text(if (showRubric) "Tutup Rubrik" else "Panduan Rubrik")
                            }
                        }
                        if (showRubric) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    text = "Kriteria penilaian lokal (non-resmi): ${criteria.joinToString(" • ")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(AppSpacing.md),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(AppSpacing.sm))
                        OutlinedTextField(
                            value = userAnswer,
                            onValueChange = { onSelectAnswer(q.id, it) },
                            label = { Text("Tulis jawaban Anda di sini") },
                            minLines = 5,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.sm))
                        AppButton(
                            text = if (state.isEvaluatingAi) "Mengevaluasi..." else "Evaluasi Draf (Non-Resmi)",
                            enabled = userAnswer.isNotBlank() && !state.isEvaluatingAi,
                            outlined = true,
                            onClick = onEvaluateSubjective,
                        )
                        state.aiFeedbackByQuestion[q.id]?.let { fb ->
                            Spacer(modifier = Modifier.height(AppSpacing.sm))
                            InlineFeedback(
                                visible = true,
                                isSuccess = true,
                                title = "Umpan Balik Pelatih Lokal (Non-Resmi)",
                                body = fb,
                            )
                        }
                    }

                    TaskType.SPEAKING_RESPONSE,
                    TaskType.LISTEN_REPEAT -> {
                        if (q.prepSeconds > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                            ) {
                                OutlinedButton(
                                    onClick = { onStartPrepCountdown(q.prepSeconds) },
                                    modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                                ) {
                                    Text(
                                        state.prepCountdownSec?.let { "Persiapan: ${it}d" }
                                            ?: "Mulai Timer Persiapan (${q.prepSeconds}d)",
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(AppSpacing.sm))
                        }
                        if (q.taskType == TaskType.LISTEN_REPEAT && q.referenceText != null) {
                            TextButton(
                                onClick = { onSpeakTts(q.referenceText) },
                                modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                            ) {
                                Text("Putar Ulang Suara Referensi (TTS)")
                            }
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                        ) {
                            if (state.recordingState.isRecording) {
                                AppButton(
                                    text = "Stop Rekam (${formatTimeMs(state.recordingState.elapsedMs)})",
                                    onClick = onStopRecording,
                                )
                            } else {
                                AppButton(
                                    text = "Rekam Jawaban (${q.speakSeconds}d)",
                                    onClick = onRequestMicAndRecord,
                                )
                            }
                            if (state.recordingPaths.containsKey(q.id)) {
                                OutlinedButton(
                                    onClick = { onPlayOwnRecording(q.id) },
                                    modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(AppSpacing.xs))
                                    Text("Putar Rekaman Saya")
                                }
                                TextButton(
                                    onClick = { onDeleteRecording(q.id) },
                                    modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                                ) {
                                    Text("Hapus Rekaman", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }

                        if (state.recordingState.isRecording) {
                            Spacer(modifier = Modifier.height(AppSpacing.sm))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(AppSpacing.sm))
                                LinearProgressIndicator(
                                    progress = { state.recordingState.amplitudeLevel.coerceIn(0.05f, 1f) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }

                        if (micDeniedNote) {
                            Spacer(modifier = Modifier.height(AppSpacing.sm))
                            InlineFeedback(
                                visible = true,
                                isSuccess = false,
                                title = "Izin Mikrofon Diperlukan",
                                body = "Berikan izin mikrofon saat diminta untuk merekam Speaking secara lokal. Anda tetap dapat mengetik catatan latihan di bawah.",
                            )
                        }

                        if (state.isTranscribing) {
                            Spacer(modifier = Modifier.height(AppSpacing.sm))
                            Text("Mentranskripsi rekaman lewat Whisper ONNX...", style = MaterialTheme.typography.labelMedium)
                        }

                        Spacer(modifier = Modifier.height(AppSpacing.sm))
                        val sttStatusNote = if (state.sttState is SttModelState.Ready) {
                            "Transkrip otomatis (Whisper ONNX — dapat diedit)"
                        } else {
                            "Catatan / transkrip manual (Whisper ONNX di Profil > Kelola Model AI)"
                        }
                        OutlinedTextField(
                            value = userAnswer,
                            onValueChange = { onSelectAnswer(q.id, it) },
                            label = { Text(sttStatusNote) },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        state.speakingMetricsByQuestion[q.id]?.let { metrics ->
                            Spacer(modifier = Modifier.height(AppSpacing.sm))
                            val overlapStr = metrics.overlapRatioWithReference?.let {
                                " • Kemiripan kalimat: ${(it * 100).toInt()}%"
                            }.orEmpty()
                            Text(
                                text = "Metrik lokal (non-resmi): ${metrics.wordCount} kata • ${metrics.wordsPerMinute} WPM • Filler: ${metrics.fillerCount}$overlapStr",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }

        val outcome = state.checkedOutcomes[q.id]
        if (outcome != null && isPractice) {
            item {
                when (outcome) {
                    is GradingOutcome.Objective -> InlineFeedback(
                        visible = true,
                        isSuccess = outcome.isCorrect,
                        title = if (outcome.isCorrect) "Benar" else "Perlu Tinjauan",
                        body = "${outcome.feedbackMessage}\n\n${q.explanation}\nSumber: [${q.rationaleSource}]",
                    )
                    is GradingOutcome.SubjectivePending -> InlineFeedback(
                        visible = true,
                        isSuccess = outcome.meetsMinimumWords,
                        title = "Catatan Subyektif",
                        body = outcome.feedbackMessage,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun QuestionNavigatorSheetContent(
    questions: List<QuestionItem>,
    answers: Map<String, String>,
    flaggedIds: Set<String>,
    currentIndex: Int,
    onSelectQuestion: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sections = remember(questions) { questions.map { it.section }.distinct() }
    var selectedSection by remember { mutableStateOf<ExamSection?>(null) }
    val indexedItems = remember(questions, selectedSection) {
        questions.mapIndexed { idx, item -> idx to item }
            .filter { (_, item) -> selectedSection == null || item.section == selectedSection }
    }
    val chunkedRows = remember(indexedItems) { indexedItems.chunked(5) }
    val answeredTotal = remember(questions, answers) {
        questions.count { !answers[it.id].isNullOrBlank() }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = AppSpacing.md),
    ) {
        SectionHeading(text = "Navigasi Soal ($answeredTotal/${questions.size} terjawab)")
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
        ) {
            NavigatorLegendItem("Aktif", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary)
            NavigatorLegendItem("Terjawab", MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.outline)
            Text(
                text = "* Ditandai ragu",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        if (sections.size > 1) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                item {
                    FilterChip(
                        selected = selectedSection == null,
                        onClick = { selectedSection = null },
                        label = { Text("Semua (${questions.size})") },
                        modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                    )
                }
                items(sections, key = { it.name }) { sec ->
                    val secCount = questions.count { it.section == sec }
                    val shortName = if (sec == ExamSection.STRUCTURE) "Structure" else sec.label
                    FilterChip(
                        selected = selectedSection == sec,
                        onClick = { selectedSection = sec },
                        label = { Text("$shortName ($secCount)") },
                        modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                    )
                }
            }
            Spacer(modifier = Modifier.height(AppSpacing.sm))
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 420.dp),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            contentPadding = PaddingValues(bottom = AppSpacing.lg),
        ) {
            items(chunkedRows, key = { row -> row.first().first }) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                ) {
                    rowItems.forEach { (idx, item) ->
                        val answered = !answers[item.id].isNullOrBlank()
                        val flagged = item.id in flaggedIds
                        val isCurrent = idx == currentIndex
                        val label = buildString {
                            append(idx + 1)
                            if (flagged) append("*")
                        }
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = when {
                                isCurrent -> MaterialTheme.colorScheme.primaryContainer
                                answered -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.surfaceContainerLow
                            },
                            border = BorderStroke(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(
                                    minWidth = AppSpacing.minTouchTarget,
                                    minHeight = AppSpacing.minTouchTarget,
                                )
                                .clickable { onSelectQuestion(idx) }
                                .semantics {
                                    contentDescription = buildString {
                                        append("Soal ${idx + 1}")
                                        if (answered) append(", terjawab")
                                        if (flagged) append(", ditandai")
                                    }
                                },
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = AppSpacing.xs, vertical = AppSpacing.xs),
                                contentAlignment = Alignment.Center,
                            ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelLarge.tabular(),
                                    color = when {
                                        isCurrent -> MaterialTheme.colorScheme.onPrimaryContainer
                                        answered -> MaterialTheme.colorScheme.onSecondaryContainer
                                        else -> MaterialTheme.colorScheme.onSurface
                                    },
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                    repeat(5 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigatorLegendItem(
    label: String,
    fill: androidx.compose.ui.graphics.Color,
    border: androidx.compose.ui.graphics.Color,
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        Surface(
            shape = MaterialTheme.shapes.extraSmall,
            color = fill,
            border = BorderStroke(1.dp, border),
            modifier = Modifier.size(12.dp),
        ) {}
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
