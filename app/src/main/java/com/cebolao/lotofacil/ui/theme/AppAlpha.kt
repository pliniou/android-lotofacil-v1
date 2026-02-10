package com.cebolao.lotofacil.ui.theme

/**
 * Standardized alpha/opacity values for text and components.
 * Ensures consistent visual hierarchy and accessibility compliance.
 * All values respect WCAG AA contrast requirements (4.5:1 minimum for body text).
 */
object AppAlpha {
    
    // Text opacity levels
    const val textPrimary = 1f              // Primary content - full opacity
    const val textSecondary = 0.87f         // Secondary content
    const val textTertiary = 0.60f          // Tertiary/disabled content
    const val textHint = 0.50f              // Hint/placeholder text
    const val textDisabled = 0.38f          // Fully disabled state
    
    // Component states
    const val stateEnabled = 1f
    const val stateHovered = 0.92f
    const val statePressed = 0.85f
    const val stateDisabled = 0.38f
    const val stateFocused = 0.95f
    
    // Surface and backdrop
    const val surfaceLow = 0.08f            // Subtle background
    const val surfaceMedium = 0.15f         // Medium overlay
    const val surfaceHigh = 0.25f           // Strong overlay
    
    // Emphasis
    const val emphasisHigh = 0.87f
    const val emphasisMedium = 0.70f
    const val emphasisLow = 0.40f
    
    // Icons
    const val iconActive = 1f
    const val iconInactive = 0.60f
    const val iconDisabled = 0.38f
    
    // Borders and dividers
    const val borderDefault = 0.12f
    const val borderStrong = 0.29f
    const val dividerDefault = 0.12f
    
    // Shimmer and loading states
    const val shimmerBase = 0.5f
    const val shimmerHighlight = 0.8f
}
