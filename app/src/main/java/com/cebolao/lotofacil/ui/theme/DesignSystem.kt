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
    val semanticColors: SemanticColors
        @Composable get() = LocalSemanticColors.current
}

object AppSpacing {
    val xs: Dp = DefaultSpacing.xs
    val sm: Dp = DefaultSpacing.sm
    val md: Dp = DefaultSpacing.md
    val lg: Dp = DefaultSpacing.lg
    val xl: Dp = DefaultSpacing.xl
    val xxl: Dp = DefaultSpacing.xxl
    val xxxl: Dp = DefaultSpacing.xxxl
}

object AppElevation {
    val none: Dp = DefaultElevation.none
    val xs: Dp = DefaultElevation.xs
    val sm: Dp = DefaultElevation.sm
    val md: Dp = DefaultElevation.md
    val lg: Dp = DefaultElevation.lg
    val xl: Dp = DefaultElevation.xl
}

object AppCardDefaults {
    val defaultPadding: Dp = DefaultCardDefaults.defaultPadding
    val contentSpacing: Dp = DefaultCardDefaults.contentSpacing
    val buttonSpacing: Dp = DefaultCardDefaults.buttonSpacing
    val elevation: Dp = DefaultCardDefaults.elevation
    val pinnedElevation: Dp = DefaultCardDefaults.pinnedElevation
    val hoverElevation: Dp = DefaultCardDefaults.hoverElevation
}

object AppShapes {
    val none = ZeroCornerSize

    val xs = RoundedCornerShape(4.dp)
    val sm = RoundedCornerShape(6.dp)
    val md = RoundedCornerShape(8.dp)
    val lg = RoundedCornerShape(12.dp)
    val xl = RoundedCornerShape(16.dp)
    val xxl = RoundedCornerShape(20.dp)
    val xxxl = RoundedCornerShape(24.dp)

    val circle = CircleShape
    val button = sm
    val card = md
    val dialog = xl
    val listItem = xs
    val chip = sm
    val badge = xs
    val numberBall = CircleShape
}

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
