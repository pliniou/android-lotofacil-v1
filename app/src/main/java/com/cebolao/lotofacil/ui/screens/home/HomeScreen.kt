package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.navigation.UiEvent
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.AppScreenDefaults
import com.cebolao.lotofacil.ui.components.AppScreenScaffold
import com.cebolao.lotofacil.ui.components.EnhancedCard
import com.cebolao.lotofacil.ui.components.ErrorActions
import com.cebolao.lotofacil.ui.components.ErrorCard
import com.cebolao.lotofacil.ui.components.FullScreenLoading
import com.cebolao.lotofacil.ui.components.PullToRefreshScreen
import com.cebolao.lotofacil.ui.components.SkeletonCard
import com.cebolao.lotofacil.ui.components.StatsCard
import com.cebolao.lotofacil.ui.components.screenContentPadding
import com.cebolao.lotofacil.ui.testtags.AppTestTags
import com.cebolao.lotofacil.ui.theme.AppAnimationConstants
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppTheme
import com.cebolao.lotofacil.ui.theme.iconButtonSize
import com.cebolao.lotofacil.ui.theme.iconExtraLarge
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
                    val resolvedMessage = event.message ?: event.messageResId?.let { messageResId ->
                        resources.getString(messageResId)
                    }
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
    object RefreshData : HomeAction()
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
                visible = state.syncProgress != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                state.syncProgress?.let { (current, total) ->
                    SyncProgressBanner(
                        current = current,
                        total = total,
                        isInitialSync = state.isInitialSync
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = AppScreenDefaults.listContentPadding(),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
            ) {
                when {
                    state.isScreenLoading -> {
                        item(key = "loading", contentType = "loading") {
                            FullScreenLoading(
                                message = "Carregando dados da Lotofácil..."
                            )
                        }
                    }

                    state.errorMessageResId != null -> {
                        item(key = "error", contentType = "error") {
                            HomeErrorState(messageResId = state.errorMessageResId) {
                                onAction(HomeAction.RefreshData)
                            }
                        }
                    }

                    else -> {
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
                                    isRefreshing = state.isRefreshing,
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
                            } ?: SkeletonCard()
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
}

@Composable
private fun RefreshButton(
    isRefreshing: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    val rotationAngle = if (isRefreshing) {
        val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(
            label = "refresh_rotation"
        )
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                animation = tween(
                    durationMillis = 1000,
                    easing = androidx.compose.animation.core.LinearEasing
                ),
                repeatMode = androidx.compose.animation.core.RepeatMode.Restart
            ),
            label = "rotation"
        ).value
    } else {
        0f
    }

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
            modifier = Modifier
                .size(iconMedium())
                .graphicsLayer {
                    rotationZ = if (isRefreshing) rotationAngle else 0f
                }
        )
    }
}

@Composable
private fun HomeScreenLoadingState() {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
    ) {
        WelcomeBanner(
            lastUpdateTime = null,
            nextDrawDate = null,
            nextDrawContest = null,
            isTodayDrawDay = false,
            onExploreFilters = {},
            onOpenChecker = {}
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AppSpacing.lg),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                CircularProgressIndicator(
                    color = colors.primary,
                    modifier = Modifier.size(iconExtraLarge())
                )
                Text(
                    text = stringResource(id = R.string.loading_data),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun HomeErrorState(messageResId: Int?, onRetry: () -> Unit) {
    ErrorCard(
        messageResId = messageResId ?: R.string.error_unknown,
        actions = { ErrorActions(onRetry = onRetry) }
    )
}
