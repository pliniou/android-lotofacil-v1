package com.cebolao.lotofacil.ui.screens.filters

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.FilterPreset
import com.cebolao.lotofacil.domain.model.FilterSelectionMode
import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.ui.components.*
import com.cebolao.lotofacil.ui.components.FilterCard
import com.cebolao.lotofacil.ui.components.GenerationActionsPanel
import com.cebolao.lotofacil.ui.model.titleRes
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppTheme
import com.cebolao.lotofacil.ui.theme.LocalSemanticColors
import com.cebolao.lotofacil.viewmodels.GenerationUiState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PresetsPanel(
    onApplyPreset: (FilterPreset) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        Text(
            text = stringResource(id = R.string.presets_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
            contentPadding = PaddingValues(bottom = AppSpacing.sm)
        ) {
            itemsIndexed(FilterPreset.entries) { index, preset ->
                 val delay = (index * 50L).coerceAtMost(300L)
                 AnimateOnEntry(delayMillis = delay) {
                    PresetCard(
                        preset = preset,
                        onClick = { onApplyPreset(preset) }
                    )
                 }
            }
            item(key = "generate_actions", contentType = "generate_actions") {
                GenerationActionsPanel(
                    generationState = GenerationUiState.Idle,
                    activeFiltersCount = 0,
                    onGenerate = { _ -> }
                )
            }
        }
    }
}

@Composable
private fun PresetCard(
    preset: FilterPreset,
    onClick: () -> Unit
) {
    AppCard(
        onClick = onClick,
        modifier = Modifier.width(160.dp),
        variant = com.cebolao.lotofacil.ui.components.CardVariant.Elevated,
        isGlassmorphic = true
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            Text(
                text = stringResource(id = preset.titleRes),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = preset.descriptionRes),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun ActiveFiltersPanel(
    activeFilters: List<FilterState>,
    modifier: Modifier = Modifier
) {
    val totalFilters = FilterType.entries.size
    val activeCount = activeFilters.size

    AnimateOnEntry(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Text(
                text = pluralStringResource(
                    id = R.plurals.active_filters_count_summary,
                    count = activeCount,
                    activeCount,
                    totalFilters
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            
            // Always show the filter chips area, even if empty
            if (activeFilters.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    mainAxisSpacing = AppSpacing.md,
                    crossAxisSpacing = AppSpacing.md
                ) {
                    activeFilters.forEach { filter ->
                        FilterChip(
                            selected = true,
                            onClick = { /* Could toggle or navigate */ },
                            label = { 
                                Text(
                                    stringResource(id = filter.type.titleRes),
                                    style = MaterialTheme.typography.labelSmall
                                ) 
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            border = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PossibleCombinationsPanel(
    possibleCombinationsCount: Long,
    isEstimated: Boolean,
    isImpossible: Boolean,
    isVeryRestrictive: Boolean,
    isAnalyzing: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val semanticColors = LocalSemanticColors.current
    val numberFormatter = remember { NumberFormat.getIntegerInstance(Locale.forLanguageTag("pt-BR")) }
    val formattedCount = remember(possibleCombinationsCount, numberFormatter) {
        numberFormatter.format(possibleCombinationsCount)
    }

    val (statusText, statusColor, statusContainerColor) = when {
        isAnalyzing -> Triple(
            stringResource(id = R.string.possible_games_calculating),
            colors.onSurfaceVariant,
            colors.surfaceVariant
        )
        isImpossible -> Triple(
            stringResource(id = R.string.possible_games_none),
            colors.onErrorContainer,
            colors.errorContainer
        )
        possibleCombinationsCount in 1..1000 -> Triple(
            stringResource(id = R.string.possible_games_exact, formattedCount),
            semanticColors.warning,
            colors.secondaryContainer
        )
        isEstimated -> Triple(
            stringResource(id = R.string.possible_games_estimated, formattedCount),
            semanticColors.success,
            colors.tertiaryContainer
        )
        else -> Triple(
            stringResource(id = R.string.possible_games_exact, formattedCount),
            semanticColors.success,
            colors.tertiaryContainer
        )
    }

    AnimateOnEntry(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Text(
                text = stringResource(id = R.string.possible_games_title),
                style = MaterialTheme.typography.titleMedium,
                color = colors.primary,
                fontWeight = FontWeight.Bold
            )
            Surface(
                color = statusContainerColor,
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, statusColor.copy(alpha = 0.35f))
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm)
                )
            }

            if (isImpossible) {
                Surface(
                    color = colors.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = stringResource(id = R.string.possible_games_warning_impossible),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm)
                    )
                }
            } else if (isVeryRestrictive) {
                Surface(
                    color = colors.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = stringResource(id = R.string.possible_games_warning_restrictive),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm)
                    )
                }
            }
        }
    }
}

// Minimal FlowRow implementation if not available in project
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val xSpace = mainAxisSpacing.roundToPx()
        val ySpace = crossAxisSpacing.roundToPx()
        
        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0)) }
        
        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentRowWidth = 0
        
        placeables.forEach { placeable ->
            if (currentRowWidth + placeable.width > constraints.maxWidth && currentRow.isNotEmpty()) {
                rows.add(currentRow)
                currentRow = mutableListOf()
                currentRowWidth = 0
            }
            currentRow.add(placeable)
            currentRowWidth += placeable.width + xSpace
        }
        rows.add(currentRow)
        
        val height = rows.sumOf { row -> row.maxOfOrNull { it.height } ?: 0 } + (rows.size - 1).coerceAtLeast(0) * ySpace
        val width = constraints.maxWidth
        
        layout(width, height) {
            var y = 0
            rows.forEach { row ->
                var x = 0
                val rowHeight = row.maxOfOrNull { it.height } ?: 0
                row.forEach { placeable ->
                    placeable.placeRelative(x, y)
                    x += placeable.width + xSpace
                }
                y += rowHeight + ySpace
            }
        }
    }
}

