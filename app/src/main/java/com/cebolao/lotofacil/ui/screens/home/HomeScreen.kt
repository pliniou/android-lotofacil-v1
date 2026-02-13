package com.cebolao.lotofacil.ui.screens.home

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.navigation.UiEvent
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.AppScreenDefaults
import com.cebolao.lotofacil.ui.components.AppScreenScaffold
import com.cebolao.lotofacil.ui.components.AppScreenStateHost
import com.cebolao.lotofacil.ui.components.EnhancedCard
import com.cebolao.lotofacil.ui.components.PullToRefreshScreen
import com.cebolao.lotofacil.ui.components.ScreenContentState
import com.cebolao.lotofacil.ui.components.screenContentPadding
import com.cebolao.lotofacil.ui.testtags.AppTestTags
import com.cebolao.lotofacil.ui.theme.AppAnimationConstants
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppTheme
import com.cebolao.lotofacil.viewmodels.DataLoadSource
import com.cebolao.lotofacil.viewmodels.HomeSyncState
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
            }
        },
        onNavigateToInsights = onNavigateToInsights
    )
}

sealed class HomeAction {
    data object RefreshData : HomeAction()
    data class PatternSelected(val pattern: StatisticPattern) : HomeAction()
    data class TimeWindowSelected(val window: Int) : HomeAction()
}

@Composable
fun HomeScreenContent(
    state: HomeUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onAction: (HomeAction) -> Unit = {},
    onNavigateToInsights: () -> Unit = {}
) {
    val syncFeedbackState = remember(state.syncState) { state.syncState.toFeedbackState() }

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

    // Only show PullToRefresh spinner during the initial "Syncing" phase (total == null).
    // Once we have progress (total != null), we hide the spinner and let the SyncProgressBanner show the details.
    val isRefreshing = remember(state.syncState) {
        state.syncState is HomeSyncState.InProgress && state.syncState.total == null
    }

    PullToRefreshScreen(
        isRefreshing = isRefreshing,
        onRefresh = { onAction(HomeAction.RefreshData) }
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
            ) {
                AnimatedVisibility(
                    visible = syncFeedbackState != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    syncFeedbackState?.let {
                        SyncProgressBanner(feedbackState = it)
                    }
                }

                HomeDataSourceStatus(
                    historySource = state.historySource,
                    statisticsSource = state.statisticsSource,
                    isShowingStaleData = state.isShowingStaleData
                )

                AppScreenStateHost(
                    state = screenState,
                    modifier = Modifier.fillMaxSize(),
                    onRetry = { onAction(HomeAction.RefreshData) },
                    onEmptyAction = { onAction(HomeAction.RefreshData) }
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = AppScreenDefaults.listContentPadding(),
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
}

private fun HomeSyncState.toFeedbackState(): SyncFeedbackState? {
    return when (this) {
        HomeSyncState.Idle -> null
        is HomeSyncState.InProgress -> SyncFeedbackState.Progress(
            current = current,
            total = total
        )

        HomeSyncState.Success -> null
        is HomeSyncState.Failed -> SyncFeedbackState.Failed(message = message)
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
    val sourceText = stringResource(
        id = R.string.home_source_status_format,
        historySourceLabel,
        statisticsSourceLabel
    )

    Text(
        text = if (isShowingStaleData) {
            "$sourceText - ${stringResource(R.string.home_stale_data_warning)}"
        } else {
            sourceText
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xs),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
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
