@file:Suppress("SameParameterValue")
package com.cebolao.lotofacil.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.cebolao.lotofacil.R

val Inter = FontFamily(
    Font(R.font.gabarito_regular, FontWeight.Normal),
    Font(R.font.gabarito_medium, FontWeight.Medium),
    Font(R.font.gabarito_semibold, FontWeight.SemiBold),
    Font(R.font.gabarito_bold, FontWeight.Bold)
)

/**
 * Calculates an adaptive scale factor based on screen configuration and accessibility settings.
 * This ensures text remains readable and properly sized across different devices
 * (phones, tablets, foldables) while respecting user accessibility settings for font size.
 *
 * Font size scaling from Android Settings > Accessibility > Display > Font Size:
 * - Small: 0.85f
 * - Normal: 1.0f (default)
 * - Large: 1.15f
 * - Larger: 1.3f
 * - Largest: 1.5f
 *
 * @return Scale factor in range [0.85f, 1.5f]
 */
@Composable
fun calculateTypographyScaleFactor(): Float {
    val configuration = LocalConfiguration.current
    
    // Base adaptation by smallest screen width to keep hierarchy balanced on larger displays.
    val smallestScreenWidthDp = configuration.smallestScreenWidthDp

    val baseScaleFactor = when {
        smallestScreenWidthDp < 360 -> 0.96f
        smallestScreenWidthDp < 600 -> 1.00f
        smallestScreenWidthDp < 840 -> 1.08f
        else -> 1.14f
    }

    // Respect Android accessibility font scaling and avoid over-amplifying it.
    val accessibilityScaling = configuration.fontScale.coerceIn(0.85f, 1.5f)

    return (baseScaleFactor * accessibilityScaling).coerceIn(0.85f, 1.6f)
}

/**
 * Creates Material3 Typography with adaptive scaling.
 * All text styles will scale based on device configuration.
 * Optimized for performance with pre-calculated font sizes.
 *
 * @param scaleFactor Multiplier for all font sizes (default calculated from device config)
 */
@Composable
fun createAdaptiveTypography(scaleFactor: Float = calculateTypographyScaleFactor()): Typography {
    return Typography(
        displayLarge = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Bold,
            fontSize = (57 * scaleFactor).sp,
            lineHeight = (64 * scaleFactor).sp,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = (45 * scaleFactor).sp,
            lineHeight = (52 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = (36 * scaleFactor).sp,
            lineHeight = (44 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = (28 * scaleFactor).sp,
            lineHeight = (36 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = (24 * scaleFactor).sp,
            lineHeight = (32 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = (20 * scaleFactor).sp,
            lineHeight = (28 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = (18 * scaleFactor).sp,
            lineHeight = (24 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Medium,
            fontSize = (16 * scaleFactor).sp,
            lineHeight = (20 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        titleSmall = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * scaleFactor).sp,
            lineHeight = (18 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Normal,
            fontSize = (16 * scaleFactor).sp,
            lineHeight = (24 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Normal,
            fontSize = (14 * scaleFactor).sp,
            lineHeight = (20 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        bodySmall = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Normal,
            fontSize = (12 * scaleFactor).sp,
            lineHeight = (16 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        labelLarge = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * scaleFactor).sp,
            lineHeight = (20 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        labelMedium = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Medium,
            fontSize = (12 * scaleFactor).sp,
            lineHeight = (16 * scaleFactor).sp,
            letterSpacing = 0.sp
        ),
        labelSmall = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Medium,
            fontSize = (11 * scaleFactor).sp,
            lineHeight = (16 * scaleFactor).sp,
            letterSpacing = 0.sp
        )
    )
}
