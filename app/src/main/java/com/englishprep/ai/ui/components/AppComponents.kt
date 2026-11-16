package com.englishprep.ai.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.englishprep.ai.domain.models.ContentPackSummary
import com.englishprep.ai.domain.models.CourseModuleItem
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.models.PackStatus
import com.englishprep.ai.ui.theme.AppSpacing
import com.englishprep.ai.ui.theme.LocalSemanticStatusColors
import com.englishprep.ai.ui.theme.MotionTokens

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
            Text(text = text, style = MaterialTheme.typography.labelLarge)
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
            Text(text = text, style = MaterialTheme.typography.labelLarge)
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
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AppSpacing.minTouchTarget)
            .clickable(onClick = onOpenSwitcher),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm + 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activeExam.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = activeExam.scaleLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.width(AppSpacing.sm))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Ganti jenis ujian",
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(AppSpacing.xs))
                Text(
                    text = "Ganti",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
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
                )
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                ) {
                    Text(
                        text = module.section.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "${module.difficulty} • ${module.estimatedMinutes}m",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (masteryPercent != null) {
                        Text(
                            text = "Penguasaan $masteryPercent%",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
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
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSample) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs),
                    )
                }
                Text(
                    text = "${pack.durationMinutes}m latihan • ${"%.1f".format(audioMb)} MB audio",
                    style = MaterialTheme.typography.labelMedium,
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
    headerLabel: String,
    prompt: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Text(
                text = headerLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Text(
                text = prompt,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(AppSpacing.md))
            content()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
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
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
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
                    Spacer(modifier = Modifier.width(AppSpacing.sm))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        val durLabel = formatTimeMs(durationMs)
                        val posLabel = formatTimeMs(positionMs)
                        val replayInfo = if (maxReplays != null) " • Putar $replaysUsed/$maxReplays" else ""
                        Text(
                            text = "$posLabel / $durLabel$replayInfo",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
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
                    )
                } else {
                    LinearProgressIndicator(
                        progress = { (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppSpacing.sm),
                    )
                }
            }

            if (allowSpeedControl) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                ) {
                    listOf(0.85f, 1.0f, 1.25f).forEach { targetSpeed ->
                        val selected = kotlin.math.abs(speed - targetSpeed) < 0.05f
                        TextButton(
                            onClick = { onChangeSpeed(targetSpeed) },
                            modifier = Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget),
                        ) {
                            Text(
                                text = "${targetSpeed}x",
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
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
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(AppSpacing.md)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(
                text = if (accuracyPercent != null) {
                    "Akurasi rata-rata $accuracyPercent% • $completedSessions sesi selesai"
                } else {
                    "Belum ada sesi selesai • Mulai latihan singkat untuk memetakan kekuatan"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (accuracyPercent != null) {
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                LinearProgressIndicator(
                    progress = { (accuracyPercent / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
    val animMs = if (statusColors.reduceMotion) 0 else MotionTokens.PressFeedbackMs
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
            .clickable(role = role) {
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
