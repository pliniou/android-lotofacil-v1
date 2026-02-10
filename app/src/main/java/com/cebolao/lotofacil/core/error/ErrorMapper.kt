package com.cebolao.lotofacil.core.error

import android.content.Context
import com.cebolao.lotofacil.R

/**
 * Converts low‑level exceptions into [AppError] instances and human‑readable
 * messages.  Centralising this logic avoids duplication in repositories and
 * use cases.
 */
object ErrorMapper {

    /**
     * Convert any [Throwable] into an [AppError].  Unknown exceptions
     * default to [UnknownError].
     */
    fun toAppError(throwable: Throwable): AppError = when (throwable) {
        is java.net.SocketTimeoutException, is java.net.UnknownHostException ->
            NetworkError(throwable)
        is java.io.IOException -> PersistenceError(throwable)
        is AppError -> throwable
        else -> UnknownError(throwable)
    }

    /**
     * Provide a user‑friendly message for an [AppError].  UI layers can
     * internationalise these messages by mapping them to string resources.
     */
    fun messageFor(error: AppError): String = when (error) {
        is NetworkError -> "No connection to server. Please try again."
        is PersistenceError -> "Error accessing local storage."
        is ParsingError -> "Error processing remote data."
        is EmptyHistoryError -> "History unavailable."
        is UnknownError -> "An unexpected error occurred."
        else -> "Unexpected error."
    }

    /**
     * Provide a user‑friendly message for an [AppError] using string resources.
     * This version is used by Android UI components that have access to Context.
     */
    fun messageFor(error: AppError, context: Context): String = when (error) {
        is NetworkError -> context.getString(R.string.error_network_connection)
        is PersistenceError -> context.getString(R.string.error_persistence)
        is ParsingError -> context.getString(R.string.error_parsing)
        is EmptyHistoryError -> context.getString(R.string.error_history_unavailable)
        is UnknownError -> context.getString(R.string.error_unexpected)
        else -> context.getString(R.string.error_unexpected)
    }
}
