package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.CheckResult
import com.cebolao.lotofacil.domain.model.GameStatistic
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.usecase.CheckGameUseCase
import com.cebolao.lotofacil.domain.usecase.ClearUnpinnedGamesUseCase
import com.cebolao.lotofacil.domain.usecase.DeleteGameUseCase
import com.cebolao.lotofacil.domain.usecase.GameCheckState
import com.cebolao.lotofacil.domain.usecase.GetSavedGamesUseCase
import com.cebolao.lotofacil.domain.usecase.RecordGameUsageUseCase
import com.cebolao.lotofacil.domain.usecase.ToggleGamePinUseCase
import com.cebolao.lotofacil.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

private const val GAMES_PAGE_SIZE = 50

@Immutable
data class GameUiState(
    val isLoading: Boolean = true,
    val visibleGames: ImmutableList<LotofacilGame> = persistentListOf(),
    val totalGamesCount: Int = 0,
    val pinnedGamesCount: Int = 0,
    val hasMoreGames: Boolean = false,
    val isLoadingMoreGames: Boolean = false,
    val analysisState: GameAnalysisUiState = GameAnalysisUiState.Idle,
    val analysisResult: GameAnalysisResult? = null,
    val showClearGamesDialog: Boolean = false,
    val gameToDelete: LotofacilGame? = null,
    val isPerformanceExpanded: Boolean = true,
    val isRecentDrawsExpanded: Boolean = true,
    val isCharacteristicsExpanded: Boolean = true
)

@Immutable
data class GameAnalysisResult(
    val game: LotofacilGame,
    val simpleStats: ImmutableList<GameStatistic>,
    val checkResult: CheckResult
)

@Immutable
sealed interface GameAnalysisUiState {
    data object Idle : GameAnalysisUiState
    data object Loading : GameAnalysisUiState
    data class Success(val gameCount: Int = 0) : GameAnalysisUiState
    data class Error(val messageResId: Int) : GameAnalysisUiState
}

