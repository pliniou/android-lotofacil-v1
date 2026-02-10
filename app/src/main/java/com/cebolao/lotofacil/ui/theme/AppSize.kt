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
    // Number ball sizes
    val numberBallTiny: Dp = 32.dp,
    val numberBallSmall: Dp = 38.dp,
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
    val numberBallTiny: Dp
        @Composable get() = AppTheme.sizes.numberBallTiny
    val numberBallSmall: Dp
        @Composable get() = AppTheme.sizes.numberBallSmall
    val numberBallMedium: Dp
        @Composable get() = AppTheme.sizes.numberBallMedium
    val numberBallLarge: Dp
        @Composable get() = AppTheme.sizes.numberBallLarge

    val chartHeightDefault: Dp
        @Composable get() = AppTheme.sizes.chartHeightDefault
    val chartHeightSmall: Dp
        @Composable get() = AppTheme.sizes.chartHeightSmall
    val chartHeightLarge: Dp
        @Composable get() = AppTheme.sizes.chartHeightLarge

    val breakpointTablet: Dp
        @Composable get() = AppTheme.sizes.breakpointTablet

    val chartLabelMinSpacingCompact: Dp
        @Composable get() = AppTheme.sizes.chartLabelMinSpacingCompact
    val chartLabelMinSpacingExpanded: Dp
        @Composable get() = AppTheme.sizes.chartLabelMinSpacingExpanded
    val chartValueLabelMinBarWidthCompact: Dp
        @Composable get() = AppTheme.sizes.chartValueLabelMinBarWidthCompact
    val chartValueLabelMinBarWidthExpanded: Dp
        @Composable get() = AppTheme.sizes.chartValueLabelMinBarWidthExpanded

    val trendMinPointSpacingCompact: Dp
        @Composable get() = AppTheme.sizes.trendMinPointSpacingCompact
    val trendMinPointSpacingExpanded: Dp
        @Composable get() = AppTheme.sizes.trendMinPointSpacingExpanded
    val trendMaxPointMarkersCompact: Int
        @Composable get() = AppTheme.sizes.trendMaxPointMarkersCompact
    val trendMaxPointMarkersExpanded: Int
        @Composable get() = AppTheme.sizes.trendMaxPointMarkersExpanded

    val cardCornerSmall: Dp
        @Composable get() = AppTheme.sizes.cardCornerSmall
    val cardCornerMedium: Dp
        @Composable get() = AppTheme.sizes.cardCornerMedium
    val cardCornerLarge: Dp
        @Composable get() = AppTheme.sizes.cardCornerLarge

    val iconSmall: Dp
        @Composable get() = AppTheme.sizes.iconSmall
    val iconSmallMedium: Dp
        @Composable get() = AppTheme.sizes.iconSmallMedium
    val iconMedium: Dp
        @Composable get() = AppTheme.sizes.iconMedium
    val iconMediumLarge: Dp
        @Composable get() = AppTheme.sizes.iconMediumLarge
    val iconLarge: Dp
        @Composable get() = AppTheme.sizes.iconLarge
    val iconExtraLarge: Dp
        @Composable get() = AppTheme.sizes.iconExtraLarge

    val touchTargetMinimum: Dp
        @Composable get() = AppTheme.sizes.touchTargetMinimum

    val buttonHeightDefault: Dp
        @Composable get() = AppTheme.sizes.buttonHeightDefault
    val buttonHeightSmall: Dp
        @Composable get() = AppTheme.sizes.buttonHeightSmall
    val buttonHeightLarge: Dp
        @Composable get() = AppTheme.sizes.buttonHeightLarge

    val dividerThickness: Dp
        @Composable get() = AppTheme.sizes.dividerThickness
    val dividerThicknessBold: Dp
        @Composable get() = AppTheme.sizes.dividerThicknessBold

    val progressIndicatorSmall: Dp
        @Composable get() = AppTheme.sizes.progressIndicatorSmall
    val progressIndicatorMedium: Dp
        @Composable get() = AppTheme.sizes.progressIndicatorMedium
    val progressIndicatorLarge: Dp
        @Composable get() = AppTheme.sizes.progressIndicatorLarge

    val chipHeight: Dp
        @Composable get() = AppTheme.sizes.chipHeight
    val badgeSize: Dp
        @Composable get() = AppTheme.sizes.badgeSize
}
