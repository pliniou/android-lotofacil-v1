package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.AppShapes
import com.cebolao.lotofacil.ui.theme.AppSpacing

/**
 * Unified Card component with flexible variant system.
 *
 * @param modifier Modifier for card styling
 * @param variant Card style variant (Elevated, Outlined, Filled, etc.)
 * @param onClick Optional callback. If provided, card becomes clickable with interactions.
 * @param shape Shape of the card
 * @param containerColor Custom background color (overrides variant defaults)
 * @param contentColor Custom content color (overrides variant defaults)
 * @param border Border stroke for card (default depends on variant)
 * @param elevation Elevation level (default depends on variant)
 * @param content Composable content
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    variant: CardVariant = CardVariant.Elevated,
    onClick: (() -> Unit)? = null,
    shape: Shape = AppShapes.lg,
    containerColor: Color? = null,
    contentColor: Color? = null,
    border: BorderStroke? = null,
    elevation: Dp? = null,
    isGlassmorphic: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    
    // Determine defaults based on variant
    val defaultContainerColor = when(variant) {
        CardVariant.Elevated -> colors.surface
        CardVariant.Outlined -> Color.Transparent
        CardVariant.Filled -> colors.surfaceVariant
    }
    
    val defaultElevation = when(variant) {
        CardVariant.Elevated -> AppElevation.sm
        else -> AppElevation.none
    }

    val finalContainerColor = when {
        isGlassmorphic && variant == CardVariant.Elevated -> colors.surface.copy(alpha = 0.85f)
        containerColor != null -> containerColor
        else -> defaultContainerColor
    }
    val finalContentColor = contentColor ?: colors.onSurface
    val finalElevation = elevation ?: defaultElevation
    val finalBorder = border ?: if (variant == CardVariant.Outlined) {
        BorderStroke(1.dp, colors.outlineVariant)
    } else null

    val cardColors = CardDefaults.cardColors(
        containerColor = finalContainerColor,
        contentColor = finalContentColor
    )

    if (onClick != null) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        // Subtle scale animation for clickable cards
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.98f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "cardScale"
        )

        Card(
            onClick = onClick,
            modifier = modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            enabled = true,
            shape = shape,
            colors = cardColors,
            elevation = CardDefaults.cardElevation(
                defaultElevation = finalElevation,
                pressedElevation = if (finalElevation > 0.dp) finalElevation * 0.5f else 0.dp
            ),
            border = finalBorder,
            interactionSource = interactionSource
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.md)
            ) {
                content()
            }
        }
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = cardColors,
            elevation = CardDefaults.cardElevation(defaultElevation = finalElevation),
            border = finalBorder
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.md)
            ) {
                content()
            }
        }
    }
}


