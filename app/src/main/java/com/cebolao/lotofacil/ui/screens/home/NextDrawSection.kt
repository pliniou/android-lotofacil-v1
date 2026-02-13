package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.utils.NumberFormatUtils
import com.cebolao.lotofacil.ui.components.EnhancedCard
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.AppSize
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.NextDrawUiModel

@Composable
fun NextDrawSection(
    nextDraw: NextDrawUiModel,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    
    EnhancedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = AppElevation.sm,
        containerColor = if (nextDraw.isAccumulated) colors.errorContainer.copy(alpha = 0.1f) else colors.surface
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.next_draw_card_title).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                
                if (nextDraw.isAccumulated) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = colors.error,
                            modifier = Modifier.size(AppSize.iconSmall)
                        )
                        Text(
                            text = stringResource(R.string.accumulated),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Value
            Text(
                text = NumberFormatUtils.formatCurrency(nextDraw.prizeEstimate),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = if (nextDraw.isAccumulated) colors.error else colors.primary
            )
            
            // Info Footer
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(AppSize.iconSmall)
                )
                Spacer(modifier = Modifier.width(AppSpacing.xs))
                Text(
                    text = stringResource(
                        R.string.next_contest_format,
                        NumberFormatUtils.formatInteger(nextDraw.contestNumber)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(AppSpacing.xs))
                Text(
                    text = "•",
                    color = colors.outline
                )
                Spacer(modifier = Modifier.width(AppSpacing.xs))
                Text(
                    text = nextDraw.date.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}


