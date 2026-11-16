package com.englishprep.ai.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = DeepTealPrimaryLight,
    onPrimary = OnDeepTealLight,
    primaryContainer = DeepTealContainerLight,
    onPrimaryContainer = OnDeepTealContainerLight,
    secondary = SlateSecondaryLight,
    onSecondary = OnSlateSecondaryLight,
    secondaryContainer = SlateContainerLight,
    onSecondaryContainer = OnSlateContainerLight,
    tertiary = WarmAmberTertiaryLight,
    onTertiary = OnWarmAmberLight,
    tertiaryContainer = WarmAmberContainerLight,
    onTertiaryContainer = OnWarmAmberContainerLight,
    background = WarmPaperBackground,
    onBackground = InkOnSurfaceLight,
    surface = WarmPaperSurface,
    onSurface = InkOnSurfaceLight,
    surfaceVariant = WarmPaperSurfaceLow,
    onSurfaceVariant = InkMutedLight,
    surfaceContainerLow = WarmPaperSurfaceLow,
    surfaceContainerHigh = WarmPaperSurfaceHigh,
    outline = HairlineOutlineLight,
    error = SemanticErrorLight,
    onError = Color.White,
    errorContainer = SemanticErrorContainerLight,
    onErrorContainer = Color(0xFF410E0B),
)

private val DarkColors = darkColorScheme(
    primary = DeepTealPrimaryDark,
    onPrimary = OnDeepTealDark,
    primaryContainer = DeepTealContainerDark,
    onPrimaryContainer = OnDeepTealContainerDark,
    secondary = SlateSecondaryDark,
    onSecondary = OnSlateSecondaryDark,
    secondaryContainer = SlateContainerDark,
    onSecondaryContainer = OnSlateContainerDark,
    tertiary = WarmAmberTertiaryDark,
    onTertiary = OnWarmAmberDark,
    tertiaryContainer = WarmAmberContainerDark,
    onTertiaryContainer = OnWarmAmberContainerDark,
    background = SlateBackgroundDark,
    onBackground = InkOnSurfaceDark,
    surface = SlateSurfaceDark,
    onSurface = InkOnSurfaceDark,
    surfaceVariant = SlateSurfaceLowDark,
    onSurfaceVariant = InkMutedDark,
    surfaceContainerLow = SlateSurfaceLowDark,
    surfaceContainerHigh = SlateSurfaceHighDark,
    outline = HairlineOutlineDark,
    error = SemanticErrorDark,
    onError = Color(0xFF601410),
    errorContainer = SemanticErrorContainerDark,
    onErrorContainer = Color(0xFFF9DEDC),
)

@Immutable
data class SemanticStatusColors(
    val success: Color,
    val successContainer: Color,
    val reduceMotion: Boolean = false,
    val hapticsEnabled: Boolean = true,
)

val LocalSemanticStatusColors = staticCompositionLocalOf {
    SemanticStatusColors(
        success = SemanticSuccessLight,
        successContainer = SemanticSuccessContainerLight,
        reduceMotion = false,
        hapticsEnabled = true,
    )
}

@Composable
fun EnglishPrepTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    reduceMotion: Boolean = false,
    hapticsEnabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    val statusColors = if (darkTheme) {
        SemanticStatusColors(SemanticSuccessDark, SemanticSuccessContainerDark, reduceMotion, hapticsEnabled)
    } else {
        SemanticStatusColors(SemanticSuccessLight, SemanticSuccessContainerLight, reduceMotion, hapticsEnabled)
    }

    CompositionLocalProvider(LocalSemanticStatusColors provides statusColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}
