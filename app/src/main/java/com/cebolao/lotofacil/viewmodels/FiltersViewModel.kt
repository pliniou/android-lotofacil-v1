package com.cebolao.lotofacil.viewmodels

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.error.AppError
import com.cebolao.lotofacil.core.error.EmptyHistoryError
import com.cebolao.lotofacil.core.error.InvalidFiltersError
import com.cebolao.lotofacil.core.error.NetworkError
import com.cebolao.lotofacil.core.error.PersistenceError
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.domain.model.FilterPreset
import com.cebolao.lotofacil.domain.model.FilterSelectionMode
import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.domain.model.LotofacilConstants
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import com.cebolao.lotofacil.domain.usecase.GenerateGamesUseCase
import com.cebolao.lotofacil.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import java.util.Random

private const val LOTOFACIL_TOTAL_COMBINATIONS = 3_268_760L
private const val LOW_COMBINATION_LIMIT = 1_000L
private const val VERY_RESTRICTIVE_LIMIT = 10L

@Immutable
data class FiltersUiState(
    val filterStates: ImmutableList<FilterState> = persistentListOf(),
    val lastDraw: ImmutableSet<Int>? = null,
    val activeFiltersCount: Int = 0,
    val generationState: GenerationUiState = GenerationUiState.Idle,
    val isLoadingLastDraw: Boolean = true,
    val lastDrawErrorMessageResId: Int? = null,
    val isSyncingData: Boolean = false,
    val possibleCombinationsCount: Long = LOTOFACIL_TOTAL_COMBINATIONS,
    val isPossibleCombinationsEstimated: Boolean = false,
    val isCombinationImpossible: Boolean = false,
    val isVeryRestrictiveCombination: Boolean = false,
    val isAnalyzingCombinations: Boolean = false,
    val errorDialog: FiltersErrorDialog? = null
)

@Immutable
data class FiltersErrorDialog(
    @StringRes val titleResId: Int,
    @StringRes val messageResId: Int,
    @StringRes val confirmResId: Int,
    @StringRes val dismissResId: Int? = null
)

