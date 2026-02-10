package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.ui.theme.DefaultAppMotion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
data class MainUiState(
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val errorMessage: String? = null
) {
    val isReady: Boolean get() = !isLoading && !hasError
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : StateViewModel<MainUiState>(MainUiState()) {

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            // Wait for both minimum splash duration and data initialization
            val startTime = System.currentTimeMillis()

            // Wait for DB to be ready once, then stop collecting to avoid extra work.
            historyRepository.isInitialized.first { it }

            val elapsedTime = System.currentTimeMillis() - startTime
            val remainingTime = DefaultAppMotion.splashMinDurationMs - elapsedTime
            if (remainingTime > 0) {
                delay(remainingTime)
            }
            updateState { it.copy(isLoading = false) }
        }
    }
}
