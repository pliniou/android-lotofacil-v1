package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.FilterSelectionMode
import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.ui.model.icon
import com.cebolao.lotofacil.ui.model.titleRes
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.AppSize
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun FilterCard(
    modifier: Modifier = Modifier,
    filterState: FilterState,
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {},
    onEnabledChange: (Boolean) -> Unit,
    onSelectionModeChange: (FilterSelectionMode) -> Unit,
    onSingleValueChange: (Float) -> Unit,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onInfoClick: () -> Unit,
    lastDrawNumbers: Set<Int>? = null
) {
    val haptic = LocalHapticFeedback.current
    val requiresData = filterState.type == FilterType.REPETIDAS_CONCURSO_ANTERIOR
    val dataAvailable = !requiresData || lastDrawNumbers != null
    val enabled = filterState.isEnabled && dataAvailable
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AppSize.touchTargetMinimum)
            .clickable(
                enabled = dataAvailable,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onExpandedChange(!expanded)
                }
            )
            .then(
                if (enabled) {
                    Modifier.background(colors.primaryContainer.copy(alpha = 0.15f))
                } else {
                    Modifier
                }
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (enabled) AppElevation.sm else AppElevation.xs
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(vertical = AppSpacing.md, horizontal = AppSpacing.lg)
        ) {
            FilterHeader(
                filterState = filterState,
                dataAvailable = dataAvailable,
                expanded = expanded,
                onInfoClick = onInfoClick,
                onExpandedChange = onExpandedChange,
                onEnabledChange = onEnabledChange
            )

            if (expanded) {
                FilterContent(
                    filterState = filterState,
                    onSelectionModeChange = onSelectionModeChange,
                    onSingleValueChange = onSingleValueChange,
                    onRangeChange = onRangeChange,
                    onRangeFinished = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) },
                    enabled = enabled
                )
            }
        }
    }
}

@Composable
private fun FilterHeader(
    filterState: FilterState,
    dataAvailable: Boolean,
    expanded: Boolean,
    onInfoClick: () -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onEnabledChange: (Boolean) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val filterTitle = stringResource(filterState.type.titleRes)
    val accessibilityDescription = stringResource(
        id = R.string.filter_accessibility_description,
        filterTitle,
        if (filterState.isEnabled) {
            stringResource(id = R.string.filter_state_enabled)
        } else {
            stringResource(id = R.string.filter_state_disabled)
        }
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = accessibilityDescription
                role = Role.Switch
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Icon(
            imageVector = filterState.type.icon,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(AppSize.iconMedium)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = filterTitle,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                color = colors.onSurface
            )

            if (!dataAvailable) {
                Text(
                    text = stringResource(id = R.string.data_unavailable),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.error,
                    maxLines = 1
                )
            } else {
                Text(
                    text = filterSummary(filterState),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }

        InfoIcon(
            tooltipText = stringResource(
                id = R.string.filter_info_content_description,
                filterTitle
            ),
            onClick = onInfoClick
        )

        Switch(
            checked = filterState.isEnabled,
            onCheckedChange = onEnabledChange,
            enabled = dataAvailable,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.primary,
                checkedTrackColor = colors.primary.copy(alpha = 0.3f),
                uncheckedThumbColor = colors.onSurfaceVariant,
                uncheckedTrackColor = colors.surfaceVariant
            )
        )

        IconButton(
            onClick = { onExpandedChange(!expanded) },
            modifier = Modifier.size(AppSize.touchTargetMinimum)
        ) {
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (expanded) {
                    stringResource(id = R.string.close_button)
                } else {
                    stringResource(id = R.string.common_view_all)
                },
                tint = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FilterContent(
    filterState: FilterState,
    onSelectionModeChange: (FilterSelectionMode) -> Unit,
    onSingleValueChange: (Float) -> Unit,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onRangeFinished: () -> Unit,
    enabled: Boolean
) {
    if (enabled) {
        Column(
            modifier = Modifier.padding(top = AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            SelectionModeToggle(
                mode = filterState.selectionMode,
                onModeChange = onSelectionModeChange
            )

            val sliderSteps =
                (filterState.type.fullRange.endInclusive - filterState.type.fullRange.start).toInt() - 1
            when (filterState.selectionMode) {
                FilterSelectionMode.SINGLE -> {
                    FilterSingleSlider(
                        value = filterState.singleValue,
                        onValueChange = onSingleValueChange,
                        onValueChangeFinished = onRangeFinished,
                        valueRange = filterState.type.fullRange,
                        steps = sliderSteps,
                        enabled = true
                    )
                }

                FilterSelectionMode.RANGE -> {
                    FilterRangeSlider(
                        value = filterState.selectedRange,
                        onValueChange = onRangeChange,
                        onValueChangeFinished = onRangeFinished,
                        valueRange = filterState.type.fullRange,
                        steps = sliderSteps,
                        enabled = true
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectionModeToggle(
    mode: FilterSelectionMode,
    onModeChange: (FilterSelectionMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.filter_mode_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        FilterChip(
            modifier = Modifier.defaultMinSize(minHeight = AppSize.touchTargetMinimum),
            selected = mode == FilterSelectionMode.SINGLE,
            onClick = { onModeChange(FilterSelectionMode.SINGLE) },
            label = { Text(text = stringResource(id = R.string.filter_mode_mono)) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        FilterChip(
            modifier = Modifier.defaultMinSize(minHeight = AppSize.touchTargetMinimum),
            selected = mode == FilterSelectionMode.RANGE,
            onClick = { onModeChange(FilterSelectionMode.RANGE) },
            label = { Text(text = stringResource(id = R.string.filter_mode_stereo)) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}

@Composable
private fun filterSummary(filterState: FilterState): String {
    return when (filterState.selectionMode) {
        FilterSelectionMode.SINGLE -> stringResource(
            id = R.string.filter_summary_value,
            filterState.singleValue.toInt()
        )
        FilterSelectionMode.RANGE -> {
            val start = filterState.selectedRange.start.toInt()
            val end = filterState.selectedRange.endInclusive.toInt()
            stringResource(id = R.string.filter_summary_range, start, end)
        }
    }
}
