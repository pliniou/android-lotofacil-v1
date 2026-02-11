package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.result.Result
import com.cebolao.lotofacil.core.result.ErrorMessageMapper
import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.ui.theme.DefaultAppMotion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@Stable
data class MainUiState(
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val initializationResult: Result<Unit>? = null,
    val needsBiometricAuth: Boolean = false,
    val needsPermissionRequest: Boolean = false
) {
    val isReady: Boolean get() = !isLoading && !hasError && initializationResult?.isSuccess == true
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val logger: AppLogger,
    errorMessageMapper: ErrorMessageMapper
) : EnhancedStateViewModel<MainUiState>(
    initialState = MainUiState(),
    errorMessageMapper = errorMessageMapper
) {

    companion object {
        private const val TAG = "MainViewModel"
    }

    init {
        initializeApp()
    }

    private fun initializeApp() {
        executeWithResult(
            loadingState = { it.copy(isLoading = true, hasError = false, errorMessage = null) },
            successState = { it, data -> 
                it.copy(isLoading = false, hasError = false, errorMessage = null, initializationResult = Result.Success(data))
            },
            errorState = { it, errorMessage -> 
                it.copy(isLoading = false, hasError = true, errorMessage = errorMessage)
            }
        ) {
            // Start initialization in background without blocking splash
            val startTime = System.currentTimeMillis()
            
            // Initialize history asynchronously without timeout
            val initResult = initializeHistory()
            
            // Ensure minimum splash duration for better UX
            val elapsedTime = System.currentTimeMillis() - startTime
            val remainingTime = DefaultAppMotion.splashMinDurationMs - elapsedTime
            if (remainingTime > 0) {
                delay(remainingTime)
            }
            
            initResult
        }
    }

    private fun initializeHistory(): Result<Unit> {
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
        return Result.Success(Unit)
    }

    fun retryInitialization() {
        initializeApp()
    }

    fun onBiometricAuthSuccess() {
        updateState { it.copy(needsBiometricAuth = false) }
    }

    fun onBiometricAuthFailed() {
        // Handle auth failure - could show error or retry
    }

    fun onBiometricAuthSkipped() {
        updateState { it.copy(needsBiometricAuth = false) }
    }

    fun onPermissionGranted() {
        updateState { it.copy(needsPermissionRequest = false) }
    }

    fun onPermissionDenied() {
        updateState { it.copy(needsPermissionRequest = false) }
    }
}
