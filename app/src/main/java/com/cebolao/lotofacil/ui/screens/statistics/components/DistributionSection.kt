package com.cebolao.lotofacil.ui.screens.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.SectionHeader
import com.cebolao.lotofacil.ui.theme.AppSpacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DistributionSection(report: StatisticsReport) {
    SectionHeader(
        title = stringResource(R.string.distributions_title),
        icon = Icons.Outlined.BarChart,
        titleStyle = MaterialTheme.typography.titleLarge,
        showDivider = true
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        modifier = Modifier.fillMaxWidth()
    ) {
        DistributionMiniCard(
            label = stringResource(R.string.even_label),
            data = report.evenDistribution,
            modifier = Modifier.weight(1f, fill = false)
        )
        DistributionMiniCard(
            label = stringResource(R.string.prime_label),
            data = report.primeDistribution,
            modifier = Modifier.weight(1f, fill = false)
        )
        DistributionMiniCard(
            label = stringResource(R.string.frame_label),
            data = report.frameDistribution,
            modifier = Modifier.weight(1f, fill = false)
        )
        DistributionMiniCard(
            label = stringResource(R.string.fibonacci_label),
            data = report.fibonacciDistribution,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}

@Composable
private fun DistributionMiniCard(
    label: String,
    data: Map<Int, Int>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return
    val modeEntry = data.maxByOrNull { it.value }

    AppCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(AppSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            modeEntry?.let {
                Text(
                    "${it.key}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    stringResource(R.string.most_common_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
