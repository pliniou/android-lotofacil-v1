package com.cebolao.lotofacil.ui.screens.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AppScreenDefaults
import com.cebolao.lotofacil.ui.components.AppScreenScaffold
import com.cebolao.lotofacil.ui.components.AppScreenStateHost
import com.cebolao.lotofacil.ui.components.ScreenContentState
import com.cebolao.lotofacil.ui.components.screenContentPadding
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.UserStatsUiState
import com.cebolao.lotofacil.viewmodels.UserStatsViewModel

@Composable
fun UserStatsScreen(
    modifier: Modifier = Modifier,
    viewModel: UserStatsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UserStatsScreenContent(
        state = uiState,
        modifier = modifier,
        onNavigateBack = onNavigateBack,
        onAction = { action ->
            when (action) {
                is UserStatsAction.LoadStats -> viewModel.loadStats()
            }
        }
    )
}

// ==================== SEALED ACTIONS ====================
sealed class UserStatsAction {
    data object LoadStats : UserStatsAction()
}

// ==================== STATELESS CONTENT ====================
@Composable
fun UserStatsScreenContent(
    state: UserStatsUiState,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onAction: (UserStatsAction) -> Unit = {}
) {
    val hasStats = remember(state.stats) {
        state.stats != null && state.stats.totalGamesGenerated > 0
    }
    val screenState = remember(state.isLoading, state.errorMessageResId, hasStats) {
        when {
            state.isLoading -> ScreenContentState.Loading()
            state.errorMessageResId != null -> ScreenContentState.Error(messageResId = state.errorMessageResId)
            !hasStats -> ScreenContentState.Empty(
                messageResId = R.string.stats_empty_message,
                icon = Icons.Outlined.BarChart
            )

            else -> ScreenContentState.Success
        }
    }

    AppScreenScaffold(
        modifier = modifier.fillMaxSize(),
        title = stringResource(id = R.string.nav_user_stats),
        onBackClick = onNavigateBack
    ) { innerPadding ->
        AppScreenStateHost(
            state = screenState,
            modifier = Modifier
                .fillMaxSize()
                .screenContentPadding(innerPadding),
            onRetry = { onAction(UserStatsAction.LoadStats) }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = AppScreenDefaults.listContentPadding(
                    horizontal = AppSpacing.lg,
                    top = AppSpacing.lg,
                    bottom = AppSpacing.xl
                ),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
            ) {
                item(key = "user_stats_section") {
                    state.stats?.let { stats ->
                        UserStatsSection(stats = stats)
                    }
                }
            }
        }
    }
}
