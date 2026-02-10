package com.cebolao.lotofacil.ui.screens.filters

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.FilterCard
import com.cebolao.lotofacil.ui.components.FilterStatsPanel
import com.cebolao.lotofacil.ui.components.GenerationActionsPanel
import com.cebolao.lotofacil.ui.components.StandardScreenHeader
import com.cebolao.lotofacil.ui.theme.AppTheme
import com.cebolao.lotofacil.viewmodels.GenerationUiState

@Composable
fun FiltersHeader(
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    
    StandardScreenHeader(
        modifier = modifier,
        title = stringResource(id = R.string.filters_header_title),
        subtitle = stringResource(id = R.string.filters_header_subtitle),
        icon = Icons.Default.FilterAlt,
        actions = {
            IconButton(onClick = {
                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                onResetFilters()
            }) {
                Icon(
                    Icons.Default.RestartAlt,
                    contentDescription = stringResource(id = R.string.reset_filters),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Composable
fun ActiveFiltersPanel(
    activeFilters: List<FilterState>,
    successProbability: Float,
    modifier: Modifier = Modifier
) {
    AnimateOnEntry(
        modifier = modifier
    ) {
        FilterStatsPanel(
            activeFilters = activeFilters,
            successProbability = successProbability
        )
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