@HiltViewModel
class FiltersViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val generateGamesUseCase: GenerateGamesUseCase,
    private val historyRepository: HistoryRepository,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: AppLogger
) : StateViewModel<FiltersUiState>(FiltersUiState()) {
    companion object {
        private const val TAG = "FiltersViewModel"
    }

    private val combinationEstimator = CombinationEstimator()
    private var combinationAnalysisJob: Job? = null
    private var generationJob: Job? = null
    private var lastGenerationRequest: Int? = null

    init {
        loadLastDraw()
        // Validate filter uniqueness in debug builds
        validateFilterUniqueness()
        observeSyncStatus()
    }

    private fun observeSyncStatus() {
        viewModelScope.launch {
            historyRepository.syncStatus.collect { status ->
                val isSyncing = status is SyncStatus.Syncing || status is SyncStatus.Progress
                updateState { it.copy(isSyncingData = isSyncing) }
            }
        }
    }

    private fun loadLastDraw() {
        viewModelScope.launch {
            val fallbackFilterStates = currentState.filterStates.takeIf { it.isNotEmpty() } ?: defaultFilterStates()
            updateState {
                it.copy(
                    isLoadingLastDraw = true,
                    lastDrawErrorMessageResId = null,
                    filterStates = fallbackFilterStates
                )
            }
            try {
                val lastDrawNumbers = historyRepository.getLastDraw()?.numbers
                updateState { state ->
                    state.copy(
                        lastDraw = lastDrawNumbers?.toImmutableSet() ?: state.lastDraw,
                        filterStates = fallbackFilterStates,
                        isLoadingLastDraw = false,
                        lastDrawErrorMessageResId = if (lastDrawNumbers == null) {
                            R.string.error_history_unavailable
                        } else {
                            null
                        }
                    )
                }
                if (lastDrawNumbers == null) {
                    logHandledError(
                        action = "load_last_draw",
                        error = EmptyHistoryError,
                        throwable = null
                    )
                    sendUiEvent(
                        UiEvent.ShowSnackbar(
                            messageResId = R.string.error_history_unavailable,
                            actionLabelResId = R.string.try_again,
                            action = UiEvent.SnackbarAction.RetryLoadLastDraw
                        )
                    )
                }
                scheduleCombinationAnalysis()
            } catch (exception: Exception) {
                updateState { state ->
                    state.copy(
                        lastDraw = state.lastDraw,
                        filterStates = fallbackFilterStates,
                        isLoadingLastDraw = false,
                        lastDrawErrorMessageResId = R.string.error_load_data_failed
                    )
                }
                logHandledError(
                    action = "load_last_draw",
                    error = exception,
                    throwable = exception
                )
                sendUiEvent(
                    UiEvent.ShowSnackbar(
                        messageResId = R.string.error_load_data_failed,
                        actionLabelResId = R.string.try_again,
                        action = UiEvent.SnackbarAction.RetryLoadLastDraw
                    )
                )
                scheduleCombinationAnalysis()
            }
        }
    }

    private fun defaultFilterStates(): ImmutableList<FilterState> {
        val filterStates = FilterType.entries
            .map { FilterState(type = it) }
            .distinctBy { it.type.name } // Ensure uniqueness
            .toImmutableList()
        
        // Log warning if duplicates were found and removed
        if (filterStates.size != FilterType.entries.size) {
            Log.w("FiltersViewModel", "Duplicate filter types detected and removed. Original: ${FilterType.entries.size}, After deduplication: ${filterStates.size}")
        }
        
        return filterStates
    }
    
    private fun validateFilterUniqueness() {
        val filterNames = FilterType.entries.map { it.name }
        val duplicateNames = filterNames.groupBy { it }.filter { it.value.size > 1 }.keys
        
        if (duplicateNames.isNotEmpty()) {
            Log.e("FiltersViewModel", "CRITICAL: Duplicate filter names found: $duplicateNames")
            throw IllegalStateException("Duplicate filter names detected: $duplicateNames")
        }
    }

    fun onFilterToggle(type: FilterType, isEnabled: Boolean) {
        updateState { state ->
            val currentFilterNames = state.filterStates.map { it.type.name }
            if (currentFilterNames.distinct().size != currentFilterNames.size) {
                Log.w("FiltersViewModel", "Duplicate filter detected in state before toggle: ${currentFilterNames.groupBy { it }.filter { it.value.size > 1 }.keys}")
            }
            
            val newFilterStates = state.filterStates.map { f ->
                if (f.type == type) f.copy(isEnabled = isEnabled) else f
            }.distinctBy { it.type.name }.toImmutableList()
            
            state.copy(
                filterStates = newFilterStates,
                activeFiltersCount = newFilterStates.count { it.isEnabled }
            )
        }
        scheduleCombinationAnalysis()
    }

    fun onRangeAdjust(type: FilterType, newRange: ClosedFloatingPointRange<Float>) {
        val snappedRange = snapRange(type = type, range = newRange)
        updateState { state ->
            val newFilterStates = state.filterStates.map { f ->
                if (f.type == type) f.withValidatedRange(snappedRange) else f
            }.distinctBy { it.type.name }.toImmutableList()
            
            // Validate no duplicates were created
            if (newFilterStates.size != state.filterStates.size) {
                Log.w("FiltersViewModel", "Filter state size changed during range adjustment - possible duplication issue")
            }
            
            state.copy(filterStates = newFilterStates)
        }
        scheduleCombinationAnalysis()
    }

    fun onSingleValueAdjust(type: FilterType, value: Float) {
        val snapped = value.roundToInt().toFloat()
        onRangeAdjust(type = type, newRange = snapped..snapped)
    }

    fun onSelectionModeChange(type: FilterType, mode: FilterSelectionMode) {
        updateState { state ->
            val newFilterStates = state.filterStates.map { filter ->
                if (filter.type != type) return@map filter
                when (mode) {
                    FilterSelectionMode.SINGLE -> {
                        val center = ((filter.selectedRange.start + filter.selectedRange.endInclusive) / 2f)
                            .roundToInt()
                            .toFloat()
                        filter.withValidatedRange(center..center)
                    }

                    FilterSelectionMode.RANGE -> {
                        if (filter.selectionMode == FilterSelectionMode.RANGE) {
                            filter
                        } else {
                            val pivot = filter.singleValue.roundToInt().toFloat()
                            val range = expandSingleValueToRange(filter.type, pivot)
                            filter.withValidatedRange(range)
                        }
                    }
                }
            }.distinctBy { it.type.name }.toImmutableList()

            state.copy(filterStates = newFilterStates)
        }
        scheduleCombinationAnalysis()
    }

    fun applyPreset(preset: FilterPreset) {
        updateState { state ->
            val newFilterStates = when (preset) {
                FilterPreset.COMMON -> FilterType.entries.map { type ->
                    FilterState(type = type, isEnabled = true, selectedRange = type.defaultRange)
                }
                FilterPreset.GAUSS -> FilterType.entries.map { type ->
                    // Narrow range around middle of default range
                    val center = (type.defaultRange.start + type.defaultRange.endInclusive) / 2
                    val halfWidth = (type.defaultRange.endInclusive - type.defaultRange.start) / 4
                    val narrowRange = (center - halfWidth).roundToInt().toFloat()..(center + halfWidth).roundToInt().toFloat()
                    FilterState(type = type, isEnabled = true, selectedRange = narrowRange)
                }
                FilterPreset.EXTREMES -> FilterType.entries.map { type ->
                    // Pick a range outside common ones (either low or high part of full range)
                    val isLow = type.ordinal % 2 == 0
                    val range = if (isLow) {
                        type.fullRange.start..type.defaultRange.start
                    } else {
                        type.defaultRange.endInclusive..type.fullRange.endInclusive
                    }
                    FilterState(type = type, isEnabled = true, selectedRange = range)
                }
            }.distinctBy { it.type.name }.toImmutableList()

            // Validate preset application
            if (newFilterStates.size != FilterType.entries.size) {
                Log.e("FiltersViewModel", "Preset application resulted in duplicate filters: expected ${FilterType.entries.size}, got ${newFilterStates.size}")
            }
            
            // Validate preset ranges
            if (newFilterStates.any { it.selectedRange.start > it.selectedRange.endInclusive }) {
                Log.e("FiltersViewModel", "Invalid preset range: start > end")
            }
            
            state.copy(
                filterStates = newFilterStates,
                activeFiltersCount = newFilterStates.count { it.isEnabled }
            )
        }
        scheduleCombinationAnalysis()
    }

    fun applyGamePreset(numbers: Set<Int>) {
        if (numbers.size != LotofacilConstants.GAME_SIZE) return

        updateState { state ->
            if (state.filterStates.isEmpty()) return@updateState state

            val repeatedFromLastDraw = state.lastDraw?.let { lastDraw ->
                numbers.intersect(lastDraw).size
            }

            val defaultRepeatedTarget =
                ((FilterType.REPETIDAS_CONCURSO_ANTERIOR.defaultRange.start +
                    FilterType.REPETIDAS_CONCURSO_ANTERIOR.defaultRange.endInclusive) / 2f).roundToInt()

            val targets = mapOf(
                FilterType.SOMA_DEZENAS to numbers.sum(),
                FilterType.PARES to numbers.count { it % 2 == 0 },
                FilterType.PRIMOS to LotofacilConstants.countMatches(numbers, LotofacilConstants.PRIME_NUMBERS),
                FilterType.MOLDURA to LotofacilConstants.countMatches(numbers, LotofacilConstants.FRAME_NUMBERS),
                FilterType.RETRATO to LotofacilConstants.countMatches(numbers, LotofacilConstants.PORTRAIT_NUMBERS),
                FilterType.FIBONACCI to LotofacilConstants.countMatches(numbers, LotofacilConstants.FIBONACCI_NUMBERS),
                FilterType.MULTIPLOS_DE_3 to LotofacilConstants.countMatches(numbers, LotofacilConstants.MULTIPLES_OF_3),
                FilterType.REPETIDAS_CONCURSO_ANTERIOR to (repeatedFromLastDraw ?: defaultRepeatedTarget)
            )

            val newFilterStates = state.filterStates.map { filter ->
                val target = targets[filter.type] ?: return@map filter
                filter.copy(
                    isEnabled = true,
                    selectedRange = presetRangeForTarget(filter.type, target)
                )
            }.toImmutableList()

            state.copy(
                filterStates = newFilterStates,
                activeFiltersCount = newFilterStates.count { it.isEnabled }
            )
        }
        scheduleCombinationAnalysis()

        sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.generator_preset_applied))
    }

    fun generateGames(quantity: Int) {
        if (_uiState.value.generationState is GenerationUiState.Loading) return
        lastGenerationRequest = quantity
        if (_uiState.value.isCombinationImpossible) {
            showNonRecoverableErrorDialog(
                titleResId = R.string.error_invalid_filters_title,
                messageResId = R.string.error_invalid_filters_message
            )
            logHandledError(
                action = "generate_games_precheck",
                error = InvalidFiltersError(null),
                throwable = null
            )
            return
        }
        if (_uiState.value.isSyncingData) {
            sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.sync_in_progress_message))
            return
        }
        generationJob?.cancel()
        generationJob = viewModelScope.launch {
            _uiState.update { it.copy(generationState = GenerationUiState.Loading, errorDialog = null) }
            when (val result = generateGamesUseCase(quantity, _uiState.value.filterStates)) {
                is AppResult.Success -> {
                    if (!isActive) return@launch
                    gameRepository.addGeneratedGames(result.value)
                    _uiState.update { it.copy(generationState = GenerationUiState.Success(result.value.size)) }
                    _uiEvent.send(UiEvent.NavigateToGeneratedGames)
                }
                is AppResult.Failure -> {
                    if (!isActive) return@launch
                    handleGenerateError(result.error)
                }
            }
            generationJob = null
        }
    }

    fun cancelGeneration() {
        generationJob?.cancel()
        generationJob = null
        updateState { it.copy(generationState = GenerationUiState.Idle) }
    }

    fun retryLoadLastDraw() {
        loadLastDraw()
    }

    fun resetFilters() {
        updateState { state ->
            val newFilterStates = FilterType.entries
                .map { FilterState(type = it) }
                .distinctBy { it.type.name }
                .toImmutableList()
            
            if (newFilterStates.size != FilterType.entries.size) {
                Log.e("FiltersViewModel", "Reset filters resulted in duplicates: expected ${FilterType.entries.size}, got ${newFilterStates.size}")
            }
            
            state.copy(
                filterStates = newFilterStates,
                activeFiltersCount = 0
            )
        }
        scheduleCombinationAnalysis()
    }

    fun requestResetAllFilters() {
        viewModelScope.launch {
            _uiEvent.send(UiEvent.ShowResetConfirmation)
        }
    }

    fun confirmResetAllFilters() {
        resetFilters()
    }

    fun dismissErrorDialog() {
        updateState { it.copy(errorDialog = null) }
    }

    fun retryGenerateGames() {
        lastGenerationRequest?.let { generateGames(it) }
    }

    private fun handleGenerateError(error: Any) {
        val appError = error as? AppError
        val messageResId = when (appError) {
            is InvalidFiltersError -> {
                showNonRecoverableErrorDialog(
                    titleResId = R.string.error_invalid_filters_title,
                    messageResId = R.string.error_invalid_filters_message
                )
                R.string.error_invalid_operation
            }
            is EmptyHistoryError -> {
                showNonRecoverableErrorDialog(
                    titleResId = R.string.error_history_unavailable_title,
                    messageResId = R.string.error_history_unavailable_message
                )
                R.string.error_history_unavailable
            }
            is NetworkError -> {
                sendUiEvent(
                    UiEvent.ShowSnackbar(
                        messageResId = R.string.error_no_connection,
                        actionLabelResId = R.string.try_again,
                        action = UiEvent.SnackbarAction.RetryGenerateGames
                    )
                )
                R.string.error_no_connection
            }
            is PersistenceError -> {
                sendUiEvent(
                    UiEvent.ShowSnackbar(
                        messageResId = R.string.error_load_data_failed,
                        actionLabelResId = R.string.try_again,
                        action = UiEvent.SnackbarAction.RetryGenerateGames
                    )
                )
                R.string.error_load_data_failed
            }
            else -> {
                sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_generating_games))
                R.string.error_generating_games
            }
        }

        updateState { it.copy(generationState = GenerationUiState.Error(messageResId)) }
        logHandledError(
            action = "generate_games",
            error = appError ?: error,
            throwable = appError?.cause ?: (error as? Throwable)
        )
    }

    private fun presetRangeForTarget(type: FilterType, target: Int): ClosedFloatingPointRange<Float> {
        val radius = when (type) {
            FilterType.SOMA_DEZENAS -> 12
            FilterType.REPETIDAS_CONCURSO_ANTERIOR -> 2
            else -> 1
        }

        val minValue = type.fullRange.start.toInt()
        val maxValue = type.fullRange.endInclusive.toInt()
        val start = max(target - radius, minValue).toFloat()
        val end = min(target + radius, maxValue).toFloat()
        return if (start <= end) start..end else target.toFloat()..target.toFloat()
    }

    private fun snapRange(
        type: FilterType,
        range: ClosedFloatingPointRange<Float>
    ): ClosedFloatingPointRange<Float> {
        val snappedStart = range.start.roundToInt().toFloat()
        val snappedEnd = range.endInclusive.roundToInt().toFloat()
        return FilterState(type = type).withValidatedRange(snappedStart..snappedEnd).selectedRange
    }

    private fun expandSingleValueToRange(
        type: FilterType,
        value: Float
    ): ClosedFloatingPointRange<Float> {
        val min = type.fullRange.start
        val max = type.fullRange.endInclusive
        val start = (value - 1f).coerceIn(min, max)
        val end = (value + 1f).coerceIn(min, max)
        return if (start <= end) start..end else value..value
    }

    private fun scheduleCombinationAnalysis() {
        combinationAnalysisJob?.cancel()
        combinationAnalysisJob = viewModelScope.launch {
            updateState { it.copy(isAnalyzingCombinations = true) }
            delay(120)

            val snapshot = _uiState.value
            val estimate = withContext(dispatchersProvider.default) {
                combinationEstimator.estimate(
                    filterStates = snapshot.filterStates,
                    lastDraw = snapshot.lastDraw
                )
            }

            if (!isActive) return@launch

            updateState { current ->
                if (current.filterStates != snapshot.filterStates || current.lastDraw != snapshot.lastDraw) {
                    current
                } else {
                    current.copy(
                        possibleCombinationsCount = estimate.count,
                        isPossibleCombinationsEstimated = estimate.isEstimated,
                        isCombinationImpossible = estimate.count == 0L,
                        isVeryRestrictiveCombination = estimate.count in 1 until VERY_RESTRICTIVE_LIMIT,
                        isAnalyzingCombinations = false
                    )
                }
            }
        }
    }

    private fun showNonRecoverableErrorDialog(
        @StringRes titleResId: Int,
        @StringRes messageResId: Int
    ) {
        updateState {
            it.copy(
                errorDialog = FiltersErrorDialog(
                    titleResId = titleResId,
                    messageResId = messageResId,
                    confirmResId = R.string.adjust_filters_button,
                    dismissResId = R.string.close_button
                )
            )
        }
    }

    private fun logHandledError(
        action: String,
        error: Any,
        throwable: Throwable?
    ) {
        val context = "userId=anonymous action=$action activeFilters=${currentState.activeFiltersCount}"
        logger.e(TAG, "Handled error: $context error=$error", throwable, logInRelease = true)
    }

}

