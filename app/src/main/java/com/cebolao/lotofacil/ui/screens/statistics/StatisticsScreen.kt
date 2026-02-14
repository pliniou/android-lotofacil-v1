package com.cebolao.lotofacil.ui.screens.statistics

import android.os.Looper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.TrendType
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.AppScreenDefaults
import com.cebolao.lotofacil.ui.components.AppScreenScaffold
import com.cebolao.lotofacil.ui.components.AppScreenStateHost
import com.cebolao.lotofacil.ui.components.ScreenContentState
import com.cebolao.lotofacil.ui.components.screenContentPadding
import com.cebolao.lotofacil.ui.components.StatisticsSkeleton
import com.cebolao.lotofacil.ui.screens.statistics.components.DistributionSection
import com.cebolao.lotofacil.ui.screens.statistics.components.FrequencySection
import com.cebolao.lotofacil.ui.screens.statistics.components.PatternSection
import com.cebolao.lotofacil.ui.screens.statistics.components.SummaryCard
import com.cebolao.lotofacil.ui.screens.statistics.components.TimeWindowFilterSection
import com.cebolao.lotofacil.ui.screens.statistics.components.TrendSection
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.DataLoadSource
import com.cebolao.lotofacil.viewmodels.StatisticsUiState
import com.cebolao.lotofacil.viewmodels.StatisticsViewModel

sealed class StatisticsAction {
    data class TimeWindowSelected(val window: Int) : StatisticsAction()
    data class PatternSizeSelected(val size: Int) : StatisticsAction()
    data class TrendTypeSelected(val type: TrendType) : StatisticsAction()
    data class TrendWindowSelected(val window: Int) : StatisticsAction()
    data object Refresh : StatisticsAction()
}

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
                StatisticsAction.Refresh -> viewModel.refresh()
            }
        }
    )
}

