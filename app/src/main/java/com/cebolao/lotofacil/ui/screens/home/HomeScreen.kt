package com.cebolao.lotofacil.ui.screens.home

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.navigation.UiEvent
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.AppScreenDefaults
import com.cebolao.lotofacil.ui.components.AppScreenScaffold
import com.cebolao.lotofacil.ui.components.AppScreenStateHost
import com.cebolao.lotofacil.ui.components.HomeSkeleton
import com.cebolao.lotofacil.ui.components.EnhancedCard
import com.cebolao.lotofacil.ui.components.PullToRefreshScreen
import com.cebolao.lotofacil.ui.components.ScreenContentState
import com.cebolao.lotofacil.ui.components.screenContentPadding
import com.cebolao.lotofacil.ui.testtags.AppTestTags
import com.cebolao.lotofacil.ui.theme.AppAnimationConstants
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppTheme
import com.cebolao.lotofacil.viewmodels.DataLoadSource
import com.cebolao.lotofacil.viewmodels.UpdateState
import com.cebolao.lotofacil.viewmodels.HomeUiState
import com.cebolao.lotofacil.viewmodels.HomeViewModel
import com.cebolao.lotofacil.viewmodels.StatisticPattern

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onNavigateToInsights: () -> Unit = {}
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalContext.current.resources

    LaunchedEffect(homeViewModel, resources) {
        homeViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    val resolvedMessage = event.message ?: event.messageResId?.let(resources::getString)
                    if (!resolvedMessage.isNullOrBlank()) {
                        snackbarHostState.showSnackbar(
                            message = resolvedMessage,
                            duration = SnackbarDuration.Long
                        )
                    }
                }

                else -> Unit
            }
        }
    }

    HomeScreenContent(
        state = uiState,
        snackbarHostState = snackbarHostState,
        onAction = { action ->
            when (action) {
                HomeAction.RefreshData -> homeViewModel.refreshData()
                is HomeAction.PatternSelected -> homeViewModel.onPatternSelected(action.pattern)
                is HomeAction.TimeWindowSelected -> homeViewModel.onTimeWindowSelected(action.window)
                HomeAction.CancelUpdate -> homeViewModel.cancelUpdate()
                HomeAction.Retry -> homeViewModel.retry()
            }
        },
        onNavigateToInsights = onNavigateToInsights
    )
}

sealed class HomeAction {
    data object RefreshData : HomeAction()
    data class PatternSelected(val pattern: StatisticPattern) : HomeAction()
    data class TimeWindowSelected(val window: Int) : HomeAction()
    data object CancelUpdate : HomeAction()
    data object Retry : HomeAction()
}

