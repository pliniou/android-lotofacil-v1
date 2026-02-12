package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.DomainError
import com.cebolao.lotofacil.domain.model.FilterPreset
import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.usecase.GenerateGamesUseCase
import com.cebolao.lotofacil.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@Immutable
data class FiltersUiState(
    val filterStates: ImmutableList<FilterState> = persistentListOf(),
    val isGenerating: Boolean = false,
    val lastDraw: ImmutableSet<Int>? = null,
    val activeFiltersCount: Int = 0,
    val generationState: GenerationUiState = GenerationUiState.Idle
)


@HiltViewModel
class FiltersViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val generateGamesUseCase: GenerateGamesUseCase,
    private val historyRepository: HistoryRepository
) : StateViewModel<FiltersUiState>(FiltersUiState()) {

    init {
        loadLastDraw()
    }

    private fun loadLastDraw() {
        viewModelScope.launch {
            val filterStates = defaultFilterStates()
            try {
                val lastDrawNumbers = historyRepository.getLastDraw()?.numbers
                updateState { state ->
                    state.copy(
                        lastDraw = lastDrawNumbers?.toImmutableSet(),
                        filterStates = filterStates
                    )
                }
            } catch (_: Exception) {
                updateState { state ->
                    state.copy(
                        lastDraw = null,
                        filterStates = filterStates
                    )
                }
                sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_load_data_failed))
            }
        }
    }

    private fun defaultFilterStates(): ImmutableList<FilterState> {
        return FilterType.entries
            .map { FilterState(type = it) }
            .toImmutableList()
    }

    fun onFilterToggle(type: FilterType, isEnabled: Boolean) {
        updateState { state ->
            val newFilterStates = state.filterStates.map { f ->
                if (f.type == type) f.copy(isEnabled = isEnabled) else f
            }.toImmutableList()
            state.copy(
                filterStates = newFilterStates,
                activeFiltersCount = newFilterStates.count { it.isEnabled }
            )
        }
    }

    fun onRangeAdjust(type: FilterType, newRange: ClosedFloatingPointRange<Float>) {
        val snappedRange = newRange.start.roundToInt().toFloat()..newRange.endInclusive.roundToInt().toFloat()
        updateState { state ->
            state.copy(
                filterStates = state.filterStates.map { f ->
                    if (f.type == type) f.copy(selectedRange = snappedRange) else f
                }.toImmutableList()
            )
        }
    }

    fun applyPreset(preset: FilterPreset) {
        updateState { state ->
            val newFilterStates = when (preset) {
                FilterPreset.COMMON -> FilterType.entries.map { type ->
                    FilterState(type = type, isEnabled = true, selectedRange = type.defaultRange)
                }
                FilterPreset.GAUSS -> FilterType.entries.map { type ->
                    // Narrow range around the middle of the default range
                    val center = (type.defaultRange.start + type.defaultRange.endInclusive) / 2
                    val halfWidth = (type.defaultRange.endInclusive - type.defaultRange.start) / 4
                    val narrowRange = (center - halfWidth).roundToInt().toFloat()..(center + halfWidth).roundToInt().toFloat()
                    FilterState(type = type, isEnabled = true, selectedRange = narrowRange)
                }
                FilterPreset.EXTREMES -> FilterType.entries.map { type ->
                    // Pick a range outside the common ones (either low or high part of full range)
                    val isLow = type.ordinal % 2 == 0
                    val range = if (isLow) {
                        type.fullRange.start..type.defaultRange.start
                    } else {
                        type.defaultRange.endInclusive..type.fullRange.endInclusive
                    }
                    FilterState(type = type, isEnabled = true, selectedRange = range)
                }
            }.toImmutableList()

            state.copy(
                filterStates = newFilterStates,
                activeFiltersCount = newFilterStates.count { it.isEnabled }
            )
        }
    }

    fun generateGames(quantity: Int) {
        if (_uiState.value.isGenerating) return
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, generationState = GenerationUiState.Loading) }
            when (val result = generateGamesUseCase(quantity, _uiState.value.filterStates)) {
                is AppResult.Success -> {
                    gameRepository.addGeneratedGames(result.value)
                    _uiState.update { it.copy(generationState = GenerationUiState.Success(result.value.size)) }
                    _uiEvent.send(UiEvent.NavigateToGeneratedGames)
                }
                is AppResult.Failure -> {
                    val messageResId = when (result.error) {
                        is DomainError.HistoryUnavailable -> R.string.error_history_unavailable
                        is DomainError.InvalidOperation -> R.string.error_invalid_operation
                        is DomainError.ValidationError -> R.string.error_validation
                        else -> R.string.error_generating_games
                    }
                    _uiState.update { it.copy(generationState = GenerationUiState.Error(messageResId)) }
                    _uiEvent.send(UiEvent.ShowSnackbar(messageResId = messageResId))
                }
            }
            _uiState.update { it.copy(isGenerating = false) }
        }
    }

    fun resetFilters() {
        updateState { state ->
            state.copy(
                filterStates = FilterType.entries.map { FilterState(type = it) }.toImmutableList(),
                activeFiltersCount = 0
            )
        }
    }

    fun requestResetAllFilters() {
        viewModelScope.launch {
            _uiEvent.send(UiEvent.ShowResetConfirmation)
        }
    }

    fun confirmResetAllFilters() {
        resetFilters()
    }

}
