package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.stringResource
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.ui.model.icon
import com.cebolao.lotofacil.ui.model.titleRes
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.AppSize
import com.cebolao.lotofacil.ui.theme.AppTheme

@Composable
fun FilterCard(
    modifier: Modifier = Modifier,
    filterState: FilterState,
    onEnabledChange: (Boolean) -> Unit,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onInfoClick: () -> Unit,
    lastDrawNumbers: Set<Int>? = null
) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    val requiresData = filterState.type == FilterType.REPETIDAS_CONCURSO_ANTERIOR
    val dataAvailable = !requiresData || lastDrawNumbers != null
    val enabled = filterState.isEnabled && dataAvailable

    val elevation by animateDpAsState(
        if (enabled) AppCardDefaults.pinnedElevation else AppElevation.xs, 
        tween(AppTheme.motion.durationElevationMs), 
        label = "elevation"
    )

    AppCard(
        modifier = modifier.fillMaxWidth(),
        elevation = elevation
    ) {
        Column(modifier = Modifier.padding(AppSpacing.lg)) {
            FilterHeader(
                filterState,
                dataAvailable,
                onInfoClick,
                onToggle = {
                    // REFINEMENT: Added haptic feedback for important actions.
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onEnabledChange(!filterState.isEnabled)
                }
            )
            AnimatedVisibility(
                visible = enabled,
                enter = expandVertically(animationSpec = tween(AppTheme.motion.durationFilterPanelMs)) + fadeIn(animationSpec = tween(AppTheme.motion.durationFilterPanelMs)),
                exit = shrinkVertically(animationSpec = tween(AppTheme.motion.durationFilterPanelMs)) + fadeOut(animationSpec = tween(AppTheme.motion.durationFilterPanelMs))
            ) {
                FilterContent(
                    filterState,
                    onRangeChange,
                    onRangeFinished = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
                )
            }
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Icon(
            imageVector = filterState.type.icon,
            contentDescription = stringResource(
                id = R.string.filter_icon_content_description,
                stringResource(filterState.type.titleRes)
            ),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(AppSize.iconMedium)
        )
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(filterState.type.titleRes), 
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1
            )
            if (!dataAvailable) {
                Text(
                    text = stringResource(id = R.string.data_unavailable),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    maxLines = 1
                )
            }
        }
        
        IconButton(
            onClick = onInfoClick,
            modifier = Modifier.size(AppSize.touchTargetMinimum)
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(
                    id = R.string.filter_info_content_description,
                    stringResource(filterState.type.titleRes)
                ),
                modifier = Modifier.size(AppSize.iconSmall)
            )
        }

        Switch(
            checked = filterState.isEnabled,
            onCheckedChange = { onToggle() },
            enabled = dataAvailable
        )
    }
}

@Composable
private fun FilterContent(
    filterState: FilterState,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onRangeFinished: () -> Unit
) {
    FilterRangeSlider(
        value = filterState.selectedRange,
        onValueChange = onRangeChange,
        onValueChangeFinished = onRangeFinished,
        valueRange = filterState.type.fullRange,
        steps = (filterState.type.fullRange.endInclusive - filterState.type.fullRange.start).toInt() - 1,
        enabled = true,
        modifier = Modifier.padding(top = AppCardDefaults.contentSpacing)
    )
}
