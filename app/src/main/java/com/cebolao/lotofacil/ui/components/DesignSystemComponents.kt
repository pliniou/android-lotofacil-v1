package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppTheme
import com.cebolao.lotofacil.ui.theme.AppShapes
import com.cebolao.lotofacil.ui.theme.LotofacilTheme

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
    isLoading: Boolean = false
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
                )
            ) {
                if (isLoading) {
                    ButtonLoadingIndicator()
                } else {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        AppButtonVariant.Secondary -> {
            FilledTonalButton(
                onClick = onClick,
                modifier = modifier.scale(scale),
                enabled = enabled && !isLoading,
                shape = AppShapes.md
            ) {
                if (isLoading) {
                    ButtonLoadingIndicator()
                } else {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        AppButtonVariant.Outline -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.scale(scale),
                enabled = enabled && !isLoading,
                shape = AppShapes.md
            ) {
                if (isLoading) {
                    ButtonLoadingIndicator()
                } else {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        AppButtonVariant.Ghost -> {
            TextButton(
                onClick = onClick,
                modifier = modifier.scale(scale),
                enabled = enabled && !isLoading,
                shape = AppShapes.md
            ) {
                if (isLoading) {
                    ButtonLoadingIndicator()
                } else {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
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


/**
 * Consistent surface with gradient background
 */
@Composable
fun GradientSurface(
    modifier: Modifier = Modifier,
    gradient: Brush,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(AppShapes.lg)
            .background(gradient),
        color = Color.Transparent
    ) {
        content()
    }
}

/**
 * Consistent section header
 */
@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    action: @Composable RowScope.() -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                subtitle?.let {
                    Spacer(modifier = Modifier.height(AppSpacing.xs))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            action()
        }
    }
}

/**
 * Consistent info pill/badge
 */
@Composable
fun InfoPill(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(AppTheme.sizes.chipHeight / 2),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.xs),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
}

enum class AppButtonVariant {
    Primary,
    Secondary,
    Outline,
    Ghost
}

@Preview(showBackground = true)
@Composable
private fun AppButtonPreview() {
    LotofacilTheme {
        Column(
            modifier = Modifier.padding(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            AppButton(
                text = "Primary Button",
                onClick = {}
            )
            AppButton(
                text = "Secondary Button",
                onClick = {},
                variant = AppButtonVariant.Secondary
            )
            AppButton(
                text = "Outline Button",
                onClick = {},
                variant = AppButtonVariant.Outline
            )
            AppButton(
                text = "Ghost Button",
                onClick = {},
                variant = AppButtonVariant.Ghost
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppCardPreview() {
    LotofacilTheme {
        AppCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = {}
        ) {
            Text(
                text = "Card content",
                modifier = Modifier.padding(AppSpacing.md)
            )
        }
    }
}
