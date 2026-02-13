package com.cebolao.lotofacil.ui.screens.checker

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import com.cebolao.lotofacil.ui.components.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.GameStatistic
import com.cebolao.lotofacil.domain.model.LotofacilConstants
import com.cebolao.lotofacil.domain.usecase.GameCheckPhase
import com.cebolao.lotofacil.navigation.UiEvent
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.AppScreenDefaults
import com.cebolao.lotofacil.ui.components.AppScreenScaffold
import com.cebolao.lotofacil.ui.components.CheckResultCard
import com.cebolao.lotofacil.ui.components.CheckerScrollableActions
import com.cebolao.lotofacil.ui.components.ConfirmationDialog
import com.cebolao.lotofacil.ui.components.ErrorActions
import com.cebolao.lotofacil.ui.components.ErrorCard
import com.cebolao.lotofacil.ui.components.GameStatsList
import com.cebolao.lotofacil.ui.components.NumberBallItem
import com.cebolao.lotofacil.ui.components.NumberGrid
import com.cebolao.lotofacil.ui.components.RecentHitsChartContent
import com.cebolao.lotofacil.ui.components.screenContentPadding
import com.cebolao.lotofacil.ui.components.shimmer
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppTheme
import com.cebolao.lotofacil.viewmodels.CheckerScreenState
import com.cebolao.lotofacil.viewmodels.CheckerUiState
import com.cebolao.lotofacil.viewmodels.CheckerViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CheckerScreen(
    modifier: Modifier = Modifier,
    checkerViewModel: CheckerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val screenState by checkerViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }

    // Handle UI events (snackbars)
    LaunchedEffect(Unit) {
        checkerViewModel.uiEvent.collectLatest { event: UiEvent ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    val message = event.message ?: event.messageResId?.let(context::getString).orEmpty()
                    if (message.isNotBlank()) {
                        val actionLabel = event.actionLabel ?: event.actionLabelResId?.let(context::getString)
                        snackbarHostState.showSnackbar(
                            message = message,
                            actionLabel = actionLabel,
                            withDismissAction = true
                        )
                    }
                }
                else -> { /* Handle other events if needed */ }
            }
        }
    }

    // Delegate to Content composable
    CheckerScreenContent(
        screenState = screenState,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onAction = { action ->
            when (action) {
                is CheckerAction.OnNumberClicked -> checkerViewModel.onNumberClicked(action.number)
                is CheckerAction.OnClearSelectionClicked -> checkerViewModel.onClearSelectionClicked()
                is CheckerAction.OnCheckGameClicked -> checkerViewModel.onCheckGameClicked()
                is CheckerAction.ConfirmClearSelection -> checkerViewModel.confirmClearSelection()
                is CheckerAction.DismissClearConfirmation -> checkerViewModel.dismissClearConfirmation()
                is CheckerAction.ConfirmClearResults -> checkerViewModel.confirmClearResults()
                is CheckerAction.DismissClearResultsConfirmation -> checkerViewModel.dismissClearResultsConfirmation()
            }
        }
    )
}

// ==================== SEALED ACTIONS ====================
sealed class CheckerAction {
    data class OnNumberClicked(val number: Int) : CheckerAction()
    object OnClearSelectionClicked : CheckerAction()
    object OnCheckGameClicked : CheckerAction()
    object ConfirmClearSelection : CheckerAction()
    object DismissClearConfirmation : CheckerAction()
    object ConfirmClearResults : CheckerAction()
    object DismissClearResultsConfirmation : CheckerAction()
}

