package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.ui.model.icon
import com.cebolao.lotofacil.ui.model.titleRes
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.AppSize

@Composable
fun FilterCard(
    modifier: Modifier = Modifier,
    filterState: FilterState,
    onEnabledChange: (Boolean) -> Unit,
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
            .heightIn(min = 56.dp)
            .clickable(
                enabled = dataAvailable,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onEnabledChange(!filterState.isEnabled)
                }
            )
            .then(
                if (enabled) {
                    Modifier.background(
                        colors.primaryContainer.copy(alpha = 0.1f)
                    )
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
                .padding(vertical = 12.dp, horizontal = AppSpacing.lg)
        ) {
            FilterHeader(
                filterState,
                dataAvailable,
                onInfoClick,
                onToggle = {
                    // Prevent double toggle since entire card is clickable
                }
            )
            FilterContent(
                filterState = filterState,
                onRangeChange = onRangeChange,
                onRangeFinished = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) },
                enabled = enabled
            )
        }
    }
}

@Composable
private fun FilterHeader(
    filterState: FilterState,
    dataAvailable: Boolean,
    onInfoClick: () -> Unit,
    onToggle: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val filterTitle = stringResource(filterState.type.titleRes)
    val accessibilityDescription = stringResource(
        id = R.string.filter_accessibility_description,
        filterTitle,
        if (filterState.isEnabled) "ativado" else "desativado"
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
            }
        }
        
        InfoIcon(
            tooltipText = stringResource(
                id = R.string.filter_info_content_description,
                filterTitle
            ),
            onClick = onInfoClick,
            modifier = Modifier
                .size(20.dp)
                .padding(end = AppSpacing.sm)
        )

        Switch(
            checked = filterState.isEnabled,
            onCheckedChange = { onToggle() },
            enabled = dataAvailable,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.primary,
                checkedTrackColor = colors.primary.copy(alpha = 0.3f),
                uncheckedThumbColor = colors.onSurfaceVariant,
                uncheckedTrackColor = colors.surfaceVariant
            ),
            modifier = Modifier.padding(start = AppSpacing.sm)
        )
    }
}

@Composable
private fun FilterContent(
    filterState: FilterState,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onRangeFinished: () -> Unit,
    enabled: Boolean
) {
    if (enabled) {
        FilterRangeSlider(
            value = filterState.selectedRange,
            onValueChange = onRangeChange,
            onValueChangeFinished = onRangeFinished,
            valueRange = filterState.type.fullRange,
            steps = (filterState.type.fullRange.endInclusive - filterState.type.fullRange.start).toInt() - 1,
            enabled = true,
            modifier = Modifier.padding(top = AppSpacing.md)
        )
    }
}
