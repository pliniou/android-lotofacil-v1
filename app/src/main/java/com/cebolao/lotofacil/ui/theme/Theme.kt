package com.cebolao.lotofacil.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val LocalAnimationEnabled = staticCompositionLocalOf { true }

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF9C27B0), // Vivid Purple
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF3E5F5),
    onPrimaryContainer = Color(0xFF4A0072),
    secondary = Color(0xFF7B1FA2), // Deep Purple
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE1BEE7),
    onSecondaryContainer = Color(0xFF4A0072),
    tertiary = Color(0xFF00C853), // Vibrant Green
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFB9F6CA),
    onTertiaryContainer = Color(0xFF005005),
    error = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFFB71C1C),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF121212),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF121212),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF616161),
    outline = Color(0xFFE0E0E0),
    outlineVariant = Color(0xFFEEEEEE),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF121212),
    inverseOnSurface = Color(0xFFF5F5F5),
    inversePrimary = Color(0xFFE1BEE7)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE040FB), // Electric Purple
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF7B1FA2),
    onPrimaryContainer = Color(0xFFF3E5F5),
    secondary = Color(0xFFBA68C8),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF4A0072),
    onSecondaryContainer = Color(0xFFE1BEE7),
    tertiary = Color(0xFF00E676), // Neon Green
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFF00C853), // Darker Green container
    onTertiaryContainer = Color(0xFFB9F6CA),
    error = Color(0xFFFF5252),
    onError = Color(0xFF000000),
    errorContainer = Color(0xFFD32F2F),
    onErrorContainer = Color(0xFFFFCDD2),
    background = Color(0xFF000000), // DEEP BLACK (OLED)
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF121212), // Slightly lighter for cards
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF1E1E1E), // Even lighter for secondary containers
    onSurfaceVariant = Color(0xFFBDBDBD),
    outline = Color(0xFF424242),
    outlineVariant = Color(0xFF2C2C2C),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFFFFFFF),
    inverseOnSurface = Color(0xFF000000),
    inversePrimary = Color(0xFF9C27B0)
)

    val semanticColors = if (darkTheme) DarkSemanticColors else LightSemanticColors

    CompositionLocalProvider(
        LocalSemanticColors provides semanticColors,
        LocalAnimationEnabled provides animationsEnabled,
        LocalSpacing provides DefaultSpacing,
        LocalElevation provides DefaultElevation,
        LocalCardDefaults provides DefaultCardDefaults,
        LocalSizes provides DefaultAppSizes,
        LocalMotion provides DefaultAppMotion
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = Shapes(
                small = AppShapes.sm,
                medium = AppShapes.md,
                large = AppShapes.lg
            ),
            content = content
        )
    }
