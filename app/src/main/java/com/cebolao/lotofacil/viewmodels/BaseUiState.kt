package com.cebolao.lotofacil.viewmodels

/**
 * Base interface for all UI states, providing common loading and error states.
 * This ensures consistency across all ViewModel state implementations.
 */
interface BaseUiState {
    val isLoading: Boolean get() = false
    val hasError: Boolean get() = false
    val errorMessageResId: Int? get() = null
    val errorMessage: String? get() = null

    val isReady: Boolean get() = !isLoading && !hasError
}
