package com.cebolao.lotofacil.ui.screens.statistics.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.TrendAnalysis
import com.cebolao.lotofacil.domain.model.TrendType
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.SectionFeedbackState
import com.cebolao.lotofacil.ui.theme.AppSpacing
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.PI

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TrendSection(
    analysis: TrendAnalysis?,
    isLoading: Boolean,
    errorResId: Int?,
    selectedType: TrendType,
    onTypeSelected: (TrendType) -> Unit
) {
    var isGaussianEnabled by remember { mutableStateOf(false) }

    SectionHeader(
        title = stringResource(R.string.trends_title),
        icon = Icons.AutoMirrored.Outlined.TrendingUp
    )

    // Type selector centered
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
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeSelected(type) },
                    label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
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
                // Header with Average and Switch
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                    ) {
                        Text(
                            text = stringResource(R.string.gaussian_curve_label),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = isGaussianEnabled,
                            onCheckedChange = { isGaussianEnabled = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.lg))

                // Distribution Chart
                TrendDistributionChart(
                    data = trend.timeline,
                    isGaussianEnabled = isGaussianEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }
        }
    }
}

@Composable
private fun TrendDistributionChart(
    data: List<Pair<Int, Float>>,
    isGaussianEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val textMeasurer = rememberTextMeasurer()

    // Aggregate data into frequency map: Value -> Count
    // Note: timeline is Pair<DrawIndex, Value>. We care about Value.
    val values = data.map { it.second }
    val frequencyMap = values.groupingBy { it.toInt() }.eachCount()

    if (frequencyMap.isEmpty()) return

    val minVal = frequencyMap.keys.min()
    val maxVal = frequencyMap.keys.max()
    val maxFreq = frequencyMap.values.max()

    // Statistics for Gaussian
    val mean = values.average()
    val variance = values.map { (it - mean).pow(2) }.average()
    val stdDev = sqrt(variance)

    Canvas(modifier = modifier) {
        val bottomPadding = 24.dp.toPx()
        val chartHeight = size.height - bottomPadding
        val chartWidth = size.width
        
        val valueRange = (maxVal - minVal).coerceAtLeast(1)
        val barWidth = chartWidth / (valueRange + 1)
        
        // Draw Bars
        frequencyMap.forEach { (value, count) ->
            val barHeight = (count.toFloat() / maxFreq) * chartHeight
            val x = ((value - minVal) * barWidth)
            
            drawRect(
                color = primary.copy(alpha = 0.6f),
                topLeft = Offset(x, chartHeight - barHeight),
                size = Size(barWidth - 2.dp.toPx(), barHeight)
            )

            // Draw X-axis labels sparsely
            if (value % 5 == 0 || value == minVal || value == maxVal) {
                 val label = value.toString()
                 val textResult = textMeasurer.measure(
                    text = label,
                    style = TextStyle(fontSize = 10.sp, color = onSurfaceVariant)
                 )
                 drawText(
                    textResult,
                    topLeft = Offset(x + barWidth/2 - textResult.size.width/2, chartHeight + 4.dp.toPx())
                 )
            }
        }

        // Draw Gaussian Curve
        if (isGaussianEnabled && stdDev > 0) {
            val path = Path()
            val points = (minVal..maxVal).map { x ->
                // Gaussian PDF: f(x) = (1 / (σ * √(2π))) * e^(-0.5 * ((x - μ) / σ)^2)
                // Scale it to match the histogram height: Multiply by TotalCount * BarWidth?
                // Or simply: Frequency = TotalSamples * PDF * BinWidth(1)
                
                val exponent = -0.5 * ((x - mean) / stdDev).pow(2)
                val pdf = (1.0 / (stdDev * sqrt(2 * PI))) * exp(exponent)
                val expectedCount = pdf * values.size // Expected frequency for this bin
                
                val y = chartHeight - ((expectedCount / maxFreq) * chartHeight).toFloat()
                val posX = ((x - minVal) * barWidth) + barWidth / 2
                
                Offset(posX, y)
            }

            if (points.isNotEmpty()) {
                path.moveTo(points.first().x, points.first().y)
                // Use quadratic bezier for smoother curve or just lines
                points.drop(1).forEach { 
                    path.lineTo(it.x, it.y) 
                }
                
                drawPath(
                    path = path,
                    color = secondary,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}
