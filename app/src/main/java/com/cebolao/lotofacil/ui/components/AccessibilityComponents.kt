package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.theme.AppShapes
import com.cebolao.lotofacil.ui.theme.AppSpacing

/**
 * Accessibility-focused click area with proper semantic descriptions
 */
@Composable
fun AccessibleClickArea(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val ripple = rememberRipple(bounded = false)
    
    Box(
        modifier = modifier
            .clip(AppShapes.md)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple,
                enabled = enabled,
                onClick = onClick
            )
            .semantics {
                this.contentDescription = contentDescription
            }
    ) {
        content()
    }
}

/**
 * Accessible text with proper content description
 */
@Composable
fun AccessibleText(
    text: String,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    color: Color = LocalContentColor.current
) {
    Text(
        text = text,
        modifier = modifier.semantics {
            this.contentDescription = contentDescription ?: text
            this.text = AnnotatedString(text)
        },
        maxLines = maxLines,
        color = color
    )
}

/**
 * Accessible button with proper semantic description
 */
@Composable
fun AccessibleButton(
    text: String,
    contentDescription: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    AppButton(
        text = text,
        onClick = onClick,
        modifier = modifier.semantics {
            this.contentDescription = contentDescription ?: text
        },
        enabled = enabled
    )
}

/**
 * Accessible card with proper semantic description
 */
@Composable
fun AccessibleCard(
    contentDescription: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AppCard(
        modifier = modifier.semantics {
            this.contentDescription = contentDescription
        },
        onClick = onClick
    ) {
        content()
    }
}

/**
 * Screen reader announcement component
 */
@Composable
fun ScreenReaderAnnouncement(
    announcement: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .alpha(0f) // Invisible but readable by screen readers
            .padding(AppSpacing.xs)
            .clearAndSetSemantics {
                contentDescription = announcement
            }
    )
}
