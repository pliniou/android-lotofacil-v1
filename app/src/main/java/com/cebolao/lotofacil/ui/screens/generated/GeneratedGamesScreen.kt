package com.cebolao.lotofacil.ui.screens.generated

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.navigation.UiEvent
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.AppScreenDefaults
import com.cebolao.lotofacil.ui.components.AppScreenScaffold
import com.cebolao.lotofacil.ui.components.CheckResultCard
import com.cebolao.lotofacil.ui.components.ConfirmationDialog
import com.cebolao.lotofacil.ui.components.EmptyState
import com.cebolao.lotofacil.ui.components.GameStatsList
import com.cebolao.lotofacil.ui.components.InfoDialog
import com.cebolao.lotofacil.ui.components.LoadingDialog
import com.cebolao.lotofacil.ui.components.RecentHitsChartContent
import com.cebolao.lotofacil.ui.components.screenContentPadding
import com.cebolao.lotofacil.ui.components.cards.GameCard
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.components.AppCard
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.components.shimmer
import com.cebolao.lotofacil.viewmodels.GameAnalysisResult
import com.cebolao.lotofacil.viewmodels.GameAnalysisUiState
import com.cebolao.lotofacil.viewmodels.GameUiState
import com.cebolao.lotofacil.viewmodels.GameViewModel
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.ui.theme.AppTheme
import com.cebolao.lotofacil.core.utils.GameShareUtils
import android.content.Intent

