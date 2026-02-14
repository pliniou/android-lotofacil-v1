package com.cebolao.lotofacil.ui.screens.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.outlined.Pattern
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.PatternAnalysis
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.AppFilterChip
import com.cebolao.lotofacil.ui.components.SectionHeader
import com.cebolao.lotofacil.ui.components.SectionFeedbackState
import com.cebolao.lotofacil.ui.theme.AppSpacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PatternSection(
    analysis: PatternAnalysis?,
    isLoading: Boolean,
    errorResId: Int?,
    selectedSize: Int,
    onSizeSelected: (Int) -> Unit
) {
    var sortByFrequencyDesc by remember { mutableStateOf(true) }
    var currentPage by remember { mutableIntStateOf(0) }
    val pageSize = 10

    SectionHeader(
        title = stringResource(R.string.patterns_title),
        icon = Icons.Outlined.Pattern,
        titleStyle = MaterialTheme.typography.titleLarge,
        showDivider = true,
        action = {
            TextButton(onClick = { sortByFrequencyDesc = !sortByFrequencyDesc }) {
                Text(text = if (sortByFrequencyDesc) stringResource(R.string.sort_frequency_desc) else stringResource(R.string.sort_frequency_asc))
            }
        }
    )

    FlowRow(
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
        modifier = Modifier.fillMaxWidth()
    ) {
        listOf(2, 3, 4).forEach { size ->
            val label = when (size) {
                2 -> stringResource(R.string.pairs_label)
                3 -> stringResource(R.string.triplets_label)
                else -> stringResource(R.string.quads_label)
            }
            AppFilterChip(
                selected = selectedSize == size,
                onClick = { onSizeSelected(size) },
                label = label
            )
        }
    }

    Spacer(modifier = Modifier.height(AppSpacing.sm))

    SectionFeedbackState(
        isLoading = isLoading,
        errorResId = errorResId
    )

    analysis?.let { patternData ->
        val sortedPatterns = remember(patternData.patterns, sortByFrequencyDesc) {
            if (sortByFrequencyDesc) {
                patternData.patterns.sortedByDescending { it.second }
            } else {
                patternData.patterns.sortedBy { it.second }
            }
        }
        val totalPages = remember(sortedPatterns) {
            if (sortedPatterns.isEmpty()) 1 else ((sortedPatterns.size - 1) / pageSize) + 1
        }
        if (currentPage > totalPages - 1) currentPage = (totalPages - 1).coerceAtLeast(0)
        val pagedPatterns = remember(sortedPatterns, currentPage) {
            val start = currentPage * pageSize
            val end = (start + pageSize).coerceAtMost(sortedPatterns.size)
            if (start >= end) emptyList() else sortedPatterns.subList(start, end)
        }
        val averageCount = remember(sortedPatterns) {
            sortedPatterns.map { it.second }.average().toFloat()
        }

        AppCard {
            Column(modifier = Modifier.padding(AppSpacing.md)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = AppSpacing.xs),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.pattern_column_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(id = R.string.frequency_column_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                pagedPatterns.forEachIndexed { index, (pattern, count) ->
                    val isRising = count >= averageCount

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (index % 2 == 0) {
                                    MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.35f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                            .padding(vertical = AppSpacing.sm),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = pattern.sorted().joinToString(" - ") { "%02d".format(it) },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${count}x",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = if (isRising) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                contentDescription = null,
                                tint = if (isRising) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (index < pagedPatterns.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(top = AppSpacing.sm),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AppSpacing.xs),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { currentPage = (currentPage - 1).coerceAtLeast(0) },
                        enabled = currentPage > 0
                    ) {
                        Text(text = stringResource(id = R.string.previous_page))
                    }
                    Text(
                        text = stringResource(
                            id = R.string.page_indicator,
                            currentPage + 1,
                            totalPages
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(
                        onClick = { currentPage = (currentPage + 1).coerceAtMost(totalPages - 1) },
                        enabled = currentPage < totalPages - 1
                    ) {
                        Text(text = stringResource(id = R.string.next_page))
                    }
                }
            }
        }
    }
}
