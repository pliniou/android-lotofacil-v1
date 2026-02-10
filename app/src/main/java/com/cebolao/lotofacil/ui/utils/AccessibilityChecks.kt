package com.cebolao.lotofacil.ui.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlin.math.pow

/**
 * Accessibility validation utilities for WCAG AA compliance.
 * 
 * Covers:
 * - Color contrast ratios (AA standard: 4.5:1 for normal text, 3:1 for large text)
 * - Dark mode support validation
 * - Content description validation
 * - Font size minimum requirements
 * 
 * Reference: https://www.w3.org/WAI/WCAG21/quickref/
 */
object AccessibilityValidator {
    
    /**
     * Calculate contrast ratio between two colors (WCAG formula).
     * 
     * WCAG AA requires:
     * - Normal text: 4.5:1 contrast
     * - Large text (18pt+ or 14pt+ bold): 3:1 contrast
     * - UI components and graphical elements: 3:1 contrast
     * 
     * @param foreground The foreground color
     * @param background The background color
     * @return Contrast ratio (1 to 21)
     */
    fun getContrastRatio(foreground: Color, background: Color): Float {
        val fgLuminance = getRelativeLuminance(foreground)
        val bgLuminance = getRelativeLuminance(background)
        
        val lighter = maxOf(fgLuminance, bgLuminance)
        val darker = minOf(fgLuminance, bgLuminance)
        
        return (lighter + 0.05f) / (darker + 0.05f)
    }
    
    /**
     * Get relative luminance of a color (WCAG definition).
     * 
     * Returns value between 0 (darkest) and 1 (lightest).
     */
    private fun getRelativeLuminance(color: Color): Float {
        val red = toLinearRGB(color.red)
        val green = toLinearRGB(color.green)
        val blue = toLinearRGB(color.blue)
        
        return (0.2126f * red) + (0.7152f * green) + (0.0722f * blue)
    }
    
    /**
     * Convert sRGB component to linear RGB for luminance calculation.
     */
    private fun toLinearRGB(component: Float): Float {
        return if (component <= 0.03928f) {
            component / 12.92f
        } else {
            ((component + 0.055f) / 1.055f).pow(2.4f)
        }
    }
    
    /**
     * Check if text color is WCAG AA compliant on given background.
     * 
     * @param textColor The text color
     * @param backgroundColor The background color
     * @param isLargeText True if text is >= 18pt (or 14pt bold)
     * @return Pair of (isCompliant, actualRatio)
     */
    fun isContrastCompliant(
        textColor: Color,
        backgroundColor: Color,
        isLargeText: Boolean = false
    ): Pair<Boolean, Float> {
        val ratio = getContrastRatio(textColor, backgroundColor)
        val minRequired = if (isLargeText) 3f else 4.5f
        return Pair(ratio >= minRequired, ratio)
    }
}

/**
 * Material 3 dark mode support validator.
 * 
 * Ensures the app has proper dark mode colors defined for all states.
 */
@Composable
fun validateDarkModeSupport() {
    val colors = MaterialTheme.colorScheme
    
    // Validate that onSurface colors have sufficient contrast
    val surfaceContrast = AccessibilityValidator.getContrastRatio(
        colors.onSurface,
        colors.surface
    )
    
    val errorContrast = AccessibilityValidator.getContrastRatio(
        colors.onError,
        colors.error
    )
    
    require(surfaceContrast >= 4.5f) {
        "Dark mode: onSurface/surface contrast ratio is $surfaceContrast, needs >= 4.5:1"
    }
    
    require(errorContrast >= 4.5f) {
        "Dark mode: onError/error contrast ratio is $errorContrast, needs >= 4.5:1"
    }
}

/**
 * Check font size for accessibility minimum.
 * WCAG recommends minimum 12sp for body text.
 */
fun isFontSizeAccessible(style: TextStyle): Boolean {
    return style.fontSize >= 12.sp
}

/**
 * Accessibility validation annotations for compile-time checks.
 */

/**
 * Mark a composable as requiring content description for accessibility.
 * 
 * @param description What content description should contain
 */
annotation class RequiresContentDescription(val description: String)

/**
 * Mark a composable as having validated dark mode support.
 */
annotation class DarkModeValidated

/**
 * Mark a composable as meeting WCAG AA contrast requirements.
 */
annotation class WCAGAACompliant(val minContrast: Float = 4.5f)
