package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.theme.AppShapes
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppTheme
import com.cebolao.lotofacil.ui.theme.BrandColors
import com.cebolao.lotofacil.ui.theme.LotofacilTheme

/**
 * Enhanced card component with consistent styling and interactions
 */
@Composable
fun EnhancedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    isPinned: Boolean = false,
    elevation: androidx.compose.ui.unit.Dp = AppTheme.elevation.none,
    content: @Composable () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val backgroundColor = if (isPinned) colors.primaryContainer else colors.surface
    val variant = if (onClick != null) CardVariant.Clickable else CardVariant.Static

    AppCard(
        modifier = modifier,
        variant = variant,
        onClick = onClick,
        shape = AppShapes.md,
        backgroundColor = backgroundColor,
        elevation = elevation
    ) {
        content()
    }
}

/**
 * Stats card with icon and value
 */
@Composable
fun StatsCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    isHighlighted: Boolean = false
) {
    EnhancedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = AppTheme.elevation.xs
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(AppTheme.sizes.iconMedium)
                    .background(
                        if (isHighlighted) {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    BrandColors.RoxoLotofacil,
                                    MaterialTheme.colorScheme.primary
                                )
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        },
                        shape = AppShapes.sm
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(AppTheme.sizes.iconSmall)
                )
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isHighlighted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Quick action card for navigation
 */
@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    EnhancedCard(
        modifier = modifier,
        onClick = onClick,
        elevation = AppTheme.elevation.xs
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(AppTheme.sizes.iconLarge)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatsCardPreview() {
    LotofacilTheme {
        StatsCard(
            title = "Último Sorteio",
            value = "3.456",
            icon = Icons.Filled.Info
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun QuickActionCardPreview() {
    LotofacilTheme {
        QuickActionCard(
            title = "Gerar Jogos",
            icon = Icons.Filled.Add,
            onClick = {}
        )
    }
}
