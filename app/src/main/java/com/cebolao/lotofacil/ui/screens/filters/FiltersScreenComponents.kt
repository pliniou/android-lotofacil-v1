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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.FilterPreset
import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.ui.components.*
import com.cebolao.lotofacil.ui.components.FilterCard
import com.cebolao.lotofacil.ui.components.GenerationActionsPanel
import com.cebolao.lotofacil.ui.model.titleRes
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppTheme
import com.cebolao.lotofacil.viewmodels.GenerationUiState

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
    if (activeFilters.isEmpty()) return

    AnimateOnEntry(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Text(
                text = "${stringResource(id = R.string.filters_analysis_title)} (${activeFilters.size})",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisSpacing = AppSpacing.xs,
                crossAxisSpacing = AppSpacing.xs
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
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = null
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
    onRangeChange: (FilterType, ClosedFloatingPointRange<Float>) -> Unit,
    onInfoClick: (FilterType) -> Unit,
    modifier: Modifier = Modifier
) {
    // Use stable keys for better performance
    items(
        count = filterStates.size,
        key = { index -> filterStates[index].type.name },
        contentType = { "filter_card" }
    ) { index ->
        val filter = filterStates[index]
        // Staggered animation
        val delay = (index * 50L).coerceAtMost(500L)
        
        AnimateOnEntry(delayMillis = delay) {
            FilterRowItem(
                filterState = filter,
                lastDrawNumbers = lastDraw,
                onFilterToggle = onFilterToggle,
                onRangeChange = onRangeChange,
                onInfoClick = onInfoClick,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun FilterRowItem(
    filterState: FilterState,
    lastDrawNumbers: Set<Int>?,
    onFilterToggle: (FilterType, Boolean) -> Unit,
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
    
    val onInfoClickForType = remember(filterState.type) {
        { onInfoClick(filterState.type) }
    }

    Box(modifier = modifier) {
        FilterCard(
            filterState = filterState,
            onEnabledChange = onToggle,
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
    modifier: Modifier = Modifier
) {
    AnimateOnEntry(
        delayMillis = AppTheme.motion.delayFiltersMs,
        modifier = modifier
    ) {
        Box {
            GenerationActionsPanel(
                generationState = generationState,
                onGenerate = onGenerate
            )
        }
    }
}
