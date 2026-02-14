package com.cebolao.lotofacil.ui.screens.statistics.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.FrequencyAnalysis
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.SectionHeader
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun FrequencySection(analysis: FrequencyAnalysis) {
    SectionHeader(
        title = stringResource(R.string.frequency_analysis_title),
        icon = Icons.Outlined.BarChart,
        titleStyle = MaterialTheme.typography.titleLarge,
        showDivider = true
    )

    // Bar chart for all 25 numbers
    FrequencyBarChart(frequencies = analysis.frequencies)

    Spacer(modifier = Modifier.height(AppSpacing.md))

    // Top numbers & overdue side by side
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

@Composable
private fun FrequencyBarChart(frequencies: Map<Int, Int>) {
    val primary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val error = MaterialTheme.colorScheme.error
    val errorContainer = MaterialTheme.colorScheme.errorContainer
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surface = MaterialTheme.colorScheme.surface
    val textMeasurer = rememberTextMeasurer()
    val maxFreq = frequencies.values.maxOrNull()?.toFloat() ?: 1f
    val avgFreq = frequencies.values.average().toFloat()

    AppCard {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(AppSpacing.md)
        ) {
            val barWidth = (size.width - 48.dp.toPx()) / 25f
            val chartHeight = size.height - 24.dp.toPx()
            val startX = 24.dp.toPx()

            (1..25).forEach { number ->
                val count = frequencies[number] ?: 0
                val barHeight = (count / maxFreq) * chartHeight
                val x = startX + (number - 1) * barWidth
                
                // Determine bar color based on frequency relative to average
                val isAboveAverage = count > avgFreq
                val isOutlier = count > avgFreq * 1.5f || count < avgFreq * 0.5f
                
                // Bar colors with proper contrast
                val barColors = when {
                    isOutlier -> listOf(error, errorContainer)
                    isAboveAverage -> listOf(primary, primaryContainer)
                    else -> listOf(primaryContainer, primary.copy(alpha = 0.7f))
                }

                // Draw bar with gradient for better contrast
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = barColors,
                        startY = chartHeight - barHeight,
                        endY = chartHeight
                    ),
                    topLeft = Offset(x + 1.dp.toPx(), chartHeight - barHeight),
                    size = Size(barWidth - 2.dp.toPx(), barHeight),
                    cornerRadius = CornerRadius(barWidth / 8)
                )
                
                // Draw value label with semi-transparent background for legibility
                if (count > 0) {
                    val valueText = count.toString()
                    val valueTextResult = textMeasurer.measure(
                        text = valueText,
                        style = TextStyle(
                            fontSize = 10.sp,
                            color = onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    val textX = x + barWidth / 2 - valueTextResult.size.width / 2
                    val textY = chartHeight - barHeight - 16.dp.toPx()
                    
                    // Semi-transparent background scrim (70% alpha)
                    drawRoundRect(
                        color = surface.copy(alpha = 0.7f),
                        topLeft = Offset(textX - 4.dp.toPx(), textY - 2.dp.toPx()),
                        size = Size(
                            valueTextResult.size.width + 8.dp.toPx(),
                            valueTextResult.size.height + 4.dp.toPx()
                        ),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                    
                    // Draw the value text
                    drawText(
                        valueTextResult,
                        topLeft = Offset(textX, textY)
                    )
                }

                // X-axis labels for key numbers (01, 05, 10, 15, 20, 25)
                if (number == 1 || number % 5 == 0) {
                    val label = "%02d".format(number)
                    val labelResult = textMeasurer.measure(
                        text = label,
                        style = TextStyle(
                            fontSize = 9.sp,
                            color = onSurface
                        )
                    )
                    drawText(
                        labelResult,
                        topLeft = Offset(
                            x + barWidth / 2 - labelResult.size.width / 2,
                            chartHeight + 4.dp.toPx()
                        )
                    )
                }
            }
        }
    }
}