@HiltViewModel
class GameViewModel @Inject constructor(
    getSavedGamesUseCase: GetSavedGamesUseCase,
    private val gameRepository: GameRepository,
    private val checkGameUseCase: CheckGameUseCase,
    private val clearUnpinnedGamesUseCase: ClearUnpinnedGamesUseCase,
    private val toggleGamePinUseCase: ToggleGamePinUseCase,
    private val deleteGameUseCase: DeleteGameUseCase,
    private val recordGameUsageUseCase: RecordGameUsageUseCase
) : StateViewModel<GameUiState>(GameUiState()) {

    val generatedGames: StateFlow<ImmutableList<LotofacilGame>> = getSavedGamesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = persistentListOf()
        )

    init {
        viewModelScope.launch {
            gameRepository.gamesCount
                .distinctUntilChanged()
                .collect {
                    refreshPagedGamesInternal()
                }
        }
    }

    fun loadMoreGames() {
        if (currentState.isLoadingMoreGames || !currentState.hasMoreGames) return
        viewModelScope.launch {
            val offset = currentState.visibleGames.size
            loadPage(offset = offset)
        }
    }

    fun refreshPagedGames() {
        viewModelScope.launch {
            refreshPagedGamesInternal()
        }
    }

    private suspend fun refreshPagedGamesInternal() {
        updateState {
            it.copy(
                isLoading = true,
                visibleGames = persistentListOf(),
                hasMoreGames = false,
                isLoadingMoreGames = false
            )
        }
        refreshSummary()
        if (currentState.totalGamesCount > 0) {
            loadPage(offset = 0)
        } else {
            updateState { it.copy(isLoading = false) }
        }
    }

    private suspend fun refreshSummary() {
        when (val summaryResult = gameRepository.getGameListSummary()) {
            is com.cebolao.lotofacil.core.result.AppResult.Success -> {
                val summary = summaryResult.value
                updateState {
                    it.copy(
                        totalGamesCount = summary.totalGames,
                        pinnedGamesCount = summary.pinnedGames
                    )
                }
            }

            is com.cebolao.lotofacil.core.result.AppResult.Failure -> {
                sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_load_data_failed))
            }
        }
    }

    private suspend fun loadPage(offset: Int) {
        updateState { it.copy(isLoadingMoreGames = true) }
        when (val pageResult = gameRepository.getGamesPage(limit = GAMES_PAGE_SIZE, offset = offset)) {
            is com.cebolao.lotofacil.core.result.AppResult.Success -> {
                val page = pageResult.value
                updateState { state ->
                    val updatedGames = if (offset == 0) {
                        page.toImmutableList()
                    } else {
                        (state.visibleGames + page).toImmutableList()
                    }
                    state.copy(
                        isLoading = false,
                        isLoadingMoreGames = false,
                        visibleGames = updatedGames,
                        hasMoreGames = updatedGames.size < state.totalGamesCount
                    )
                }
            }

            is com.cebolao.lotofacil.core.result.AppResult.Failure -> {
                updateState { it.copy(isLoading = false, isLoadingMoreGames = false, hasMoreGames = false) }
                sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_load_data_failed))
            }
        }
    }

    fun onClearGamesRequested() {
        viewModelScope.launch { updateState { it.copy(showClearGamesDialog = true) } }
    }
    fun confirmClearUnpinned() {
        viewModelScope.launch {
            clearUnpinnedGamesUseCase()
            updateState { it.copy(showClearGamesDialog = false) }
            sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.unpinned_games_cleared))
        }
    }
    fun dismissClearDialog() {
        updateState { it.copy(showClearGamesDialog = false) }
    }
    fun onDeleteGameRequested(game: LotofacilGame) {
        viewModelScope.launch { updateState { it.copy(gameToDelete = game) } }
    }
    fun confirmDeleteGame(game: LotofacilGame) {
        viewModelScope.launch {
            deleteGameUseCase(game)
            updateState { it.copy(gameToDelete = null) }
            sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.game_deleted_confirmation))
        }
    }
    fun dismissDeleteDialog() {
        updateState { it.copy(gameToDelete = null) }
    }
    fun analyzeGame(game: LotofacilGame) {
        if (currentState.analysisState is GameAnalysisUiState.Loading) return
        viewModelScope.launch {
            updateState { it.copy(analysisState = GameAnalysisUiState.Loading) }
            try {
                val checkState = withTimeoutOrNull(5000L) {
                    checkGameUseCase(game.numbers)
                        .first { it is GameCheckState.Success || it is GameCheckState.Failure }
                }
                
                when (checkState) {
                    is GameCheckState.Success -> {
                        recordGameUsageUseCase(game.id)
                        val result = GameAnalysisResult(
                            game = game,
                            simpleStats = checkState.stats.toImmutableList(),
                            checkResult = checkState.result
                        )
                        updateState { it.copy(analysisState = GameAnalysisUiState.Success(), analysisResult = result) }
                    }
                    is GameCheckState.Failure -> {
                        val messageResId = when (checkState.error) {
                            is com.cebolao.lotofacil.core.error.EmptyHistoryError -> R.string.error_no_history
                            else -> R.string.error_analysis_failed
                        }
                        updateState { it.copy(analysisState = GameAnalysisUiState.Error(messageResId)) }
                    }
                    null -> {
                        // Timeout occurred
                        updateState { it.copy(analysisState = GameAnalysisUiState.Error(R.string.error_analysis_timeout)) }
                        sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_analysis_timeout))
                    }
                    else -> Unit
                }
            } catch (_: Exception) {
                val messageResId = R.string.error_analysis_failed
                updateState { it.copy(analysisState = GameAnalysisUiState.Error(messageResId)) }
                sendUiEvent(UiEvent.ShowSnackbar(messageResId = messageResId))
            }
        }
    }
    fun dismissAnalysisDialog() {
        updateState { it.copy(analysisState = GameAnalysisUiState.Idle, analysisResult = null) }
    }

    fun togglePerformanceExpanded() {
        updateState { it.copy(isPerformanceExpanded = !it.isPerformanceExpanded) }
    }

    fun toggleRecentDrawsExpanded() {
        updateState { it.copy(isRecentDrawsExpanded = !it.isRecentDrawsExpanded) }
    }

    fun toggleCharacteristicsExpanded() {
        updateState { it.copy(isCharacteristicsExpanded = !it.isCharacteristicsExpanded) }
    }
    fun togglePinState(game: LotofacilGame) {
        viewModelScope.launch {
            toggleGamePinUseCase(game)
            refreshPagedGamesInternal()
        }
    }
}
