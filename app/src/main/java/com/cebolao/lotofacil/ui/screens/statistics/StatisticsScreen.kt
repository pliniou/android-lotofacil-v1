package com.cebolao.lotofacil.ui.screens.statistics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Pattern
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.usecase.FrequencyAnalysis
import com.cebolao.lotofacil.domain.usecase.PatternAnalysis
import com.cebolao.lotofacil.domain.usecase.TrendAnalysis
import com.cebolao.lotofacil.domain.usecase.TrendType
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.AppScreenDefaults
import com.cebolao.lotofacil.ui.components.AppScreenScaffold
import com.cebolao.lotofacil.ui.components.ErrorCard
import com.cebolao.lotofacil.ui.components.LoadingData
import com.cebolao.lotofacil.ui.components.screenContentPadding
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.StatisticsUiState
import com.cebolao.lotofacil.viewmodels.StatisticsViewModel

// ==================== SEALED ACTIONS ====================
sealed class StatisticsAction {
    data class TimeWindowSelected(val window: Int) : StatisticsAction()
    data class PatternSizeSelected(val size: Int) : StatisticsAction()
    data class TrendTypeSelected(val type: TrendType) : StatisticsAction()
    data class TrendWindowSelected(val window: Int) : StatisticsAction()
    data object Refresh : StatisticsAction()
}

// ==================== STATEFUL WRAPPER ====================
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StatisticsScreenContent(
        state = uiState,
        modifier = modifier,
        onNavigateBack = onNavigateBack,
        onAction = { action ->
            when (action) {
                is StatisticsAction.TimeWindowSelected -> viewModel.onTimeWindowSelected(action.window)
                is StatisticsAction.PatternSizeSelected -> viewModel.onPatternSizeSelected(action.size)
                is StatisticsAction.TrendTypeSelected -> viewModel.onTrendTypeSelected(action.type)
                is StatisticsAction.TrendWindowSelected -> viewModel.onTrendWindowSelected(action.window)
                is StatisticsAction.Refresh -> viewModel.refresh()
            }
        }
    )
}

// ==================== STATELESS CONTENT ====================
@Composable
fun StatisticsScreenContent(
    state: StatisticsUiState,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onAction: (StatisticsAction) -> Unit = {}
) {
    AppScreenScaffold(
        modifier = modifier.fillMaxSize(),
        title = stringResource(R.string.insights_title),
        icon = Icons.Filled.Analytics,
        onBackClick = onNavigateBack,
        actions = {
            IconButton(onClick = { onAction(StatisticsAction.Refresh) }) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = stringResource(R.string.refresh_button),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                LoadingData(
                    message = stringResource(R.string.loading_data),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            state.errorMessageResId != null -> {
                ErrorCard(
                    messageResId = state.errorMessageResId,
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(AppSpacing.lg),
                    actions = { onAction(StatisticsAction.Refresh) }
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .screenContentPadding(innerPadding),
                    contentPadding = AppScreenDefaults.listContentPadding(
                        horizontal = AppSpacing.lg,
                        top = AppSpacing.md,
                        bottom = AppSpacing.xl
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
                ) {
                    // ── Time Window Filter ───────────────────────────
                    item(key = "time_window_filter") {
                        TimeWindowFilterSection(
                            selectedWindow = state.selectedTimeWindow,
                            windows = state.timeWindows,
                            totalDraws = state.totalDrawsAvailable,
                            onWindowSelected = { onAction(StatisticsAction.TimeWindowSelected(it)) }
                        )
                    }

                    // ── Summary Card ────────────────────────────────
                    state.report?.let { report ->
                        item(key = "summary_card") {
                            SummaryCard(
                                totalDrawsAnalyzed = report.totalDrawsAnalyzed,
                                averageSum = report.averageSum
                            )
                        }
                    }

                    // ── Frequency Analysis ──────────────────────────
                    state.frequencyAnalysis?.let { freq ->
                        item(key = "frequency_section") {
                            FrequencySection(analysis = freq)
                        }
                    }

                    // ── Distribution Overview ───────────────────────
                    state.report?.let { report ->
                        item(key = "distribution_section") {
                            DistributionSection(report = report)
                        }
                    }

                    // ── Pattern Analysis ────────────────────────────
                    item(key = "pattern_section") {
                        PatternSection(
                            analysis = state.patternAnalysis,
                            isLoading = state.isPatternLoading,
                            errorResId = state.patternErrorResId,
                            selectedSize = state.selectedPatternSize,
                            onSizeSelected = { onAction(StatisticsAction.PatternSizeSelected(it)) }
                        )
                    }

                    // ── Trend Analysis ──────────────────────────────
                    item(key = "trend_section") {
                        TrendSection(
                            analysis = state.trendAnalysis,
                            isLoading = state.isTrendLoading,
                            errorResId = state.trendErrorResId,
                            selectedType = state.selectedTrendType,
                            selectedWindow = state.selectedTrendWindow,
                            onTypeSelected = { onAction(StatisticsAction.TrendTypeSelected(it)) },
                            onWindowSelected = { onAction(StatisticsAction.TrendWindowSelected(it)) }
                        )
                    }
                }
            }
        }
    }
}

// ── Time Window Filter ──────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TimeWindowFilterSection(
    selectedWindow: Int,
    windows: List<Int>,
    totalDraws: Int,
    onWindowSelected: (Int) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.time_window_filter_label),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            windows.forEach { window ->
                val label = if (window == 0) {
                    stringResource(R.string.all_draws_label)
                } else {
                    stringResource(R.string.last_n_draws_label, window)
                }
                FilterChip(
                    selected = selectedWindow == window,
                    onClick = { onWindowSelected(window) },
                    label = { Text(label, style = MaterialTheme.typography.labelLarge) }
                )
            }
        }
        if (totalDraws > 0) {
            Text(
                text = stringResource(R.string.total_draws_available_label, totalDraws),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = AppSpacing.xs)
            )
        }
    }
}

