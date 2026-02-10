package com.cebolao.lotofacil.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.service.UserStats
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.NumberBall
import com.cebolao.lotofacil.ui.theme.AppAlpha
import com.cebolao.lotofacil.ui.theme.AppSpacing
import kotlin.math.abs

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserStatsSection(
    stats: UserStats,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
    ) {
        // Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            StatSummaryCard(
                title = stringResource(id = R.string.stats_total_generated),
                value = stats.totalGamesGenerated.toString(),
                modifier = Modifier.weight(1f)
            )
            StatSummaryCard(
                title = stringResource(id = R.string.stats_total_played),
                value = stats.totalGamesPlayed.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        // Most Played Numbers
        AppCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(AppSpacing.md)) {
                Text(
                    text = stringResource(id = R.string.stats_top_numbers),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = AppSpacing.sm)
                )
                
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    stats.mostPlayedNumbers.forEach { (number, count) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            NumberBall(number = number)
                            Text(
                                text = "${count}x",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Performance
        AppCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ) {
            Column(modifier = Modifier.padding(AppSpacing.md)) {
                Text(
                    text = stringResource(id = R.string.stats_performance),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = AppSpacing.sm)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.stats_avg_hits),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                        modifier = Modifier.fillMaxWidth(0.5f)
                    ) {
                        Text(
                            text = "%.2f".format(stats.averageHits),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black
                        )
                        
                        TrendIndicator(value = stats.averageHits)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatSummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
@Composable
private fun TrendIndicator(
    value: Float,
    modifier: Modifier = Modifier,
    baselineValue: Float = 7.5f // Expected value for 15 numbers in 15 draws
) {
    val colors = MaterialTheme.colorScheme
    val (arrow, color, delta, trendLabel) = remember(value) {
        val diff = value - baselineValue
        when {
            abs(diff) < 0.5 -> Tuple4("→", colors.outline, "0%", "estável")
            diff > 0 -> Tuple4("↑", colors.primary, "+${(diff / baselineValue * 100).toInt()}%", "alta")
            else -> Tuple4("↓", colors.error, "${(diff / baselineValue * 100).toInt()}%", "baixa")
        }
    }
    val trendDescription = stringResource(
        R.string.trend_indicator_description,
        trendLabel,
        delta
    )
    
    Box(
        modifier = modifier
            .size(40.dp)
            .background(
                color = color.copy(alpha = AppAlpha.textDisabled),
                shape = RoundedCornerShape(AppSpacing.sm)
            )
            .semantics {
                contentDescription = trendDescription
                role = Role.Image
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(AppSpacing.xs)
        ) {
            Text(
                text = arrow,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = delta,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Classe auxiliar para retornar 4 valores
data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