@Composable
fun HomeScreenContent(
    state: HomeUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onAction: (HomeAction) -> Unit = {},
    onNavigateToInsights: () -> Unit = {}
) {
    val refreshSuccessMessage = stringResource(id = R.string.refresh_success)
    val syncFailedFallback = stringResource(id = R.string.error_sync_failed)
    val retryLabel = stringResource(id = R.string.try_again)

    val hasPrimaryData = remember(state.lastDrawStats, state.statistics, state.lastUpdateTime) {
        state.lastDrawStats != null || state.statistics != null || !state.lastUpdateTime.isNullOrBlank()
    }

    val screenState = remember(
        state.isScreenLoading,
        state.errorMessageResId,
        hasPrimaryData
    ) {
        when {
            state.isScreenLoading && !hasPrimaryData -> {
                ScreenContentState.Loading(messageResId = R.string.home_loading_message)
            }

            state.errorMessageResId != null && !hasPrimaryData -> {
                ScreenContentState.Error(messageResId = state.errorMessageResId)
            }

            !state.isScreenLoading && !hasPrimaryData -> {
                ScreenContentState.Empty(
                    messageResId = R.string.home_empty_message,
                    actionLabelResId = R.string.refresh_button
                )
            }

            else -> ScreenContentState.Success
        }
    }

    LaunchedEffect(state.updateState, refreshSuccessMessage, syncFailedFallback, retryLabel) {
        when (val updateState = state.updateState) {
            UpdateState.Success -> {
                snackbarHostState.showSnackbar(
                    message = refreshSuccessMessage,
                    duration = SnackbarDuration.Short
                )
            }

            is UpdateState.Error -> {
                val result = snackbarHostState.showSnackbar(
                    message = updateState.message ?: syncFailedFallback,
                    actionLabel = retryLabel,
                    duration = SnackbarDuration.Long
                )
                if (result == SnackbarResult.ActionPerformed) {
                    onAction(HomeAction.Retry)
                }
            }

            else -> {}
        }
    }

    val isUpdateInProgress = state.updateState is UpdateState.Loading

    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshScreen(
            isRefreshing = isUpdateInProgress,
            onRefresh = { onAction(HomeAction.RefreshData) },
            testTag = AppTestTags.HomeRefreshAction
        ) {
            AppScreenScaffold(
                modifier = Modifier.fillMaxSize(),
                title = stringResource(id = R.string.cebolao_title),
                subtitle = stringResource(id = R.string.lotofacil_subtitle),
                iconPainter = painterResource(id = R.drawable.ic_cebolalogo),
                snackbarHostState = snackbarHostState,
                actions = {
                    // Actions removed as per requirement (PullToRefresh is sufficient)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .screenContentPadding(innerPadding)
                        .padding(top = AppSpacing.md)
                ) {
                    HomeDataSourceStatus(
                        historySource = state.historySource,
                        statisticsSource = state.statisticsSource,
                        isShowingStaleData = state.isShowingStaleData
                    )

                    AppScreenStateHost(
                        state = screenState,
                        modifier = Modifier.fillMaxSize(),
                        onRetry = { onAction(HomeAction.RefreshData) },
                        onEmptyAction = { onAction(HomeAction.RefreshData) },
                        loadingContent = {
                            HomeSkeleton()
                        }
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = AppScreenDefaults.listContentPadding(top = AppSpacing.lg),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
                        ) {
                            item(key = "greeting", contentType = "greeting") {
                                GreetingSection(
                                    nextDrawAccumulated = state.nextDraw?.isAccumulated == true,
                                    isDrawDay = state.isTodayDrawDay,
                                    lastUpdateTime = state.lastUpdateTime
                                )

                            }

                            state.nextDraw?.let { nextDraw ->
                                item(key = "next_draw", contentType = "next_draw") {
                                    AnimateOnEntry(delayMillis = AppAnimationConstants.Delays.Minimal.toLong()) {
                                        NextDrawSection(nextDraw = nextDraw)
                                    }
                                }
                            }

                            item(key = "last_draw", contentType = "last_draw") {
                                state.lastDrawStats?.let { stats ->
                                    AnimateOnEntry(
                                        delayMillis = AppAnimationConstants.Delays.Minimal.toLong()
                                    ) {
                                        EnhancedCard(
                                            modifier = Modifier.fillMaxWidth(),
                                            elevation = AppTheme.elevation.xs
                                        ) {
                                            LastDrawSection(stats)
                                        }
                                    }
                                } ?: HomeSectionPlaceholder(
                                    messageResId = R.string.home_last_draw_unavailable
                                )
                            }

                            item(key = "statistics", contentType = "statistics_preview") {
                                AnimateOnEntry(
                                    delayMillis = AppAnimationConstants.Delays.Short.toLong()
                                ) {
                                    QuickInsightsSection(
                                        stats = state.statistics,
                                        isLoading = state.isStatsLoading,
                                        isUpdateInProgress = isUpdateInProgress,
                                        selectedPattern = state.selectedPattern,
                                        selectedTimeWindow = state.selectedTimeWindow,
                                        statisticsSource = state.statisticsSource,
                                        isShowingStaleData = state.isShowingStaleData,
                                        onPatternSelected = { onAction(HomeAction.PatternSelected(it)) },
                                        onTimeWindowSelected = { onAction(HomeAction.TimeWindowSelected(it)) },
                                        onViewAll = onNavigateToInsights
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isUpdateInProgress,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.9f))
            ) {
                SyncProgressBanner(
                    updateState = state.updateState,
                    onCancel = { onAction(HomeAction.CancelUpdate) },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.sm)
                )
            }
        }
    }
}

@Composable
private fun HomeDataSourceStatus(
    historySource: DataLoadSource,
    statisticsSource: DataLoadSource,
    isShowingStaleData: Boolean
) {
    val historySourceLabel = when (historySource) {
        DataLoadSource.CACHE -> stringResource(R.string.home_source_history_cache)
        DataLoadSource.NETWORK -> stringResource(R.string.home_source_history_network)
        DataLoadSource.COMPUTED -> stringResource(R.string.home_source_history_cache)
    }
    val statisticsSourceLabel = when (statisticsSource) {
        DataLoadSource.CACHE -> stringResource(R.string.home_source_stats_cache)
        DataLoadSource.NETWORK -> stringResource(R.string.home_source_stats_network)
        DataLoadSource.COMPUTED -> stringResource(R.string.home_source_stats_computed)
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = historySourceLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "|",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Icon(
                imageVector = Icons.Outlined.BarChart,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = statisticsSourceLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isShowingStaleData) {
                Text(
                    text = " - ${stringResource(R.string.home_stale_data_warning)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun HomeSectionPlaceholder(
    @StringRes messageResId: Int
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Text(
            text = stringResource(id = messageResId),
            modifier = Modifier.padding(AppSpacing.lg),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

