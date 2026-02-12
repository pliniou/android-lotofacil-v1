package com.cebolao.lotofacil.ui.screens.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AppScreenDefaults
import com.cebolao.lotofacil.ui.components.AppScreenScaffold
import com.cebolao.lotofacil.ui.components.EmptyState
import com.cebolao.lotofacil.ui.components.ErrorCard
import com.cebolao.lotofacil.ui.components.FullScreenLoading
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

    // Delegate to Content composable
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
    object LoadStats : UserStatsAction()
}

// ==================== STATELESS CONTENT ====================
@Composable
fun UserStatsScreenContent(
    state: UserStatsUiState,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onAction: (UserStatsAction) -> Unit = {}
) {
    AppScreenScaffold(
        modifier = modifier.fillMaxSize(),
        title = stringResource(id = R.string.nav_user_stats),
        onBackClick = onNavigateBack
    ) { innerPadding ->
        when {
            state.isLoading -> {
                FullScreenLoading(
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
                    actions = { onAction(UserStatsAction.LoadStats) }
                )
            }
            state.stats != null && state.stats.totalGamesGenerated > 0 -> {
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
                    item(key = "user_stats_section") {
                        UserStatsSection(stats = state.stats)
                    }
                }
            }

            else -> {
                EmptyState(
                    messageResId = R.string.stats_empty_message,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}
