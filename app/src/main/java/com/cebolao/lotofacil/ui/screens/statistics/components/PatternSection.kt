package com.cebolao.lotofacil.ui.screens.statistics.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.outlined.Pattern
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.PatternAnalysis
import com.cebolao.lotofacil.ui.components.AppCard
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
    SectionHeader(
        title = stringResource(R.string.patterns_title),
        icon = Icons.Outlined.Pattern
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        listOf(2, 3, 4).forEach { size ->
            val label = when (size) {
                2 -> stringResource(R.string.pairs_label)
                3 -> stringResource(R.string.triplets_label)
                else -> stringResource(R.string.quads_label)
            }
            FilterChip(
                selected = selectedSize == size,
                onClick = { onSizeSelected(size) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }

    Spacer(modifier = Modifier.height(AppSpacing.sm))

    AnimatedVisibility(visible = isLoading, enter = fadeIn(), exit = fadeOut()) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AppSpacing.sm)
        )
    }

    if (errorResId != null) {
        Text(
            stringResource(errorResId),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(AppSpacing.sm)
        )
    }

    analysis?.let { patternData ->
        AppCard {
            Column(modifier = Modifier.padding(AppSpacing.md)) {
                patternData.patterns.forEachIndexed { index, (pattern, count) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppSpacing.xs),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            pattern.sorted().joinToString(" - ") { "%02d".format(it) },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "$count×",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (index < patternData.patterns.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}
