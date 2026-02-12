package com.cebolao.lotofacil.ui.screens.generated

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
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
import com.cebolao.lotofacil.core.utils.GameShareUtils
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.navigation.UiEvent
import com.cebolao.lotofacil.ui.components.AppScreenDefaults
import com.cebolao.lotofacil.ui.components.AppScreenScaffold
import com.cebolao.lotofacil.ui.components.screenContentPadding
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.GameUiState
import com.cebolao.lotofacil.viewmodels.GameViewModel

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

                else -> Unit
            }
        }
    }

    GeneratedGamesScreenContent(
        games = games,
        state = uiState,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onAction = { action ->
            when (action) {
                GeneratedGamesAction.ClearGamesRequested -> gameViewModel.onClearGamesRequested()
                GeneratedGamesAction.ConfirmClearUnpinned -> gameViewModel.confirmClearUnpinned()
                GeneratedGamesAction.DismissClearDialog -> gameViewModel.dismissClearDialog()
                is GeneratedGamesAction.ConfirmDeleteGame -> gameViewModel.confirmDeleteGame(action.game)
                GeneratedGamesAction.DismissDeleteDialog -> gameViewModel.dismissDeleteDialog()
                is GeneratedGamesAction.AnalyzeGame -> gameViewModel.analyzeGame(action.game)
                is GeneratedGamesAction.TogglePinState -> gameViewModel.togglePinState(action.game)
                is GeneratedGamesAction.DeleteGameRequested -> gameViewModel.onDeleteGameRequested(action.game)
                GeneratedGamesAction.DismissAnalysisDialog -> gameViewModel.dismissAnalysisDialog()
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
    data class TogglePinState(val game: LotofacilGame) : GeneratedGamesAction()
    data class DeleteGameRequested(val game: LotofacilGame) : GeneratedGamesAction()
    object DismissAnalysisDialog : GeneratedGamesAction()
}

@Composable
fun GeneratedGamesScreenContent(
    state: GameUiState,
    modifier: Modifier = Modifier,
    games: List<LotofacilGame> = emptyList(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onAction: (GeneratedGamesAction) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    val hasUnpinnedGames by remember(games) {
        derivedStateOf { games.any { !it.isPinned } }
    }
    val isGamesEmpty by remember(games) {
        derivedStateOf { games.isEmpty() }
    }
    val pinnedGamesCount by remember(games) {
        derivedStateOf { games.count { it.isPinned } }
    }

    GeneratedGamesDialogs(
        state = state,
        onAction = onAction
    )

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
            if (!state.isLoading && !isGamesEmpty) {
                item(key = "games_overview", contentType = "games_overview") {
                    GeneratedGamesOverviewCard(
                        totalGames = games.size,
                        pinnedGames = pinnedGamesCount,
                        modifier = Modifier.padding(bottom = AppSpacing.xs)
                    )
                }
            }

            generatedGamesListContent(
                state = state,
                games = games,
                onAction = onAction
            )
        }
    }
}
