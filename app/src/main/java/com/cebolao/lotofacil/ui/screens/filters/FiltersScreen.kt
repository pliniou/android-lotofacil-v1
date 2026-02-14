package com.cebolao.lotofacil.ui.screens.filters

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.navigation.UiEvent
import com.cebolao.lotofacil.navigation.toPresetNumbersOrNull
import com.cebolao.lotofacil.ui.components.AppScreenDefaults
import com.cebolao.lotofacil.ui.components.AppScreenScaffold
import com.cebolao.lotofacil.ui.components.AppScreenStateHost
import com.cebolao.lotofacil.ui.components.ConfirmationDialog
import com.cebolao.lotofacil.ui.components.ScreenContentState
import com.cebolao.lotofacil.ui.components.screenContentPadding
import com.cebolao.lotofacil.ui.model.descriptionRes
import com.cebolao.lotofacil.ui.model.titleRes
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.viewmodels.FiltersUiState
import com.cebolao.lotofacil.viewmodels.FiltersViewModel

@Composable
fun FiltersScreen(
    modifier: Modifier = Modifier,
    filtersViewModel: FiltersViewModel = hiltViewModel(),
    initialPreset: String? = null,
    onNavigateToGeneratedGames: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by filtersViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showDialogFor by remember { mutableStateOf<FilterType?>(null) }
    var showResetConfirmation by remember { mutableStateOf(false) }
    var lastAppliedPreset by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(initialPreset) {
        val preset = initialPreset?.takeIf { it.isNotBlank() } ?: return@LaunchedEffect
        if (preset != lastAppliedPreset) {
            preset.toPresetNumbersOrNull()?.let { filtersViewModel.applyGamePreset(it) }
            lastAppliedPreset = preset
        }
    }

    LaunchedEffect(Unit) {
        filtersViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.NavigateToGeneratedGames -> onNavigateToGeneratedGames()
                is UiEvent.ShowSnackbar -> {
                    val message = event.message ?: event.messageResId?.let(context::getString).orEmpty()
                    if (message.isNotBlank()) {
                        val actionLabel = event.actionLabel ?: event.actionLabelResId?.let(context::getString)
                        val result = snackbarHostState.showSnackbar(
                            message = message,
                            actionLabel = actionLabel,
                            withDismissAction = true
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            when (event.action) {
                                UiEvent.SnackbarAction.RetryLoadLastDraw -> filtersViewModel.retryLoadLastDraw()
                                UiEvent.SnackbarAction.RetryGenerateGames -> filtersViewModel.retryGenerateGames()
                                null -> Unit
                            }
                        }
                    }
                }
                is UiEvent.ShowResetConfirmation -> showResetConfirmation = true
            }
        }
    }

    // Delegate to Content composable
    FiltersScreenContent(
        state = uiState,
        snackbarHostState = snackbarHostState,
        haptic = haptic,
        modifier = modifier,
        showDialogFor = showDialogFor,
        onShowDialog = { showDialogFor = it },
        showResetConfirmation = showResetConfirmation,
        onShowResetConfirmation = { showResetConfirmation = it },
        onDismissErrorDialog = { filtersViewModel.dismissErrorDialog() },
        onAction = { action ->
            when (action) {
                is FiltersAction.RequestResetAllFilters -> filtersViewModel.requestResetAllFilters()
                is FiltersAction.ConfirmResetAllFilters -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    filtersViewModel.confirmResetAllFilters()
                    showResetConfirmation = false
                }
                is FiltersAction.OnFilterToggle -> filtersViewModel.onFilterToggle(action.type, action.enabled)
                is FiltersAction.OnRangeChange -> filtersViewModel.onRangeAdjust(action.type, action.range)
                is FiltersAction.GenerateGames -> filtersViewModel.generateGames(action.quantity)
                is FiltersAction.ApplyPreset -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    filtersViewModel.applyPreset(action.preset)
                }
                FiltersAction.RetryLoadLastDraw -> filtersViewModel.retryLoadLastDraw()
            }
        },
        onBackClick = onBackClick
    )
}

// ==================== SEALED ACTIONS ====================
sealed class FiltersAction {
    object RequestResetAllFilters : FiltersAction()
    object ConfirmResetAllFilters : FiltersAction()
    object RetryLoadLastDraw : FiltersAction()
    data class OnFilterToggle(val type: FilterType, val enabled: Boolean) : FiltersAction()
    data class OnRangeChange(val type: FilterType, val range: ClosedFloatingPointRange<Float>) : FiltersAction()
    data class GenerateGames(val quantity: Int) : FiltersAction()
    data class ApplyPreset(val preset: com.cebolao.lotofacil.domain.model.FilterPreset) : FiltersAction()
}

