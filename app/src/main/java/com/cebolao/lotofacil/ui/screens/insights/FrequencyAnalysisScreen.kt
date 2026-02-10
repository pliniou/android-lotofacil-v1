package com.cebolao.lotofacil.ui.screens.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.usecase.FrequencyAnalysis
import com.cebolao.lotofacil.domain.usecase.TrendType
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.AppScreenDefaults
import com.cebolao.lotofacil.ui.components.AppScreenScaffold
import com.cebolao.lotofacil.ui.components.EmptyState
import com.cebolao.lotofacil.ui.components.ErrorActions
import com.cebolao.lotofacil.ui.components.ErrorCard
import com.cebolao.lotofacil.ui.components.LoadingData
import com.cebolao.lotofacil.ui.components.screenContentPadding
import com.cebolao.lotofacil.ui.theme.AppSize
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.InsightsUiState
import com.cebolao.lotofacil.viewmodels.InsightsViewModel

@Composable
fun FrequencyAnalysisScreen(
    modifier: Modifier = Modifier,
    viewModel: InsightsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FrequencyAnalysisScreenContent(
        state = uiState,
        modifier = modifier,
        onNavigateBack = onNavigateBack,
        onAction = { action ->
            when (action) {
                is FrequencyAnalysisAction.LoadFrequencyAnalysis -> viewModel.loadFrequencyAnalysis()
                is FrequencyAnalysisAction.PatternSizeSelected -> viewModel.onPatternSizeSelected(action.size)
                is FrequencyAnalysisAction.TrendTypeSelected -> viewModel.onTrendTypeSelected(action.type)
                is FrequencyAnalysisAction.TrendWindowSelected -> viewModel.onTrendWindowSelected(action.window)
                is FrequencyAnalysisAction.ReloadPattern -> viewModel.refreshPatternAnalysis()
                is FrequencyAnalysisAction.ReloadTrend -> viewModel.refreshTrendAnalysis()
            }
        }
    )
}

// ==================== SEALED ACTIONS ====================
sealed class FrequencyAnalysisAction {
    object LoadFrequencyAnalysis : FrequencyAnalysisAction()
    object ReloadPattern : FrequencyAnalysisAction()
    object ReloadTrend : FrequencyAnalysisAction()
    data class PatternSizeSelected(val size: Int) : FrequencyAnalysisAction()
    data class TrendTypeSelected(val type: TrendType) : FrequencyAnalysisAction()
    data class TrendWindowSelected(val window: Int) : FrequencyAnalysisAction()
}

