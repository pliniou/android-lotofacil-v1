package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.ui.components.BarChart
import com.cebolao.lotofacil.ui.components.NumberBall
import com.cebolao.lotofacil.ui.components.shimmer
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.iconMedium
import com.cebolao.lotofacil.ui.theme.iconSmall
import com.cebolao.lotofacil.viewmodels.StatisticPattern
import com.cebolao.lotofacil.core.utils.NumberFormatUtils
import kotlinx.collections.immutable.toImmutableList

@Composable
fun StatisticsSection(
    stats: StatisticsReport?,
    isStatsLoading: Boolean,
    selectedTimeWindow: Int,
    selectedPattern: StatisticPattern,
    onTimeWindowSelected: (Int) -> Unit,
    onPatternSelected: (StatisticPattern) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Icon(
                Icons.Default.Timeline,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = stringResource(id = R.string.statistics_center),
                style = MaterialTheme.typography.titleLarge,
                color = colors.onSurface,
                fontWeight = FontWeight.Bold
            )
        }

        stats?.let { report ->
            // Time Window Selector
            TimeWindowSelector(selectedTimeWindow, onTimeWindowSelected)

            AnimatedContent(targetState = isStatsLoading, label = "stats_loading") { loading ->
                if (loading) {
                    StatsLoadingSkeleton()
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)) {
                        
                        // Most Overdue Card
                        ElevatedStatCard(
                            title = stringResource(id = R.string.most_delayed_numbers),
                            icon = Icons.Default.HourglassEmpty,
                            iconTint = colors.tertiary
                        ) {
                            GridNumbersDisplay(
                                numbers = report.mostOverdueNumbers,
                                suffix = stringResource(id = R.string.delayed_suffix)
                            )
                        }

                        // Most Frequent Card
                        ElevatedStatCard(
                            title = stringResource(id = R.string.most_drawn_numbers),
                            icon = Icons.Default.LocalFireDepartment,
                            iconTint = colors.error
                        ) {
                            GridNumbersDisplay(
                                numbers = report.mostFrequentNumbers,
                                suffix = stringResource(id = R.string.frequency_suffix)
                            )
                        }

                        // Charts Section
                        ElevatedCard(
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = AppElevation.sm),
                            colors = CardDefaults.elevatedCardColors(containerColor = colors.surface)
                        ) {
                            Column(modifier = Modifier.padding(vertical = AppSpacing.lg)) {
                                PaddingBox {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                                        Icon(Icons.Default.BarChart, null, tint = colors.primary)
                                        Text(
                                            text = stringResource(id = R.string.pattern_analysis_title),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(AppSpacing.md))
                                DistributionCharts(report, selectedPattern, onPatternSelected)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeWindowSelector(selected: Int, onSelect: (Int) -> Unit) {
    val windows = listOf(9, 13, 20, 25, 30, 50, 200, 500, 1000, 2000, 0)
    
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        PaddingBox {
            Text(
                text = stringResource(id = R.string.analysis_period_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            contentPadding = PaddingValues(horizontal = AppSpacing.lg)
        ) {
            items(windows, key = { it }) { window ->
                val label = if (window == 0) stringResource(id = R.string.time_window_all)
                           else stringResource(id = R.string.time_window_last, window)
                
                FilterChip(
                    selected = window == selected,
                    onClick = { onSelect(window) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors()
                )
            }
        }
    }
}

@Composable
private fun ElevatedStatCard(
    title: String,
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = AppElevation.sm),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.lg)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(iconMedium())
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(AppSpacing.lg))
            content()
        }
    }
}

@Composable
private fun GridNumbersDisplay(numbers: List<Pair<Int, Int>>, suffix: String) {
    // Determine the max columns dynamically or use FlowRow better
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
         for ((num, value) in numbers.take(5)) { // Simplified to Row of 5 for now
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
            ) {
                NumberBall(
                    number = num, 
                    size = 48.dp
                )
                Text(
                    text = "${NumberFormatUtils.formatInteger(value)}$suffix",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun DistributionCharts(
    stats: StatisticsReport,
    selected: StatisticPattern,
    onSelect: (StatisticPattern) -> Unit
) {
    val patterns = remember { StatisticPattern.entries.toTypedArray() }
    
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = AppSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            items(patterns, key = { it.name }) { pattern ->
                val isSelected = selected == pattern
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelect(pattern) },
                    label = { Text(stringResource(id = pattern.titleRes)) },
                    leadingIcon = { 
                        Icon(
                            pattern.icon, 
                            null, 
                            modifier = Modifier.size(iconSmall())
                        ) 
                    }
                )
            }
        }

        val data = remember(selected, stats) {
            when (selected) {
                StatisticPattern.SUM -> stats.sumDistribution
                StatisticPattern.EVENS -> stats.evenDistribution
                StatisticPattern.PRIMES -> stats.primeDistribution
                StatisticPattern.FRAME -> stats.frameDistribution
                StatisticPattern.PORTRAIT -> stats.portraitDistribution
                StatisticPattern.FIBONACCI -> stats.fibonacciDistribution
                StatisticPattern.MULTIPLES_OF_3 -> stats.multiplesOf3Distribution
            }.toList().sortedBy { it.first }.map { NumberFormatUtils.formatInteger(it.first) to it.second }.toImmutableList()
        }
        
        val max by remember(data) { 
            derivedStateOf { data.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1 }
        }

        AnimatedContent(targetState = data, label = "chart") { list ->
            if (list.isNotEmpty()) {
                BarChart(
                    data = list,
                    maxValue = max,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.md)
                )
            }
        }
    }
}

@Composable
private fun PaddingBox(content: @Composable () -> Unit) {
    Box(modifier = Modifier.padding(horizontal = AppSpacing.lg)) {
        content()
    }
}




@Composable
fun StatsLoadingSkeleton() {
    val colors = MaterialTheme.colorScheme
    
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)) {
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
            Column(Modifier.padding(AppSpacing.lg), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                Box(Modifier.size(150.dp, 20.dp).clip(MaterialTheme.shapes.small).shimmer())
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    for (i in 0 until 5) {
                        Box(Modifier.size(40.dp).clip(MaterialTheme.shapes.medium).shimmer())
                    }
                }
            }
        }
        HorizontalDivider(color = colors.outline.copy(alpha = 0.1f))
        
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
             Column(Modifier.padding(AppSpacing.lg)) {
                 Box(Modifier.fillMaxWidth().height(150.dp).clip(MaterialTheme.shapes.medium).shimmer())
             }
        }
    }
}