// ==================== STATELESS CONTENT ====================
@Composable
fun FiltersScreenContent(
    state: FiltersUiState,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    haptic: HapticFeedback = LocalHapticFeedback.current,
    showDialogFor: FilterType? = null,
    onShowDialog: (FilterType?) -> Unit = {},
    showResetConfirmation: Boolean = false,
    onShowResetConfirmation: (Boolean) -> Unit = {},
    onDismissErrorDialog: () -> Unit = {},
    onAction: (FiltersAction) -> Unit = {},
    onBackClick: (() -> Unit)? = null
) {
    val hasContent = state.filterStates.isNotEmpty()
    val pageState = remember(state.filterStates, state.isLoadingLastDraw, state.lastDrawErrorMessageResId, hasContent) {
        when {
            state.isLoadingLastDraw && !hasContent -> ScreenContentState.Loading()
            state.lastDrawErrorMessageResId != null && !hasContent -> ScreenContentState.Error(
                messageResId = state.lastDrawErrorMessageResId
            )
            else -> ScreenContentState.Success
        }
    }

    // Filter info dialog
    showDialogFor?.let { type ->
        val filterInfo = FilterInfo(
            id = type,
            title = stringResource(type.titleRes),
            description = stringResource(type.descriptionRes)
        )
        ModalBottomSheet(
            onDismissRequest = { onShowDialog(null) }
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(AppSpacing.md)
            ) {
                Text(
                    text = filterInfo.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = filterInfo.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = { onShowDialog(null) }) {
                    Text(text = stringResource(id = R.string.understood_button))
                }
            }
        }
    }

    // Reset confirmation dialog
    if (showResetConfirmation) {
        ConfirmationDialog(
            title = stringResource(id = R.string.reset_filters_title),
            message = stringResource(id = R.string.reset_filters_message),
            confirmText = stringResource(id = R.string.reset_button),
            dismissText = stringResource(id = R.string.cancel_button),
            onConfirm = { onAction(FiltersAction.ConfirmResetAllFilters) },
            onDismiss = { onShowResetConfirmation(false) }
        )
    }

    state.errorDialog?.let { dialog ->
        ConfirmationDialog(
            title = stringResource(id = dialog.titleResId),
            message = stringResource(id = dialog.messageResId),
            confirmText = stringResource(id = dialog.confirmResId),
            dismissText = dialog.dismissResId?.let { stringResource(id = it) }.orEmpty(),
            onConfirm = onDismissErrorDialog,
            onDismiss = onDismissErrorDialog
        )
    }

    AppScreenScaffold(
        modifier = modifier.fillMaxSize(),
        title = stringResource(id = R.string.filters_header_title),
        subtitle = stringResource(id = R.string.filters_header_subtitle),
        icon = Icons.Filled.FilterAlt,
        onBackClick = onBackClick,
        snackbarHostState = snackbarHostState,
        actions = {
            androidx.compose.material3.IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAction(FiltersAction.RequestResetAllFilters)
                }
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.RestartAlt,
                    contentDescription = stringResource(id = R.string.reset_filters),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { innerPadding ->
        AppScreenStateHost(
            state = pageState,
            modifier = Modifier
                .fillMaxSize()
                .screenContentPadding(innerPadding),
            onRetry = { onAction(FiltersAction.RetryLoadLastDraw) }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = AppScreenDefaults.listContentPadding(),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(AppSpacing.xl)
            ) {

                item(key = "presets", contentType = "presets") {
                    PresetsPanel(
                        onApplyPreset = { preset -> onAction(FiltersAction.ApplyPreset(preset)) }
                    )
                }

                item(key = "active_filters", contentType = "active_filters") {
                    ActiveFiltersPanel(
                        activeFilters = state.filterStates.filter { it.isEnabled }
                    )
                }

                item(key = "possible_combinations", contentType = "possible_combinations") {
                    PossibleCombinationsPanel(
                        possibleCombinationsCount = state.possibleCombinationsCount,
                        isEstimated = state.isPossibleCombinationsEstimated,
                        isImpossible = state.isCombinationImpossible,
                        isVeryRestrictive = state.isVeryRestrictiveCombination,
                        isAnalyzing = state.isAnalyzingCombinations
                    )
                }

                filterList(
                    filterStates = state.filterStates,
                    lastDraw = state.lastDraw,
                    onFilterToggle = { type, enabled ->
                        onAction(FiltersAction.OnFilterToggle(type, enabled))
                    },
                    onRangeChange = { type, range ->
                        onAction(FiltersAction.OnRangeChange(type, range))
                    },
                    onInfoClick = { type -> onShowDialog(type) }
                )

                item(key = "generate_actions", contentType = "generate_actions") {
                    GenerateActionsPanel(
                        generationState = state.generationState,
                        isDataSyncing = state.isSyncingData,
                        activeFiltersCount = state.activeFiltersCount,
                        isCombinationPossible = !state.isCombinationImpossible,
                        onGenerate = { quantity -> onAction(FiltersAction.GenerateGames(quantity)) }
                    )
                }
            }
        }
    }
}
