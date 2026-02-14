package com.cebolao.lotofacil.ui.screens.generated

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.LotofacilGame
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
import com.cebolao.lotofacil.viewmodels.GameAnalysisResult
import com.cebolao.lotofacil.viewmodels.GameAnalysisUiState
import com.cebolao.lotofacil.viewmodels.GameUiState

@Composable
internal fun GeneratedGamesDialogs(
    state: GameUiState,
    onAction: (GeneratedGamesAction) -> Unit,
    onTogglePerformance: () -> Unit,
    onToggleRecentDraws: () -> Unit,
    onToggleCharacteristics: () -> Unit
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
        is GameAnalysisUiState.Loading -> {
            LoadingDialog(text = stringResource(id = R.string.analyzing_game))
        }
        is GameAnalysisUiState.Error -> {
            InfoDialog(
                onDismissRequest = { onAction(GeneratedGamesAction.DismissAnalysisDialog) },
                dialogTitle = stringResource(id = R.string.analysis_error_title),
                dismissButtonText = stringResource(id = R.string.close_button)
            ) {
                Text(text = stringResource(id = analysisState.messageResId))
            }
        }
        is GameAnalysisUiState.Success -> {
            state.analysisResult?.let { result ->
                InfoDialog(
                    onDismissRequest = { onAction(GeneratedGamesAction.DismissAnalysisDialog) },
                    dialogTitle = stringResource(id = R.string.game_analysis_title),
                    icon = Icons.Outlined.BarChart,
                    dismissButtonText = stringResource(id = R.string.close_button)
                ) {
                    GameAnalysisContent(
                        state = state,
                        result = result,
                        onTogglePerformance = onTogglePerformance,
                        onToggleRecentDraws = onToggleRecentDraws,
                        onToggleCharacteristics = onToggleCharacteristics
                    )
                }
            }
        }
        GameAnalysisUiState.Idle -> Unit
    }
}

@Composable
internal fun GeneratedGamesOverviewCard(
    totalGames: Int,
    pinnedGames: Int,
    modifier: Modifier = Modifier,
    onNavigateToAnalysis: (() -> Unit)? = null
) {
    AppCard(
        onClick = onNavigateToAnalysis,
        modifier = modifier,
        variant = CardVariant.Elevated,
        isGlassmorphic = true
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Text(
                text = stringResource(id = R.string.generated_games_overview_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xl)
            ) {
                OverviewMetric(
                    title = stringResource(id = R.string.total_games_label),
                    value = totalGames
                )
                OverviewMetric(
                    title = stringResource(id = R.string.pinned_games_label),
                    value = pinnedGames
                )
            }
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
internal fun GameAnalysisContent(
    state: GameUiState,
    result: GameAnalysisResult,
    onTogglePerformance: () -> Unit,
    onToggleRecentDraws: () -> Unit,
    onToggleCharacteristics: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        SectionHeader(
            title = stringResource(id = R.string.historical_performance),
            icon = Icons.Outlined.Analytics,
            isExpanded = state.isPerformanceExpanded,
            onToggle = onTogglePerformance
        )
        AnimatedVisibility(
            visible = state.isPerformanceExpanded,
            enter = expandVertically(animationSpec = tween(durationMillis = 220)) + fadeIn(animationSpec = tween(durationMillis = 180)),
            exit = shrinkVertically(animationSpec = tween(durationMillis = 200)) + fadeOut(animationSpec = tween(durationMillis = 160))
        ) {
            CheckResultCard(result = result.checkResult)
        }

        SectionHeader(
            title = stringResource(id = R.string.recent_hits_title),
            icon = Icons.Outlined.BarChart,
            isExpanded = state.isRecentDrawsExpanded,
            onToggle = onToggleRecentDraws
        )
        AnimatedVisibility(
            visible = state.isRecentDrawsExpanded,
            enter = expandVertically(animationSpec = tween(durationMillis = 220)) + fadeIn(animationSpec = tween(durationMillis = 180)),
            exit = shrinkVertically(animationSpec = tween(durationMillis = 200)) + fadeOut(animationSpec = tween(durationMillis = 160))
        ) {
            if (result.checkResult.recentHits.isNotEmpty()) {
                RecentHitsChartContent(recentHits = result.checkResult.recentHits)
            } else {
                Text(
                    text = stringResource(id = R.string.empty_state_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        SectionHeader(
            title = stringResource(id = R.string.game_stats_title),
            icon = Icons.Outlined.EmojiEvents,
            isExpanded = state.isCharacteristicsExpanded,
            onToggle = onToggleCharacteristics
        )
        AnimatedVisibility(
            visible = state.isCharacteristicsExpanded,
            enter = expandVertically(animationSpec = tween(durationMillis = 220)) + fadeIn(animationSpec = tween(durationMillis = 180)),
            exit = shrinkVertically(animationSpec = tween(durationMillis = 200)) + fadeOut(animationSpec = tween(durationMillis = 160))
        ) {
            GameStatsList(stats = result.simpleStats)
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(AppSpacing.sm))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onToggle) {
            androidx.compose.material3.Icon(
                imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (isExpanded) "Collapse section" else "Expand section",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

internal fun LazyListScope.generatedGamesListContent(
    games: List<LotofacilGame>,
    onAction: (GeneratedGamesAction) -> Unit
) {
    items(games, key = { it.id }) { game ->
        val dismissState = rememberSwipeToDismissBoxState(
            positionalThreshold = { totalDistance -> totalDistance * 0.35f },
            confirmValueChange = { targetValue ->
                if (targetValue == SwipeToDismissBoxValue.EndToStart && !game.isPinned) {
                    onAction(GeneratedGamesAction.DeleteGameRequested(game))
                }
                false
            }
        )

        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = false,
            enableDismissFromEndToStart = !game.isPinned,
            backgroundContent = {
                SwipeDeleteBackground(isPinned = game.isPinned)
            }
        ) {
            GameCard(
                game = game,
                modifier = Modifier.animateContentSize(animationSpec = tween(durationMillis = 250)),
                onAnalyzeClick = { onAction(GeneratedGamesAction.AnalyzeGame(game)) },
                onDuplicateClick = { onAction(GeneratedGamesAction.DuplicateAndEditGame(game)) },
                onShareClick = { onAction(GeneratedGamesAction.ShareGame(game)) },
                onPinClick = { onAction(GeneratedGamesAction.TogglePinState(game)) },
                onDeleteClick = { onAction(GeneratedGamesAction.DeleteGameRequested(game)) }
            )
        }
    }
}

@Composable
private fun SwipeDeleteBackground(isPinned: Boolean) {
    val colors = MaterialTheme.colorScheme
    val backgroundColor = if (isPinned) {
        colors.secondaryContainer
    } else {
        colors.errorContainer
    }
    val contentColor = if (isPinned) {
        colors.onSecondaryContainer
    } else {
        colors.onErrorContainer
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = AppSpacing.lg),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            androidx.compose.material3.Icon(
                imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Filled.Delete,
                contentDescription = null,
                tint = contentColor
            )
            Text(
                text = if (isPinned) {
                    stringResource(id = R.string.pinned_label)
                } else {
                    stringResource(id = R.string.delete_game)
                },
                style = MaterialTheme.typography.labelMedium,
                color = contentColor
            )
        }
    }
}