@Composable
fun GeneratedGamesScreen(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = hiltViewModel()
) {
    val games by gameViewModel.generatedGames.collectAsStateWithLifecycle()
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
                else -> {}
            }
        }
    }

    // Delegate to Content composable
    GeneratedGamesScreenContent(
        games = games,
        state = uiState,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onAction = { action ->
            when (action) {
                is GeneratedGamesAction.ClearGamesRequested -> gameViewModel.onClearGamesRequested()
                is GeneratedGamesAction.ConfirmClearUnpinned -> gameViewModel.confirmClearUnpinned()
                is GeneratedGamesAction.DismissClearDialog -> gameViewModel.dismissClearDialog()
                is GeneratedGamesAction.ConfirmDeleteGame -> gameViewModel.confirmDeleteGame(action.game)
                is GeneratedGamesAction.DismissDeleteDialog -> gameViewModel.dismissDeleteDialog()
                is GeneratedGamesAction.AnalyzeGame -> gameViewModel.analyzeGame(action.game)
                is GeneratedGamesAction.TogglePinState -> gameViewModel.togglePinState(action.game)
                is GeneratedGamesAction.DeleteGameRequested -> gameViewModel.onDeleteGameRequested(action.game)
                is GeneratedGamesAction.DismissAnalysisDialog -> gameViewModel.dismissAnalysisDialog()
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

// ==================== SEALED ACTIONS ====================
sealed class GeneratedGamesAction {
    object ClearGamesRequested : GeneratedGamesAction()
    object ConfirmClearUnpinned : GeneratedGamesAction()
    object DismissClearDialog : GeneratedGamesAction()
    data class ConfirmDeleteGame(val game: LotofacilGame) : GeneratedGamesAction()
    object DismissDeleteDialog : GeneratedGamesAction()
    data class AnalyzeGame(val game: LotofacilGame) : GeneratedGamesAction()
    data class ShareGame(val game: LotofacilGame) : GeneratedGamesAction()
    data class TogglePinState(val game: LotofacilGame) : GeneratedGamesAction()
    data class DeleteGameRequested(val game: LotofacilGame) : GeneratedGamesAction()
    object DismissAnalysisDialog : GeneratedGamesAction()
}

// ==================== STATELESS CONTENT ====================
@Composable
fun GeneratedGamesScreenContent(
    state: GameUiState,
    modifier: Modifier = Modifier,
    games: List<LotofacilGame> = emptyList(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onAction: (GeneratedGamesAction) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    // Optimize expensive calculations with derivedStateOf
    val hasUnpinnedGames by remember(games) {
        derivedStateOf { games.any { !it.isPinned } }
    }
    val isGamesEmpty by remember(games) {
        derivedStateOf { games.isEmpty() }
    }

    if (state.showClearGamesDialog) {
        ConfirmationDialog(
            title = stringResource(id = R.string.clear_games_title),
            message = stringResource(id = R.string.clear_games_message),
            confirmText = stringResource(id = R.string.clear_button),
            dismissText = stringResource(id = R.string.cancel_button),
            onConfirm = { onAction(GeneratedGamesAction.ConfirmClearUnpinned) },
            onDismiss = { onAction(GeneratedGamesAction.DismissClearDialog) }
        )
    }

    state.gameToDelete?.let { game ->
        ConfirmationDialog(
            title = stringResource(id = R.string.delete_game_title),
            message = stringResource(id = R.string.delete_game_message),
            confirmText = stringResource(id = R.string.delete_button),
            dismissText = stringResource(id = R.string.cancel_button),
            onConfirm = { onAction(GeneratedGamesAction.ConfirmDeleteGame(game)) },
            onDismiss = { onAction(GeneratedGamesAction.DismissDeleteDialog) }
        )
    }

    when (val analysisState = state.analysisState) {
        is GameAnalysisUiState.Success -> {
            state.analysisResult?.let { result ->
                GameAnalysisDialog(
                    result = result,
                    onDismissRequest = { onAction(GeneratedGamesAction.DismissAnalysisDialog) }
                )
            }
        }
        is GameAnalysisUiState.Loading -> {
            LoadingDialog(text = stringResource(id = R.string.analyzing_game))
        }
        is GameAnalysisUiState.Error -> {
            ConfirmationDialog(
                title = stringResource(id = R.string.analysis_error_title),
                message = stringResource(id = analysisState.messageResId),
                confirmText = stringResource(id = R.string.close_button),
                dismissText = "",
                onConfirm = { onAction(GeneratedGamesAction.DismissAnalysisDialog) },
                onDismiss = { onAction(GeneratedGamesAction.DismissAnalysisDialog) }
            )
        }
        is GameAnalysisUiState.Idle -> {}
    }

    AppScreenScaffold(
        modifier = modifier.fillMaxSize(),
        title = stringResource(id = R.string.my_games),
        subtitle = stringResource(id = R.string.my_games_subtitle),
        icon = Icons.AutoMirrored.Filled.ListAlt,
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .screenContentPadding(innerPadding),
            contentPadding = AppScreenDefaults.listContentPadding(),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {

            if (state.isLoading) {
                items(5, contentType = { "game_card_skeleton" }) {
                    GameCardSkeleton()
                }
            } else if (isGamesEmpty) {
                item(contentType = "empty_state") {
                    EmptyState(
                        messageResId = R.string.empty_games_message,
                        icon = Icons.AutoMirrored.Filled.ListAlt
                    )
                }
            } else {
                itemsIndexed(
                    games,
                    key = { _, game -> game.id },
                    contentType = { _, _ -> "game_card" }
                ) { index, game ->
                    val motion = AppTheme.motion
                    val animationDelay = remember(index, motion.delayStaggerMs, motion.maxStaggerDelayMs) {
                        (index.toLong() * motion.delayStaggerMs)
                            .coerceAtMost(motion.maxStaggerDelayMs)
                    }
                    AnimateOnEntry(delayMillis = animationDelay) {
                        GameCard(
                            game = game,
                            onAnalyzeClick = { onAction(GeneratedGamesAction.AnalyzeGame(game)) },
                            onShareClick = { onAction(GeneratedGamesAction.ShareGame(game)) },
                            onPinClick = { onAction(GeneratedGamesAction.TogglePinState(game)) },
                            onDeleteClick = { onAction(GeneratedGamesAction.DeleteGameRequested(game)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameAnalysisDialog(
    result: GameAnalysisResult,
    onDismissRequest: () -> Unit
) {
    InfoDialog(
        onDismissRequest = onDismissRequest,
        dialogTitle = stringResource(id = R.string.game_analysis_title),
        dismissButtonText = stringResource(id = R.string.close_button)
    ) {
        Text(
            stringResource(id = R.string.historical_performance),
            style = MaterialTheme.typography.titleLarge,
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = AppSpacing.xs))
        if (result.checkResult.scoreCounts.isEmpty()) {
            Text(
                stringResource(id = R.string.never_won_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            CheckResultCard(result = result.checkResult)
        }
        RecentHitsChartContent(
            recentHits = result.checkResult.recentHits,
            modifier = Modifier.padding(top = AppSpacing.lg)
        )
        Text(
            stringResource(id = R.string.game_stats_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = AppSpacing.lg)
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = AppSpacing.xs))
        GameStatsList(stats = result.simpleStats)

    }
}
@Composable
private fun GameCardSkeleton() {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .shimmer(),
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        // Shimmering content
    }
}
