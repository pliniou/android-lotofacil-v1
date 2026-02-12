package com.cebolao.lotofacil.ui.components

import android.annotation.SuppressLint
import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppTheme
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun BarChart(
    data: ImmutableList<Pair<String, Int>>,
    modifier: Modifier = Modifier,
    chartHeight: Dp? = null,
    maxValue: Int,
    showGaussCurve: Boolean = true,
    highlightThreshold: Int? = null,
    barSpacing: Dp = 10.dp,
    minBarWidth: Dp = 12.dp,
    yAxisLabelWidth: Dp = 40.dp,
    xAxisLabelHeight: Dp = 34.dp,
    valueLabelHeight: Dp = 24.dp
) {
    if (data.isEmpty()) return

    val safeMaxValue = maxValue.coerceAtLeast(1)
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val tertiaryContainer = MaterialTheme.colorScheme.tertiaryContainer
    val sizes = AppTheme.sizes
    val resolvedChartHeight = chartHeight ?: sizes.chartHeightDefault
    val labelSpacingCompact = sizes.chartLabelMinSpacingCompact
    val labelSpacingExpanded = sizes.chartLabelMinSpacingExpanded
    val valueLabelMinBarWidthCompact = sizes.chartValueLabelMinBarWidthCompact
    val valueLabelMinBarWidthExpanded = sizes.chartValueLabelMinBarWidthExpanded
    val labelSmall = MaterialTheme.typography.labelSmall
    val density = LocalDensity.current
    val textSize = remember(density, labelSmall) { density.run { labelSmall.fontSize.toPx() } }

    val textPaint = remember(textSize, onSurfaceVariant) {
        Paint().apply {
            isAntiAlias = true
            this.textSize = textSize
            this.color = onSurfaceVariant.toArgb()
            this.textAlign = Paint.Align.RIGHT
        }
    }
    val valuePaint = remember(textSize, primaryColor) {
        Paint().apply {
            isAntiAlias = true
            this.textSize = textSize
            this.color = primaryColor.toArgb()
            this.textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
    }
    val labelPaint = remember(textSize, onSurfaceVariant) {
        Paint().apply {
            isAntiAlias = true
            this.textSize = textSize
            this.color = onSurfaceVariant.toArgb()
            this.textAlign = Paint.Align.CENTER
        }
    }

    val chartDescription = pluralStringResource(
        id = R.plurals.chart_frequency_description,
        count = data.size,
        data.size,
        safeMaxValue
    )


    BoxWithConstraints(modifier = modifier) {
        val isWide = maxWidth >= 600.dp
        val minChartWidth = remember(data, minBarWidth, barSpacing, yAxisLabelWidth) {
            if (data.isEmpty()) 0.dp else {
                yAxisLabelWidth + (minBarWidth + barSpacing) * data.size + barSpacing
            }
        }
        val chartWidth = if (maxWidth < minChartWidth) minChartWidth else maxWidth
        val shouldScroll = chartWidth > maxWidth + 1.dp
        val scrollModifier = if (shouldScroll) Modifier.horizontalScroll(rememberScrollState()) else Modifier

        Box(modifier = scrollModifier) {
            Canvas(
                modifier = Modifier
                    .width(chartWidth)
                    .height(resolvedChartHeight)
                    .semantics {
                        role = Role.Image
                        contentDescription = chartDescription
                    }
            ) {
                val yAxisLabelWidthPx = yAxisLabelWidth.toPx()
                val xAxisLabelHeightPx = xAxisLabelHeight.toPx()
                val valueLabelHeightPx = valueLabelHeight.toPx()
                val chartAreaWidth = size.width - yAxisLabelWidthPx
                val chartAreaHeight = size.height - xAxisLabelHeightPx - valueLabelHeightPx

                // Draw refined grid
                drawGrid(
                    yAxisLabelWidthPx,
                    chartAreaHeight,
                    valueLabelHeightPx,
                    safeMaxValue,
                    textPaint,
                    outlineVariant
                )

                val barSpacingPx = barSpacing.toPx()
                val totalSpacing = barSpacingPx * (data.size + 1)
                val barWidth = ((chartAreaWidth - totalSpacing) / data.size)
                    .coerceAtLeast(minBarWidth.toPx())
                val minLabelSpacingPx = (if (isWide) labelSpacingExpanded else labelSpacingCompact).toPx()
                val slotWidth = barWidth + barSpacingPx
                val labelStep = if (slotWidth <= 0f) 1 else {
                    ceil(minLabelSpacingPx / slotWidth).toInt().coerceAtLeast(1)
                }
                val showValueLabels = barWidth >= (if (isWide) valueLabelMinBarWidthExpanded else valueLabelMinBarWidthCompact).toPx()

                // Draw Gaussian Curve Backdrop
                if (showGaussCurve && data.size > 2) {
                    drawGaussianCurve(
                        data = data,
                        yAxisLabelWidth = yAxisLabelWidthPx,
                        chartAreaHeight = chartAreaHeight,
                        topPadding = valueLabelHeightPx,
                        maxValue = safeMaxValue,
                        color = tertiaryColor.copy(alpha = 0.3f),
                        barWidth = barWidth,
                        barSpacing = barSpacingPx
                    )
                }

                data.forEachIndexed { index, (label, value) ->
                    val progressFactor = animatedProgress.value
                    val barHeight = (value.toFloat() / safeMaxValue) * chartAreaHeight * progressFactor
                    val left = yAxisLabelWidthPx + barSpacingPx + index * (barWidth + barSpacingPx)
                    val barCenterX = left + barWidth / 2

                    // Bar background (Subtle surface)
                    drawRoundRect(
                        color = surfaceVariant.copy(alpha = 0.05f),
                        topLeft = Offset(left, valueLabelHeightPx),
                        size = Size(barWidth, chartAreaHeight),
                        cornerRadius = CornerRadius(barWidth / 5)
                    )

                    // Actual data bar with Gradient
                    if (barHeight > 0) {
                        val isHighlighted = highlightThreshold != null && value >= highlightThreshold
                        val barColor1 = if (isHighlighted) tertiaryColor else primaryColor
                        val barColor2 = if (isHighlighted) tertiaryContainer else secondaryColor

                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(barColor1, barColor2),
                                startY = valueLabelHeightPx + chartAreaHeight - barHeight,
                                endY = valueLabelHeightPx + chartAreaHeight
                            ),
                            topLeft = Offset(left, valueLabelHeightPx + chartAreaHeight - barHeight),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(x = barWidth / 3, y = barWidth / 3)
                        )
                    }

                    val shouldDrawLabel = index % labelStep == 0 || index == data.lastIndex

                    // Value text above the bar with scale animation
                    val valueTextY = valueLabelHeightPx + chartAreaHeight - barHeight - 8.dp.toPx()
                    if (showValueLabels && shouldDrawLabel && progressFactor > 0.6f) {
                        val textAlpha = ((progressFactor - 0.6f) * 2.5f).coerceIn(0f, 1f)
                        valuePaint.alpha = (textAlpha * 255).toInt()
                        drawContext.canvas.nativeCanvas.drawText(
                            value.toString(),
                            barCenterX,
                            valueTextY,
                            valuePaint
                        )
                    }

                    // X-Axis Label - Horizontal instead of 45Â° rotation
                    if (shouldDrawLabel) {
                        val labelTextY = size.height - xAxisLabelHeightPx + 16.dp.toPx()
                        drawContext.canvas.nativeCanvas.drawText(
                            label,
                            barCenterX,
                            labelTextY,
                            labelPaint
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawGaussianCurve(
    data: ImmutableList<Pair<String, Int>>,
    yAxisLabelWidth: Float,
    chartAreaHeight: Float,
    topPadding: Float,
    maxValue: Int,
    color: Color,
    barWidth: Float,
    barSpacing: Float
) {
    val barWidthWithSpacing = barWidth + barSpacing
    
    // Calculate mean and standard deviation of categories (indices)
    // We assume the distribution follows the bars' values
    val totalWeight = data.sumOf { it.second }.toFloat().coerceAtLeast(1f)
    var mean = 0f
    data.forEachIndexed { index, pair ->
        mean += index * (pair.second / totalWeight)
    }
    
    var variance = 0f
    data.forEachIndexed { index, pair ->
        variance += (index - mean).pow(2) * (pair.second / totalWeight)
    }
    val stdDev = sqrt(variance).coerceAtLeast(0.5f)

    val path = Path()
    val points = 50
    for (i in 0..points) {
        val xRelative = i.toFloat() / points * (data.size - 1)
        val x = yAxisLabelWidth + barSpacing + (xRelative * barWidthWithSpacing) + barWidth / 2
        
        // Normal distribution formula
        val exponent = -0.5f * ((xRelative - mean) / stdDev).pow(2)
        val gaussianValue = exp(exponent)
        
        // Scale to fit chart height (using max observed value as peak reference)
        val y = topPadding + chartAreaHeight - (gaussianValue * chartAreaHeight * (data.maxOf { it.second }.toFloat() / maxValue))
        
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
    )
}

private fun DrawScope.drawGrid(
    yAxisLabelWidth: Float,
    chartAreaHeight: Float,
    topPadding: Float,
    maxValue: Int,
    textPaint: Paint,
    lineColor: Color
) {
    val gridLines = 4
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))

    (0..gridLines).forEach { i ->
        val y = topPadding + chartAreaHeight * (1f - i.toFloat() / gridLines)
        val value = (maxValue * i.toFloat() / gridLines).roundToInt()
        
        // Horizontal grid line
        drawLine(
            color = lineColor.copy(alpha = 0.2f),
            start = Offset(yAxisLabelWidth, y),
            end = Offset(size.width, y),
            strokeWidth = 1.dp.toPx(),
            pathEffect = if (i == 0) null else dashEffect
        )
        
        // Y-Axis labels with better formatting
        val formattedValue = if (value > 99) {
            "${value / 100}${if (value % 100 > 0) ".${(value % 100) / 10}" else ""}x"
        } else {
            value.toString()
        }
        val textY = y + (textPaint.descent() - textPaint.ascent()) / 2 - textPaint.descent()
        drawContext.canvas.nativeCanvas.drawText(
            formattedValue,
            yAxisLabelWidth - 6.dp.toPx(),
            textY,
            textPaint
        )
    }
    
    // Y-Axis line
    drawLine(
        color = lineColor.copy(alpha = 0.3f),
        start = Offset(yAxisLabelWidth, topPadding),
        end = Offset(yAxisLabelWidth, topPadding + chartAreaHeight),
        strokeWidth = 1.5.dp.toPx()
    )
}