// ==================== STATELESS CONTENT ====================
@Composable
fun CheckerScreenContent(
    screenState: CheckerScreenState,
    modifier: Modifier = Modifier,
    snackbarHostState: androidx.compose.material3.SnackbarHostState = remember { androidx.compose.material3.SnackbarHostState() },
    onAction: (CheckerAction) -> Unit = {}
) {
    val listState = rememberLazyListState()
    
    val isButtonEnabled by remember(screenState.selectedNumbers.size, screenState.uiState) {
        derivedStateOf {
            screenState.selectedNumbers.size == LotofacilConstants.GAME_SIZE && screenState.uiState !is CheckerUiState.Loading
        }
    }

    AppScreenScaffold(
        modifier = modifier.fillMaxSize(),
        title = stringResource(id = R.string.check_game_title),
        subtitle = stringResource(id = R.string.check_game_subtitle),
        icon = Icons.AutoMirrored.Filled.FactCheck,
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .screenContentPadding(paddingValues),
            state = listState,
            contentPadding = AppScreenDefaults.listContentPadding(),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
        ) {
            item(key = "number_grid", contentType = "number_grid") {
                NumberGridSection(
                    selectedNumbers = screenState.selectedNumbers,
                    onNumberClicked = { number -> onAction(CheckerAction.OnNumberClicked(number)) }
                )
            }

            item(key = "actions", contentType = "checker_actions") {
                CheckerScrollableActions(
                    selectedCount = screenState.selectedNumbers.size,
                    isLoading = screenState.uiState is CheckerUiState.Loading,
                    isButtonEnabled = isButtonEnabled,
                    onClearClick = { onAction(CheckerAction.OnClearSelectionClicked) },
                    onCheckClick = { onAction(CheckerAction.OnCheckGameClicked) }
                )
            }

            item(key = "result", contentType = "checker_result") {
                val currentUiState = screenState.uiState
                AnimatedContent(
                    targetState = currentUiState,
                    label = "checker_result_content"
                ) { checkerState ->
                    when (checkerState) {
                        is CheckerUiState.Idle -> { /* Nothing shown */ }
                        is CheckerUiState.Loading -> {
                            val loadingMessage = when (checkerState.phase) {
                                GameCheckPhase.HISTORICAL -> stringResource(id = R.string.checker_loading_history)
                                GameCheckPhase.CALCULATION -> stringResource(id = R.string.checker_calculating_results)
                                GameCheckPhase.STATISTICS -> stringResource(id = R.string.checker_analyzing_stats)
                            }
                            CheckerLoadingContent(
                                progress = checkerState.progress,
                                message = loadingMessage
                            )
                        }
                        is CheckerUiState.Success -> {
                            CheckerSuccessContent(
                                result = checkerState.result,
                                stats = checkerState.simpleStats
                            )
                        }
                        is CheckerUiState.Error -> {
                            ErrorCard(
                                messageResId = checkerState.messageResId,
                                actions = {
                                    if (checkerState.canRetry) {
                                        ErrorActions(
                                            onRetry = { onAction(CheckerAction.OnCheckGameClicked) },
                                            retryText = stringResource(id = R.string.try_again)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Clear selection confirmation dialog
    if (screenState.showClearConfirmation) {
        ConfirmationDialog(
            title = stringResource(R.string.checker_clear_selection_title),
            message = stringResource(R.string.checker_clear_selection_message),
            confirmText = stringResource(R.string.clear_button),
            dismissText = stringResource(R.string.cancel_button),
            onConfirm = { onAction(CheckerAction.ConfirmClearSelection) },
            onDismiss = { onAction(CheckerAction.DismissClearConfirmation) }
        )
    }

    // Clear results confirmation dialog
    if (screenState.showClearResultsConfirmation) {
        ConfirmationDialog(
            title = stringResource(R.string.checker_results_cleared_title),
            message = stringResource(R.string.checker_results_cleared_message),
            confirmText = stringResource(R.string.check_game_button),
            dismissText = stringResource(R.string.cancel_button),
            onConfirm = { onAction(CheckerAction.ConfirmClearResults) },
            onDismiss = { onAction(CheckerAction.DismissClearResultsConfirmation) }
        )
    }
}

@Composable
private fun NumberGridSection(
    selectedNumbers: Set<Int>,
    onNumberClicked: (Int) -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        variant = com.cebolao.lotofacil.ui.components.CardVariant.Elevated,
        isGlassmorphic = true
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            Text(
                text = stringResource(id = R.string.selected_numbers_progress, selectedNumbers.size),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Optimize grid items generation with stable keys
            val gridItems = remember(selectedNumbers) {
                (1..25).map { num ->
                    NumberBallItem(
                        number = num,
                        isSelected = selectedNumbers.contains(num),
                        isDisabled = false
                    )
                }
            }
            
            NumberGrid(
                items = gridItems,
                onNumberClicked = onNumberClicked
            )
        }
    }
}

@Composable
private fun CheckerLoadingContent(progress: Float, message: String) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = AppElevation.sm),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                strokeCap = StrokeCap.Round
            )
            CheckerResultSkeleton()
        }
    }
}

@Composable
private fun CheckerSuccessContent(
    result: com.cebolao.lotofacil.domain.model.CheckResult,
    stats: ImmutableList<GameStatistic>
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)) {
        AnimateOnEntry {
            CheckResultCard(result = result)
        }

        AnimateOnEntry(delayMillis = AppTheme.motion.delayCheckerMs) {
            AppCard(
                modifier = Modifier.fillMaxWidth(),
                variant = com.cebolao.lotofacil.ui.components.CardVariant.Elevated,
                isGlassmorphic = true
            ) {
                Column(
                    modifier = Modifier.padding(AppSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
                ) {
                    Text(
                        text = stringResource(id = R.string.game_stats_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    GameStatsList(stats = stats)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    RecentHitsChartContent(recentHits = result.recentHits)
                }
            }
        }
    }
}

@Composable
private fun CheckerResultSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(MaterialTheme.shapes.medium)
                .shimmer()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            for (i in 0 until 3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clip(MaterialTheme.shapes.small)
                        .shimmer()
                )
            }
        }
    }
}
