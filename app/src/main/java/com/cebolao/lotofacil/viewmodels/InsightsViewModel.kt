package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.usecase.FrequencyAnalysis
import com.cebolao.lotofacil.domain.usecase.GetFrequencyAnalysisUseCase
import com.cebolao.lotofacil.domain.usecase.GetPatternAnalysisUseCase
import com.cebolao.lotofacil.domain.usecase.GetTrendAnalysisUseCase
import com.cebolao.lotofacil.domain.usecase.PatternAnalysis
import com.cebolao.lotofacil.domain.usecase.TrendAnalysis
import com.cebolao.lotofacil.domain.usecase.TrendType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
data class InsightsUiState(
    val isLoading: Boolean = false,
    val frequencyAnalysis: FrequencyAnalysis? = null,
    val patternAnalysis: PatternAnalysis? = null,
    val trendAnalysis: TrendAnalysis? = null,
    val isPatternLoading: Boolean = false,
    val isTrendLoading: Boolean = false,
    val selectedPatternSize: Int = 2,
    val selectedTrendType: TrendType = TrendType.SUM,
    val selectedTrendWindow: Int = 50,
    val errorMessageResId: Int? = null,
    val patternErrorMessageResId: Int? = null,
    val trendErrorMessageResId: Int? = null
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val getFrequencyAnalysisUseCase: GetFrequencyAnalysisUseCase,
    private val getPatternAnalysisUseCase: GetPatternAnalysisUseCase,
    private val getTrendAnalysisUseCase: GetTrendAnalysisUseCase
) : StateViewModel<InsightsUiState>(InsightsUiState()) {

    init {
        loadFrequencyAnalysis()
        loadPatternAnalysis(currentState.selectedPatternSize)
        loadTrendAnalysis(currentState.selectedTrendType, currentState.selectedTrendWindow)
    }

    fun loadFrequencyAnalysis() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, errorMessageResId = null) }
            when (val result = getFrequencyAnalysisUseCase()) {
                is AppResult.Success -> {
                    updateState { it.copy(isLoading = false, frequencyAnalysis = result.value) }
                }
                is AppResult.Failure -> {
                    updateState { it.copy(isLoading = false, errorMessageResId = R.string.error_load_data_failed) }
                }
            }
        }
    }

    private fun loadPatternAnalysis(size: Int) {
        viewModelScope.launch {
            updateState { it.copy(isPatternLoading = true, patternErrorMessageResId = null) }
            when (val result = getPatternAnalysisUseCase(size)) {
                is AppResult.Success -> {
                    updateState { it.copy(patternAnalysis = result.value, isPatternLoading = false) }
                }
                is AppResult.Failure -> {
                    updateState {
                        it.copy(
                            isPatternLoading = false,
                            patternErrorMessageResId = R.string.error_load_data_failed
                        )
                    }
                }
            }
        }
    }

    private fun loadTrendAnalysis(type: TrendType, windowSize: Int) {
        viewModelScope.launch {
            updateState { it.copy(isTrendLoading = true, trendErrorMessageResId = null) }
            when (val result = getTrendAnalysisUseCase(type, windowSize)) {
                is AppResult.Success -> {
                    updateState { it.copy(trendAnalysis = result.value, isTrendLoading = false) }
                }
                is AppResult.Failure -> {
                    updateState {
                        it.copy(
                            isTrendLoading = false,
                            trendErrorMessageResId = R.string.error_load_data_failed
                        )
                    }
                }
            }
        }
    }
}
