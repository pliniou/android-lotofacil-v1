package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.error.EmptyHistoryError
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.FrequencyAnalysis
import com.cebolao.lotofacil.domain.model.PatternAnalysis
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.domain.model.TrendAnalysis
import com.cebolao.lotofacil.domain.model.TrendType
import com.cebolao.lotofacil.domain.usecase.GetStatisticsDataUseCase
import com.cebolao.lotofacil.domain.usecase.StatisticsDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

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

    // Data origin
    val statisticsSource: DataLoadSource = DataLoadSource.CACHE,
    val isShowingStaleData: Boolean = false,

    // Error states
    val isHistoryEmpty: Boolean = false,
    val errorMessageResId: Int? = null,
    val patternErrorResId: Int? = null,
    val trendErrorResId: Int? = null,

    // Total draws
    val totalDrawsAvailable: Int = 0
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getStatisticsDataUseCase: GetStatisticsDataUseCase,
    private val dispatchersProvider: DispatchersProvider
) : StateViewModel<StatisticsUiState>(StatisticsUiState()) {

    init {
        loadAllStatistics()
    }

    private fun loadAllStatistics(forceRefresh: Boolean = false) {
        viewModelScope.launch(dispatchersProvider.io) {
            updateState {
                it.copy(
                    isLoading = true,
                    isHistoryEmpty = false,
                    errorMessageResId = null
                )
            }

            when (
                val result = getStatisticsDataUseCase.loadScreenData(
                    timeWindow = currentState.selectedTimeWindow,
                    patternSize = currentState.selectedPatternSize,
                    trendType = currentState.selectedTrendType,
                    trendWindow = currentState.selectedTrendWindow,
                    forceRefresh = forceRefresh
                )
            ) {
                is AppResult.Success -> {
                    val source = when (result.value.reportSnapshot.source) {
                        StatisticsDataSource.CACHE -> DataLoadSource.CACHE
                        StatisticsDataSource.COMPUTED -> DataLoadSource.COMPUTED
                    }
                    updateState {
                        it.copy(
                            isLoading = false,
                            report = result.value.reportSnapshot.report,
                            frequencyAnalysis = result.value.frequencyAnalysis,
                            patternAnalysis = result.value.patternAnalysis,
                            trendAnalysis = result.value.trendAnalysis,
                            totalDrawsAvailable = result.value.reportSnapshot.totalHistorySize,
                            statisticsSource = source,
                            isShowingStaleData = result.value.reportSnapshot.isStale,
                            isHistoryEmpty = false,
                            errorMessageResId = null,
                            patternErrorResId = null,
                            trendErrorResId = null
                        )
                    }
                }

                is AppResult.Failure -> {
                    val isEmptyHistory = result.error is EmptyHistoryError
                    updateState {
                        it.copy(
                            isLoading = false,
                            isHistoryEmpty = isEmptyHistory,
                            errorMessageResId = if (isEmptyHistory) null else R.string.error_load_data_failed
                        )
                    }
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
        loadAllStatistics(forceRefresh = true)
    }

    private fun reloadPatterns(size: Int) {
        viewModelScope.launch(dispatchersProvider.io) {
            updateState { it.copy(isPatternLoading = true, patternErrorResId = null) }
            when (
                val result = getStatisticsDataUseCase.loadPatternAnalysis(
                    timeWindow = currentState.selectedTimeWindow,
                    patternSize = size
                )
            ) {
                is AppResult.Success -> {
                    updateState {
                        it.copy(
                            isPatternLoading = false,
                            patternAnalysis = result.value
                        )
                    }
                }

                is AppResult.Failure -> {
                    updateState {
                        it.copy(
                            isPatternLoading = false,
                            patternErrorResId = R.string.error_load_data_failed
                        )
                    }
                }
            }
        }
    }

    private fun reloadTrend(type: TrendType, windowSize: Int) {
        viewModelScope.launch(dispatchersProvider.io) {
            updateState { it.copy(isTrendLoading = true, trendErrorResId = null) }
            when (
                val result = getStatisticsDataUseCase.loadTrendAnalysis(
                    timeWindow = currentState.selectedTimeWindow,
                    trendType = type,
                    trendWindow = windowSize
                )
            ) {
                is AppResult.Success -> {
                    updateState {
                        it.copy(
                            isTrendLoading = false,
                            trendAnalysis = result.value
                        )
                    }
                }

                is AppResult.Failure -> {
                    updateState {
                        it.copy(
                            isTrendLoading = false,
                            trendErrorResId = R.string.error_load_data_failed
                        )
                    }
                }
            }
        }
    }
}
