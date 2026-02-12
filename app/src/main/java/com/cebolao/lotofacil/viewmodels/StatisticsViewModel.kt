package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.domain.model.FrequencyAnalysis
import com.cebolao.lotofacil.domain.model.PatternAnalysis
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.domain.model.TrendAnalysis
import com.cebolao.lotofacil.domain.model.TrendType
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.service.StatisticsEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
data class StatisticsUiState(
    val isLoading: Boolean = true,
    val report: StatisticsReport? = null,
    val frequencyAnalysis: FrequencyAnalysis? = null,
    val patternAnalysis: PatternAnalysis? = null,
    val trendAnalysis: TrendAnalysis? = null,

    // Section loading states
    val isPatternLoading: Boolean = false,
    val isTrendLoading: Boolean = false,

    // Filters
    val selectedTimeWindow: Int = 0,
    val selectedPatternSize: Int = 2,
    val selectedTrendType: TrendType = TrendType.SUM,
    val selectedTrendWindow: Int = 50,

    // Available time windows
    val timeWindows: List<Int> = listOf(0, 10, 20, 50, 100, 200, 500, 1500, 2000),

    // Error states
    val errorMessageResId: Int? = null,
    val patternErrorResId: Int? = null,
    val trendErrorResId: Int? = null,

    // Total draws
    val totalDrawsAvailable: Int = 0
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statisticsEngine: StatisticsEngine,
    private val historyRepository: HistoryRepository,
    private val dispatchersProvider: DispatchersProvider
) : StateViewModel<StatisticsUiState>(StatisticsUiState()) {

    init {
        loadAllStatistics()
    }

    private fun loadAllStatistics() {
        viewModelScope.launch(dispatchersProvider.io) {
            updateState { it.copy(isLoading = true, errorMessageResId = null) }

            try {
                val history = historyRepository.getHistory().first()
                if (history.isEmpty()) {
                    updateState {
                        it.copy(
                            isLoading = false,
                            errorMessageResId = R.string.error_load_data_failed
                        )
                    }
                    return@launch
                }

                val window = currentState.selectedTimeWindow
                val draws = if (window > 0) history.take(window) else history

                // Full report
                val report = statisticsEngine.analyze(draws)

                // Frequency analysis
                val frequencies = statisticsEngine.getNumberFrequencies(draws)
                val topNumbers = statisticsEngine.getTopNumbers(frequencies)
                val overdueNumbers = statisticsEngine.getOverdueNumbers(draws)
                val frequencyAnalysis = FrequencyAnalysis(
                    frequencies = frequencies,
                    topNumbers = topNumbers,
                    overdueNumbers = overdueNumbers,
                    totalDraws = draws.size
                )

                // Pattern analysis
                val patterns = statisticsEngine.getCommonPatterns(
                    draws,
                    currentState.selectedPatternSize
                )
                val patternAnalysis = PatternAnalysis(
                    size = currentState.selectedPatternSize,
                    patterns = patterns,
                    totalDraws = draws.size
                )

                // Trend analysis
                val trendType = currentState.selectedTrendType
                val trendWindow = currentState.selectedTrendWindow
                val timeline = computeTimeline(draws, trendType, trendWindow)
                val averageValue = if (timeline.isNotEmpty()) {
                    timeline.map { it.second }.average().toFloat()
                } else 0f
                val trendAnalysis = TrendAnalysis(
                    type = trendType,
                    timeline = timeline,
                    averageValue = averageValue
                )

                updateState {
                    it.copy(
                        isLoading = false,
                        report = report,
                        frequencyAnalysis = frequencyAnalysis,
                        patternAnalysis = patternAnalysis,
                        trendAnalysis = trendAnalysis,
                        totalDrawsAvailable = history.size,
                        errorMessageResId = null
                    )
                }
            } catch (_: Exception) {
                updateState {
                    it.copy(
                        isLoading = false,
                        errorMessageResId = R.string.error_load_data_failed
                    )
                }
            }
        }
    }

    fun onTimeWindowSelected(window: Int) {
        if (currentState.selectedTimeWindow == window) return
        updateState { it.copy(selectedTimeWindow = window) }
        loadAllStatistics()
    }

    fun onPatternSizeSelected(size: Int) {
        if (currentState.selectedPatternSize == size) return
        updateState { it.copy(selectedPatternSize = size) }
        reloadPatterns(size)
    }

    fun onTrendTypeSelected(type: TrendType) {
        if (currentState.selectedTrendType == type) return
        updateState { it.copy(selectedTrendType = type) }
        reloadTrend(type, currentState.selectedTrendWindow)
    }

    fun onTrendWindowSelected(window: Int) {
        if (currentState.selectedTrendWindow == window) return
        updateState { it.copy(selectedTrendWindow = window) }
        reloadTrend(currentState.selectedTrendType, window)
    }

    fun refresh() {
        loadAllStatistics()
    }

    private fun reloadPatterns(size: Int) {
        viewModelScope.launch(dispatchersProvider.io) {
            updateState { it.copy(isPatternLoading = true, patternErrorResId = null) }
            try {
                val history = historyRepository.getHistory().first()
                val window = currentState.selectedTimeWindow
                val draws = if (window > 0) history.take(window) else history
                val patterns = statisticsEngine.getCommonPatterns(draws, size)
                updateState {
                    it.copy(
                        isPatternLoading = false,
                        patternAnalysis = PatternAnalysis(
                            size = size,
                            patterns = patterns,
                            totalDraws = draws.size
                        )
                    )
                }
            } catch (_: Exception) {
                updateState {
                    it.copy(
                        isPatternLoading = false,
                        patternErrorResId = R.string.error_load_data_failed
                    )
                }
            }
        }
    }

    private fun reloadTrend(type: TrendType, windowSize: Int) {
        viewModelScope.launch(dispatchersProvider.io) {
            updateState { it.copy(isTrendLoading = true, trendErrorResId = null) }
            try {
                val history = historyRepository.getHistory().first()
                val window = currentState.selectedTimeWindow
                val draws = if (window > 0) history.take(window) else history
                val timeline = computeTimeline(draws, type, windowSize)
                val averageValue = if (timeline.isNotEmpty()) {
                    timeline.map { it.second }.average().toFloat()
                } else 0f

                updateState {
                    it.copy(
                        isTrendLoading = false,
                        trendAnalysis = TrendAnalysis(
                            type = type,
                            timeline = timeline,
                            averageValue = averageValue
                        )
                    )
                }
            } catch (_: Exception) {
                updateState {
                    it.copy(
                        isTrendLoading = false,
                        trendErrorResId = R.string.error_load_data_failed
                    )
                }
            }
        }
    }

    private fun computeTimeline(
        draws: List<com.cebolao.lotofacil.domain.model.HistoricalDraw>,
        type: TrendType,
        windowSize: Int
    ): List<Pair<Int, Float>> = when (type) {
        TrendType.SUM -> statisticsEngine.getAverageSumTimeline(draws, windowSize)
        TrendType.EVENS -> statisticsEngine.getDistributionTimeline(draws, windowSize) { it.evens }
        TrendType.PRIMES -> statisticsEngine.getDistributionTimeline(draws, windowSize) { it.primes }
        TrendType.FRAME -> statisticsEngine.getDistributionTimeline(draws, windowSize) { it.frame }
        TrendType.PORTRAIT -> statisticsEngine.getDistributionTimeline(draws, windowSize) { it.portrait }
        TrendType.FIBONACCI -> statisticsEngine.getDistributionTimeline(draws, windowSize) { it.fibonacci }
        TrendType.MULTIPLES_OF_3 -> statisticsEngine.getDistributionTimeline(draws, windowSize) { it.multiplesOf3 }
    }
}