// ── Summary Card ────────────────────────────────────────────────────────

@Composable
private fun SummaryCard(totalDrawsAnalyzed: Int, averageSum: Float) {
    AppCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    stringResource(R.string.draws_analyzed_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "$totalDrawsAnalyzed",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    stringResource(R.string.average_sum_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "%.1f".format(averageSum),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ── Frequency Section ───────────────────────────────────────────────────

@Composable
private fun FrequencySection(analysis: FrequencyAnalysis) {
    SectionHeader(
        title = stringResource(R.string.frequency_analysis_title),
        icon = Icons.Outlined.BarChart
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
                            stringResource(R.string.draws_ago_label, overdue),
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
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val textMeasurer = rememberTextMeasurer()
    val maxFreq = frequencies.values.maxOrNull()?.toFloat() ?: 1f

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

                // Bar
                drawRect(
                    color = primary.copy(alpha = 0.7f + 0.3f * (count / maxFreq)),
                    topLeft = Offset(x + 1.dp.toPx(), chartHeight - barHeight),
                    size = Size(barWidth - 2.dp.toPx(), barHeight)
                )

                // Label every 5th number
                if (number % 5 == 0 || number == 1) {
                    val label = "%02d".format(number)
                    val textResult = textMeasurer.measure(
                        text = label,
                        style = TextStyle(
                            fontSize = 9.sp,
                            color = onSurfaceVariant
                        )
                    )
                    drawText(
                        textResult,
                        topLeft = Offset(
                            x + barWidth / 2 - textResult.size.width / 2,
                            chartHeight + 4.dp.toPx()
                        )
                    )
                }
            }
        }
    }
}

// ── Distribution Section ────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DistributionSection(report: com.cebolao.lotofacil.domain.model.StatisticsReport) {
    SectionHeader(
        title = stringResource(R.string.distributions_title),
        icon = Icons.Outlined.BarChart
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

// ── Pattern Section ─────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PatternSection(
    analysis: PatternAnalysis?,
    isLoading: Boolean,
    errorResId: Int?,
    selectedSize: Int,
    onSizeSelected: (Int) -> Unit
) {
    SectionHeader(
        title = stringResource(R.string.patterns_title),
        icon = Icons.Outlined.Pattern
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        listOf(2, 3, 4).forEach { size ->
            val label = when (size) {
                2 -> stringResource(R.string.pairs_label)
                3 -> stringResource(R.string.triplets_label)
                else -> stringResource(R.string.quads_label)
            }
            FilterChip(
                selected = selectedSize == size,
                onClick = { onSizeSelected(size) },
                label = { Text(label) }
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

    analysis?.let { patternData ->
        AppCard {
            Column(modifier = Modifier.padding(AppSpacing.md)) {
                patternData.patterns.forEachIndexed { index, (pattern, count) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppSpacing.xs),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            pattern.sorted().joinToString(" - ") { "%02d".format(it) },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "$count×",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (index < patternData.patterns.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

// ── Trend Section ───────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TrendSection(
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
        icon = Icons.Outlined.TrendingUp
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
                label = { Text(label, style = MaterialTheme.typography.labelMedium) }
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
                label = { Text("$window") }
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

// ── Shared Components ───────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(AppSpacing.sm))
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    HorizontalDivider(
        modifier = Modifier.padding(vertical = AppSpacing.xs),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}
