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
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppElevation

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
    shape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    border: BorderStroke? = null,
    elevation: Dp = AppCardDefaults.elevation,
    tonalElevation: Dp = AppElevation.none,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val cardColors = if (backgroundColor != null || contentColor != null) {
        CardDefaults.elevatedCardColors(
            containerColor = backgroundColor ?: colors.surface,
            contentColor = contentColor ?: colors.onSurface
        )
    } else {
        CardDefaults.elevatedCardColors()
    }

    when (variant) {
        is CardVariant.Static -> {
            androidx.compose.material3.ElevatedCard(
                modifier = modifier,
                shape = shape,
                colors = cardColors,
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = elevation,
                    hoveredElevation = AppCardDefaults.hoverElevation
                )
            ) {
                content()
            }
        }
        is CardVariant.Clickable -> {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.98f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "scale"
            )

            val cardModifier = modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(shape)
                .then(if (border != null) Modifier.border(border, shape) else Modifier)

            androidx.compose.material3.ElevatedCard(
                modifier = cardModifier,
                onClick = onClick ?: {},
                enabled = enabled,
                shape = shape,
                colors = cardColors,
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = elevation,
                    pressedElevation = if (isPressed) elevation * 0.5f else elevation,
                    hoveredElevation = AppCardDefaults.hoverElevation
                )
            ) {
                content()
            }
        }
        is CardVariant.Surfaced -> {
            androidx.compose.material3.ElevatedCard(
                modifier = modifier,
                shape = shape,
                colors = cardColors,
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = if (tonalElevation == AppElevation.none) AppElevation.none else AppElevation.xs,
                    hoveredElevation = AppCardDefaults.hoverElevation
                )
            ) {
                content()
            }
        }
    }
}

// ==================== CARD VARIANTS ====================
/**
 * Sealed class defining card presentation variants.
 * 
 * - Static: Non-interactive card with elevation
 * - Clickable: Interactive card with press animation and scale feedback
 * - Surfaced: Low-elevation card for surface-like appearance
 */
sealed class CardVariant {
    object Static : CardVariant()
    object Clickable : CardVariant()
    object Surfaced : CardVariant()
}

// ==================== DEPRECATED ALIASES ====================
// For backwards compatibility. New code should use AppCard with variant parameter.

@Deprecated(
    message = "Use AppCard(variant = CardVariant.Static) instead",
    replaceWith = ReplaceWith("AppCard(modifier, variant = CardVariant.Static, shape, backgroundColor, contentColor, elevation = elevation, content = content)")
)
@Composable
fun ClickableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    border: BorderStroke? = null,
    elevation: Dp = AppCardDefaults.elevation,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    AppCard(
        modifier = modifier,
        variant = CardVariant.Clickable,
        onClick = onClick,
        shape = shape,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        border = border,
        elevation = elevation,
        enabled = enabled,
        content = content
    )
}

@Deprecated(
    message = "Use AppCard(variant = CardVariant.Surfaced) instead",
    replaceWith = ReplaceWith("AppCard(modifier, variant = CardVariant.Surfaced, shape = shape, tonalElevation = tonalElevation, content = content)")
)
@Composable
fun SurfaceCard(
    modifier: Modifier = Modifier,
    tonalElevation: Dp = AppElevation.none,
    shape: Shape = MaterialTheme.shapes.medium,
    content: @Composable () -> Unit
) {
    AppCard(
        modifier = modifier,
        variant = CardVariant.Surfaced,
        shape = shape,
        tonalElevation = tonalElevation,
        content = content
    )
}
