package com.cebolao.lotofacil.ui.screens.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.usecase.TrendAnalysis
import com.cebolao.lotofacil.domain.usecase.TrendType
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.TrendChart
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun TrendSection(
    analysis: TrendAnalysis?,
    selectedType: TrendType,
    selectedWindow: Int,
    onTypeSelected: (TrendType) -> Unit,
    onWindowSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showCard: Boolean = true,
    showHeader: Boolean = true
) {
    val content: @Composable () -> Unit = {
        Column(modifier = Modifier.padding(if (showCard) AppSpacing.md else 0.dp)) {
            if (showHeader) {
                Text(
                    text = stringResource(id = R.string.trends_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.trends_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Trend Type Selector
            TrendTypeSelector(selectedType, onTypeSelected)

            // Trend Window Selector
            TrendWindowSelector(selectedWindow, onWindowSelected)

            if (analysis != null) {
                TrendChart(
                    data = analysis.timeline,
                    modifier = Modifier.padding(top = AppSpacing.lg)
                )
                
                Text(
                    text = stringResource(id = R.string.trend_avg_label, analysis.averageValue),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = AppSpacing.sm)
                )
            }
        }
    }

    if (showCard) {
        AppCard(
            modifier = modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colorScheme.surface
        ) {
            content()
        }
    } else {
        Column(modifier = modifier) { content() }
    }
}

@Composable
private fun TrendTypeSelector(selected: TrendType, onSelect: (TrendType) -> Unit) {
    val types = TrendType.entries
    LazyRow(
        contentPadding = PaddingValues(vertical = AppSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        items(types, key = { it.name }) { type ->
            FilterChip(
                onClick = { onSelect(type) },
                label = { 
                    Text(stringResource(id = when(type) {
                        TrendType.SUM -> R.string.trend_type_sum
                        TrendType.EVENS -> R.string.trend_type_evens
                        TrendType.PRIMES -> R.string.trend_type_primes
                        TrendType.FRAME -> R.string.trend_type_frame
                        TrendType.PORTRAIT -> R.string.portrait_label
                        TrendType.FIBONACCI -> R.string.fibonacci_label
                        TrendType.MULTIPLES_OF_3 -> R.string.multiples_of_3_label
                    }))
                },
                selected = selected == type,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = if (selected == type) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@Composable
private fun TrendWindowSelector(selected: Int, onSelect: (Int) -> Unit) {
    val windows = listOf(20, 50, 100, 200)
    LazyRow(
        contentPadding = PaddingValues(vertical = AppSpacing.sm),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        items(windows, key = { it }) { window ->
            FilterChip(
                onClick = { onSelect(window) },
                label = {
                    Text(
                        pluralStringResource(
                            id = R.plurals.trend_window_label,
                            count = window,
                            window
                        )
                    )
                },
                selected = selected == window,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = if (selected == window) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}
