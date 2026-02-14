package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.utils.NumberFormatUtils
import com.cebolao.lotofacil.ui.components.AppButton
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.ui.components.AppButtonVariant
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.CardVariant
import com.cebolao.lotofacil.ui.components.BarChart
import com.cebolao.lotofacil.ui.components.InfoIcon
import com.cebolao.lotofacil.ui.components.shimmer
import com.cebolao.lotofacil.ui.text.AppStrings
import com.cebolao.lotofacil.ui.testtags.AppTestTags
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.DataLoadSource
import com.cebolao.lotofacil.viewmodels.StatisticPattern
import kotlinx.collections.immutable.toImmutableList

@Composable
private fun FilterGroup(
    label: String,
    tooltipText: String,
    options: List<FilterOption>,
    selectedId: Any,
    onOptionSelected: (Any) -> Unit,
    onInfoClicked: () -> Unit,
    enabled: Boolean = true
) {
    val colors = MaterialTheme.colorScheme

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.onSurface
        )
        InfoIcon(
            tooltipText = tooltipText,
            onClick = onInfoClicked,
            enabled = enabled
        )
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = AppSpacing.lg),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        items(options) { option ->
            FilterChip(
                selected = selectedId == option.id,
                onClick = { onOptionSelected(option.id) },
                enabled = enabled,
                label = {
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colors.primary,
                    selectedLabelColor = colors.onPrimary
                )
            )
        }
    }
}

private data class FilterOption(val id: Any, val label: String)

@Composable
private fun HotNumbersSection(
    modifier: Modifier = Modifier,
    numbers: List<Pair<Int, Int>>,
    onViewAll: () -> Unit,
    isActionEnabled: Boolean = true
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Text(
            text = stringResource(R.string.most_drawn_numbers),
            style = MaterialTheme.typography.labelMedium,
            color = colors.primary,
            fontWeight = FontWeight.Bold
        )
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            numbers.take(5).forEach { (num, freq) ->
                Surface(
                    color = colors.tertiaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = {
                            Text(
                                num.toString(),
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.onTertiaryContainer
                            )
                        },
                        supportingContent = {
                            Text(
                                stringResource(
                                    id = R.string.home_hot_number_frequency,
                                    NumberFormatUtils.formatInteger(freq)
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onTertiaryContainer
                            )
                        }
                    )
                }
            }
        }
        if (numbers.size > 5) {
            TextButton(
                onClick = onViewAll,
                modifier = Modifier.align(Alignment.End),
                enabled = isActionEnabled
            ) {
                Text(stringResource(id = AppStrings.Ctas.viewAll))
            }
        }
    }
}

