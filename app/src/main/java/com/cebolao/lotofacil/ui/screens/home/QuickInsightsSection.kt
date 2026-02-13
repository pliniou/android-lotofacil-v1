package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.utils.NumberFormatUtils
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.ui.components.NumberBall
import com.cebolao.lotofacil.ui.components.shimmer
import com.cebolao.lotofacil.ui.testtags.AppTestTags
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.DataLoadSource
import com.cebolao.lotofacil.viewmodels.StatisticPattern

@Composable
fun QuickInsightsSection(
    stats: StatisticsReport?,
    isLoading: Boolean,
    selectedPattern: StatisticPattern,
    selectedTimeWindow: Int,
    statisticsSource: DataLoadSource,
    isShowingStaleData: Boolean,
    onPatternSelected: (StatisticPattern) -> Unit,
    onTimeWindowSelected: (Int) -> Unit,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = AppElevation.sm),
        colors = CardDefaults.elevatedCardColors(containerColor = colors.surface)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                Icon(
                    Icons.Default.Analytics,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(id = R.string.quick_insights_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.sm))

            DataStatusLabel(
                statisticsSource = statisticsSource,
                isShowingStaleData = isShowingStaleData
            )

            Spacer(modifier = Modifier.height(AppSpacing.md))

            InsightFilterRow(
                selectedPattern = selectedPattern,
                selectedTimeWindow = selectedTimeWindow,
                onPatternSelected = onPatternSelected,
                onTimeWindowSelected = onTimeWindowSelected
            )

            Spacer(modifier = Modifier.height(AppSpacing.lg))

            AnimatedContent(targetState = isLoading, label = "stats_preview_loading") { loading ->
                if (loading) {
                    CompactStatsLoadingSkeleton()
                } else {
                    stats?.let { report ->
                        val distribution = report.distributionFor(selectedPattern)
                        val dominant = distribution.maxByOrNull { it.value }

                        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)) {
                            Text(
                                text = stringResource(R.string.most_drawn_numbers),
                                style = MaterialTheme.typography.labelLarge,
                                color = colors.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                report.mostFrequentNumbers.take(5).forEach { (num, freq) ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        NumberBall(number = num, size = 36.dp)
                                        Text(
                                            text = "${NumberFormatUtils.formatInteger(freq)}x",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = colors.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            dominant?.let { (bucket, frequency) ->
                                Text(
                                    text = stringResource(
                                        R.string.home_pattern_hint,
                                        stringResource(selectedPattern.titleRes),
                                        NumberFormatUtils.formatInteger(bucket),
                                        NumberFormatUtils.formatInteger(frequency)
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant
                                )
                            }
                        }
                    } ?: Text(
                        text = stringResource(R.string.empty_state_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.xl))

            FilledTonalButton(
                onClick = onViewAll,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(AppTestTags.HomeInsightsButton),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = colors.primaryContainer,
                    contentColor = colors.onPrimaryContainer
                )
            ) {
                Text(text = stringResource(id = R.string.quick_insights_cta))
                Spacer(modifier = Modifier.width(AppSpacing.sm))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InsightFilterRow(
    selectedPattern: StatisticPattern,
    selectedTimeWindow: Int,
    onPatternSelected: (StatisticPattern) -> Unit,
    onTimeWindowSelected: (Int) -> Unit
) {
    val windows = listOf(0, 20, 50, 100)

    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            StatisticPattern.entries.forEach { pattern ->
                FilterChip(
                    selected = pattern == selectedPattern,
                    onClick = { onPatternSelected(pattern) },
                    label = {
                        Text(
                            text = stringResource(id = pattern.titleRes),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors()
                )
            }
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            windows.forEach { window ->
                val label = if (window == 0) {
                    stringResource(R.string.time_window_all)
                } else {
                    stringResource(R.string.time_window_last, window)
                }
                FilterChip(
                    selected = selectedTimeWindow == window,
                    onClick = { onTimeWindowSelected(window) },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors()
                )
            }
        }
    }
}

@Composable
private fun DataStatusLabel(
    statisticsSource: DataLoadSource,
    isShowingStaleData: Boolean
) {
    val sourceText = when (statisticsSource) {
        DataLoadSource.CACHE -> stringResource(R.string.home_source_stats_cache)
        DataLoadSource.NETWORK -> stringResource(R.string.home_source_stats_network)
        DataLoadSource.COMPUTED -> stringResource(R.string.home_source_stats_computed)
    }
    val message = if (isShowingStaleData) {
        "$sourceText - ${stringResource(R.string.home_stale_data_warning)}"
    } else {
        sourceText
    }

    Text(
        text = message,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

private fun StatisticsReport.distributionFor(pattern: StatisticPattern): Map<Int, Int> {
    return when (pattern) {
        StatisticPattern.SUM -> sumDistribution
        StatisticPattern.EVENS -> evenDistribution
        StatisticPattern.FIBONACCI -> fibonacciDistribution
        StatisticPattern.MULTIPLES_OF_3 -> multiplesOf3Distribution
    }
}

@Composable
private fun CompactStatsLoadingSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)) {
        Box(
            Modifier
                .size(100.dp, 16.dp)
                .clip(MaterialTheme.shapes.small)
                .shimmer()
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(5) {
                Box(
                    Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .shimmer()
                )
            }
        }
    }
}
