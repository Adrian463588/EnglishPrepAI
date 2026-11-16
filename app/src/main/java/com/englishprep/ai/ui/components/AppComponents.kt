package com.englishprep.ai.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.englishprep.ai.domain.models.ContentPackSummary
import com.englishprep.ai.domain.models.CourseModuleItem
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.PackStatus
import com.englishprep.ai.ui.theme.AppSpacing
import com.englishprep.ai.ui.theme.LocalSemanticStatusColors
import com.englishprep.ai.ui.theme.MotionTokens

@Composable
fun rememberIsExpandedWidth(): Boolean = LocalConfiguration.current.screenWidthDp >= 840

@Composable
fun ResponsiveScreenContainer(
    modifier: Modifier = Modifier,
    maxWidth: Dp = 760.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = maxWidth),
            content = content,
        )
    }
}

fun TextStyle.tabular(): TextStyle = copy(fontFeatureSettings = "tnum")

@Composable
fun SectionHeading(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.semantics { heading() },
    )
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    outlined: Boolean = false,
    hapticOnTap: Boolean = false,
) {
    val statusColors = LocalSemanticStatusColors.current
    val reduceMotion = statusColors.reduceMotion
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (!reduceMotion && pressed && enabled) 0.98f else 1f,
        animationSpec = tween(durationMillis = MotionTokens.PressFeedbackMs),
        label = "appButtonScale",
    )

    val triggerClick = {
        if (hapticOnTap && statusColors.hapticsEnabled) {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        onClick()
    }

    if (outlined) {
        OutlinedButton(
            onClick = triggerClick,
            enabled = enabled,
            interactionSource = interactionSource,
            shape = MaterialTheme.shapes.small,
            modifier = modifier
                .heightIn(min = AppSpacing.minTouchTarget)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
            )
        }
    } else {
        Button(
            onClick = triggerClick,
            enabled = enabled,
            interactionSource = interactionSource,
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            modifier = modifier
                .heightIn(min = AppSpacing.minTouchTarget)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun ExamSelector(
    activeExam: ExamId,
    onOpenSwitcher: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
        modifier = modifier
            .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
            .clickable(onClick = onOpenSwitcher),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm + 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            Text(
                text = activeExam.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Ganti jenis ujian",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ModuleRow(
    module: CourseModuleItem,
    masteryPercent: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
            .clickable(onClick = onClick)
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm + 4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = module.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                val shortSection = if (module.section.name == "STRUCTURE") "Structure" else module.section.label
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                ) {
                    Text(
                        text = "$shortSection • ${module.difficulty} • ${module.estimatedMinutes} mnt",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (masteryPercent != null && masteryPercent > 0) {
                        Text(
                            text = "• $masteryPercent%",
                            style = MaterialTheme.typography.labelMedium.tabular(),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(AppSpacing.sm))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PackageRow(
    pack: ContentPackSummary,
    onPracticeClick: () -> Unit,
    onStrictClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSample = pack.status != PackStatus.READY
    val audioMb = pack.audioBytes / (1024f * 1024f)
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = if (isSample) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text = if (isSample) {
                            "Sample • ${pack.availableCount}/${pack.blueprintCount} soal"
                        } else {
                            "Ready • ${pack.availableCount} soal"
                        },
                        style = MaterialTheme.typography.labelMedium.tabular(),
                        color = if (isSample) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = AppSpacing.sm, vertical = 2.dp),
                    )
                }
                Text(
                    text = "${pack.durationMinutes} mnt • ${"%.1f".format(audioMb)} MB audio",
                    style = MaterialTheme.typography.labelMedium.tabular(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Text(
                text = pack.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(
                text = pack.theme,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(AppSpacing.md))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                AppButton(
                    text = "Mode Latihan",
                    onClick = onPracticeClick,
                )
                AppButton(
                    text = if (isSample) "Tryout Strict (Sample)" else "Full Mock Strict",
                    onClick = onStrictClick,
                    outlined = true,
                )
            }
        }
    }
}

@Composable
fun QuestionCard(
    headerLabel: String?,
    prompt: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (!headerLabel.isNullOrBlank()) {
            Text(
                text = headerLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(AppSpacing.xs))
        }
        Text(
            text = prompt,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(AppSpacing.md))
        content()
    }
}

@Composable
fun AudioPlayerBar(
    title: String,
    isPlaying: Boolean,
    positionMs: Long,
    durationMs: Long,
    speed: Float,
    allowSpeedControl: Boolean,
    allowSeek: Boolean,
    replaysUsed: Int,
    maxReplays: Int?,
    errorMessage: String?,
    onTogglePlay: () -> Unit,
    onReplay: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onChangeSpeed: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val replayLocked = maxReplays != null && replaysUsed >= maxReplays
    val displayTitle = remember(title) {
        title.replace(Regex("\\s*\\([A-Z0-9-]+\\)$"), "").ifBlank { "Audio Soal" }
    }
    var speedMenuOpen by remember { mutableStateOf(false) }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                IconButton(
                    onClick = onTogglePlay,
                    enabled = !replayLocked || isPlaying,
                    modifier = Modifier.size(AppSpacing.minTouchTarget),
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Jeda audio" else "Putar audio",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(modifier = Modifier.width(AppSpacing.xs))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val durLabel = formatTimeMs(durationMs)
                    val posLabel = formatTimeMs(positionMs)
                    val replayInfo = if (maxReplays != null) " • Putar $replaysUsed/$maxReplays" else ""
                    Text(
                        text = "$posLabel / $durLabel$replayInfo",
                        style = MaterialTheme.typography.labelMedium.tabular(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (allowSpeedControl) {
                    Box {
                        TextButton(
                            onClick = { speedMenuOpen = true },
                            modifier = Modifier.defaultMinSize(minWidth = AppSpacing.minTouchTarget, minHeight = AppSpacing.minTouchTarget),
                        ) {
                            Text(
                                text = "${speed}x",
                                style = MaterialTheme.typography.labelLarge.tabular(),
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Atur kecepatan audio",
                            )
                        }
                        DropdownMenu(
                            expanded = speedMenuOpen,
                            onDismissRequest = { speedMenuOpen = false },
                        ) {
                            listOf(0.85f, 1.0f, 1.25f).forEach { targetSpeed ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${targetSpeed}x",
                                            fontWeight = if (kotlin.math.abs(speed - targetSpeed) < 0.05f) FontWeight.Bold else FontWeight.Normal,
                                        )
                                    },
                                    onClick = {
                                        speedMenuOpen = false
                                        onChangeSpeed(targetSpeed)
                                    },
                                )
                            }
                        }
                    }
                }
                IconButton(
                    onClick = onReplay,
                    enabled = !replayLocked,
                    modifier = Modifier.size(AppSpacing.minTouchTarget),
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = "Ulangi audio",
                    )
                }
            }

            if (durationMs > 0L) {
                if (allowSeek) {
                    Slider(
                        value = positionMs.toFloat().coerceIn(0f, durationMs.toFloat()),
                        onValueChange = { onSeekTo(it.toLong()) },
                        valueRange = 0f..durationMs.toFloat(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    LinearProgressIndicator(
                        progress = { (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppSpacing.xs),
                    )
                }
            }

            if (replayLocked) {
                Text(
                    text = "Batas putar ulang mode Strict tercapai ($replaysUsed/$maxReplays).",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
fun ProgressSummary(
    title: String,
    accuracyPercent: Int?,
    completedSessions: Int,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                if (accuracyPercent != null) {
                    Text(
                        text = "$accuracyPercent%",
                        style = MaterialTheme.typography.titleLarge.tabular(),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(
                text = if (accuracyPercent != null) {
                    "Akurasi rata-rata • $completedSessions sesi selesai"
                } else {
                    "Belum ada riwayat sesi selesai"
                },
                style = MaterialTheme.typography.bodyMedium.tabular(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (accuracyPercent != null) {
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                LinearProgressIndicator(
                    progress = { (accuracyPercent / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun PreferenceRow(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
            .clickable(onClick = onClick)
            .padding(vertical = AppSpacing.sm + 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(modifier = Modifier.width(AppSpacing.sm))
        if (trailing != null) {
            trailing()
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun InlineFeedback(
    visible: Boolean,
    isSuccess: Boolean,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val statusColors = LocalSemanticStatusColors.current
    val bgColor by animateColorAsState(
        targetValue = if (isSuccess) statusColors.successContainer else MaterialTheme.colorScheme.errorContainer,
        animationSpec = tween(durationMillis = if (statusColors.reduceMotion) 0 else MotionTokens.AnswerCheckMs),
        label = "feedbackBg",
    )
    val fgColor = if (isSuccess) statusColors.success else MaterialTheme.colorScheme.error

    AnimatedVisibility(
        visible = visible,
        enter = if (statusColors.reduceMotion) fadeIn(tween(0)) else fadeIn(tween(MotionTokens.AnswerCheckMs)),
        exit = if (statusColors.reduceMotion) fadeOut(tween(0)) else fadeOut(tween(MotionTokens.PressFeedbackMs)),
        modifier = modifier,
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = bgColor,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(AppSpacing.md)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = fgColor,
                    )
                    Spacer(modifier = Modifier.width(AppSpacing.sm))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = fgColor,
                    )
                }
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (actionLabel != null && onAction != null) {
                    Spacer(modifier = Modifier.height(AppSpacing.sm))
                    TextButton(
                        onClick = onAction,
                        modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                    ) {
                        Text(text = actionLabel)
                    }
                }
            }
        }
    }
}

@Composable
fun OfflineModelState(
    modelLabel: String,
    isReady: Boolean,
    detail: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val statusColors = LocalSemanticStatusColors.current
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = if (isReady) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (isReady) statusColors.success else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.width(AppSpacing.sm))
                Column {
                    Text(
                        text = modelLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = detail,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (actionLabel != null && onAction != null) {
                Spacer(modifier = Modifier.width(AppSpacing.sm))
                TextButton(
                    onClick = onAction,
                    modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                ) {
                    Text(text = actionLabel)
                }
            }
        }
    }
}

@Composable
fun SelectableOptionRow(
    selected: Boolean,
    label: String,
    role: Role,
    onClick: () -> Unit,
    leading: @Composable () -> Unit,
) {
    val statusColors = LocalSemanticStatusColors.current
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val animMs = if (statusColors.reduceMotion) 0 else MotionTokens.PressFeedbackMs
    val scale by animateFloatAsState(
        targetValue = if (!statusColors.reduceMotion && pressed) 0.985f else 1f,
        animationSpec = tween(animMs),
        label = "optionScale",
    )
    val bgColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(animMs),
        label = "optionBg",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
        },
        animationSpec = tween(animMs),
        label = "optionBorder",
    )

    Surface(
        shape = MaterialTheme.shapes.small,
        color = bgColor,
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.xs)
            .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = role,
            ) {
                if (statusColors.hapticsEnabled) {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                onClick()
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs),
        ) {
            leading()
            Spacer(modifier = Modifier.width(AppSpacing.sm))
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

fun formatTimeMs(ms: Long): String {
    val totalSec = (ms / 1000L).coerceAtLeast(0L)
    val min = totalSec / 60L
    val sec = totalSec % 60L
    return "%02d:%02d".format(min, sec)
}
