package com.cebolao.lotofacil.ui.screens.generated

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.utils.GameShareUtils
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.navigation.UiEvent
import com.cebolao.lotofacil.ui.components.AppScreenDefaults
import com.cebolao.lotofacil.ui.components.AppScreenScaffold
import com.cebolao.lotofacil.ui.components.AppScreenStateHost
import com.cebolao.lotofacil.ui.components.ScreenContentState
import com.cebolao.lotofacil.ui.components.screenContentPadding
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.GameUiState
import com.cebolao.lotofacil.viewmodels.GameViewModel

@Composable
fun GeneratedGamesScreen(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = hiltViewModel(),
    onNavigateToFilters: () -> Unit = {},
    onNavigateToAnalysis: (LotofacilGame) -> Unit = {},
    onDuplicateAndEdit: (LotofacilGame) -> Unit = {},
    onBackClick: (() -> Unit)? = null
) {
    val uiState by gameViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        gameViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    val message = event.message ?: event.messageResId?.let(context::getString).orEmpty()
                    if (message.isNotBlank()) {
                        snackbarHostState.showSnackbar(message = message)
                    }
                }

                else -> Unit
            }
        }
    }

    GeneratedGamesScreenContent(
        state = uiState,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onNavigateToFilters = onNavigateToFilters,
        onTogglePerformance = gameViewModel::togglePerformanceExpanded,
        onToggleRecentDraws = gameViewModel::toggleRecentDrawsExpanded,
        onToggleCharacteristics = gameViewModel::toggleCharacteristicsExpanded,
        onBackClick = onBackClick,
        onAction = { action ->
            when (action) {
                GeneratedGamesAction.ClearGamesRequested -> gameViewModel.onClearGamesRequested()
                GeneratedGamesAction.ConfirmClearUnpinned -> gameViewModel.confirmClearUnpinned()
                GeneratedGamesAction.DismissClearDialog -> gameViewModel.dismissClearDialog()
                is GeneratedGamesAction.ConfirmDeleteGame -> gameViewModel.confirmDeleteGame(action.game)
                GeneratedGamesAction.DismissDeleteDialog -> gameViewModel.dismissDeleteDialog()
                is GeneratedGamesAction.AnalyzeGame -> onNavigateToAnalysis(action.game)
                is GeneratedGamesAction.TogglePinState -> gameViewModel.togglePinState(action.game)
                is GeneratedGamesAction.DeleteGameRequested -> gameViewModel.onDeleteGameRequested(action.game)
                GeneratedGamesAction.DismissAnalysisDialog -> gameViewModel.dismissAnalysisDialog()
                GeneratedGamesAction.LoadMoreGames -> gameViewModel.loadMoreGames()
                is GeneratedGamesAction.DuplicateAndEditGame -> onDuplicateAndEdit(action.game)
                is GeneratedGamesAction.ShareGame -> {
                    val shareText = GameShareUtils.formatGameForWhatsApp(action.game)
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val chooser = Intent.createChooser(
                        sendIntent,
                        context.getString(R.string.share_game_chooser_title)
                    )
                    context.startActivity(chooser)
                }
            }
        }
    )
}

sealed class GeneratedGamesAction {
    object ClearGamesRequested : GeneratedGamesAction()
    object ConfirmClearUnpinned : GeneratedGamesAction()
    object DismissClearDialog : GeneratedGamesAction()
    data class ConfirmDeleteGame(val game: LotofacilGame) : GeneratedGamesAction()
    object DismissDeleteDialog : GeneratedGamesAction()
    data class AnalyzeGame(val game: LotofacilGame) : GeneratedGamesAction()
    data class ShareGame(val game: LotofacilGame) : GeneratedGamesAction()
    data class DuplicateAndEditGame(val game: LotofacilGame) : GeneratedGamesAction()
    data class TogglePinState(val game: LotofacilGame) : GeneratedGamesAction()
    data class DeleteGameRequested(val game: LotofacilGame) : GeneratedGamesAction()
    object LoadMoreGames : GeneratedGamesAction()
    object DismissAnalysisDialog : GeneratedGamesAction()
}

