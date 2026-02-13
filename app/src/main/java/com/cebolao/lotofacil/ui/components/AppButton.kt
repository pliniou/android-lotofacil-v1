package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.theme.AppShapes

/**
 * Consistent button components using Design System tokens
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppButtonVariant = AppButtonVariant.Primary,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale = if (isPressed) 0.98f else 1f
    
    when (variant) {
        AppButtonVariant.Primary -> {
            Button(
                onClick = onClick,
                modifier = modifier.scale(scale),
                enabled = enabled && !isLoading,
                shape = AppShapes.md,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 0.dp
                ),
                contentPadding = ButtonDefaults.ContentPadding
            ) {
                ButtonContent(text, isLoading, icon)
            }
        }
        
        AppButtonVariant.Secondary -> {
            FilledTonalButton(
                onClick = onClick,
                modifier = modifier.scale(scale),
                enabled = enabled && !isLoading,
                shape = AppShapes.md
            ) {
                ButtonContent(text, isLoading, icon)
            }
        }
        
        AppButtonVariant.Outline -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.scale(scale),
                enabled = enabled && !isLoading,
                shape = AppShapes.md
            ) {
                ButtonContent(text, isLoading, icon)
            }
        }
        
        AppButtonVariant.Ghost -> {
            TextButton(
                onClick = onClick,
                modifier = modifier.scale(scale),
                enabled = enabled && !isLoading,
                shape = AppShapes.md
            ) {
                ButtonContent(text, isLoading, icon)
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    isLoading: Boolean,
    icon: (@Composable () -> Unit)? = null
) {
    if (isLoading) {
        ButtonLoadingIndicator()
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            icon?.invoke()
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ButtonLoadingIndicator() {
    androidx.compose.material3.CircularProgressIndicator(
        modifier = Modifier.size(16.dp),
        strokeWidth = 2.dp,
        color = MaterialTheme.colorScheme.onPrimary
    )
}

enum class AppButtonVariant {
    Primary,
    Secondary,
    Outline,
    Ghost
}
