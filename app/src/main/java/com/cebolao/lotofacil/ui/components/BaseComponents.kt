package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.AppShapes

/**
 * Unified Card component with flexible variant system.
 * 
 * Replaces: AppCard, ClickableCard, SurfaceCard (deprecated, use this instead)
 * 
 * @param modifier Modifier for card styling
 * @param variant Tipo de card (Static, Clickable, Surfaced)
 * @param onClick Callback when card is clicked (required for Clickable variant)
 * @param shape Shape of the card
 * @param backgroundColor Custom background color (overrides variant defaults)
 * @param contentColor Custom content color (overrides variant defaults)
 * @param border Border stroke for card
 * @param elevation Elevation level (for Static/Clickable variants)
 * @param tonalElevation Tonal elevation (for Surfaced variant)
 * @param enabled Whether card is enabled (for Clickable variant)
 * @param content Composable content
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    variant: CardVariant = CardVariant.Static,
    onClick: (() -> Unit)? = null,
    shape: Shape = AppShapes.lg, // Use consistent shape token
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    border: BorderStroke? = null,
    elevation: Dp = when (variant) {
        CardVariant.Static -> AppElevation.sm
        CardVariant.Clickable -> AppElevation.sm
        CardVariant.Surfaced -> AppElevation.none
    },
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    
    val targetContainerColor = backgroundColor ?: when(variant) {
        CardVariant.Surfaced -> colors.surfaceVariant.copy(alpha = 0.5f)
        else -> colors.surface
    }
    val targetContentColor = contentColor ?: colors.onSurface

    val cardColors = CardDefaults.elevatedCardColors(
        containerColor = targetContainerColor,
        contentColor = targetContentColor
    )

    when (variant) {
        CardVariant.Static -> {
            androidx.compose.material3.ElevatedCard(
                modifier = modifier,
                shape = shape,
                colors = cardColors,
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation)
            ) {
                content()
            }
        }
        CardVariant.Clickable -> {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            
            // Subtle scale animation
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.98f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "cardScale"
            )

            androidx.compose.material3.ElevatedCard(
                modifier = modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .then(if (border != null) Modifier.border(border, shape) else Modifier),
                onClick = onClick ?: {},
                enabled = enabled,
                shape = shape,
                colors = cardColors,
                interactionSource = interactionSource,
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = elevation,
                    pressedElevation = elevation * 0.5f,
                    hoveredElevation = AppElevation.md
                )
            ) {
                content()
            }
        }
        CardVariant.Surfaced -> {
            androidx.compose.material3.OutlinedCard(
                modifier = modifier,
                shape = shape,
                colors = CardDefaults.outlinedCardColors(
                    containerColor = targetContainerColor,
                    contentColor = targetContentColor
                ),
                border = border ?: BorderStroke(1.dp, colors.outlineVariant),
            ) {
                content()
            }
        }
    }
}

sealed class CardVariant {
    data object Static : CardVariant()
    data object Clickable : CardVariant()
    data object Surfaced : CardVariant()
}
