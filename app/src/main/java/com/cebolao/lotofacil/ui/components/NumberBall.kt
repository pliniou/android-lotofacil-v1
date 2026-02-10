package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.AppAnimationConstants
import com.cebolao.lotofacil.core.utils.NumberFormatUtils
import com.cebolao.lotofacil.ui.testtags.AppTestTags

// ==================== LAYER 1: DISPLAY (Pure rendering) ====================
/**
 * Pure presentation layer - renders number ball with no state management.
 * Perfect for lists, previews, and simple displays.
 * 
 * @param number The number to display (1-25)
 * @param containerColor Background color
 * @param contentColor Text color
 * @param borderColor Border color (if no border, use containerColor)
 * @param size Ball diameter
 * @param borderWidth Border thickness
 * @param shape Card shape
 * @param modifier Modifier for additional styling
 */
@Stable
@Composable
fun NumberBallDisplay(
    number: Int,
    containerColor: Color,
    contentColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    borderWidth: Dp = 1.dp,
    shape: androidx.compose.ui.graphics.Shape = MaterialTheme.shapes.medium,
    contentDescription: String = ""
) {
    val tonalElevation = AppElevation.none
    
    Surface(
        modifier = modifier
            .size(size)
            .clip(shape)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = shape
            )
            .semantics {
                if (contentDescription.isNotEmpty()) {
                    this.contentDescription = contentDescription
                }
                role = Role.Button
            },
        shape = shape,
        color = containerColor,
        tonalElevation = tonalElevation
    ) {
        val formattedNumber = remember(number) { NumberFormatUtils.formatLotteryNumber(number) }

        Box(contentAlignment = Alignment.Center) {
            Text(
                text = formattedNumber,
                color = contentColor,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = (size.value / 2.6).sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

// ==================== LAYER 2: INTERACTIVE (With state transitions) ====================
/**
 * Interactive presentation layer - renders animated number ball with interaction handling.
 * Supports animation on state changes but user provides colors/visibility.
 * Good for controlled components where parent manages state.
 * 
 * @param number The number to display
 * @param containerColor Static container color
 * @param contentColor Static content color
 * @param borderColor Static border color
 * @param isDisabled Whether ball is disabled
 * @param onClick Click handler
 */
@Composable
fun NumberBallInteractive(
    number: Int,
    containerColor: Color,
    contentColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    borderWidth: Dp = 1.dp,
    shape: Shape = MaterialTheme.shapes.medium,
    isDisabled: Boolean = false,
    onClick: (() -> Unit)? = null,
    contentDescription: String = ""
) {
    val animationDuration = AppAnimationConstants.Durations.Medium.toInt()
    val hapticFeedback = LocalHapticFeedback.current
    
    val animatedContainerColor by animateColorAsState(
        targetValue = containerColor,
        animationSpec = tween(animationDuration),
        label = "containerColor"
    )
    val animatedContentColor by animateColorAsState(
        targetValue = contentColor,
        animationSpec = tween(animationDuration),
        label = "contentColor"
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = borderColor,
        animationSpec = tween(animationDuration),
        label = "borderColor"
    )

    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .size(size)
            .clip(shape)
            .border(
                width = borderWidth,
                color = animatedBorderColor,
                shape = shape
            )
            .then(
                if (onClick != null && !isDisabled) {
                    Modifier
                        .clickable(interactionSource, indication = null) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onClick()
                        }
                        .focusable(interactionSource = interactionSource)
                        .onKeyEvent { keyEvent ->
                            if (keyEvent.key == Key.Enter || keyEvent.key == Key.Spacebar) {
                                if (keyEvent.type == KeyEventType.KeyUp) {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onClick()
                                    true
                                } else false
                            } else false
                        }
                } else Modifier
            )
            .semantics {
                if (contentDescription.isNotEmpty()) {
                    this.contentDescription = contentDescription
                }
                role = Role.Button
            },
        shape = shape,
        color = animatedContainerColor,
        tonalElevation = AppElevation.none
    ) {
        val formattedNumber = remember(number) { NumberFormatUtils.formatLotteryNumber(number) }

        Box(contentAlignment = Alignment.Center) {
            Text(
                text = formattedNumber,
                color = animatedContentColor,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = (size.value / 2.6).sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

// ==================== LAYER 3: FULL COMPONENT (With state logic) ====================
/**
 * Complete NumberBall component - manages its own state, colors, and interactions.
 * This is the main entry point most components should use.
 * 
 * Provides intelligent color management based on state:
 * - Disabled: Faded appearance
 * - Selected: Primary color with higher border width
 * - Highlighted: Container variant with border highlight
 * - Default: Surface variant appearance
 * 
 * @param number The lottery number (1-25)
 * @param isSelected Whether number is currently selected
 * @param isHighlighted Whether number should be highlighted
 * @param isDisabled Whether number is disabled (no interaction)
 * @param onClick Callback when number is clicked
 */
@Stable
@Composable
fun NumberBall(
    number: Int,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    isSelected: Boolean = false,
    isHighlighted: Boolean = false,
    isDisabled: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val shape = MaterialTheme.shapes.medium
    val colors = MaterialTheme.colorScheme

    // State-based colors
    val (containerColor, contentColor, borderColor) = when {
        isDisabled -> Triple(
            colors.onSurface.copy(alpha = 0.08f),
            colors.onSurface.copy(alpha = 0.30f),
            colors.outline.copy(alpha = 0.5f)
        )
        isSelected -> Triple(
            colors.primary,
            colors.onPrimary,
            colors.primary.copy(alpha = 0.8f)
        )
        isHighlighted -> Triple(
            colors.primaryContainer,
            colors.primary,
            colors.primary.copy(alpha = 0.8f)
        )
        else -> Triple(
            colors.surfaceVariant,
            colors.onSurface,
            colors.outline.copy(alpha = 0.7f)
        )
    }

    val stateResId = when {
        isSelected -> R.string.number_state_selected
        isHighlighted -> R.string.number_state_highlighted
        isDisabled -> R.string.number_state_disabled
        else -> R.string.number_state_available
    }
    val stateLabel = stringResource(id = stateResId)
    val numberContentDescription = stringResource(
        id = R.string.number_content_description,
        number,
        stateLabel
    )

    val borderWidthValue = remember(isSelected, isHighlighted) {
        if (isHighlighted && !isSelected) 2.dp else 1.dp
    }

    val taggedModifier = modifier.testTag("${AppTestTags.NumberBallPrefix}$number")

    NumberBallInteractive(
        number = number,
        containerColor = containerColor,
        contentColor = contentColor,
        borderColor = borderColor,
        modifier = taggedModifier,
        size = size,
        borderWidth = borderWidthValue,
        shape = shape,
        isDisabled = isDisabled,
        onClick = onClick,
        contentDescription = numberContentDescription
    )
}
