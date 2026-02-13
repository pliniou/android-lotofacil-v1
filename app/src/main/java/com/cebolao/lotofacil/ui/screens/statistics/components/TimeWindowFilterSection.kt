package com.cebolao.lotofacil.ui.screens.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.testtags.AppTestTags
import com.cebolao.lotofacil.ui.theme.AppSpacing


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimeWindowFilterSection(
    selectedWindow: Int,
    windows: List<Int>,
    onWindowSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.time_window_filter_label),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            modifier = Modifier.fillMaxWidth()
        ) {
            windows.forEach { window ->
                val label = if (window == 0) {
                    stringResource(R.string.all_draws_label)
                } else {
                    stringResource(R.string.last_n_draws_label, window)
                }
                val baseModifier = Modifier.padding(top = AppSpacing.xs)
                val finalModifier = if (window == 0) {
                     baseModifier.testTag(AppTestTags.InsightsGaussianToggle)
                } else {
                     baseModifier
                }
                
                FilterChip(
                    modifier = finalModifier,
                    selected = selectedWindow == window,
                    onClick = { onWindowSelected(window) },
                    label = { Text(label, style = MaterialTheme.typography.labelLarge) }
                )
        }
        }
}
}
