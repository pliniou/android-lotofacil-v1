package com.cebolao.lotofacil.ui.screens.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.TrendAnalysis
import com.cebolao.lotofacil.domain.model.TrendType
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.AppFilterChip
import com.cebolao.lotofacil.ui.components.InfoIcon
import com.cebolao.lotofacil.ui.components.SectionFeedbackState
import com.cebolao.lotofacil.ui.components.SectionHeader
import com.cebolao.lotofacil.ui.components.TrendChart
import com.cebolao.lotofacil.ui.theme.AppCharts
import com.cebolao.lotofacil.ui.theme.AppSpacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TrendSection(
    analysis: TrendAnalysis?,
    isLoading: Boolean,
    errorResId: Int?,
    selectedType: TrendType,
    onTypeSelected: (TrendType) -> Unit
) {
    var showHelp by remember { mutableStateOf(false) }

    if (showHelp) {
        AlertDialog(
            onDismissRequest = { showHelp = false },
            title = { Text(text = stringResource(id = R.string.common_information_title)) },
            text = { Text(text = stringResource(id = R.string.tooltip_analysis_type)) },
            confirmButton = {
                TextButton(onClick = { showHelp = false }) {
                    Text(text = stringResource(id = R.string.common_ok))
                }
            }
        )
    }

    SectionHeader(
        title = stringResource(R.string.trends_title),
        icon = Icons.AutoMirrored.Outlined.TrendingUp,
        titleStyle = MaterialTheme.typography.titleLarge,
        showDivider = true,
        action = {
            InfoIcon(
                tooltipText = stringResource(id = R.string.tooltip_analysis_type),
                onClick = { showHelp = true }
            )
        }
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            modifier = Modifier.fillMaxWidth()
        ) {
            TrendType.entries.forEach { type ->
                val label = when (type) {
                    TrendType.SUM -> stringResource(R.string.sum_label)
                    TrendType.EVENS -> stringResource(R.string.even_label)
                    TrendType.PRIMES -> stringResource(R.string.prime_label)
                    TrendType.FRAME -> stringResource(R.string.frame_label)
                    TrendType.PORTRAIT -> stringResource(R.string.portrait_label)
                    TrendType.FIBONACCI -> stringResource(R.string.fibonacci_label)
                    TrendType.MULTIPLES_OF_3 -> stringResource(R.string.multiples_of_3_label)
                }
                AppFilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeSelected(type) },
                    label = label,
                    modifier = Modifier.padding(horizontal = AppSpacing.xs)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(AppSpacing.md))

    SectionFeedbackState(
        isLoading = isLoading,
        errorResId = errorResId
    )

    analysis?.let { trend ->
        AppCard {
            Column(
                modifier = Modifier.padding(AppSpacing.md),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            stringResource(R.string.average_value_label),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "%.1f".format(trend.averageValue),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = stringResource(R.string.trend_tap_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.lg))

                TrendChart(
                    data = trend.timeline,
                    lineColor = AppCharts.distribution().line,
                    chartHeight = 220.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