private data class CombinationEstimate(
    val count: Long,
    val isEstimated: Boolean
)

private data class CountRange(val min: Int, val max: Int)

private class CombinationEstimator {
    private val random = Random()
    private val numberPool = IntArray(25) { it + 1 }
    private val prefixSums = IntArray(numberPool.size + 1).apply {
        for (index in numberPool.indices) {
            this[index + 1] = this[index] + numberPool[index]
        }
    }
    private val sampleSize = 30_000

    fun estimate(
        filterStates: List<FilterState>,
        lastDraw: Set<Int>?
    ): CombinationEstimate {
        val enabledFilters = filterStates.filter { it.isEnabled }
        if (enabledFilters.isEmpty()) {
            return CombinationEstimate(
                count = LOTOFACIL_TOTAL_COMBINATIONS,
                isEstimated = false
            )
        }

        if (enabledFilters.any { it.type == FilterType.REPETIDAS_CONCURSO_ANTERIOR } && lastDraw == null) {
            return CombinationEstimate(count = 0L, isEstimated = false)
        }

        val exactUpToLimit = countValidCombinations(
            enabledFilters = enabledFilters,
            lastDraw = lastDraw.orEmpty(),
            maxCount = LOW_COMBINATION_LIMIT + 1
        )
        if (exactUpToLimit <= LOW_COMBINATION_LIMIT) {
            return CombinationEstimate(count = exactUpToLimit, isEstimated = false)
        }

        val estimated = estimateBySampling(
            enabledFilters = enabledFilters,
            lastDraw = lastDraw.orEmpty()
        ).coerceAtLeast(LOW_COMBINATION_LIMIT + 1)

        return CombinationEstimate(count = estimated, isEstimated = true)
    }