@Composable
fun GeneratedGamesScreenContent(
    state: GameUiState,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onNavigateToFilters: () -> Unit = {},
    onTogglePerformance: () -> Unit = {},
    onToggleRecentDraws: () -> Unit = {},
    onToggleCharacteristics: () -> Unit = {},
    onAction: (GeneratedGamesAction) -> Unit = {},
    onBackClick: (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme
    val games = state.visibleGames

    val hasUnpinnedGames by remember(state.totalGamesCount, state.pinnedGamesCount) {
        derivedStateOf { state.totalGamesCount > state.pinnedGamesCount }
    }
    val isGamesEmpty by remember(state.totalGamesCount) {
        derivedStateOf { state.totalGamesCount == 0 }
    }
    val pageState by remember(state.isLoading, isGamesEmpty) {
        derivedStateOf {
            when {
                state.isLoading -> ScreenContentState.Loading(messageResId = R.string.loading_games)
                isGamesEmpty -> ScreenContentState.Empty(
                    messageResId = R.string.empty_games_message,
                    icon = Icons.Outlined.Casino,
                    actionLabelResId = R.string.generate_first_game
                )

                else -> ScreenContentState.Success
            }
        }
    }

    GeneratedGamesDialogs(
        state = state,
        onAction = onAction,
        onTogglePerformance = onTogglePerformance,
        onToggleRecentDraws = onToggleRecentDraws,
        onToggleCharacteristics = onToggleCharacteristics
    )

    AppScreenScaffold(
        modifier = modifier.fillMaxSize(),
        title = stringResource(id = R.string.my_games),
        subtitle = stringResource(id = R.string.my_games_subtitle),
        icon = Icons.AutoMirrored.Filled.ListAlt,
        onBackClick = onBackClick,
        snackbarHostState = snackbarHostState,
        actions = {
            if (hasUnpinnedGames) {
                IconButton(onClick = { onAction(GeneratedGamesAction.ClearGamesRequested) }) {
                    Icon(
                        Icons.Default.DeleteSweep,
                        contentDescription = stringResource(id = R.string.cd_clear_games),
                        tint = colors.error
                    )
                }
            }
        }
    ) { innerPadding ->
        AppScreenStateHost(
            state = pageState,
            modifier = Modifier
                .fillMaxSize()
                .screenContentPadding(innerPadding),
            onEmptyAction = onNavigateToFilters
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = 0.8f,
                            stiffness = 300f
                        )
                    ),
                contentPadding = AppScreenDefaults.listContentPadding(),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                item(key = "games_overview", contentType = "games_overview") {
                    GeneratedGamesOverviewCard(
                        totalGames = state.totalGamesCount,
                        pinnedGames = state.pinnedGamesCount,
                        modifier = Modifier.padding(bottom = AppSpacing.xs)
                    )
                }

                generatedGamesListContent(
                    games = games,
                    onAction = onAction
                )

                if (state.hasMoreGames || state.isLoadingMoreGames) {
                    item(key = "games_load_more", contentType = "games_load_more") {
                        LaunchedEffect(games.size, state.hasMoreGames, state.isLoadingMoreGames) {
                            if (state.hasMoreGames && !state.isLoadingMoreGames) {
                                onAction(GeneratedGamesAction.LoadMoreGames)
                            }
                        }
                        LoadMoreGamesItem(
                            isLoading = state.isLoadingMoreGames,
                            hasMoreGames = state.hasMoreGames,
                            onLoadMore = { onAction(GeneratedGamesAction.LoadMoreGames) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadMoreGamesItem(
    isLoading: Boolean,
    hasMoreGames: Boolean,
    onLoadMore: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .wrapContentHeight(align = Alignment.CenterVertically),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> CircularProgressIndicator()
            hasMoreGames -> {
                TextButton(onClick = onLoadMore) {
                    Text(text = stringResource(id = R.string.load_more_games))
                }
            }
        }
    }
}
