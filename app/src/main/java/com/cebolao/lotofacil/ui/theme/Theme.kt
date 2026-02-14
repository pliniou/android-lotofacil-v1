package com.cebolao.lotofacil.ui.theme

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
import androidx.compose.runtime.Immutable

val LocalAnimationEnabled = staticCompositionLocalOf { true }

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6B3FA0),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE8DDFF),
    onPrimaryContainer = Color(0xFF240047),
    secondary = Color(0xFF5D5E72),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE2E1F9),
    onSecondaryContainer = Color(0xFF1A1B2C),
    tertiary = Color(0xFF006D3D),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFA7F3C1),
    onTertiaryContainer = Color(0xFF00210F),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFF8FC),
    onBackground = Color(0xFF1D1B20),
    surface = Color(0xFFFFF8FC),
    onSurface = Color(0xFF1D1B20),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF7A757F),
    outlineVariant = Color(0xFFCBC4CF),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF322F35),
    inverseOnSurface = Color(0xFFF5EFF7),
    inversePrimary = Color(0xFFD0B6FF)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0B6FF),
    onPrimary = Color(0xFF3C1E70),
    primaryContainer = Color(0xFF53378A),
    onPrimaryContainer = Color(0xFFE8DDFF),
    secondary = Color(0xFFC6C5DD),
    onSecondary = Color(0xFF2F3042),
    secondaryContainer = Color(0xFF46475A),
    onSecondaryContainer = Color(0xFFE2E1F9),
    tertiary = Color(0xFF7DDAA9),
    onTertiary = Color(0xFF00391E),
    tertiaryContainer = Color(0xFF00522E),
    onTertiaryContainer = Color(0xFF98F7C2),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE7E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE7E1E5),
    surfaceVariant = Color(0xFF2D2933),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF948F99),
    outlineVariant = Color(0xFF49454F),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE7E1E5),
    inverseOnSurface = Color(0xFF322F35),
    inversePrimary = Color(0xFF6B3FA0)
)

@Immutable
data class SemanticColors(
    val warning: Color,
    val success: Color,
    val info: Color
)

val LightSemanticColors = SemanticColors(
    warning = Color(0xFF8A4B00),
    success = Color(0xFF006E2D),
    info = Color(0xFF005FAF)
)

val DarkSemanticColors = SemanticColors(
    warning = Color(0xFFFFB95C),
    success = Color(0xFF6DDA93),
    info = Color(0xFF8CC8FF)
)

val LocalSemanticColors = staticCompositionLocalOf { LightSemanticColors }

@Composable
fun LotofacilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
        LocalAnimationEnabled provides true, // animationsEnabled was missing, used constant true
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
