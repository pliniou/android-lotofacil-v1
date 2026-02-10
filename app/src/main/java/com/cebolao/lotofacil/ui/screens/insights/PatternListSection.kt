package com.cebolao.lotofacil.ui.screens.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.usecase.PatternAnalysis
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.NumberBall
import com.cebolao.lotofacil.ui.theme.AppSize
import com.cebolao.lotofacil.ui.theme.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternListSection(
    analysis: PatternAnalysis?,
    selectedSize: Int,
    onSizeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showCard: Boolean = true,
    showHeader: Boolean = true
) {
    val content: @Composable () -> Unit = {
        Column(modifier = Modifier.padding(if (showCard) AppSpacing.md else 0.dp)) {
            if (showHeader) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.patterns_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    PatternInfoTooltip()
                }
                
                Text(
                    text = stringResource(id = R.string.patterns_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = AppSpacing.md)
                )
            }

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = if (showHeader) AppSpacing.sm else 0.dp)
            ) {
                val sizes = listOf(2, 3, 4)
                sizes.forEachIndexed { index, size ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = sizes.size),
                        onClick = { onSizeSelected(size) },
                        selected = selectedSize == size
                    ) {
                        Text(stringResource(id = when(size) {
                            2 -> R.string.pattern_size_pairs
                            3 -> R.string.pattern_size_triplets
                            else -> R.string.pattern_size_quadruplets
                        }))
                    }
                }
            }

            if (analysis != null) {
                Column(
                    modifier = Modifier.padding(top = AppSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
                ) {
                    analysis.patterns.forEach { (combination, frequency) ->
                        PatternItem(combination = combination, frequency = frequency)
                    }
                }
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
private fun PatternItem(combination: Set<Int>, frequency: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            combination.sorted().forEach { number ->
                NumberBall(number = number, size = AppSize.numberBallTiny)
            }
        }
        
        FilterChip(
            selected = true,
            onClick = { },
            label = { 
                Text(
                    text = "$frequency ${stringResource(id = R.string.frequency_suffix)}",
                    style = MaterialTheme.typography.labelMedium
                ) 
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternInfoTooltip() {
    val tooltipState = rememberTooltipState()
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(stringResource(id = R.string.pattern_info_tooltip))
            }
        },
        state = tooltipState
    ) {
        IconButton(onClick = { /* Tooltip shown on long press */ }) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(id = R.string.cd_info),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
