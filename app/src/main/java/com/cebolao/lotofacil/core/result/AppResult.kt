package com.cebolao.lotofacil.core.result

/**
 * Backward-compatible result type used across existing domain/data layers.
 * Kept while the codebase transitions to the newer Result abstraction.
 */
sealed class AppResult<out T> {
    data class Success<T>(val value: T) : AppResult<T>()
    data class Failure(val error: Any) : AppResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    fun getOrNull(): T? = when (this) {
        is Success -> value
        is Failure -> null
    }

    inline fun onSuccess(block: (T) -> Unit): AppResult<T> {
        if (this is Success) block(value)
        return this
    }

    inline fun onFailure(block: (Any) -> Unit): AppResult<T> {
        if (this is Failure) block(error)
        return this
    }
}

fun <T> T.toSuccess(): AppResult<T> = AppResult.Success(this)
fun toFailure(error: Any): AppResult<Nothing> = AppResult.Failure(error)
