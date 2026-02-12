package com.cebolao.lotofacil.ui.components

/**
 * Real-time validation feedback component.
 * Shows validation state with visual feedback and error messages.
 */
data class ValidationState(
    val isValid: Boolean = true,
    val errorMessage: String? = null
) {
    companion object {
        fun Error(message: String) = ValidationState(isValid = false, errorMessage = message)
    }
}