    private fun countValidCombinations(
        enabledFilters: List<FilterState>,
        lastDraw: Set<Int>,
        maxCount: Long
    ): Long {
        val rangesByType = enabledFilters.associate { state ->
            state.type to CountRange(
                min = state.selectedRange.start.roundToInt(),
                max = state.selectedRange.endInclusive.roundToInt()
            )
        }

        val sumRange = rangesByType[FilterType.SOMA_DEZENAS]
        val evenRange = rangesByType[FilterType.PARES]
        val primeRange = rangesByType[FilterType.PRIMOS]
        val frameRange = rangesByType[FilterType.MOLDURA]
        val portraitRange = rangesByType[FilterType.RETRATO]
        val fibonacciRange = rangesByType[FilterType.FIBONACCI]
        val multiplesOf3Range = rangesByType[FilterType.MULTIPLOS_DE_3]
        val repeatedRange = rangesByType[FilterType.REPETIDAS_CONCURSO_ANTERIOR]

        val evenSuffix = buildSuffixCount { it % 2 == 0 }
        val primeSuffix = buildSuffixCount { it in LotofacilConstants.PRIME_NUMBERS }
        val frameSuffix = buildSuffixCount { it in LotofacilConstants.FRAME_NUMBERS }
        val portraitSuffix = buildSuffixCount { it in LotofacilConstants.PORTRAIT_NUMBERS }
        val fibonacciSuffix = buildSuffixCount { it in LotofacilConstants.FIBONACCI_NUMBERS }
        val multiplesOf3Suffix = buildSuffixCount { it in LotofacilConstants.MULTIPLES_OF_3 }
        val repeatedSuffix = buildSuffixCount { it in lastDraw }

        fun isRangeFeasible(
            range: CountRange?,
            currentCount: Int,
            startIndex: Int,
            remainingSlots: Int,
            suffixMatches: IntArray
        ): Boolean {
            if (range == null) return true

            val remainingNumbers = numberPool.size - startIndex
            if (remainingSlots > remainingNumbers) return false

            val remainingMatches = suffixMatches[startIndex]
            val remainingNonMatches = remainingNumbers - remainingMatches
            val minAdditional = max(0, remainingSlots - remainingNonMatches)
            val maxAdditional = min(remainingSlots, remainingMatches)
            val minFinal = currentCount + minAdditional
            val maxFinal = currentCount + maxAdditional
            return maxFinal >= range.min && minFinal <= range.max
        }

        fun minPossibleSum(startIndex: Int, picks: Int): Int {
            if (picks == 0) return 0
            val endExclusive = startIndex + picks
            return prefixSums[endExclusive] - prefixSums[startIndex]
        }

        fun maxPossibleSum(picks: Int): Int {
            if (picks == 0) return 0
            val maxStart = numberPool.size - picks
            return prefixSums[numberPool.size] - prefixSums[maxStart]
        }

        fun rangeContains(range: CountRange?, value: Int): Boolean {
            return range == null || value in range.min..range.max
        }

        fun dfs(
            startIndex: Int,
            selectedCount: Int,
            sum: Int,
            evens: Int,
            primes: Int,
            frame: Int,
            portrait: Int,
            fibonacci: Int,
            multiplesOf3: Int,
            repeated: Int
        ): Long {
            val remainingSlots = LotofacilConstants.GAME_SIZE - selectedCount
            if (remainingSlots < 0) return 0
            if (remainingSlots > numberPool.size - startIndex) return 0

            if (sumRange != null) {
                val minSum = sum + minPossibleSum(startIndex, remainingSlots)
                val maxSum = sum + maxPossibleSum(remainingSlots)
                if (maxSum < sumRange.min || minSum > sumRange.max) return 0
            }

            if (!isRangeFeasible(evenRange, evens, startIndex, remainingSlots, evenSuffix)) return 0
            if (!isRangeFeasible(primeRange, primes, startIndex, remainingSlots, primeSuffix)) return 0
            if (!isRangeFeasible(frameRange, frame, startIndex, remainingSlots, frameSuffix)) return 0
            if (!isRangeFeasible(portraitRange, portrait, startIndex, remainingSlots, portraitSuffix)) return 0
            if (!isRangeFeasible(fibonacciRange, fibonacci, startIndex, remainingSlots, fibonacciSuffix)) return 0
            if (!isRangeFeasible(multiplesOf3Range, multiplesOf3, startIndex, remainingSlots, multiplesOf3Suffix)) return 0
            if (!isRangeFeasible(repeatedRange, repeated, startIndex, remainingSlots, repeatedSuffix)) return 0

            if (selectedCount == LotofacilConstants.GAME_SIZE) {
                val valid = rangeContains(sumRange, sum) &&
                    rangeContains(evenRange, evens) &&
                    rangeContains(primeRange, primes) &&
                    rangeContains(frameRange, frame) &&
                    rangeContains(portraitRange, portrait) &&
                    rangeContains(fibonacciRange, fibonacci) &&
                    rangeContains(multiplesOf3Range, multiplesOf3) &&
                    rangeContains(repeatedRange, repeated)
                return if (valid) 1 else 0
            }

            var total = 0L
            val lastCandidate = numberPool.size - remainingSlots
            for (index in startIndex..lastCandidate) {
                val number = numberPool[index]
                total += dfs(
                    startIndex = index + 1,
                    selectedCount = selectedCount + 1,
                    sum = sum + number,
                    evens = evens + if (number % 2 == 0) 1 else 0,
                    primes = primes + if (number in LotofacilConstants.PRIME_NUMBERS) 1 else 0,
                    frame = frame + if (number in LotofacilConstants.FRAME_NUMBERS) 1 else 0,
                    portrait = portrait + if (number in LotofacilConstants.PORTRAIT_NUMBERS) 1 else 0,
                    fibonacci = fibonacci + if (number in LotofacilConstants.FIBONACCI_NUMBERS) 1 else 0,
                    multiplesOf3 = multiplesOf3 + if (number in LotofacilConstants.MULTIPLES_OF_3) 1 else 0,
                    repeated = repeated + if (number in lastDraw) 1 else 0
                )
                if (total >= maxCount) return maxCount
            }
            return total
        }

        return dfs(
            startIndex = 0,
            selectedCount = 0,
            sum = 0,
            evens = 0,
            primes = 0,
            frame = 0,
            portrait = 0,
            fibonacci = 0,
            multiplesOf3 = 0,
            repeated = 0
        )
    }

