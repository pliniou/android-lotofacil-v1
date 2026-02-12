package com.cebolao.lotofacil.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.core.result.ErrorMessageMapper
import com.cebolao.lotofacil.core.result.Result
import com.cebolao.lotofacil.core.result.mapErrorMessages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Enhanced Base ViewModel with Result pattern support and improved error handling.
 */
abstract class EnhancedBaseViewModel : ViewModel() {

    protected val jobTracker = JobTracker()

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
}
