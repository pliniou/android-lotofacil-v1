package com.cebolao.lotofacil.domain.model

import com.cebolao.lotofacil.core.error.AppError

/**
 * Represents errors that occur within the Domain Layer.
 * These should be mapped to UI messages in the Presentation Layer.
 */
sealed interface DomainError : AppError {

    /**
     * Functionality or data is not available (e.g., no internet for history).
     */
    data object HistoryUnavailable : DomainError {
        override val cause: Throwable? = null
    }

    /**
     * The requested operation is invalid given the current state.
     */
    data class InvalidOperation(val reason: String) : DomainError {
        override val cause: Throwable? = null
    }

    /**
     * A specific validation error for filters or inputs.
     */
    data class ValidationError(val message: String) : DomainError {
        override val cause: Throwable? = null
    }

    /**
     * An unknown error occurred.
     */
    data class Unknown(override val cause: Throwable? = null) : DomainError
}
