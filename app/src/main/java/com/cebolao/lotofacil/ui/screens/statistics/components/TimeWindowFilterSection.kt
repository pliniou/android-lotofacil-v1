package com.cebolao.lotofacil.ui.screens.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppSpacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimeWindowFilterSection(
    selectedWindow: Int,
    windows: List<Int>,
    totalDraws: Int,
    onWindowSelected: (Int) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.time_window_filter_label),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            windows.forEach { window ->
                val label = if (window == 0) {
                    stringResource(R.string.all_draws_label)
                } else {
                    stringResource(R.string.last_n_draws_label, window)
                }
                FilterChip(
                    selected = selectedWindow == window,
                    onClick = { onWindowSelected(window) },
                    label = { Text(label, style = MaterialTheme.typography.labelLarge) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
        if (totalDraws > 0) {
            Text(
                text = pluralStringResource(R.plurals.total_draws_available_label, totalDraws, totalDraws),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = AppSpacing.xs)
            )
        }
    }
}
