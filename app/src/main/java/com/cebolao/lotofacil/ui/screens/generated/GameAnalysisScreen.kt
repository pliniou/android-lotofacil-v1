package com.cebolao.lotofacil.ui.screens.generated

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.AppScreenDefaults
import com.cebolao.lotofacil.ui.components.AppScreenScaffold
import com.cebolao.lotofacil.ui.components.AppScreenStateHost
import com.cebolao.lotofacil.ui.components.NumberBall
import com.cebolao.lotofacil.ui.components.ScreenContentState
import com.cebolao.lotofacil.ui.components.screenContentPadding
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.GameAnalysisUiState
import com.cebolao.lotofacil.viewmodels.GameViewModel

@Composable
fun GameAnalysisScreen(
    gameId: String,
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onGenerateVariation: (LotofacilGame) -> Unit
) {
    val games by gameViewModel.generatedGames.collectAsStateWithLifecycle()
    val uiState by gameViewModel.uiState.collectAsStateWithLifecycle()
    val game = remember(games, gameId) {
        games.firstOrNull { it.id == gameId }
    }

    LaunchedEffect(game?.id) {
        if (game != null) {
            gameViewModel.analyzeGame(game)
        }
    }

    val pageState = remember(game, uiState.analysisState, uiState.analysisResult) {
        when {
            game == null -> ScreenContentState.Error(messageResId = R.string.error_game_not_found)
            uiState.analysisState is GameAnalysisUiState.Loading -> {
                ScreenContentState.Loading(messageResId = R.string.analyzing_game)
            }

            uiState.analysisState is GameAnalysisUiState.Error -> {
                ScreenContentState.Error(messageResId = (uiState.analysisState as GameAnalysisUiState.Error).messageResId)
            }

            uiState.analysisResult?.game?.id == gameId -> ScreenContentState.Success
            else -> ScreenContentState.Loading(messageResId = R.string.analyzing_game)
        }
    }

    AppScreenScaffold(
        modifier = modifier.fillMaxSize(),
        title = stringResource(id = R.string.game_analysis_title),
        subtitle = stringResource(id = R.string.game_analysis_subtitle),
        icon = Icons.Outlined.BarChart,
        onBackClick = onBackClick,
        floatingActionButton = {
            game?.let { currentGame ->
                ExtendedFloatingActionButton(
                    onClick = { onGenerateVariation(currentGame) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Casino,
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(text = stringResource(id = R.string.generate_similar_game_button))
                    }
                )
            }
        }
    ) { innerPadding ->
        AppScreenStateHost(
            state = pageState,
            modifier = Modifier
                .fillMaxSize()
                .screenContentPadding(innerPadding),
            onRetry = {
                game?.let { gameViewModel.analyzeGame(it) }
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = AppScreenDefaults.listContentPadding(),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                game?.let { currentGame ->
                    item(key = "analysis_numbers", contentType = "analysis_numbers") {
                        AnalysisNumbersCard(game = currentGame)
                    }
                }

                uiState.analysisResult?.takeIf { it.game.id == gameId }?.let { result ->
                    item(key = "analysis_content", contentType = "analysis_content") {
                        AppCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            androidx.compose.foundation.layout.Column(
                                modifier = Modifier.padding(AppSpacing.lg),
                                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
                            ) {
                                GameAnalysisContent(
                                    state = uiState,
                                    result = result,
                                    onTogglePerformance = gameViewModel::togglePerformanceExpanded,
                                    onToggleRecentDraws = gameViewModel::toggleRecentDrawsExpanded,
                                    onToggleCharacteristics = gameViewModel::toggleCharacteristicsExpanded
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AnalysisNumbersCard(game: LotofacilGame) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            Text(text = stringResource(id = R.string.analysis_numbers_title))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                game.numbers.sorted().forEach { number ->
                    NumberBall(number = number)
                }
            }
        }
    }
}
