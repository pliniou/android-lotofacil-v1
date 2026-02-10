package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.collections.immutable.toImmutableList
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.GameStatistic
import com.cebolao.lotofacil.domain.model.GameStatisticType
import com.cebolao.lotofacil.ui.theme.AppSpacing
import kotlinx.collections.immutable.ImmutableList

@Composable
fun GameStatsList(
    stats: ImmutableList<GameStatistic>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        for (stat in stats) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stat.type.label(), style = MaterialTheme.typography.bodyMedium)
                Text(
                    stat.value.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun GameStatisticType.label(): String {
    val labelRes = when (this) {
        GameStatisticType.SUM -> R.string.sum_label
        GameStatisticType.EVENS -> R.string.even_label
        GameStatisticType.ODDS -> R.string.odd_label
        GameStatisticType.PRIMES -> R.string.prime_label
        GameStatisticType.FIBONACCI -> R.string.fibonacci_label
        GameStatisticType.FRAME -> R.string.frame_label
        GameStatisticType.PORTRAIT -> R.string.portrait_label
        GameStatisticType.MULTIPLES_OF_3 -> R.string.multiples_of_3_label
    }
    return stringResource(id = labelRes)
}

@Composable
fun RecentHitsChartContent(
    recentHits: List<Pair<Int, Int>>,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 180.dp
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Using a small dot or icon for chart header
                Box(Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
            }
            Text(stringResource(id = R.string.recent_hits_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
        
        val chartData: ImmutableList<Pair<String, Int>> = remember(recentHits) {
            recentHits.map { Pair(it.first.toString().takeLast(4), it.second) }.toImmutableList()
        }
        val maxValue = remember(chartData) {
            (chartData.maxOfOrNull { it.second }?.coerceAtLeast(10) ?: 10)
        }
        BarChart(
            data = chartData,
            maxValue = maxValue,
            showGaussCurve = false,
            highlightThreshold = 11,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppSpacing.sm)
                .height(chartHeight)
        )
    }
}
