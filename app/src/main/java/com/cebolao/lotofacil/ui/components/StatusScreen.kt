package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppSpacing

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

/**
 * Unified screen status handler composable
 * 
 * Centralizes Loading, Error, Empty, and Success state handling
 * Eliminates ~50 lines of duplicated code per screen
 * 
 * Usage:
 * ```kotlin
 * val status: StateFlow<ScreenStatus<MyData>> by viewModel.status.collectAsStateWithLifecycle()
 * 
 * StatusScreen(
 *     status = status,
 *     loadingMessage = "Carregando dados...",
 *     onRetry = { viewModel.retry() },
 *     onSuccess = { data -> MyContent(data) }
 * )
 * ```
 */
@Composable
fun <T> StatusScreen(
    status: ScreenStatus<T>,
    modifier: Modifier = Modifier,
    loadingMessage: String? = null,
    onRetry: (() -> Unit)? = null,
    onEmpty: (@Composable () -> Unit)? = null,
    onSuccess: @Composable (T) -> Unit
) {
    when (status) {
        is ScreenStatus.Loading -> {
            LoadingData(
                message = loadingMessage ?: stringResource(id = R.string.loading_data),
                modifier = modifier.fillMaxSize()
            )
        }
        
        is ScreenStatus.Error -> {
            ErrorCard(
                messageResId = status.messageResId ?: R.string.error_generic,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.lg),
                actions = if (onRetry != null) {
                    { Button(onClick = onRetry) { Text(stringResource(id = R.string.retry_button)) } }
                } else null
            )
        }
        
        is ScreenStatus.Empty -> {
            if (onEmpty != null) {
                onEmpty()
            } else {
                EmptyState(
                    modifier = modifier.fillMaxSize(),
                    message = stringResource(id = R.string.empty_state_message)
                )
            }
        }
        
        is ScreenStatus.Success -> {
            onSuccess(status.data)
        }
    }
}

/**
 * Simplified loading content handler
 * Use when you only need to handle loading vs content
 */
@Composable
fun <T> LoadingOrContent(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    loadingMessage: String? = null,
    content: @Composable () -> Unit
) {
    if (isLoading) {
        LoadingData(
            message = loadingMessage ?: stringResource(id = R.string.loading_data),
            modifier = modifier.fillMaxSize()
        )
    } else {
        content()
    }
}
