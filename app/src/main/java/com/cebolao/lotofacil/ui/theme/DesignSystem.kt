package com.cebolao.lotofacil.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Spacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val xxl: Dp = 24.dp,
    val xxxl: Dp = 32.dp
)

@Immutable
data class Elevation(
    val none: Dp = 0.dp,
    val xs: Dp = 0.5.dp,
    val sm: Dp = 1.dp,
    val md: Dp = 2.dp,
    val lg: Dp = 4.dp,
    val xl: Dp = 8.dp
)

@Immutable
data class AppCardDefaultsTokens(
    val defaultPadding: Dp,
    val contentSpacing: Dp,
    val buttonSpacing: Dp,
    val elevation: Dp,
    val pinnedElevation: Dp,
    val hoverElevation: Dp
)

val DefaultSpacing = Spacing()
val DefaultElevation = Elevation()
val DefaultCardDefaults = AppCardDefaultsTokens(
    defaultPadding = DefaultSpacing.lg,
    contentSpacing = DefaultSpacing.md,
    buttonSpacing = DefaultSpacing.sm,
    elevation = DefaultElevation.none,
    pinnedElevation = DefaultElevation.xs,
    hoverElevation = DefaultElevation.sm
)

val LocalSpacing = staticCompositionLocalOf { DefaultSpacing }
val LocalElevation = staticCompositionLocalOf { DefaultElevation }
val LocalSizes = staticCompositionLocalOf { DefaultAppSizes }
val LocalMotion = staticCompositionLocalOf { DefaultAppMotion }
val LocalCardDefaults = staticCompositionLocalOf { DefaultCardDefaults }

object AppTheme {
    val spacing: Spacing
        @Composable get() = LocalSpacing.current
    val elevation: Elevation
        @Composable get() = LocalElevation.current
    val sizes: AppSizes
        @Composable get() = LocalSizes.current
    val motion: AppMotion
        @Composable get() = LocalMotion.current
    val cardDefaults: AppCardDefaultsTokens
        @Composable get() = LocalCardDefaults.current
}

object AppSpacing {
    val xs: Dp @Composable get() = AppTheme.spacing.xs
    val sm: Dp @Composable get() = AppTheme.spacing.sm
    val md: Dp @Composable get() = AppTheme.spacing.md
    val lg: Dp @Composable get() = AppTheme.spacing.lg
    val xl: Dp @Composable get() = AppTheme.spacing.xl
    val xxl: Dp @Composable get() = AppTheme.spacing.xxl
    val xxxl: Dp @Composable get() = AppTheme.spacing.xxxl
}

object AppElevation {
    val none: Dp @Composable get() = AppTheme.elevation.none
    val xs: Dp @Composable get() = AppTheme.elevation.xs
    val sm: Dp @Composable get() = AppTheme.elevation.sm
    val md: Dp @Composable get() = AppTheme.elevation.md
    val lg: Dp @Composable get() = AppTheme.elevation.lg
    val xl: Dp @Composable get() = AppTheme.elevation.xl
}

object AppCardDefaults {
    val defaultPadding: Dp @Composable get() = AppTheme.cardDefaults.defaultPadding
    val contentSpacing: Dp @Composable get() = AppTheme.cardDefaults.contentSpacing
    val buttonSpacing: Dp @Composable get() = AppTheme.cardDefaults.buttonSpacing
    val elevation: Dp @Composable get() = AppTheme.cardDefaults.elevation
    val pinnedElevation: Dp @Composable get() = AppTheme.cardDefaults.pinnedElevation
    val hoverElevation: Dp @Composable get() = AppTheme.cardDefaults.hoverElevation
}

object AppShapes {
    // Corner radii for different components
    val none = ZeroCornerSize

    val xs = RoundedCornerShape(4.dp)
    val sm = RoundedCornerShape(6.dp)
    val md = RoundedCornerShape(8.dp)
    val lg = RoundedCornerShape(12.dp)
    val xl = RoundedCornerShape(16.dp)
    val xxl = RoundedCornerShape(20.dp)
    val xxxl = RoundedCornerShape(24.dp)

    // Specialized shapes
    val circle = CircleShape
    val button = sm
    val card = md
    val dialog = xl
    val listItem = xs
    val chip = sm
    val badge = xs
    val numberBall = CircleShape
}

// Composable helpers for common sizes that might be used across the app
@Composable
fun iconSmall() = AppTheme.sizes.iconSmall

@Composable
fun iconMedium() = AppTheme.sizes.iconMedium

@Composable
fun iconLarge() = AppTheme.sizes.iconLarge

@Composable
fun iconExtraLarge() = AppTheme.sizes.iconExtraLarge

@Composable
fun iconButtonSize() = AppTheme.sizes.touchTargetMinimum
