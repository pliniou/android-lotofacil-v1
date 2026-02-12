package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.ui.theme.DefaultAppMotion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
data class MainUiState(
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val errorMessageResId: Int? = null,
    val needsBiometricAuth: Boolean = false,
    val needsPermissionRequest: Boolean = false
) {
    val isReady: Boolean get() = !isLoading && !hasError
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val logger: AppLogger
) : StateViewModel<MainUiState>(MainUiState()) {

    companion object {
        private const val TAG = "MainViewModel"
    }

    init {
        initializeApp()
    }

    fun retryInitialization() {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, hasError = false, errorMessageResId = null) }

            // Start initialization in background without blocking splash
            val startTime = System.currentTimeMillis()
            val initResult = initializeHistory()

            // Ensure minimum splash duration for better UX
            val elapsedTime = System.currentTimeMillis() - startTime
            val remainingTime = DefaultAppMotion.splashMinDurationMs - elapsedTime
            if (remainingTime > 0) {
                delay(remainingTime)
            }

            when (initResult) {
                is AppResult.Success -> {
                    updateState { it.copy(isLoading = false, hasError = false, errorMessageResId = null) }
                }
                is AppResult.Failure -> {
                    updateState {
                        it.copy(
                            isLoading = false,
                            hasError = true,
                            errorMessageResId = R.string.error_load_data_failed
                        )
                    }
                }
            }
        }
    }

    private fun initializeHistory(): AppResult<Unit> {
        // Launch sync in background without blocking initialization
        viewModelScope.launch {
            when (val syncResult = historyRepository.syncHistory()) {
                is AppResult.Success -> {
                    logger.d(TAG, "History sync completed successfully")
                }
                is AppResult.Failure -> {
                    val error = syncResult.error
                    logger.e(TAG, "History sync failed", (error as? Throwable) ?: Exception(error.toString()))
                }
            }
        }
        // Return success immediately to allow app initialization
        return AppResult.Success(Unit)
    }
}