// ==================== STATELESS CONTENT ====================
@Composable
fun FrequencyAnalysisScreenContent(
    state: InsightsUiState,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onAction: (FrequencyAnalysisAction) -> Unit = {}
) {
    var showGaussCurve by rememberSaveable { mutableStateOf(false) }

    AppScreenScaffold(
        modifier = modifier.fillMaxSize(),
        title = stringResource(id = R.string.insights_title),
        onBackClick = onNavigateBack
    ) { innerPadding ->
        when {
            state.isLoading -> {
                LoadingData(
                    message = stringResource(id = R.string.loading_data),
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
                    actions = {
                        ErrorActions(onRetry = { onAction(FrequencyAnalysisAction.LoadFrequencyAnalysis) })
                    }
                )
            }
            state.frequencyAnalysis != null -> {
                val analysis = state.frequencyAnalysis
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .screenContentPadding(innerPadding),
                    contentPadding = AppScreenDefaults.listContentPadding(
                        horizontal = AppSpacing.lg,
                        top = AppSpacing.lg,
                        bottom = AppSpacing.xl
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
                ) {
                    item(key = "summary", contentType = "summary") {
                        InsightsSummaryCard(totalDraws = analysis.totalDraws)
                    }

                    item(key = "frequency_section", contentType = "frequency_section") {
                        StatsSectionCard(
                            title = stringResource(id = R.string.frequency_analysis_title),
                            subtitle = stringResource(id = R.string.frequency_chart_subtitle),
                            trailing = {
                                GaussianCurveToggle(
                                    showGaussCurve = showGaussCurve,
                                    onToggle = { showGaussCurve = it }
                                )
                            }
                        ) {
                            FrequencyBarChart(
                                frequencies = analysis.frequencies,
                                showGaussCurve = showGaussCurve
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = AppSpacing.lg),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )

                            DistributionSubsections(analysis = analysis)
                        }
                    }

                    item(key = "pattern_section", contentType = "pattern_section") {
                        StatsSectionCard(
                            title = stringResource(id = R.string.patterns_title),
                            subtitle = stringResource(id = R.string.patterns_subtitle),
                            trailing = { PatternInfoTooltip() }
                        ) {
                            SectionStateContent(
                                isLoading = state.isPatternLoading,
                                errorMessageResId = state.patternErrorMessageResId,
                                onRetry = { onAction(FrequencyAnalysisAction.ReloadPattern) }
                            ) {
                                PatternListSection(
                                    analysis = state.patternAnalysis,
                                    selectedSize = state.selectedPatternSize,
                                    onSizeSelected = { size -> onAction(FrequencyAnalysisAction.PatternSizeSelected(size)) },
                                    showCard = false,
                                    showHeader = false
                                )
                            }
                        }
                    }

                    item(key = "trend_section", contentType = "trend_section") {
                        StatsSectionCard(
                            title = stringResource(id = R.string.trends_title),
                            subtitle = stringResource(id = R.string.trends_subtitle)
                        ) {
                            SectionStateContent(
                                isLoading = state.isTrendLoading,
                                errorMessageResId = state.trendErrorMessageResId,
                                onRetry = { onAction(FrequencyAnalysisAction.ReloadTrend) }
                            ) {
                                TrendSection(
                                    analysis = state.trendAnalysis,
                                    selectedType = state.selectedTrendType,
                                    selectedWindow = state.selectedTrendWindow,
                                    onTypeSelected = { type -> onAction(FrequencyAnalysisAction.TrendTypeSelected(type)) },
                                    onWindowSelected = { window -> onAction(FrequencyAnalysisAction.TrendWindowSelected(window)) },
                                    showCard = false,
                                    showHeader = false
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                EmptyState(
                    messageResId = R.string.error_no_history,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun InsightsSummaryCard(totalDraws: Int, modifier: Modifier = Modifier) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.total_draws_analyzed_format, totalDraws),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun StatsSectionCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (trailing != null) {
                    Column(modifier = Modifier.padding(start = AppSpacing.sm)) {
                        trailing()
                    }
                }
            }
            content()
        }
    }
}

@Composable
private fun SectionStateContent(
    isLoading: Boolean,
    errorMessageResId: Int?,
    onRetry: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    when {
        isLoading -> {
            LoadingData(
                message = stringResource(id = R.string.loading_data),
                modifier = Modifier.fillMaxWidth()
            )
        }
        errorMessageResId != null -> {
            ErrorCard(
                messageResId = errorMessageResId,
                modifier = Modifier.fillMaxWidth(),
                actions = { ErrorActions(onRetry = onRetry) }
            )
        }
        else -> content()
    }
}

@Composable
private fun DistributionSubsections(analysis: FrequencyAnalysis) {
    BoxWithConstraints {
        val isWide = maxWidth >= AppSize.breakpointTablet
        if (isWide) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.lg),
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                TopNumbersSection(
                    topNumbers = analysis.topNumbers,
                    showCard = false,
                    modifier = Modifier.weight(1f)
                )
                RecencySection(
                    overdueNumbers = analysis.overdueNumbers,
                    showCard = false,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)) {
                TopNumbersSection(
                    topNumbers = analysis.topNumbers,
                    showCard = false,
                    modifier = Modifier.fillMaxWidth()
                )
                RecencySection(
                    overdueNumbers = analysis.overdueNumbers,
                    showCard = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
