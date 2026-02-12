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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.navigation.UiEvent
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.AppScreenDefaults
import com.cebolao.lotofacil.ui.components.AppScreenScaffold
import com.cebolao.lotofacil.ui.components.ErrorActions
import com.cebolao.lotofacil.ui.components.ErrorCard
import com.cebolao.lotofacil.ui.components.screenContentPadding
import com.cebolao.lotofacil.ui.testtags.AppTestTags
import com.cebolao.lotofacil.ui.theme.AppAnimationConstants
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.iconButtonSize
import com.cebolao.lotofacil.ui.theme.iconExtraLarge
import com.cebolao.lotofacil.ui.theme.iconMedium
import com.cebolao.lotofacil.ui.theme.iconSmall
import com.cebolao.lotofacil.viewmodels.HomeUiState
import com.cebolao.lotofacil.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    // Handle events (navigation, snackbar)
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
                else -> {}
            }
        }
    }

    // Delegate styling and layout to Content composable
    HomeScreenContent(
        state = uiState,
        snackbarHostState = snackbarHostState,
        onAction = { action ->
            when (action) {
                is HomeAction.RefreshData -> homeViewModel.refreshData()
            }
        },
        onNavigateToExploreFilters = onExploreFilters,
        onNavigateToChecker = onOpenChecker,
        onNavigateToInsights = onNavigateToInsights,
        onNavigateToAbout = onNavigateToAbout,
        onNavigateToGames = onNavigateToGames
    )
}

// ==================== SEALED ACTIONS ====================
sealed class HomeAction {
    object RefreshData : HomeAction()
}

// ==================== STATELESS CONTENT ====================
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
            // Banner de sincronização inicial ou progresso manual
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
                            HomeScreenLoadingState()
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
                        item(key = "last_draw", contentType = "last_draw") {
                            state.lastDrawStats?.let { stats ->
                                AnimateOnEntry(
                                    delayMillis = AppAnimationConstants.Delays.Minimal.toLong()
                                ) {
                                    LastDrawSection(stats)
                                }
                            }
                        }
                        item(key = "statistics", contentType = "statistics_preview") {
                            AnimateOnEntry(
                                delayMillis = AppAnimationConstants.Delays.Short.toLong()
                            ) {
                                CompactStatisticsPreview(
                                    stats = state.statistics,
                                    isLoading = state.isStatsLoading,
                                    onViewAll = onNavigateToInsights
                                )
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

    // Rotação contínua infinita durante sincronização
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(
        label = "refresh_rotation"
    )
    val rotationAngle by infiniteTransition.animateFloat(
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
    )

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

// ==================== SEÇÃO DE NAVEGAÇÃO RÁPIDA ====================
@Composable
private fun QuickNavSection(
    onNavigateToInsights: () -> Unit,
    onNavigateToGames: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Text(
                text = stringResource(id = R.string.quick_links_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            FilterChip(
                selected = false,
                onClick = onNavigateToInsights,
                label = { Text(stringResource(id = R.string.nav_statistics_label)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.BarChart,
                        contentDescription = null,
                        modifier = Modifier.size(iconMedium())
                    )
                }
            )
            FilterChip(
                selected = false,
                onClick = onNavigateToGames,
                label = { Text(stringResource(id = R.string.nav_my_games_label)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.SportsEsports,
                        contentDescription = null,
                        modifier = Modifier.size(iconMedium())
                    )
                }
            )
            FilterChip(
                selected = false,
                onClick = onNavigateToAbout,
                label = { Text(stringResource(id = R.string.nav_about_label)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(iconMedium())
                    )
                }
            )
        }
    }
}
@Composable
private fun SyncProgressBanner(
    current: Int,
    total: Int,
    isInitialSync: Boolean
) {
    val colors = MaterialTheme.colorScheme
    val progress = (current.toFloat() / total).coerceIn(0f, 1f)
    
    // Tonal elevation for a modern "premium" feel
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = AppSpacing.md),
        shape = MaterialTheme.shapes.medium,
        color = colors.surfaceContainerHigh,
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            colors.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(iconSmall())
                    )
                    Text(
                        text = if (isInitialSync) {
                            stringResource(id = R.string.initial_sync_label)
                        } else {
                            stringResource(id = R.string.refresh_sync_label)
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                }
                Text(
                    text = "$current / $total",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small),
                color = colors.primary,
                trackColor = colors.surfaceContainerHighest
            )
        }
    }
}
