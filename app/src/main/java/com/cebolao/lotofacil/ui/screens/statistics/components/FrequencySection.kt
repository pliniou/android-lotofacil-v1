package com.cebolao.lotofacil.ui.screens.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.FrequencyAnalysis
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.BarChart
import com.cebolao.lotofacil.ui.components.SectionHeader
import com.cebolao.lotofacil.ui.theme.AppCharts
import com.cebolao.lotofacil.ui.theme.AppSpacing
import kotlinx.collections.immutable.toImmutableList

@Composable
fun FrequencySection(analysis: FrequencyAnalysis) {
    SectionHeader(
        title = stringResource(R.string.frequency_analysis_title),
        icon = Icons.Outlined.BarChart,
        titleStyle = MaterialTheme.typography.titleLarge,
        showDivider = true
    )

    val chartData = remember(analysis.frequencies) {
        analysis.frequencies
            .toList()
            .sortedBy { it.first }
            .map { (number, freq) -> "%02d".format(number) to freq }
            .toImmutableList()
    }
    val maxValue = remember(analysis.frequencies) { analysis.frequencies.values.maxOrNull() ?: 1 }

    BarChart(
        data = chartData,
        maxValue = maxValue,
        highlightThreshold = maxValue,
        showGaussCurve = true,
        palette = AppCharts.frequency(),
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    )

    Spacer(modifier = Modifier.height(AppSpacing.md))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        AppCard(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.padding(AppSpacing.md)) {
                Text(
                    stringResource(R.string.top_numbers_label),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                analysis.topNumbers.forEach { number ->
                    val count = analysis.frequencies[number] ?: 0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "%02d".format(number),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "$count",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        AppCard(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.padding(AppSpacing.md)) {
                Text(
                    stringResource(R.string.overdue_numbers_label),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                analysis.overdueNumbers.take(5).forEach { (number, overdue) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "%02d".format(number),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            pluralStringResource(R.plurals.draws_ago_label, overdue, overdue),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
