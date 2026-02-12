package com.cebolao.lotofacil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// CompositionLocal para habilitar/desabilitar animações globalmente
val LocalAnimationEnabled = androidx.compose.runtime.staticCompositionLocalOf { true }

/**
 * Enhanced color scheme with official Lotofácil brand colors.
 * Features the official purple palette with premium deep black theme.
 */
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF803594), // Roxo Lotofácil (R128 G53 B148)
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB37FC6), // Lighter purple container
    onPrimaryContainer = Color(0xFF2D003B),
    
    secondary = Color(0xFF702A82), // Roxo-Escuro (R112 G42 B130)
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE6D0ED), // Light purple variant
    onSecondaryContainer = Color(0xFF2D003B),
    
    tertiary = Color(0xFF00A651), // Verde Lotofácil official
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE8F5E8), // Light green variant
    onTertiaryContainer = Color(0xFF003D1F),
    
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    
    background = Color(0xFFFAFAFA), // Clean white
    onBackground = Color(0xFF1A1A1A), // Deep charcoal
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
    primary = Color(0xFFB37FC6), // Lighter Roxo Lotofácil for dark theme
    onPrimary = Color(0xFF2D003B),
    primaryContainer = Color(0xFF702A82), // Roxo-Escuro container
    onPrimaryContainer = Color(0xFFE6D0ED),
    
    secondary = Color(0xFFE6D0ED), // Light purple accent for dark theme
    onSecondary = Color(0xFF2D003B),
    secondaryContainer = Color(0xFF803594), // Roxo Lotofácil container
    onSecondaryContainer = Color(0xFFB37FC6),
    
    tertiary = Color(0xFF00C875), // Vibrant green for dark theme
    onTertiary = Color(0xFF003D1F),
    tertiaryContainer = Color(0xFF005D33), // Deep green container
    onTertiaryContainer = Color(0xFFE8F5E8),
    
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    
    background = Color(0xFF0A0A0A), // Deep black background
    onBackground = Color(0xFFF5F5F5), // Light text
    surface = Color(0xFF121212), // Slightly lighter black
    onSurface = Color(0xFFE5E5E5), // Light surface text
    surfaceVariant = Color(0xFF1A1A1A), // Dark variant
    onSurfaceVariant = Color(0xFFA0A0A0), // Medium light gray
    
    outline = Color(0xFF404040), // Dark gray outline
    outlineVariant = Color(0xFF2A2A2A), // Very dark outline
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE5E5E5),
    inverseOnSurface = Color(0xFF121212),
    inversePrimary = Color(0xFF803594)
)

/**
 * Semantic colors for specific UI states and meanings.
 */
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
    success = Color(0xFF00A651), // Verde Lotofácil official
    onSuccess = Color(0xFFFFFFFF),
    successContainer = Color(0xFFE8F5E8),
    onSuccessContainer = Color(0xFF003D1F),
    warning = Color(0xFF702A82), // Roxo-Escuro for warnings
    onWarning = Color(0xFFFFFFFF),
    warningContainer = Color(0xFFE6D0ED),
    onWarningContainer = Color(0xFF2D003B),
    info = Color(0xFF803594), // Roxo Lotofácil for info
    onInfo = Color(0xFFFFFFFF),
    infoContainer = Color(0xFFB37FC6),
    onInfoContainer = Color(0xFF2D003B)
)

private val DarkSemanticColors = SemanticColors(
    success = Color(0xFF00C875), // Vibrant green for dark theme
    onSuccess = Color(0xFF003D1F),
    successContainer = Color(0xFF005D33),
    onSuccessContainer = Color(0xFFE8F5E8),
    warning = Color(0xFFB37FC6), // Lighter Roxo Lotofácil for warnings
    onWarning = Color(0xFF2D003B),
    warningContainer = Color(0xFF702A82),
    onWarningContainer = Color(0xFFE6D0ED),
    info = Color(0xFFE6D0ED), // Light purple for info
    onInfo = Color(0xFF2D003B),
    infoContainer = Color(0xFF803594),
    onInfoContainer = Color(0xFFB37FC6)
)

/**
 * CompositionLocal for semantic colors.
 */
val LocalSemanticColors = androidx.compose.runtime.staticCompositionLocalOf {
    LightSemanticColors
}

/**
 * Enhanced MaterialTheme with design tokens and semantic colors.
 */
@Composable
fun CebolaoLotofacilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Enabled for Android 12+ premium experience
    animationsEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
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
            typography = Typography(),
            shapes = Shapes(),
            content = content
        )
    }
}
