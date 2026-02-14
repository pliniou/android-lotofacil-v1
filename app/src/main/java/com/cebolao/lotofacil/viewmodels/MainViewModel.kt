package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.domain.model.ThemeMode
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.UserPreferencesRepository
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
    val errorMessageResId: Int? = null,
    val isInitializationComplete: Boolean = false,
    val isStartupSyncComplete: Boolean = false,
    val startupSyncErrorMessageResId: Int? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val needsBiometricAuth: Boolean = false,
    val needsPermissionRequest: Boolean = false
) {
    val isReady: Boolean get() = isInitializationComplete && !isLoading && !hasError
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val logger: AppLogger
) : StateViewModel<MainUiState>(MainUiState()) {

    companion object {
        private const val TAG = "MainViewModel"
    }

    init {
        observeThemeMode()
        initializeApp()
    }

    fun retryInitialization() {
        initializeApp()
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            userPreferencesRepository.saveThemeMode(mode)
        }
    }

    private fun initializeApp() {
        viewModelScope.launch {
            updateState {
                it.copy(
                    isLoading = true,
                    hasError = false,
                    errorMessageResId = null,
                    isInitializationComplete = false,
                    isStartupSyncComplete = false,
                    startupSyncErrorMessageResId = null
                )
            }

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
                    updateState {
                        it.copy(
                            isLoading = false,
                            hasError = false,
                            errorMessageResId = null,
                            isInitializationComplete = true
                        )
                    }
                    triggerStartupSync()
                }
                is AppResult.Failure -> {
                    updateState {
                        it.copy(
                            isLoading = false,
                            hasError = true,
                            errorMessageResId = R.string.error_load_data_failed,
                            isInitializationComplete = false
                        )
                    }
                }
            }
        }
    }

    private fun observeThemeMode() {
        viewModelScope.launch {
            userPreferencesRepository.themeMode.collect { mode ->
                updateState { it.copy(themeMode = mode) }
            }
        }
    }

    private suspend fun initializeHistory(): AppResult<Unit> {
        return try {
            historyRepository.isInitialized.first { it }
            AppResult.Success(Unit)
        } catch (throwable: Throwable) {
            logger.e(TAG, "History initialization failed", throwable)
            AppResult.Failure(com.cebolao.lotofacil.core.error.ErrorMapper.toAppError(throwable))
        }
    }

    private fun triggerStartupSync() {
        viewModelScope.launch {
            when (val syncResult = historyRepository.syncHistory()) {
                is AppResult.Success -> {
                    logger.d(TAG, "History startup sync completed successfully")
                    updateState { it.copy(isStartupSyncComplete = true, startupSyncErrorMessageResId = null) }
                }

                is AppResult.Failure -> {
                    val error = syncResult.error
                    logger.e(TAG, "History startup sync failed", (error as? Throwable) ?: Exception(error.toString()))
                    updateState {
                        it.copy(
                            isStartupSyncComplete = true,
                            startupSyncErrorMessageResId = R.string.error_sync_failed
                        )
                    }
                }
            }
        }
    }
}
