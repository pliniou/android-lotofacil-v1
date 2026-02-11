package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.core.LinearEasing
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
import com.cebolao.lotofacil.ui.theme.BrandColors

/**
 * Optimized shimmer modifier for loading states with Lotofácil brand colors.
 * Uses proper composition locals and optimized animation specs for better performance.
 * Modernized with reduced alpha for subtle effect and brand-aligned colors.
 */
fun Modifier.shimmer(): Modifier = composed {
    LocalDensity.current
    val transition = rememberInfiniteTransition(label = "shimmer")
    
    // Performance optimization: Use efficient animation values to reduce CPU overhead
    val translateAnim by transition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translation"
    )

    // Brand-aligned shimmer colors using official Lotofácil palette
    val shimmerColors = listOf(
        Color.Transparent,
        BrandColors.RoxoLotofacil.copy(alpha = 0.15f), // Uses official brand purple
        Color.Transparent
    )

    // Performance optimization: Pre-calculate brush for better rendering performance
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim - 1000f, y = translateAnim - 1000f),
        end = Offset(x = translateAnim + 1000f, y = translateAnim + 1000f)
    )
    
    background(brush = brush)
}
