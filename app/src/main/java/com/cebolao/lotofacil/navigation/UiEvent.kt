package com.cebolao.lotofacil.navigation

import androidx.annotation.StringRes

/**
 * Sealed interface representing one-off UI events that should be handled
 * by the UI layer (typically showing messages or navigation).
 */
sealed interface UiEvent {
    data class ShowSnackbar(
        @StringRes val messageResId: Int? = null,
        val message: String? = null,
        @StringRes val actionLabelResId: Int? = null,
        val actionLabel: String? = null,
        val action: SnackbarAction? = null
    ) : UiEvent
    sealed interface SnackbarAction {
        data object RetryLoadLastDraw : SnackbarAction
        data object RetryGenerateGames : SnackbarAction
    }
    data object NavigateToGeneratedGames : UiEvent
    data object ShowResetConfirmation : UiEvent
}
