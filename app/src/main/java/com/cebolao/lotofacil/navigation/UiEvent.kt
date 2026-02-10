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
        val actionLabel: String? = null
    ) : UiEvent
    data class Navigate(val destination: Destination) : UiEvent
    data class NavigateBack(val route: String? = null) : UiEvent
    data object NavigateToGeneratedGames : UiEvent
    data object NavigateUp : UiEvent
    data object ShowResetConfirmation : UiEvent
}
