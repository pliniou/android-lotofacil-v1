package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.error.AppError
import com.cebolao.lotofacil.core.error.EmptyHistoryError
import com.cebolao.lotofacil.core.error.UnknownError
import com.cebolao.lotofacil.domain.model.CheckResult
import com.cebolao.lotofacil.domain.model.GameStatistic
import com.cebolao.lotofacil.domain.model.LotofacilConstants
import com.cebolao.lotofacil.domain.usecase.CheckGameUseCase
import com.cebolao.lotofacil.domain.usecase.GameCheckPhase
import com.cebolao.lotofacil.domain.usecase.GameCheckState
import com.cebolao.lotofacil.domain.usecase.SaveCheckUseCase
import com.cebolao.lotofacil.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@Immutable
data class CheckerScreenState(
    val uiState: CheckerUiState = CheckerUiState.Idle,
    val selectedNumbers: ImmutableSet<Int> = persistentSetOf(),
    val showClearConfirmation: Boolean = false,
    val showClearResultsConfirmation: Boolean = false
)

@Immutable
sealed interface CheckerUiState {
    data object Idle : CheckerUiState
    data class Loading(val phase: GameCheckPhase, val progress: Float = 0f) : CheckerUiState
    data class Success(
        val result: CheckResult,
        val simpleStats: ImmutableList<GameStatistic>
    ) : CheckerUiState
    data class Error(val messageResId: Int, val canRetry: Boolean = true) : CheckerUiState
}

@HiltViewModel
class CheckerViewModel @Inject constructor(
    private val checkGameUseCase: CheckGameUseCase,
    private val saveCheckUseCase: SaveCheckUseCase,
    savedStateHandle: SavedStateHandle
) : StateViewModel<CheckerScreenState>(CheckerScreenState()) {

    private var checkJob: Job? = null
    
    init {
        // Parse route arguments from SavedStateHandle directly
        savedStateHandle.get<String>("numbers")?.let { arg ->
            val numbers = arg.split(',').mapNotNull { it.toIntOrNull() }.toSet()
            if (numbers.isNotEmpty()) {
                updateState { it.copy(selectedNumbers = numbers.toPersistentSet()) }
                if (numbers.size == LotofacilConstants.GAME_SIZE) {
                    onCheckGameClicked()
                }
            }
        }
    }

    fun onNumberClicked(number: Int) {
        if (currentState.uiState is CheckerUiState.Loading) return
        
        updateState { state ->
            val newSelection = state.selectedNumbers.toMutableSet()
            if (number in newSelection) {
                newSelection.remove(number)
            } else if (newSelection.size < LotofacilConstants.GAME_SIZE) {
                newSelection.add(number)
            }
            state.copy(
                selectedNumbers = newSelection.toPersistentSet(),
                uiState = CheckerUiState.Idle
            )
        }
    }

    fun onCheckGameClicked() {
        val selection = currentState.selectedNumbers
        if (selection.size != LotofacilConstants.GAME_SIZE) {
            updateState { it.copy(uiState = CheckerUiState.Error(R.string.checker_incomplete_selection, canRetry = false)) }
            return
        }
        
        // Show confirmation if there are existing results
        if (currentState.uiState is CheckerUiState.Success) {
            updateState { it.copy(showClearResultsConfirmation = true) }
        } else {
            checkGame(selection)
        }
    }

    fun confirmClearResults() {
        updateState { it.copy(showClearResultsConfirmation = false) }
        checkGame(currentState.selectedNumbers)
    }

    fun dismissClearResultsConfirmation() {
        updateState { it.copy(showClearResultsConfirmation = false) }
    }

    private fun checkGame(numbers: Set<Int>) {
        checkJob?.cancel()
        checkJob = checkGameUseCase(numbers)
            .onEach { state ->
                when (state) {
                    is GameCheckState.InProgress -> {
                        updateState {
                            it.copy(
                                uiState = CheckerUiState.Loading(
                                    phase = state.phase,
                                    progress = state.progress
                                )
                            )
                        }
                    }
                    is GameCheckState.Success -> {
                        updateState {
                            it.copy(
                                uiState = CheckerUiState.Success(
                                    result = state.result,
                                    simpleStats = state.stats.toImmutableList()
                                )
                            )
                        }
                        // Save check to history
                        saveCheckToHistory(numbers, state.result)
                        sendUiEvent(
                            UiEvent.ShowSnackbar(messageResId = R.string.checker_checking_success)
                        )
                    }
                    is GameCheckState.Failure -> {
                        val errorState = mapErrorToUiState(state.error)
                        updateState { it.copy(uiState = errorState) }
                        sendUiEvent(
                            UiEvent.ShowSnackbar(messageResId = R.string.checker_checking_error)
                        )
                    }
                }
            }
            .catch { throwable ->
                val fallbackState = mapErrorToUiState(UnknownError(throwable))
                updateState { it.copy(uiState = fallbackState) }
                sendUiEvent(
                    UiEvent.ShowSnackbar(messageResId = R.string.checker_checking_error)
                )
            }
            .launchIn(viewModelScope)
        
        jobTracker.track(checkJob!!)
    }

    private fun mapErrorToUiState(error: AppError): CheckerUiState {
        return when (error) {
            is EmptyHistoryError -> CheckerUiState.Error(
                messageResId = R.string.error_no_history,
                canRetry = true
            )
            else -> CheckerUiState.Error(messageResId = R.string.error_unknown, canRetry = true)
        }
    }

    fun onClearSelectionClicked() {
        if (currentState.selectedNumbers.isNotEmpty()) {
            updateState { it.copy(showClearConfirmation = true) }
        }
    }

    fun confirmClearSelection() {
        checkJob?.cancel()
        updateState { CheckerScreenState() }
        sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.checker_clear_confirmation))
    }

    fun dismissClearConfirmation() {
        updateState { it.copy(showClearConfirmation = false) }
    }

    private fun saveCheckToHistory(gameNumbers: Set<Int>, result: CheckResult) {
        saveCheckUseCase(gameNumbers, result.lastCheckedContest, result)
            .catch { e ->
                // Log silenciosamente - não impede operação principal
                android.util.Log.w("CheckerViewModel", "Failed to save check history: ${e.message}")
            }
            .launchIn(viewModelScope)
    }
}
