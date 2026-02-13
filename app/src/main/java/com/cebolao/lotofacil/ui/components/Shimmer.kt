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
import androidx.compose.material3.MaterialTheme
import com.cebolao.lotofacil.ui.theme.BrandColors

/**
 * Optimized shimmer modifier for loading states with Lotofácil brand colors.
 * Modernized with customizable colors and softer animation for a premium feel.
 */
fun Modifier.shimmer(
    baseColor: Color? = null,
    highlightColor: Color? = null,
    durationMillis: Int = 1200
): Modifier = composed {
    val colorScheme = MaterialTheme.colorScheme
    val actualBaseColor = baseColor ?: colorScheme.surfaceVariant.copy(alpha = 0.4f)
    val actualHighlightColor = highlightColor ?: BrandColors.RoxoLotofacil.copy(alpha = 0.15f)

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translation"
    )

    val shimmerColors = listOf(
        actualBaseColor,
        actualHighlightColor,
        actualBaseColor,
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim - 500f, y = translateAnim - 500f),
        end = Offset(x = translateAnim + 500f, y = translateAnim + 500f)
    )
    
    background(brush = brush)
}
