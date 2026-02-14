package com.cebolao.lotofacil.ui.screens.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AppFilterChip
import com.cebolao.lotofacil.ui.components.InfoIcon
import com.cebolao.lotofacil.ui.testtags.AppTestTags
import com.cebolao.lotofacil.ui.theme.AppSpacing


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimeWindowFilterSection(
    selectedWindow: Int,
    windows: List<Int>,
    onWindowSelected: (Int) -> Unit
) {
    var showHelp by remember { mutableStateOf(false) }

    if (showHelp) {
        AlertDialog(
            onDismissRequest = { showHelp = false },
            title = { Text(text = stringResource(id = R.string.common_information_title)) },
            text = { Text(text = stringResource(id = R.string.tooltip_period)) },
            confirmButton = {
                TextButton(onClick = { showHelp = false }) {
                    Text(text = stringResource(id = R.string.common_ok))
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            Text(
                text = stringResource(R.string.time_window_filter_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            InfoIcon(
                tooltipText = stringResource(id = R.string.tooltip_period),
                onClick = { showHelp = true }
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
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

                AppFilterChip(
                    modifier = finalModifier,
                    selected = selectedWindow == window,
                    onClick = { onWindowSelected(window) },
                    label = label
                )
            }
        }
    }
}