fun LazyListScope.filterList(
    filterStates: List<FilterState>,
    lastDraw: Set<Int>?,
    onFilterToggle: (FilterType, Boolean) -> Unit,
    onSelectionModeChange: (FilterType, FilterSelectionMode) -> Unit,
    onSingleValueChange: (FilterType, Float) -> Unit,
    onRangeChange: (FilterType, ClosedFloatingPointRange<Float>) -> Unit,
    onInfoClick: (FilterType) -> Unit,
    modifier: Modifier = Modifier
) {
    // Validate input for duplicates
    val filterNames = filterStates.map { it.type.name }
    val duplicates = filterNames.groupBy { it }.filter { it.value.size > 1 }.keys
    if (duplicates.isNotEmpty()) {
        android.util.Log.w("FilterList", "Duplicate filter types detected: $duplicates")
    }
    
    // Use stable keys for better performance - ensure uniqueness
    val uniqueFilterStates = filterStates.distinctBy { it.type.name }
    
    items(
        items = uniqueFilterStates,
        key = { filter -> "filter_${filter.type.name}" },
        contentType = { "filter_card" }
    ) { filter ->
        // Staggered animation based on original position to maintain order
        val originalIndex = filterStates.indexOfFirst { it.type.name == filter.type.name }
        val delay = (originalIndex * 50L).coerceAtMost(500L)
        
        AnimateOnEntry(delayMillis = delay) {
            FilterRowItem(
                filterState = filter,
                lastDrawNumbers = lastDraw,
                onFilterToggle = onFilterToggle,
                onSelectionModeChange = onSelectionModeChange,
                onSingleValueChange = onSingleValueChange,
                onRangeChange = onRangeChange,
                onInfoClick = onInfoClick,
                modifier = modifier.padding(vertical = AppSpacing.xs)
            )
        }
    }
}

@Composable
private fun FilterRowItem(
    filterState: FilterState,
    lastDrawNumbers: Set<Int>?,
    onFilterToggle: (FilterType, Boolean) -> Unit,
    onSelectionModeChange: (FilterType, FilterSelectionMode) -> Unit,
    onSingleValueChange: (FilterType, Float) -> Unit,
    onRangeChange: (FilterType, ClosedFloatingPointRange<Float>) -> Unit,
    onInfoClick: (FilterType) -> Unit,
    modifier: Modifier = Modifier
) {
    // Stable lambdas for better performance
    val onToggle = remember(filterState.type) {
        { enabled: Boolean -> onFilterToggle(filterState.type, enabled) }
    }
    
    val onRangeChangeForType = remember(filterState.type) {
        { range: ClosedFloatingPointRange<Float> ->
            onRangeChange(filterState.type, range) 
        }
    }
    val onModeChangeForType = remember(filterState.type) {
        { mode: FilterSelectionMode ->
            onSelectionModeChange(filterState.type, mode)
        }
    }
    val onSingleValueChangeForType = remember(filterState.type) {
        { value: Float ->
            onSingleValueChange(filterState.type, value)
        }
    }
    
    val onInfoClickForType = remember(filterState.type) {
        { onInfoClick(filterState.type) }
    }

    Box(modifier = modifier) {
        FilterCard(
            filterState = filterState,
            onEnabledChange = onToggle,
            onSelectionModeChange = onModeChangeForType,
            onSingleValueChange = onSingleValueChangeForType,
            onRangeChange = onRangeChangeForType,
            onInfoClick = onInfoClickForType,
            lastDrawNumbers = lastDrawNumbers
        )
    }
}

@Composable
fun GenerateActionsPanel(
    generationState: GenerationUiState,
    onGenerate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isDataSyncing: Boolean = false,
    activeFiltersCount: Int = 0,
    isCombinationPossible: Boolean = true
) {
    AnimateOnEntry(
        delayMillis = AppTheme.motion.delayFiltersMs,
        modifier = modifier
    ) {
        Box {
            GenerationActionsPanel(
                generationState = generationState,
                isDataSyncing = isDataSyncing,
                activeFiltersCount = activeFiltersCount,
                isCombinationPossible = isCombinationPossible,
                onGenerate = onGenerate
            )
        }
    }
}
