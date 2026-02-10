package com.cebolao.lotofacil.core.result

import com.cebolao.lotofacil.core.error.AppError

sealed interface AppResult<out T> {
    data class Success<out T>(val value: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>
}

fun <T> T.toSuccess(): AppResult.Success<T> = AppResult.Success(this)

/**
 * Extension functions for AppResult conversion in the domain layer.
 * These provide convenient methods for converting between success/failure types.
 */

/**
 * Convert AppResult to nullable value, returning null for failures.
 */
fun <T> AppResult<T>.getOrNull(): T? = when (this) {
    is AppResult.Success -> value
    is AppResult.Failure -> null
}

/**
 * Convert AppResult to nullable value with a default value for failures.
 */
fun <T> AppResult<T>.getOrDefault(default: T): T = when (this) {
    is AppResult.Success -> value
    is AppResult.Failure -> default
}

/**
 * Execute action only for success cases.
 */
inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(value)
    return this
}

/**
 * Execute action only for failure cases.
 */
inline fun <T> AppResult<T>.onFailure(action: (AppError) -> Unit): AppResult<T> {
    if (this is AppResult.Failure) action(error)
    return this
}

/**
 * Map success values to new types, preserving failures.
 */
inline fun <T, R> AppResult<T>.map(crossinline transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(value))
    is AppResult.Failure -> this
}

/**
 * Transform failures to new failures, preserving success values.
 */
inline fun <T> AppResult<T>.mapFailure(crossinline transform: (AppError) -> AppError): AppResult<T> = when (this) {
    is AppResult.Success -> this
    is AppResult.Failure -> AppResult.Failure(transform(error))
}