@Composable
private fun ColdNumbersSection(
    modifier: Modifier = Modifier,
    numbers: List<Pair<Int, Int>>,
    onViewAll: () -> Unit,
    isActionEnabled: Boolean = true
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Text(
            text = stringResource(R.string.overdue_numbers_label),
            style = MaterialTheme.typography.labelMedium,
            color = colors.error,
            fontWeight = FontWeight.Bold
        )
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            numbers.take(5).forEach { (num, overdue) ->
                Surface(
                    color = colors.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = {
                            Text(
                                num.toString(),
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.onErrorContainer
                            )
                        },
                        supportingContent = {
                            Text(
                                stringResource(
                                    id = R.string.home_cold_number_overdue,
                                    NumberFormatUtils.formatInteger(overdue)
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onErrorContainer
                            )
                        }
                    )
                }
            }
        }
        if (numbers.size > 5) {
            TextButton(
                onClick = onViewAll,
                modifier = Modifier.align(Alignment.End),
                enabled = isActionEnabled
            ) {
                Text(stringResource(id = AppStrings.Ctas.viewAll))
            }
        }
    }
}

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
    modifier: Modifier = Modifier,
    isUpdateInProgress: Boolean = false,
    onViewAllHot: () -> Unit = {},
    onViewAllCold: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    var showTooltip by remember { mutableStateOf<Int?>(null) }

    if (showTooltip != null) {
        AlertDialog(
            onDismissRequest = { showTooltip = null },
            title = { Text(stringResource(id = R.string.common_information_title)) },
            text = { Text(stringResource(id = showTooltip!!)) },
            confirmButton = {
                TextButton(onClick = { showTooltip = null }) {
                    Text(stringResource(id = R.string.common_ok))
                }
            }
        )
    }
    AppCard(
        modifier = modifier.fillMaxWidth(),
        variant = CardVariant.Elevated,
        isGlassmorphic = true
    ) {
        Column {
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

            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
                FilterGroup(
                    label = stringResource(id = AppStrings.Labels.analysisType),
                    tooltipText = stringResource(id = AppStrings.Tooltips.analysisType),
                    options = StatisticPattern.entries.map { FilterOption(it, stringResource(it.titleRes)) },
                    selectedId = selectedPattern,
                    onOptionSelected = { onPatternSelected(it as StatisticPattern) },
                    onInfoClicked = { showTooltip = AppStrings.Tooltips.analysisType },
                    enabled = !isUpdateInProgress
                )
                FilterGroup(
                    label = stringResource(id = AppStrings.Labels.period),
                    tooltipText = stringResource(id = AppStrings.Tooltips.period),
                    options = listOf(0, 20, 50, 100).map { FilterOption(it, if (it == 0) stringResource(R.string.time_window_all) else stringResource(R.string.time_window_last, it)) },
                    selectedId = selectedTimeWindow,
                    onOptionSelected = { onTimeWindowSelected(it as Int) },
                    onInfoClicked = { showTooltip = AppStrings.Tooltips.period },
                    enabled = !isUpdateInProgress
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.lg))

            AnimatedContent(targetState = isLoading, label = "stats_preview_loading") { loading ->
                if (loading) {
                    CompactStatsLoadingSkeleton()
                } else {
                    stats?.let { report ->
                        val distribution = report.distributionFor(selectedPattern)
                        
                        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)) {
                            
                            // Bar Chart for Pattern Distribution
                            if (distribution.isNotEmpty()) {
                                val chartData = remember(distribution) {
                                    distribution.map { (key, value) ->
                                        "%02d".format(key) to value
                                    }.sortedBy { it.first.toIntOrNull() ?: 0 }
                                    .toImmutableList()
                                }
                                val maxValue = remember(distribution) { distribution.values.maxOrNull() ?: 1 }

                                BarChart(
                                    data = chartData,
                                    maxValue = maxValue,
                                    highlightThreshold = maxValue,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp),
                                    showGaussCurve = true
                                )
                            }

                            Spacer(modifier = Modifier.height(AppSpacing.sm))

                            // Hot & Cold Numbers
                            BoxWithConstraints {
                                val isWide = maxWidth >= 600.dp
                                if (isWide) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
                                    ) {
                                        HotNumbersSection(
                                            modifier = Modifier.weight(1f),
                                            numbers = report.mostFrequentNumbers,
                                            onViewAll = onViewAllHot,
                                        )
                                        VerticalDivider(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .width(1.dp)
                                        )
                                        ColdNumbersSection(
                                            modifier = Modifier.weight(1f),
                                            numbers = report.mostOverdueNumbers,
                                            onViewAll = onViewAllCold,
                                        )
                                    }
                                } else {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
                                    ) {
                                        HotNumbersSection(
                                            numbers = report.mostFrequentNumbers,
                                            onViewAll = onViewAllHot,
                                        )
                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                        ColdNumbersSection(
                                            numbers = report.mostOverdueNumbers,
                                            onViewAll = onViewAllCold,
                                        )
                                    }
                                }
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

            AppButton(
                text = stringResource(id = R.string.quick_insights_cta),
                onClick = onViewAll,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(AppTestTags.HomeInsightsButton),
                variant = AppButtonVariant.Secondary,
            )
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