@Composable
fun StatisticsScreenContent(
    state: StatisticsUiState,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onAction: (StatisticsAction) -> Unit = {}
) {
    val hasContent = remember(
        state.report,
        state.frequencyAnalysis,
        state.patternAnalysis,
        state.trendAnalysis
    ) {
        state.report != null ||
            state.frequencyAnalysis != null ||
            state.patternAnalysis != null ||
            state.trendAnalysis != null
    }

    val screenState = remember(state.isLoading, state.errorMessageResId, state.isHistoryEmpty, hasContent) {
        when {
            state.isLoading -> ScreenContentState.Loading()
            state.isHistoryEmpty -> ScreenContentState.Empty(
                messageResId = R.string.insights_history_empty_message,
                icon = Icons.Filled.Analytics,
                actionLabelResId = R.string.refresh_button
            )
            state.errorMessageResId != null -> ScreenContentState.Error(messageResId = state.errorMessageResId)
            !hasContent -> ScreenContentState.Empty(
                messageResId = R.string.insights_empty_message,
                icon = Icons.Filled.Analytics,
                actionLabelResId = R.string.refresh_button
            )

            else -> ScreenContentState.Success
        }
    }

    AppScreenScaffold(
        modifier = modifier.fillMaxSize(),
        title = stringResource(R.string.insights_title),
        icon = Icons.Filled.Analytics,
        onBackClick = onNavigateBack,
        actions = {
            IconButton(onClick = { onAction(StatisticsAction.Refresh) }) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = stringResource(R.string.refresh_button),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { innerPadding ->
        AppScreenStateHost(
            state = screenState,
            modifier = Modifier
                .fillMaxSize()
                .screenContentPadding(innerPadding),
            onRetry = { onAction(StatisticsAction.Refresh) },
            onEmptyAction = { onAction(StatisticsAction.Refresh) },
            loadingContent = {
                StatisticsSkeleton()
            }
        ) {
            val listModifier = Modifier
                .fillMaxSize()
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = 0.8f,
                        stiffness = 300f
                    )
                )
            val listContentPadding = AppScreenDefaults.listContentPadding(
                horizontal = AppSpacing.lg,
                top = AppSpacing.lg,
                bottom = AppSpacing.xl
            )

            if (Looper.myLooper() != null) {
                LazyColumn(
                    modifier = listModifier,
                    contentPadding = listContentPadding,
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
                ) {
                    item(key = "statistics_data_status") {
                        val sourceLabel = when (state.statisticsSource) {
                            DataLoadSource.CACHE -> stringResource(R.string.home_source_stats_cache)
                            DataLoadSource.NETWORK -> stringResource(R.string.home_source_stats_network)
                            DataLoadSource.COMPUTED -> stringResource(R.string.home_source_stats_computed)
                        }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (state.isShowingStaleData) {
                                    "$sourceLabel - ${stringResource(R.string.home_stale_data_warning)}"
                                } else {
                                    sourceLabel
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    item(key = "time_window_filter") {
                        AnimateOnEntry(delayMillis = 50) {
                            TimeWindowFilterSection(
                                selectedWindow = state.selectedTimeWindow,
                                windows = state.timeWindows,
                                onWindowSelected = { onAction(StatisticsAction.TimeWindowSelected(it)) }
                            )
                        }
                    }

                    state.frequencyAnalysis?.let { freq ->
                        item(key = "frequency_section") {
                            AnimateOnEntry(delayMillis = 100) {
                                FrequencySection(analysis = freq)
                            }
                        }
                    }

                    state.report?.let { report ->
                        item(key = "summary_card") {
                            AnimateOnEntry(delayMillis = 150) {
                                SummaryCard(
                                    totalDrawsAnalyzed = report.totalDrawsAnalyzed,
                                    averageSum = report.averageSum
                                )
                            }
                        }
                    }

                    state.report?.let { report ->
                        item(key = "distribution_section") {
                            AnimateOnEntry(delayMillis = 200) {
                                DistributionSection(report = report)
                            }
                        }
                    }

                    item(key = "pattern_section") {
                        AnimateOnEntry(delayMillis = 250) {
                            PatternSection(
                                analysis = state.patternAnalysis,
                                isLoading = state.isPatternLoading,
                                errorResId = state.patternErrorResId,
                                selectedSize = state.selectedPatternSize,
                                onSizeSelected = { onAction(StatisticsAction.PatternSizeSelected(it)) }
                            )
                        }
                    }

                    item(key = "trend_section") {
                        AnimateOnEntry(delayMillis = 300) {
                            TrendSection(
                                analysis = state.trendAnalysis,
                                isLoading = state.isTrendLoading,
                                errorResId = state.trendErrorResId,
                                selectedType = state.selectedTrendType,
                                onTypeSelected = { onAction(StatisticsAction.TrendTypeSelected(it)) }
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = listModifier
                        .verticalScroll(rememberScrollState())
                        .padding(listContentPadding),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
                ) {
                    val sourceLabel = when (state.statisticsSource) {
                        DataLoadSource.CACHE -> stringResource(R.string.home_source_stats_cache)
                        DataLoadSource.NETWORK -> stringResource(R.string.home_source_stats_network)
                        DataLoadSource.COMPUTED -> stringResource(R.string.home_source_stats_computed)
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (state.isShowingStaleData) {
                                "$sourceLabel - ${stringResource(R.string.home_stale_data_warning)}"
                            } else {
                                sourceLabel
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    AnimateOnEntry(delayMillis = 50) {
                        TimeWindowFilterSection(
                            selectedWindow = state.selectedTimeWindow,
                            windows = state.timeWindows,
                            onWindowSelected = { onAction(StatisticsAction.TimeWindowSelected(it)) }
                        )
                    }

                    state.frequencyAnalysis?.let { freq ->
                        AnimateOnEntry(delayMillis = 100) {
                            FrequencySection(analysis = freq)
                        }
                    }

                    state.report?.let { report ->
                        AnimateOnEntry(delayMillis = 150) {
                            SummaryCard(
                                totalDrawsAnalyzed = report.totalDrawsAnalyzed,
                                averageSum = report.averageSum
                            )
                        }
                    }

                    state.report?.let { report ->
                        AnimateOnEntry(delayMillis = 200) {
                            DistributionSection(report = report)
                        }
                    }

                    AnimateOnEntry(delayMillis = 250) {
                        PatternSection(
                            analysis = state.patternAnalysis,
                            isLoading = state.isPatternLoading,
                            errorResId = state.patternErrorResId,
                            selectedSize = state.selectedPatternSize,
                            onSizeSelected = { onAction(StatisticsAction.PatternSizeSelected(it)) }
                        )
                    }

                    AnimateOnEntry(delayMillis = 300) {
                        TrendSection(
                            analysis = state.trendAnalysis,
                            isLoading = state.isTrendLoading,
                            errorResId = state.trendErrorResId,
                            selectedType = state.selectedTrendType,
                            onTypeSelected = { onAction(StatisticsAction.TrendTypeSelected(it)) }
                        )
                    }
                }
            }
        }
    }
}
