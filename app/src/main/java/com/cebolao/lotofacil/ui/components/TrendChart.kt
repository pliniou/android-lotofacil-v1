package com.cebolao.lotofacil.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppTheme

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun TrendChart(
    data: List<Pair<Int, Float>>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    chartHeight: Dp? = null,
    minPointSpacing: Dp? = null,
    maxPointMarkers: Int? = null
) {
    if (data.isEmpty()) return

    val colors = MaterialTheme.colorScheme
    val sizes = AppTheme.sizes
    val resolvedChartHeight = chartHeight ?: sizes.chartHeightDefault
    val resolvedMinPointSpacing = minPointSpacing ?: sizes.trendMinPointSpacingExpanded
    val resolvedMaxPointMarkers = maxPointMarkers ?: sizes.trendMaxPointMarkersExpanded
    val compactMaxPointMarkers = sizes.trendMaxPointMarkersCompact
    val maxVal = remember(data) { data.maxOf { it.second }.coerceAtLeast(1f) }
    val minVal = remember(data) { data.minOf { it.second }.coerceAtMost(maxVal - 0.1f) }
    val range = (maxVal - minVal).coerceAtLeast(0.1f)
    val chartDescription = stringResource(id = R.string.trend_analysis_title)

    BoxWithConstraints(modifier = modifier) {
        val isWide = maxWidth >= 600.dp
        val horizontalPadding = AppSpacing.sm * 2
        val effectiveMinPointSpacing = if (isWide) resolvedMinPointSpacing else 32.dp
        val minChartWidth = remember(data, effectiveMinPointSpacing, horizontalPadding) {
            if (data.size <= 1) 0.dp else effectiveMinPointSpacing * (data.size - 1) + horizontalPadding
        }
        val chartWidth = if (maxWidth < minChartWidth) minChartWidth else maxWidth
        val shouldScroll = chartWidth > maxWidth + 1.dp
        val scrollModifier = if (shouldScroll) Modifier.horizontalScroll(rememberScrollState()) else Modifier

        Box(modifier = scrollModifier) {
            Canvas(
                modifier = Modifier
                    .width(chartWidth)
                    .height(resolvedChartHeight)
                    .padding(horizontal = AppSpacing.sm)
                    .semantics {
                        role = Role.Image
                        contentDescription = chartDescription
                    }
            ) {
                val width = size.width
                val height = size.height
                val stepX = width / (data.size - 1).coerceAtLeast(1)

                val points = data.mapIndexed { index, pair ->
                    val x = index * stepX
                    val y = height - ((pair.second - minVal) / range) * height
                    Offset(x, y)
                }

                val guideColor = colors.outlineVariant.copy(alpha = 0.35f)
                repeat(3) { idx ->
                    val y = height * (idx + 1) / 4f
                    drawLine(
                        color = guideColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                    )
                }

                // Draw line
                val path = Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            lineTo(points[i].x, points[i].y)
                        }
                    }
                }

                val areaPath = Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points.first().x, height)
                        points.forEach { point -> lineTo(point.x, point.y) }
                        lineTo(points.last().x, height)
                        close()
                    }
                }

                drawPath(
                    path = areaPath,
                    color = lineColor.copy(alpha = 0.12f)
                )

                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 3.dp.toPx())
                )

                // Draw points (dots) - skip when dense to reduce visual noise
                val safeMaxMarkers = (if (isWide) resolvedMaxPointMarkers else compactMaxPointMarkers)
                    .coerceAtLeast(12)
                val markerStep = (points.size / safeMaxMarkers).coerceAtLeast(1)
                points.forEachIndexed { index, offset ->
                    if (index % markerStep == 0 || index == points.lastIndex) {
                        drawCircle(
                            color = lineColor,
                            radius = 4.dp.toPx(),
                            center = offset
                        )
                    }
                }
            }
        }
    }
}
