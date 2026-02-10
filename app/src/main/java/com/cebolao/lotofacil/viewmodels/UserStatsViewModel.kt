package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.service.UserStats
import com.cebolao.lotofacil.domain.usecase.GetUserGameStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class UserStatsUiState(
    val isLoading: Boolean = false,
    val stats: UserStats? = null,
    val errorMessageResId: Int? = null
)

@HiltViewModel
class UserStatsViewModel @Inject constructor(
    private val getUserGameStatisticsUseCase: GetUserGameStatisticsUseCase,
    private val gameRepository: GameRepository
) : StateViewModel<UserStatsUiState>(UserStatsUiState()) {

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, errorMessageResId = null) }
            when (val result = getUserGameStatisticsUseCase()) {
                is AppResult.Success -> {
                    updateState { it.copy(isLoading = false, stats = result.value) }
                }
                is AppResult.Failure -> {
                    updateState { it.copy(isLoading = false, errorMessageResId = R.string.error_load_data_failed) }
                }
            }
        }
    }

    fun recordGameUsage(gameId: String) {
        viewModelScope.launch {
            gameRepository.recordGameUsage(gameId)
            loadStats() // Refresh stats after recording usage
        }
    }
}
