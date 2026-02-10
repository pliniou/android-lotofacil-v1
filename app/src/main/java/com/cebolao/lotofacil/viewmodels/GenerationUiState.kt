package com.cebolao.lotofacil.viewmodels

import androidx.compose.runtime.Stable

@Stable
sealed interface GenerationUiState {
    data object Idle : GenerationUiState
    data object Loading : GenerationUiState
    data class Success(val gameCount: Int) : GenerationUiState
    data class Error(val messageResId: Int) : GenerationUiState
}
