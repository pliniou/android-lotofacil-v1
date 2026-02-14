package com.cebolao.lotofacil.ui.screens.checker

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import com.cebolao.lotofacil.ui.components.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.CropFree
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.LooksOne
import androidx.compose.material.icons.outlined.LooksTwo
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.GameStatistic
import com.cebolao.lotofacil.domain.model.GameStatisticType
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
import com.cebolao.lotofacil.ui.components.NumberBallItem
import com.cebolao.lotofacil.ui.components.NumberGrid
import com.cebolao.lotofacil.ui.components.RecentHitsChartContent
import com.cebolao.lotofacil.ui.components.screenContentPadding
import com.cebolao.lotofacil.ui.components.shimmer
import com.cebolao.lotofacil.ui.text.AppStrings
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
    checkerViewModel: CheckerViewModel = hiltViewModel(),
    onBackClick: (() -> Unit)? = null,
    onGenerateNewGame: () -> Unit = {},
    onRefineWithPattern: (Set<Int>) -> Unit = {}
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
        onBackClick = onBackClick,
        onGenerateNewGame = onGenerateNewGame,
        onRefineWithPattern = onRefineWithPattern,
        onAction = { action ->
            when (action) {
                is CheckerAction.OnNumberClicked -> checkerViewModel.onNumberClicked(action.number)
                is CheckerAction.OnClearSelectionClicked -> checkerViewModel.onClearSelectionClicked()
                is CheckerAction.OnCheckGameClicked -> checkerViewModel.onCheckGameClicked()
                is CheckerAction.ConfirmClearSelection -> checkerViewModel.confirmClearSelection()
                is CheckerAction.DismissClearConfirmation -> checkerViewModel.dismissClearConfirmation()
                is CheckerAction.ConfirmClearResults -> checkerViewModel.confirmClearResults()
                is CheckerAction.DismissClearResultsConfirmation -> checkerViewModel.dismissClearResultsConfirmation()
                is CheckerAction.OnSaveGameClicked -> checkerViewModel.onSaveGameClicked()
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
    object OnSaveGameClicked : CheckerAction()
}

// ==================== STATELESS CONTENT ====================
@Composable
fun CheckerScreenContent(
    screenState: CheckerScreenState,
    modifier: Modifier = Modifier,
    snackbarHostState: androidx.compose.material3.SnackbarHostState = remember { androidx.compose.material3.SnackbarHostState() },
    onBackClick: (() -> Unit)? = null,
    onGenerateNewGame: () -> Unit = {},
    onRefineWithPattern: (Set<Int>) -> Unit = {},
    onAction: (CheckerAction) -> Unit = {}
) {
    val listState = rememberLazyListState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
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
        onBackClick = onBackClick,
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .screenContentPadding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = AppScreenDefaults.listContentPadding(
                    bottom = AppSpacing.xxxl + AppTheme.sizes.buttonHeightDefault + AppSpacing.lg
                ),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
            ) {
                item(key = "number_grid", contentType = "number_grid") {
                    NumberGridSection(
                        selectedNumbers = screenState.selectedNumbers,
                        onNumberClicked = { number -> onAction(CheckerAction.OnNumberClicked(number)) }
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
                                    stats = checkerState.simpleStats,
                                    selectedNumbers = screenState.selectedNumbers,
                                    isSavingGame = screenState.isSavingGame,
                                    isSavedGame = checkerState.isSavedGame,
                                    onGenerateNewGame = onGenerateNewGame,
                                    onRefineWithPattern = onRefineWithPattern,
                                    onSaveGame = { onAction(CheckerAction.OnSaveGameClicked) }
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

            val actionsModifier = if (isLandscape) {
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md)
                    .widthIn(max = 460.dp)
            } else {
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md)
            }

            AppCard(
                modifier = actionsModifier,
                variant = com.cebolao.lotofacil.ui.components.CardVariant.Elevated,
                isGlassmorphic = true
            ) {
                CheckerScrollableActions(
                    modifier = Modifier.fillMaxWidth(),
                    selectedCount = screenState.selectedNumbers.size,
                    isLoading = screenState.uiState is CheckerUiState.Loading,
                    isButtonEnabled = isButtonEnabled,
                    onClearClick = { onAction(CheckerAction.OnClearSelectionClicked) },
                    onCheckClick = { onAction(CheckerAction.OnCheckGameClicked) }
                )
            }
        }
    }

    val loadingState = screenState.uiState as? CheckerUiState.Loading
    if (loadingState != null) {
        val loadingMessage = when (loadingState.phase) {
            GameCheckPhase.HISTORICAL -> stringResource(id = R.string.checker_loading_history)
            GameCheckPhase.CALCULATION -> stringResource(id = R.string.checker_calculating_results)
            GameCheckPhase.STATISTICS -> stringResource(id = R.string.checker_analyzing_stats)
        }
        LoadingDialog(text = loadingMessage)
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
            val counterColor = when {
                selectedNumbers.size == 15 -> MaterialTheme.colorScheme.primary
                selectedNumbers.size > 15 -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurface
            }
            
            Text(
                text = stringResource(
                    id = AppStrings.Labels.selectedNumbers,
                    selectedNumbers.size,
                    LotofacilConstants.GAME_SIZE
                ),
                style = MaterialTheme.typography.titleMedium,
                color = counterColor
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
                ballSize = AppTheme.sizes.numberBallSmall,
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
    stats: ImmutableList<GameStatistic>,
    selectedNumbers: Set<Int>,
    isSavingGame: Boolean,
    isSavedGame: Boolean,
    onGenerateNewGame: () -> Unit,
    onRefineWithPattern: (Set<Int>) -> Unit,
    onSaveGame: () -> Unit
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
                    Text(
                        text = stringResource(id = R.string.checker_stats_interactive_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    InteractiveGameStatsGrid(stats = stats)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    RecentHitsChartContent(recentHits = result.recentHits)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                    ) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = onGenerateNewGame
                        ) {
                            Text(text = stringResource(id = R.string.generate_new_game_button))
                        }
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = { onRefineWithPattern(selectedNumbers) }
                        ) {
                            Text(text = stringResource(id = R.string.refine_from_pattern_button))
                        }
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSavingGame && !isSavedGame,
                        onClick = onSaveGame
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(AppSpacing.xs))
                        Text(
                            text = when {
                                isSavedGame -> stringResource(id = R.string.checker_game_saved_button_done)
                                isSavingGame -> stringResource(id = R.string.checker_game_saving_button)
                                else -> stringResource(id = R.string.checker_save_game_button)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InteractiveGameStatsGrid(stats: ImmutableList<GameStatistic>) {
    var selectedStat by remember(stats) { mutableStateOf<GameStatistic?>(null) }

    selectedStat?.let { stat ->
        val label = stat.type.labelForChecker()
        val insight = stat.type.insightForValue(stat.value)
        InfoDialog(
            onDismissRequest = { selectedStat = null },
            dialogTitle = "$label ${stat.value}"
        ) {
            Text(
                text = stringResource(id = R.string.checker_stat_range_label, insight.range),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(
                    id = if (insight.inMostCommonRange) {
                        R.string.checker_stat_status_good
                    } else {
                        R.string.checker_stat_status_attention
                    }
                ),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(id = R.string.checker_stat_context_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        stats.chunked(2).forEach { rowStats ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                rowStats.forEach { stat ->
                    AppCard(
                        modifier = Modifier.weight(1f),
                        variant = com.cebolao.lotofacil.ui.components.CardVariant.Filled,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        onClick = { selectedStat = stat }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = stat.type.iconForChecker(),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stat.type.labelForChecker(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = stat.value.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
                if (rowStats.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private data class CheckerStatInsight(
    val range: String,
    val inMostCommonRange: Boolean
)

private fun GameStatisticType.insightForValue(value: Int): CheckerStatInsight {
    return when (this) {
        com.cebolao.lotofacil.domain.model.GameStatisticType.SUM -> {
            CheckerStatInsight(range = "170 a 220", inMostCommonRange = value in 170..220)
        }
        com.cebolao.lotofacil.domain.model.GameStatisticType.EVENS -> {
            CheckerStatInsight(range = "6 a 9", inMostCommonRange = value in 6..9)
        }
        com.cebolao.lotofacil.domain.model.GameStatisticType.ODDS -> {
            CheckerStatInsight(range = "6 a 9", inMostCommonRange = value in 6..9)
        }
        com.cebolao.lotofacil.domain.model.GameStatisticType.PRIMES -> {
            CheckerStatInsight(range = "4 a 7", inMostCommonRange = value in 4..7)
        }
        com.cebolao.lotofacil.domain.model.GameStatisticType.FIBONACCI -> {
            CheckerStatInsight(range = "3 a 5", inMostCommonRange = value in 3..5)
        }
        com.cebolao.lotofacil.domain.model.GameStatisticType.FRAME -> {
            CheckerStatInsight(range = "8 a 11", inMostCommonRange = value in 8..11)
        }
        com.cebolao.lotofacil.domain.model.GameStatisticType.PORTRAIT -> {
            CheckerStatInsight(range = "4 a 7", inMostCommonRange = value in 4..7)
        }
        com.cebolao.lotofacil.domain.model.GameStatisticType.MULTIPLES_OF_3 -> {
            CheckerStatInsight(range = "3 a 6", inMostCommonRange = value in 3..6)
        }
    }
}

@Composable
private fun GameStatisticType.labelForChecker(): String {
    val labelRes = when (this) {
        com.cebolao.lotofacil.domain.model.GameStatisticType.SUM -> R.string.sum_label
        com.cebolao.lotofacil.domain.model.GameStatisticType.EVENS -> R.string.even_label
        com.cebolao.lotofacil.domain.model.GameStatisticType.ODDS -> R.string.odd_label
        com.cebolao.lotofacil.domain.model.GameStatisticType.PRIMES -> R.string.prime_label
        com.cebolao.lotofacil.domain.model.GameStatisticType.FIBONACCI -> R.string.fibonacci_label
        com.cebolao.lotofacil.domain.model.GameStatisticType.FRAME -> R.string.frame_label
        com.cebolao.lotofacil.domain.model.GameStatisticType.PORTRAIT -> R.string.portrait_label
        com.cebolao.lotofacil.domain.model.GameStatisticType.MULTIPLES_OF_3 -> R.string.multiples_of_3_label
    }
    return stringResource(id = labelRes)
}

private fun GameStatisticType.iconForChecker(): ImageVector {
    return when (this) {
        com.cebolao.lotofacil.domain.model.GameStatisticType.SUM -> Icons.Outlined.Functions
        com.cebolao.lotofacil.domain.model.GameStatisticType.EVENS -> Icons.Outlined.LooksTwo
        com.cebolao.lotofacil.domain.model.GameStatisticType.ODDS -> Icons.Outlined.LooksOne
        com.cebolao.lotofacil.domain.model.GameStatisticType.PRIMES -> Icons.Outlined.StarOutline
        com.cebolao.lotofacil.domain.model.GameStatisticType.FIBONACCI -> Icons.Outlined.Timeline
        com.cebolao.lotofacil.domain.model.GameStatisticType.FRAME -> Icons.Outlined.GridOn
        com.cebolao.lotofacil.domain.model.GameStatisticType.PORTRAIT -> Icons.Outlined.CropFree
        com.cebolao.lotofacil.domain.model.GameStatisticType.MULTIPLES_OF_3 -> Icons.Outlined.FormatListNumbered
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
