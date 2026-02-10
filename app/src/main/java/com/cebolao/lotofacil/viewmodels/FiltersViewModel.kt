package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.DomainError
import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.usecase.GenerateGamesUseCase
import com.cebolao.lotofacil.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.roundToInt

@Immutable
data class FiltersUiState(
    val filterStates: ImmutableList<FilterState> = persistentListOf(),
    val isGenerating: Boolean = false,
    val lastDraw: ImmutableSet<Int>? = null,
    val activeFiltersCount: Int = 0,
    val successProbability: Float = 1f,
    val generationState: GenerationUiState = GenerationUiState.Idle
)


@HiltViewModel
class FiltersViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val generateGamesUseCase: GenerateGamesUseCase,
    private val historyRepository: HistoryRepository
) : StateViewModel<FiltersUiState>(FiltersUiState()) {

    // Throttle probability calculations to prevent excessive recomputations
    private var probabilityCalculationJob: Job? = null
    private val calculationThrottleMs = 150L // Throttle for 150ms

    init {
        loadLastDraw()
    }

    override fun onCleared() {
        super.onCleared()
        // Cancel probability calculation job to prevent memory leaks
        probabilityCalculationJob?.cancel()
    }

    private fun loadLastDraw() {
        viewModelScope.launch {
            val lastDrawNumbers = historyRepository.getLastDraw()?.numbers
            updateState { state ->
                state.copy(
                    lastDraw = lastDrawNumbers?.toImmutableSet(),
                    filterStates = FilterType.entries.map { FilterState(type = it) }.toImmutableList()
                )
            }
            updateProbability()
        }
    }

    fun onFilterToggle(type: FilterType, isEnabled: Boolean) {
        updateState { state ->
            state.copy(
                filterStates = state.filterStates.map { f ->
                    if (f.type == type) f.copy(isEnabled = isEnabled) else f
                }.toImmutableList()
            )
        }
        updateProbability()
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
        scheduleProbabilityUpdate()
    }

    private fun scheduleProbabilityUpdate() {
        // Cancel existing job to prevent multiple simultaneous calculations
        probabilityCalculationJob?.cancel()
        
        probabilityCalculationJob = viewModelScope.launch {
            // Throttle calculation to prevent excessive recomputations
            delay(calculationThrottleMs)
            updateProbability()
        }
    }

    private fun updateProbability() {
        _uiState.update { state ->
            val activeFilters = state.filterStates.filter { it.isEnabled }
            val activeFiltersCount = activeFilters.size
            
            // Only recalculate probability if there are active filters
            val successProbability = if (activeFiltersCount > 0) {
                calculateSuccessProbability(activeFilters)
            } else {
                1f // Default probability when no filters are active
            }
            
            state.copy(
                activeFiltersCount = activeFiltersCount,
                successProbability = successProbability
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
        _uiState.update { state ->
            state.copy(
                filterStates = FilterType.entries.map { FilterState(type = it) }.toImmutableList(),
                activeFiltersCount = 0,
                successProbability = 1f
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

    private fun calculateSuccessProbability(activeFilters: List<FilterState>): Float {
        if (activeFilters.isEmpty()) return 1f
        val probability = activeFilters.fold(1.0) { acc, filter ->
            val rangeCoverage = calculateRangeCoverage(filter)
            val filterSuccessChance = filter.type.historicalSuccessRate.toDouble().pow(1.0 - rangeCoverage)
            acc * filterSuccessChance
        }
        return probability.toFloat().coerceIn(0f, 1f)
    }

    private fun calculateRangeCoverage(filter: FilterState): Float {
        if (!filter.isEnabled) return 0f
        
        // Guard against inverted ranges by normalizing them
        val selectedRange = filter.selectedRange
        val start = selectedRange.start.coerceAtMost(selectedRange.endInclusive)
        val endInclusive = selectedRange.endInclusive.coerceAtLeast(selectedRange.start)
        val normalizedRange = start..endInclusive
        
        // Return 1f when the range is empty or invalid
        if (normalizedRange.isEmpty() || start > endInclusive) return 1f
        
        val totalRange = filter.type.fullRange.endInclusive - filter.type.fullRange.start
        return if (totalRange > 0) {
            (normalizedRange.endInclusive - normalizedRange.start) / totalRange
        } else {
            1f // Return 1f for invalid total range
        }
    }
}
