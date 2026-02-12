package com.cebolao.lotofacil.core.result

/**
 * Backward-compatible result type used across existing domain/data layers.
 * Kept while the codebase transitions to the newer Result abstraction.
 */
sealed class AppResult<out T> {
    data class Success<T>(val value: T) : AppResult<T>()
    data class Failure(val error: Any) : AppResult<Nothing>()

}

fun <T> T.toSuccess(): AppResult<T> = AppResult.Success(this)
