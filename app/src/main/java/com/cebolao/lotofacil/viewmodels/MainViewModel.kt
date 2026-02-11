package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Stable
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.result.Result
import com.cebolao.lotofacil.core.result.ErrorMessageMapper
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.ui.theme.DefaultAppMotion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    errorMessageMapper: ErrorMessageMapper
) : EnhancedStateViewModel<MainUiState>(
    initialState = MainUiState(),
    errorMessageMapper = errorMessageMapper
) {

    init {
        initializeApp()
    }

    private fun initializeApp() {
        executeWithResult(
            loadingState = { it.copy(isLoading = true, hasError = false, errorMessage = null) },
            successState = { it, _ -> it.copy(isLoading = false, hasError = false, errorMessage = null) },
            errorState = { it, errorMessage -> it.copy(isLoading = false, hasError = true, errorMessage = errorMessage) }
        ) {
            // Wait for both minimum splash duration and data initialization
            val startTime = System.currentTimeMillis()

            // Initialize history with Result pattern
            val initResult = initializeHistory()
            
            // Wait for minimum splash duration
            val elapsedTime = System.currentTimeMillis() - startTime
            val remainingTime = DefaultAppMotion.splashMinDurationMs - elapsedTime
            if (remainingTime > 0) {
                delay(remainingTime)
            }
            
            initResult
        }
    }

    private suspend fun initializeHistory(): Result<Unit> {
        return when (val syncResult = historyRepository.syncHistory()) {
            is AppResult.Success -> Result.Success(Unit)
            is AppResult.Failure -> {
                val error = syncResult.error
                val throwable = (error as? Throwable) ?: Exception(error.toString())
                Result.Error(throwable, "Failed to initialize history")
            }
        }
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
