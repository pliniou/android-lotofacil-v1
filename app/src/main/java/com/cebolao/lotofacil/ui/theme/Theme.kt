package com.cebolao.lotofacil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
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
import android.os.Build

// CompositionLocal para habilitar/desabilitar animações globalmente
val LocalAnimationEnabled = androidx.compose.runtime.staticCompositionLocalOf { true }

/**
 * Enhanced color scheme with semantic colors for the Lotofácil app.
 * Includes brand colors and semantic color definitions.
 */
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1B5E20), // Material Green 800
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFA5D6A7), // Material Green 200
    onPrimaryContainer = Color(0xFF1B5E20),
    
    secondary = Color(0xFF388E3C), // Material Green 700
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFC8E6C9), // Material Green 100
    onSecondaryContainer = Color(0xFF1B5E20),
    
    tertiary = Color(0xFFFF6F00), // Material Orange A700
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFE0B2), // Material Orange 100
    onTertiaryContainer = Color(0xFFE65100),
    
    error = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFFB71C1C),
    
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1C1C1C),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1C1C),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF49454F),
    
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFF81C784)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784), // Material Green 300
    onPrimary = Color(0xFF1B5E20),
    primaryContainer = Color(0xFF2E7D32), // Material Green 800
    onPrimaryContainer = Color(0xFFC8E6C9),
    
    secondary = Color(0xFF66BB6A), // Material Green 400
    onSecondary = Color(0xFF1B5E20),
    secondaryContainer = Color(0xFF2E7D32), // Material Green 800
    onSecondaryContainer = Color(0xFFC8E6C9),
    
    tertiary = Color(0xFFFFB74D), // Material Orange 300
    onTertiary = Color(0xFFBF360C),
    tertiaryContainer = Color(0xFFE65100), // Material Orange A700
    onTertiaryContainer = Color(0xFFFFE0B2),
    
    error = Color(0xFFEF5350),
    onError = Color(0xFFB71C1C),
    errorContainer = Color(0xFFC62828),
    onErrorContainer = Color(0xFFFFCDD2),
    
    background = Color(0xFF121212),
    onBackground = Color(0xFFE1E2E1),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE1E2E1),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFCAC4D0),
    
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE1E2E1),
    inverseOnSurface = Color(0xFF1C1C1C),
    inversePrimary = Color(0xFF1B5E20)
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
    success = Color(0xFF2E7D32),
    onSuccess = Color(0xFFFFFFFF),
    successContainer = Color(0xFFC8E6C9),
    onSuccessContainer = Color(0xFF1B5E20),
    warning = Color(0xFFFF8F00),
    onWarning = Color(0xFFFFFFFF),
    warningContainer = Color(0xFFFFE082),
    onWarningContainer = Color(0xFFBF360C),
    info = Color(0xFF1976D2),
    onInfo = Color(0xFFFFFFFF),
    infoContainer = Color(0xFFBBDEFB),
    onInfoContainer = Color(0xFF0D47A1)
)

private val DarkSemanticColors = SemanticColors(
    success = Color(0xFF66BB6A),
    onSuccess = Color(0xFF1B5E20),
    successContainer = Color(0xFF2E7D32),
    onSuccessContainer = Color(0xFFC8E6C9),
    warning = Color(0xFFFFB74D),
    onWarning = Color(0xFFBF360C),
    warningContainer = Color(0xFFE65100),
    onWarningContainer = Color(0xFFFFE082),
    info = Color(0xFF64B5F6),
    onInfo = Color(0xFF0D47A1),
    infoContainer = Color(0xFF1565C0),
    onInfoContainer = Color(0xFFBBDEFB)
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
    dynamicColor: Boolean = false, // Disabled for consistent brand colors
    animationsEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val semanticColors = if (darkTheme) DarkSemanticColors else LightSemanticColors

    CompositionLocalProvider(
        LocalSemanticColors provides semanticColors,
        LocalAnimationEnabled provides animationsEnabled
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography(),
            shapes = Shapes(),
            content = content
        )
    }
}

/**
 * Extension property to access semantic colors from MaterialTheme.
 */
val MaterialTheme.semanticColors: SemanticColors
    @Composable get() = LocalSemanticColors.current
