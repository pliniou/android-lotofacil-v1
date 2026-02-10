package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Shimmer loading effect component.
 * Creates a placeholder with animated gradient shimmer while data loads.
 *
 * Provides better UX perception by showing content is loading.
 */
@Composable
fun ShimmerLoader(
    modifier: Modifier = Modifier,
    width: Float = Float.POSITIVE_INFINITY,
    height: Int = 16,
    cornerRadius: Int = 8
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerPosition = infiniteTransition.animateFloat(
        initialValue = -width,
        targetValue = width * 2,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            )
        ),
        label = "shimmer_animation"
    )

    val shimmerColor = MaterialTheme.colorScheme.surfaceVariant
    val highlightColor = MaterialTheme.colorScheme.surfaceContainerLowest

    val brush = Brush.linearGradient(
        colors = listOf(
            shimmerColor,
            highlightColor,
            shimmerColor
        ),
        start = Offset(shimmerPosition.value, 0f),
        end = Offset(shimmerPosition.value + width, height.toFloat())
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(brush)
    )
}

/**
 * Card placeholder with shimmer effect.
 * Used for card loading states.
 */
@Composable
fun ShimmerCardLoader(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            ShimmerLoader(height = 20, cornerRadius = 4)
        }
    }
}

/**
 * List placeholder with multiple shimmer loaders.
 * Used for list loading states.
 */
@Composable
fun ShimmerListLoader(
    modifier: Modifier = Modifier,
    itemCount: Int = 3
) {
    repeat(itemCount) {
        ShimmerCardLoader(modifier = modifier)
    }
}
