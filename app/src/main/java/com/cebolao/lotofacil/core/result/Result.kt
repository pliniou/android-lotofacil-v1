package com.cebolao.lotofacil.core.result

import androidx.compose.runtime.Immutable
import com.cebolao.lotofacil.core.error.AppError
import kotlin.UnsafeVariance

/**
 * Unified Result type for the entire application.
 * Consolidates functionality from both Result and AppResult classes.
 * Provides a type-safe way to handle operations that can succeed, fail, or be loading.
 */
@Immutable
sealed class Result<out T> {
    
    /**
     * Represents a successful operation with data.
     */
    @Immutable
    data class Success<T>(val data: T) : Result<T>()
    
    /**
     * Represents a failed operation with error information.
     */
    @Immutable
    data class Error(
        val exception: Throwable,
        val message: String = exception.message ?: "Unknown error occurred",
        val appError: AppError? = null
    ) : Result<Nothing>()
    
    /**
     * Represents a loading state.
     */
    @Immutable
    object Loading : Result<Nothing>()
    
    // Extension functions for convenient usage
    
    /**
     * Returns the success data or null if this is not a success.
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    /**
     * Returns the success data or the provided default value.
     */
    fun getOrDefault(default: @UnsafeVariance T): T = when (this) {
        is Success -> data
        else -> default
    }
    
    /**
     * Returns the success data or throws the exception if this is an error.
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> throw IllegalStateException("Result is still loading")
    }
    
    /**
     * Checks if this result is a success.
     */
    val isSuccess: Boolean
        get() = this is Success
    
    /**
     * Checks if this result is an error.
     */
    val isError: Boolean
        get() = this is Error
    
    /**
     * Checks if this result is loading.
     */
    val isLoading: Boolean
        get() = this is Loading
    
    /**
     * Maps the success value to another type.
     */
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> Loading
    }
    
    /**
     * Maps the error to another error type.
     */
    inline fun mapError(transform: (Throwable) -> Throwable): Result<T> = when (this) {
        is Success -> this
        is Error -> Error(transform(exception), message, appError)
        is Loading -> Loading
    }
    
    /**
     * Performs an action if this is a success.
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    /**
     * Performs an action if this is an error.
     */
    inline fun onError(action: (Throwable) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }
    
    /**
     * Performs an action if this is loading.
     */
    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }
    
    companion object {
        
        /**
         * Creates a success result.
         */
        fun <T> success(data: T): Result<T> = Success(data)
        
        /**
         * Creates an error result.
         */
        fun <T> error(
            exception: Throwable,
            message: String = exception.message ?: "Unknown error occurred",
            appError: AppError? = null
        ): Result<T> = Error(exception, message, appError)
        
        /**
         * Creates a loading result.
         */
        fun <T> loading(): Result<T> = Loading
        
        /**
         * Wraps a suspending operation in a try-catch block.
         */
        suspend fun <T> catch(block: suspend () -> T): Result<T> {
            return try {
                Success(block())
            } catch (e: Exception) {
                Error(e)
            }
        }
    }
}

// Extension functions for convenient conversion

/**
 * Converts any value to a success result.
 */
fun <T> T.toResult(): Result<T> = Result.success(this)

/**
 * Converts a throwable to an error result.
 */
fun <T> Throwable.toErrorResult(): Result<T> = Result.error(this)

/**
 * Converts a nullable value to a result, treating null as error.
 */
fun <T> T?.toResultOrError(error: Throwable = IllegalStateException("Value is null")): Result<T> {
    return this?.let { Result.success(it) } ?: Result.error(error)
}

// Kotlin Result interop

/**
 * Converts Kotlin's Result to our Result.
 */
fun <T> kotlin.Result<T>.toAppResult(): Result<T> {
    return fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.error(it) }
    )
}

/**
 * Converts our Result to Kotlin's Result.
 */
fun <T> Result<T>.toKotlinResult(): kotlin.Result<T> {
    return when (this) {
        is Result.Success -> kotlin.Result.success(data)
        is Result.Error -> kotlin.Result.failure(exception)
        is Result.Loading -> kotlin.Result.failure(IllegalStateException("Result is loading"))
    }
}
