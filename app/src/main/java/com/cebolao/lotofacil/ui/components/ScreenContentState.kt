package com.cebolao.lotofacil.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppSpacing

sealed interface ScreenContentState {
    data class Loading(
        @StringRes val messageResId: Int = R.string.loading_data
    ) : ScreenContentState

    data class Empty(
        @StringRes val messageResId: Int = R.string.empty_state_message,
        val icon: ImageVector? = null,
        @StringRes val actionLabelResId: Int? = null
    ) : ScreenContentState

    data class Error(
        @StringRes val messageResId: Int,
        val canRetry: Boolean = true
    ) : ScreenContentState

    data object Success : ScreenContentState
}

@Composable
fun AppScreenStateHost(
    state: ScreenContentState,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    onEmptyAction: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    when (state) {
        is ScreenContentState.Loading -> {
            FullScreenLoading(
                message = stringResource(id = state.messageResId),
                modifier = modifier.fillMaxSize()
            )
        }

        is ScreenContentState.Error -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(AppSpacing.lg),
                contentAlignment = Alignment.Center
            ) {
                ErrorCard(
                    messageResId = state.messageResId,
                    actions = {
                        if (state.canRetry && onRetry != null) {
                            ErrorActions(onRetry = onRetry)
                        }
                    }
                )
            }
        }

        is ScreenContentState.Empty -> {
            EmptyState(
                messageResId = state.messageResId,
                icon = state.icon,
                actionLabelResId = state.actionLabelResId,
                onAction = onEmptyAction,
                modifier = modifier.fillMaxSize()
            )
        }

        ScreenContentState.Success -> content()
    }
}

@Composable
fun SectionFeedbackState(
    isLoading: Boolean,
    @StringRes errorResId: Int?,
    modifier: Modifier = Modifier
) {
    when {
        isLoading -> {
            LinearProgressIndicator(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = AppSpacing.sm)
            )
        }

        errorResId != null -> {
            Text(
                text = stringResource(id = errorResId),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = modifier.padding(AppSpacing.sm)
            )
        }
    }
}