    private fun estimateBySampling(
        enabledFilters: List<FilterState>,
        lastDraw: Set<Int>
    ): Long {
        var validCount = 0
        repeat(sampleSize) {
            val sample = randomCombination()
            if (isSampleValid(sample, enabledFilters, lastDraw)) validCount++
        }
        if (validCount == 0) return LOW_COMBINATION_LIMIT + 1

        val ratio = validCount.toDouble() / sampleSize.toDouble()
        return (ratio * LOTOFACIL_TOTAL_COMBINATIONS).roundToInt().toLong()
    }

    private fun randomCombination(): IntArray {
        val numbers = numberPool.copyOf()
        for (index in 0 until LotofacilConstants.GAME_SIZE) {
            val swapIndex = index + random.nextInt(numbers.size - index)
            val current = numbers[index]
            numbers[index] = numbers[swapIndex]
            numbers[swapIndex] = current
        }
        return numbers.copyOfRange(0, LotofacilConstants.GAME_SIZE)
    }

    private fun isSampleValid(
        numbers: IntArray,
        enabledFilters: List<FilterState>,
        lastDraw: Set<Int>
    ): Boolean {
        val numbersSet = numbers.toSet()
        val sum = numbers.sum()
        val evens = numbers.count { it % 2 == 0 }
        val primes = numbers.count { it in LotofacilConstants.PRIME_NUMBERS }
        val frame = numbers.count { it in LotofacilConstants.FRAME_NUMBERS }
        val portrait = numbers.count { it in LotofacilConstants.PORTRAIT_NUMBERS }
        val fibonacci = numbers.count { it in LotofacilConstants.FIBONACCI_NUMBERS }
        val multiplesOf3 = numbers.count { it in LotofacilConstants.MULTIPLES_OF_3 }
        val repeated = numbersSet.intersect(lastDraw).size

        return enabledFilters.all { filter ->
            val value = when (filter.type) {
                FilterType.SOMA_DEZENAS -> sum
                FilterType.PARES -> evens
                FilterType.PRIMOS -> primes
                FilterType.MOLDURA -> frame
                FilterType.RETRATO -> portrait
                FilterType.FIBONACCI -> fibonacci
                FilterType.MULTIPLOS_DE_3 -> multiplesOf3
                FilterType.REPETIDAS_CONCURSO_ANTERIOR -> repeated
            }
            value.toFloat() in filter.selectedRange
        }
    }

    private fun buildSuffixCount(match: (Int) -> Boolean): IntArray {
        val suffix = IntArray(numberPool.size + 1)
        for (index in numberPool.lastIndex downTo 0) {
            suffix[index] = suffix[index + 1] + if (match(numberPool[index])) 1 else 0
        }
        return suffix
    }
}
