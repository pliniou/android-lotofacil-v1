package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.utils.NumberFormatUtils
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.ui.components.NumberBall
import com.cebolao.lotofacil.ui.components.shimmer
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.testtags.AppTestTags

/**
 * Compact statistics preview for HomeScreen.
 * Shows only key highlights (top 5 frequent/overdue numbers) with a link to full analysis.
 * Detailed statistics are centralized in FrequencyAnalysisScreen (Insights).
 */
@Composable
fun CompactStatisticsPreview(
    stats: StatisticsReport?,
    isLoading: Boolean,
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
            // Header with title and "View all" button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    Icon(
                        Icons.Default.Timeline,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.statistics_center),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(
                    onClick = onViewAll,
                    modifier = Modifier.testTag(AppTestTags.HomeInsightsButton)
                ) {
                    Text(
                        text = stringResource(id = R.string.prize_details_expand),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.md))

            // Summary row: draws analyzed + average sum
            stats?.let { report ->
                if (report.totalDrawsAnalyzed > 0) {
                    val formattedDraws = remember(report.totalDrawsAnalyzed) {
                        NumberFormatUtils.formatInteger(report.totalDrawsAnalyzed)
                    }
                    val formattedAvg = remember(report.averageSum) {
                        "%.1f".format(report.averageSum)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SummaryChip(
                            label = stringResource(id = R.string.draws_analyzed_label),
                            value = formattedDraws
                        )
                        SummaryChip(
                            label = stringResource(id = R.string.avg_sum_range_label),
                            value = formattedAvg
                        )
                    }
                    Spacer(modifier = Modifier.height(AppSpacing.sm))
                }
            }

            AnimatedContent(targetState = isLoading, label = "stats_preview_loading") { loading ->
                if (loading) {
                    CompactStatsLoadingSkeleton()
                } else {
                    stats?.let { report ->
                        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)) {
                            // Most Overdue (compact row)
                            CompactNumberRow(
                                title = stringResource(id = R.string.most_delayed_numbers),
                                icon = Icons.Default.HourglassEmpty,
                                iconTint = colors.tertiary,
                                numbers = report.mostOverdueNumbers.take(5),
                                suffix = stringResource(id = R.string.delayed_suffix)
                            )

                            // Most Frequent (compact row)
                            CompactNumberRow(
                                title = stringResource(id = R.string.most_drawn_numbers),
                                icon = Icons.Default.LocalFireDepartment,
                                iconTint = colors.error,
                                numbers = report.mostFrequentNumbers.take(5),
                                suffix = stringResource(id = R.string.frequency_suffix)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactNumberRow(
    title: String,
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    numbers: List<Pair<Int, Int>>,
    suffix: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.semantics {
            contentDescription = "$title: ${numbers.joinToString { it.first.toString() }}"
        },
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for ((num, value) in numbers) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                ) {
                    NumberBall(
                        number = num,
                        size = 40.dp
                    )
                    Text(
                        text = "${NumberFormatUtils.formatInteger(value)}$suffix",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactStatsLoadingSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)) {
        repeat(2) {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                Box(
                    Modifier
                        .size(120.dp, 16.dp)
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
                                .size(40.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .shimmer()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryChip(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
