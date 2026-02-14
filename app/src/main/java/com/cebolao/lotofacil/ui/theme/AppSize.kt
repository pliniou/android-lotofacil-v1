package com.cebolao.lotofacil.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Standardized size values for components across the application.
 * Provides a single source of truth for all component dimensions.
 */
@Immutable
data class AppSizes(
    // Spacing scale
    val spacingXSmall: Dp = 4.dp,
    val spacingSmall: Dp = 8.dp,
    val spacingMedium: Dp = 12.dp,
    val spacingLarge: Dp = 16.dp,
    val spacingXLarge: Dp = 20.dp,
    val spacingXXLarge: Dp = 24.dp,

    // Top bar and navigation
    val topBarHeight: Dp = 56.dp,

    // Number ball sizes
    val numberBallTiny: Dp = 32.dp,
    val numberBallSmall: Dp = 42.dp,
    val numberBallMedium: Dp = 48.dp,
    val numberBallLarge: Dp = 56.dp,

    // Chart dimensions
    val chartHeightDefault: Dp = 240.dp,
    val chartHeightSmall: Dp = 180.dp,
    val chartHeightLarge: Dp = 300.dp,

    // Breakpoints
    val breakpointTablet: Dp = 720.dp,

    // Chart label density
    val chartLabelMinSpacingCompact: Dp = 32.dp,
    val chartLabelMinSpacingExpanded: Dp = 14.dp,
    val chartValueLabelMinBarWidthCompact: Dp = 18.dp,
    val chartValueLabelMinBarWidthExpanded: Dp = 10.dp,

    // Trend chart density
    val trendMinPointSpacingCompact: Dp = 18.dp,
    val trendMinPointSpacingExpanded: Dp = 8.dp,
    val trendMaxPointMarkersCompact: Int = 12,
    val trendMaxPointMarkersExpanded: Int = 28,

    // Card and container sizes
    val cardCornerSmall: Dp = 8.dp,
    val cardCornerMedium: Dp = 12.dp,
    val cardCornerLarge: Dp = 16.dp,

    // Icon sizes (matches Material Design guidelines)
    val iconSmall: Dp = 20.dp,       // Xs
    val iconSmallMedium: Dp = 22.dp, // Sm
    val iconMedium: Dp = 24.dp,      // Md
    val iconMediumLarge: Dp = 28.dp, // Lg
    val iconLarge: Dp = 32.dp,       // Xl
    val iconExtraLarge: Dp = 48.dp,  // 2xl

    // Touch target minimum size (Material Design accessibility)
    val touchTargetMinimum: Dp = 48.dp,

    // Button dimensions
    val buttonHeightDefault: Dp = 48.dp,
    val buttonHeightSmall: Dp = 40.dp,
    val buttonHeightLarge: Dp = 56.dp,

    // Divider and separator heights
    val dividerThickness: Dp = 1.dp,
    val dividerThicknessBold: Dp = 2.dp,

    // Progress indicator sizes
    val progressIndicatorSmall: Dp = 24.dp,
    val progressIndicatorMedium: Dp = 40.dp,
    val progressIndicatorLarge: Dp = 56.dp,

    // Badge and chip sizes
    val chipHeight: Dp = 32.dp,
    val badgeSize: Dp = 24.dp
)

val DefaultAppSizes = AppSizes()

object AppSize {
    val spacingXSmall: Dp
        @Composable get() = AppTheme.sizes.spacingXSmall
    val spacingSmall: Dp
        @Composable get() = AppTheme.sizes.spacingSmall
    val spacingMedium: Dp
        @Composable get() = AppTheme.sizes.spacingMedium
    val spacingLarge: Dp
        @Composable get() = AppTheme.sizes.spacingLarge
    val spacingXLarge: Dp
        @Composable get() = AppTheme.sizes.spacingXLarge
    val spacingXXLarge: Dp
        @Composable get() = AppTheme.sizes.spacingXXLarge

    val topBarHeight: Dp
        @Composable get() = AppTheme.sizes.topBarHeight

    val numberBallTiny: Dp
        @Composable get() = AppTheme.sizes.numberBallTiny
    val numberBallSmall: Dp
        @Composable get() = AppTheme.sizes.numberBallSmall

    val chartHeightSmall: Dp
        @Composable get() = AppTheme.sizes.chartHeightSmall

    val breakpointTablet: Dp
        @Composable get() = AppTheme.sizes.breakpointTablet

    val trendMinPointSpacingCompact: Dp
        @Composable get() = AppTheme.sizes.trendMinPointSpacingCompact

    val iconSmall: Dp
        @Composable get() = AppTheme.sizes.iconSmall
    val iconSmallMedium: Dp
        @Composable get() = AppTheme.sizes.iconSmallMedium
    val iconMedium: Dp
        @Composable get() = AppTheme.sizes.iconMedium

    val touchTargetMinimum: Dp
        @Composable get() = AppTheme.sizes.touchTargetMinimum

    val buttonHeightDefault: Dp
        @Composable get() = AppTheme.sizes.buttonHeightDefault

    val chipHeight: Dp
        @Composable get() = AppTheme.sizes.chipHeight
}
