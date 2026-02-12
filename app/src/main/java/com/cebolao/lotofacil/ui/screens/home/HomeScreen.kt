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
import androidx.compose.foundation.layout.size
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
import com.cebolao.lotofacil.ui.theme.iconButtonSize
import com.cebolao.lotofacil.ui.theme.iconMedium
import com.cebolao.lotofacil.viewmodels.HomeUiState
import com.cebolao.lotofacil.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onExploreFilters: () -> Unit = {},
    onOpenChecker: () -> Unit = {},
    onNavigateToInsights: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToGames: () -> Unit = {}
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
            }
        },
        onNavigateToExploreFilters = onExploreFilters,
        onNavigateToChecker = onOpenChecker,
        onNavigateToInsights = onNavigateToInsights,
        onNavigateToAbout = onNavigateToAbout,
        onNavigateToGames = onNavigateToGames
    )
}

sealed class HomeAction {
    data object RefreshData : HomeAction()
}

@Composable
fun HomeScreenContent(
    state: HomeUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onAction: (HomeAction) -> Unit = {},
    onNavigateToExploreFilters: () -> Unit = {},
    onNavigateToChecker: () -> Unit = {},
    onNavigateToInsights: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToGames: () -> Unit = {}
) {
    val syncFeedbackState = remember(state.syncProgress, state.isRefreshing, state.isInitialSync) {
        state.syncProgress?.let { (current, total) ->
            SyncFeedbackState.Progress(
                current = current,
                total = total,
                isInitialSync = state.isInitialSync
            )
        } ?: if (state.isRefreshing) {
            SyncFeedbackState.Refreshing
        } else {
            null
        }
    }

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

    PullToRefreshScreen(
        isRefreshing = state.isRefreshing,
        onRefresh = { onAction(HomeAction.RefreshData) }
    ) {
        AppScreenScaffold(
            modifier = Modifier.fillMaxSize(),
            title = stringResource(id = R.string.cebolao_title),
            subtitle = stringResource(id = R.string.lotofacil_subtitle),
            iconPainter = painterResource(id = R.drawable.ic_cebolalogo),
            snackbarHostState = snackbarHostState,
            actions = {
                RefreshButton(
                    isRefreshing = state.isRefreshing,
                    onClick = { onAction(HomeAction.RefreshData) }
                )
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
                        item(key = "welcome_banner", contentType = "welcome_banner") {
                            EnhancedCard(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = AppTheme.elevation.xs
                            ) {
                                WelcomeBanner(
                                    lastUpdateTime = state.lastUpdateTime,
                                    nextDrawDate = state.nextDrawDate,
                                    nextDrawContest = state.nextDrawContest,
                                    isTodayDrawDay = state.isTodayDrawDay,
                                    historySource = state.historySource,
                                    statisticsSource = state.statisticsSource,
                                    isShowingStaleData = state.isShowingStaleData,
                                    onExploreFilters = onNavigateToExploreFilters,
                                    onOpenChecker = onNavigateToChecker
                                )
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
                                EnhancedCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = AppTheme.elevation.xs
                                ) {
                                    CompactStatisticsPreview(
                                        stats = state.statistics,
                                        isLoading = state.isStatsLoading,
                                        onViewAll = onNavigateToInsights
                                    )
                                }
                            }
                        }

                        item(key = "quick_nav", contentType = "quick_nav") {
                            AnimateOnEntry(
                                delayMillis = AppAnimationConstants.Delays.Medium.toLong()
                            ) {
                                QuickNavSection(
                                    onNavigateToInsights = onNavigateToInsights,
                                    onNavigateToGames = onNavigateToGames,
                                    onNavigateToAbout = onNavigateToAbout
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RefreshButton(
    isRefreshing: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    IconButton(
        onClick = onClick,
        enabled = !isRefreshing,
        modifier = Modifier
            .size(iconButtonSize())
            .testTag(AppTestTags.HomeRefreshAction)
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = stringResource(id = R.string.cd_refresh_data),
            tint = if (isRefreshing) colors.primary else colors.onSurfaceVariant,
            modifier = Modifier.size(iconMedium())
        )
    }
}

@Composable
private fun HomeSectionPlaceholder(
    @StringRes messageResId: Int
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Text(
            text = stringResource(id = messageResId),
            modifier = Modifier.padding(AppSpacing.lg),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
