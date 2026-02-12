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
    primary = Color(0xFF803594),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB37FC6),
    onPrimaryContainer = Color(0xFF2D003B),
    secondary = Color(0xFF702A82),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE6D0ED),
    onSecondaryContainer = Color(0xFF2D003B),
    tertiary = Color(0xFF00A651),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE8F5E8),
    onTertiaryContainer = Color(0xFF003D1F),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF666666),
    outline = Color(0xFFD1D5DB),
    outlineVariant = Color(0xFFE5E7EB),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF2C2C2C),
    inverseOnSurface = Color(0xFFF0F0F0),
    inversePrimary = Color(0xFFB37FC6)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB37FC6),
    onPrimary = Color(0xFF2D003B),
    primaryContainer = Color(0xFF702A82),
    onPrimaryContainer = Color(0xFFE6D0ED),
    secondary = Color(0xFFE6D0ED),
    onSecondary = Color(0xFF2D003B),
    secondaryContainer = Color(0xFF803594),
    onSecondaryContainer = Color(0xFFB37FC6),
    tertiary = Color(0xFF00C875),
    onTertiary = Color(0xFF003D1F),
    tertiaryContainer = Color(0xFF005D33),
    onTertiaryContainer = Color(0xFFE8F5E8),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0A0A0A),
    onBackground = Color(0xFFF5F5F5),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFE5E5E5),
    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFFA0A0A0),
    outline = Color(0xFF404040),
    outlineVariant = Color(0xFF2A2A2A),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE5E5E5),
    inverseOnSurface = Color(0xFF121212),
    inversePrimary = Color(0xFF803594)
)

data class SemanticColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color
)

private val LightSemanticColors = SemanticColors(
    success = Color(0xFF00A651),
    onSuccess = Color(0xFFFFFFFF),
    successContainer = Color(0xFFE8F5E8),
    onSuccessContainer = Color(0xFF003D1F),
    warning = Color(0xFF702A82),
    onWarning = Color(0xFFFFFFFF),
    warningContainer = Color(0xFFE6D0ED),
    onWarningContainer = Color(0xFF2D003B),
    info = Color(0xFF803594),
    onInfo = Color(0xFFFFFFFF),
    infoContainer = Color(0xFFB37FC6),
    onInfoContainer = Color(0xFF2D003B)
)

private val DarkSemanticColors = SemanticColors(
    success = Color(0xFF00C875),
    onSuccess = Color(0xFF003D1F),
    successContainer = Color(0xFF005D33),
    onSuccessContainer = Color(0xFFE8F5E8),
    warning = Color(0xFFB37FC6),
    onWarning = Color(0xFF2D003B),
    warningContainer = Color(0xFF702A82),
    onWarningContainer = Color(0xFFE6D0ED),
    info = Color(0xFFE6D0ED),
    onInfo = Color(0xFF2D003B),
    infoContainer = Color(0xFF803594),
    onInfoContainer = Color(0xFFB37FC6)
)

val LocalSemanticColors = staticCompositionLocalOf { LightSemanticColors }

@Composable
fun CebolaoLotofacilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    animationsEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val useDynamicColor = dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val colorScheme = when {
        useDynamicColor && darkTheme -> dynamicDarkColorScheme(context)
        useDynamicColor -> dynamicLightColorScheme(context)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

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
}

