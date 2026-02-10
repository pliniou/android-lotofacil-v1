package com.cebolao.lotofacil.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Provide a composition local to toggle animations on or off
val LocalAnimationEnabled = staticCompositionLocalOf { true }

private val DarkColors = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = Color(0xFF003737),
    primaryContainer = Color(0xFF00504F),
    onPrimaryContainer = Color(0xFF9CF1EF),
    secondary = BrandSecondary,
    onSecondary = Color(0xFF1D3534),
    secondaryContainer = Color(0xFF334B4A),
    onSecondaryContainer = Color(0xFFCCE8E6),
    tertiary = Color(0xFFB7C7EA),
    onTertiary = Color(0xFF21304B),
    tertiaryContainer = Color(0xFF384764),
    onTertiaryContainer = Color(0xFFD8E3FF),
    error = Error,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceContainerHigh,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
)

private val LightColors = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF9CF1EF),
    onPrimaryContainer = Color(0xFF002020),
    secondary = BrandSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCE8E6),
    onSecondaryContainer = Color(0xFF051F1E),
    tertiary = Color(0xFF4F5F7D),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD8E3FF),
    onTertiaryContainer = Color(0xFF081C36),
    error = Error,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceContainerHighest,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    surfaceContainerLowest = LightSurfaceContainerLowest,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest,
)

@Composable
fun CebolaoLotofacilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    animationsEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    val spacing = DefaultSpacing
    val elevation = DefaultElevation
    val sizes = DefaultAppSizes
    val motion = DefaultAppMotion
    val cardDefaults = DefaultCardDefaults

    CompositionLocalProvider(
        LocalAnimationEnabled provides animationsEnabled,
        LocalSpacing provides spacing,
        LocalElevation provides elevation,
        LocalSizes provides sizes,
        LocalMotion provides motion,
        LocalCardDefaults provides cardDefaults
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = createAdaptiveTypography(),
            shapes = Shapes,
            content = content
        )
    }
}
