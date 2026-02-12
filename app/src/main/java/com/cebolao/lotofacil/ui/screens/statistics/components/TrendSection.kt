package com.cebolao.lotofacil.ui.screens.statistics.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.TrendAnalysis
import com.cebolao.lotofacil.domain.model.TrendType
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.theme.AppSpacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TrendSection(
    analysis: TrendAnalysis?,
    isLoading: Boolean,
    errorResId: Int?,
    selectedType: TrendType,
    selectedWindow: Int,
    onTypeSelected: (TrendType) -> Unit,
    onWindowSelected: (Int) -> Unit
) {
    SectionHeader(
        title = stringResource(R.string.trends_title),
        icon = Icons.AutoMirrored.Outlined.TrendingUp
    )

    // Type selector
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
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
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }

    Spacer(modifier = Modifier.height(AppSpacing.sm))

    // Window selector
    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        listOf(20, 50, 100).forEach { window ->
            FilterChip(
                selected = selectedWindow == window,
                onClick = { onWindowSelected(window) },
                label = { Text("$window") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }

    Spacer(modifier = Modifier.height(AppSpacing.sm))

    AnimatedVisibility(visible = isLoading, enter = fadeIn(), exit = fadeOut()) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AppSpacing.sm)
        )
    }

    if (errorResId != null) {
        Text(
            stringResource(errorResId),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(AppSpacing.sm)
        )
    }

    analysis?.let { trend ->
        AppCard {
            Column(modifier = Modifier.padding(AppSpacing.md)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
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

                Spacer(modifier = Modifier.height(AppSpacing.md))

                // Line chart
                TrendLineChart(
                    data = trend.timeline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
    }
}

@Composable
private fun TrendLineChart(
    data: List<Pair<Int, Float>>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val primary = MaterialTheme.colorScheme.primary
    val surface = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)

    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas

        val minVal = data.minOf { it.second }
        val maxVal = data.maxOf { it.second }
        val range = (maxVal - minVal).coerceAtLeast(1f)

        val stepX = size.width / (data.size - 1).toFloat()
        val chartHeight = size.height

        val path = Path()
        val fillPath = Path()

        data.forEachIndexed { index, (_, value) ->
            val x = index * stepX
            val y = chartHeight - ((value - minVal) / range * chartHeight)

            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, chartHeight)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        // Fill
        fillPath.lineTo(size.width, chartHeight)
        fillPath.close()
        drawPath(fillPath, surface)

        // Line
        drawPath(
            path = path,
            color = primary,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}


