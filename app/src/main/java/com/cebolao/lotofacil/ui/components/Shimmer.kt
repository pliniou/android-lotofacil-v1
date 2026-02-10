package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import com.cebolao.lotofacil.ui.theme.AppTheme

/**
 * Optimized shimmer modifier for loading states.
 * Uses proper composition locals and optimized animation specs for better performance.
 * Modernized with reduced alpha for subtle effect.
 */
fun Modifier.shimmer(): Modifier = composed {
    LocalDensity.current
    val transition = rememberInfiniteTransition(label = "shimmer")
    
    // Optimize animation values for better performance
    val translateAnim by transition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = AppTheme.motion.durationShimmerMs,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslation"
    )

    // Use theme-aware colors with reduced alpha for modern look
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.25f),
        Color.LightGray.copy(alpha = 0.08f),
        Color.LightGray.copy(alpha = 0.25f),
    )

    // Optimize brush calculation
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim - 1000f, y = translateAnim - 1000f),
        end = Offset(x = translateAnim + 1000f, y = translateAnim + 1000f)
    )
    
    background(brush = brush)
}
