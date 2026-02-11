package com.cebolao.lotofacil.domain.model

import com.cebolao.lotofacil.core.error.AppError

/**
 * Represents errors that occur within the Domain Layer.
 * These should be mapped to UI messages in the Presentation Layer.
 */
sealed class DomainError(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause), AppError {

    /**
     * Network-related errors.
     */
    data class NetworkError(override val cause: Throwable? = null) :
        DomainError(message = "Network error", cause = cause)

    /**
     * Data access or parsing errors.
     */
    data class DataError(override val cause: Throwable? = null) :
        DomainError(message = "Data error", cause = cause)

    /**
     * Validation errors for user input.
     */
    data class ValidationError(
        override val message: String,
        override val cause: Throwable? = null
    ) : DomainError(message = message, cause = cause)

    /**
     * Resource not found errors.
     */
    data class NotFoundError(
        val resource: String = "Resource",
        override val cause: Throwable? = null
    ) : DomainError(message = "$resource not found", cause = cause)

    /**
     * Permission-related errors.
     */
    data class PermissionError(
        val permission: String = "Required permission",
        override val cause: Throwable? = null
    ) : DomainError(message = "Permission required: $permission", cause = cause)

    /**
     * Functionality or data is not available (e.g., no internet for history).
     */
    data object HistoryUnavailable : DomainError(message = "History unavailable")

    /**
     * The requested operation is invalid given the current state.
     */
    data class InvalidOperation(
        val reason: String,
        override val cause: Throwable? = null
    ) : DomainError(message = "Invalid operation: $reason", cause = cause)

    /**
     * An unknown error occurred.
     */
    data class Unknown(override val cause: Throwable? = null) :
        DomainError(message = "Unknown error", cause = cause)

    /**
     * Kept for backward compatibility with earlier naming.
     */
    data class UnknownError(override val cause: Throwable? = null) :
        DomainError(message = "Unknown error", cause = cause)
}
