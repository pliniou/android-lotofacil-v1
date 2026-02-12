package com.cebolao.lotofacil.ui.screens.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.FrequencyAnalysis
import com.cebolao.lotofacil.domain.model.PatternAnalysis
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.domain.model.TrendAnalysis
import com.cebolao.lotofacil.domain.model.TrendType
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.AppScreenDefaults
import com.cebolao.lotofacil.ui.components.AppScreenScaffold
import com.cebolao.lotofacil.ui.components.ErrorCard
import com.cebolao.lotofacil.ui.components.FullScreenLoading
import com.cebolao.lotofacil.ui.components.screenContentPadding
import com.cebolao.lotofacil.ui.screens.statistics.components.DistributionSection
import com.cebolao.lotofacil.ui.screens.statistics.components.FrequencySection
import com.cebolao.lotofacil.ui.screens.statistics.components.PatternSection
import com.cebolao.lotofacil.ui.screens.statistics.components.SummaryCard
import com.cebolao.lotofacil.ui.screens.statistics.components.TimeWindowFilterSection
import com.cebolao.lotofacil.ui.screens.statistics.components.TrendSection
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
                FullScreenLoading(
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
