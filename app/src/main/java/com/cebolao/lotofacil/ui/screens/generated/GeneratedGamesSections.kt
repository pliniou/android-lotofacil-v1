package com.cebolao.lotofacil.ui.screens.generated

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.CardVariant
import com.cebolao.lotofacil.ui.components.CheckResultCard
import com.cebolao.lotofacil.ui.components.ConfirmationDialog
import com.cebolao.lotofacil.ui.components.GameStatsList
import com.cebolao.lotofacil.ui.components.InfoDialog
import com.cebolao.lotofacil.ui.components.LoadingDialog
import com.cebolao.lotofacil.ui.components.RecentHitsChartContent
import com.cebolao.lotofacil.ui.components.cards.GameCard
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppTheme
import com.cebolao.lotofacil.viewmodels.GameAnalysisResult
import com.cebolao.lotofacil.viewmodels.GameAnalysisUiState
import com.cebolao.lotofacil.viewmodels.GameUiState

@Composable
internal fun GeneratedGamesDialogs(
    state: GameUiState,
    onAction: (GeneratedGamesAction) -> Unit
) {
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

        GameAnalysisUiState.Idle -> Unit
    }
}

@Composable
internal fun GeneratedGamesOverviewCard(
    totalGames: Int,
    pinnedGames: Int,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier,
        variant = CardVariant.Surfaced,
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xl)
        ) {
            OverviewMetric(
                title = stringResource(id = R.string.stats_total_generated),
                value = totalGames,
                modifier = Modifier.weight(1f)
            )
            OverviewMetric(
                title = stringResource(id = R.string.pinned_label),
                value = pinnedGames,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

internal fun LazyListScope.generatedGamesListContent(
    games: List<LotofacilGame>,
    onAction: (GeneratedGamesAction) -> Unit
) {
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

@Composable
private fun OverviewMetric(
    title: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
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
