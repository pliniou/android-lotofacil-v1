package com.cebolao.lotofacil.ui.components

/**
 * Sealed class representing screen loading/content/error states
 * 
 * Type-safe state management for any screen content
 * Eliminates boilerplate for handling Loading, Error, Empty, Success states
 */
sealed class ScreenStatus<T> {
    class Loading<T> : ScreenStatus<T>()
    data class Success<T>(val data: T) : ScreenStatus<T>()
    data class Error<T>(val exception: Throwable, val messageResId: Int? = null) : ScreenStatus<T>()
    class Empty<T> : ScreenStatus<T>()
}
