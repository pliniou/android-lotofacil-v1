package com.cebolao.lotofacil.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.core.result.Result
import com.cebolao.lotofacil.core.result.mapErrorMessages
import com.cebolao.lotofacil.core.result.ErrorMessageMapper
import com.cebolao.lotofacil.navigation.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Enhanced Base ViewModel with Result pattern support and improved error handling.
 */
abstract class EnhancedBaseViewModel : ViewModel() {
    protected val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    protected val jobTracker = JobTracker()

    protected fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    override fun onCleared() {
        super.onCleared()
        jobTracker.cancelAll()
    }
}

/**
 * Enhanced StateViewModel with Result pattern support and automatic error mapping.
 */
abstract class EnhancedStateViewModel<S>(
    initialState: S,
    protected val errorMessageMapper: ErrorMessageMapper? = null
) : EnhancedBaseViewModel() {

    protected val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    protected val currentState: S get() = _uiState.value

    protected fun updateState(update: (S) -> S) {
        _uiState.update(update)
    }

    /**
     * Executes a suspend function and updates state with Result pattern.
     */
    protected fun <T> executeWithResult(
        loadingState: (S) -> S,
        successState: (S, T) -> S,
        errorState: (S, String) -> S,
        block: suspend () -> Result<T>
    ) {
        viewModelScope.launch {
            updateState { loadingState(it) }
            
            val result = block()
                .let { result ->
                    if (errorMessageMapper != null) {
                        result.mapErrorMessages(errorMessageMapper)
                    } else {
                        result
                    }
                }
            
            result.onSuccess { data ->
                updateState { successState(it, data) }
            }.onError { throwable ->
                updateState { errorState(it, throwable.message ?: "Unknown error") }
            }
        }
    }

    /**
     * Executes a suspend function and updates state with Result pattern, using Flow.
     */
    protected fun <T> executeFlowWithResult(
        flow: Flow<Result<T>>,
        loadingState: (S) -> S,
        successState: (S, T) -> S,
        errorState: (S, String) -> S
    ) {
        flow
            .onStart { updateState { loadingState(it) } }
            .onEach { result ->
                result.onSuccess { data ->
                    updateState { successState(it, data) }
                }.onError { throwable ->
                    updateState { errorState(it, throwable.message ?: "Unknown error") }
                }
            }
            .catch { throwable ->
                val errorMessage = errorMessageMapper?.mapError(throwable)
                    ?: throwable.message
                    ?: "Unknown error"
                updateState { errorState(currentState, errorMessage) }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Executes a simple operation without Result pattern.
     */
    protected fun executeOperation(
        loadingState: (S) -> S,
        completionState: (S) -> S = { it },
        errorState: (S, String) -> S,
        block: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                updateState { loadingState(it) }
                block()
                updateState { completionState(it) }
            } catch (e: Exception) {
                val errorMessage = errorMessageMapper?.mapError(e) ?: e.message ?: "Unknown error"
                updateState { errorState(it, errorMessage) }
            }
        }
    }
}

/**
 * UI state with Result pattern support.
 */
data class ResultUiState<T>(
    val result: Result<T>? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null
) {
    val data: T? get() = result?.getOrNull()
    val isSuccess: Boolean get() = result?.isSuccess == true
    val isError: Boolean get() = result?.isError == true
    val isReady: Boolean get() = !isLoading && !hasError
}
