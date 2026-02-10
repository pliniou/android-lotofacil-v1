package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.RestrictivenessCategory
import com.cebolao.lotofacil.ui.model.*
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppShapes
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppTheme

@Composable
fun FilterStatsPanel(
    activeFilters: List<FilterState>,
    successProbability: Float, // ATUALIZADO: Recebe a probabilidade de sucesso
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        elevation = AppCardDefaults.elevation
    ) {
        Column(
            modifier = Modifier.padding(AppCardDefaults.defaultPadding),
            verticalArrangement = Arrangement.spacedBy(AppCardDefaults.contentSpacing)
        ) {
            Text(stringResource(id = R.string.filters_analysis_title), style = MaterialTheme.typography.titleMedium)

            // ATUALIZAÇÃO: Usa a nova métrica de probabilidade
            FilterRestrictiveness(probability = successProbability)

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            FilterStatistics(activeFilters)
        }
    }
}

@Composable
private fun FilterRestrictiveness(probability: Float) {
    val animatedProbability by animateFloatAsState(
        targetValue = probability,
        animationSpec = tween(AppTheme.motion.durationProbabilityMs),
        label = "probabilityAnimation"
    )

    val (progressColor, textColor) = when {
        animatedProbability < 0.1f -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.error
        animatedProbability < 0.4f -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.primary
    }

    val animatedProgressColor by animateColorAsState(targetValue = progressColor, label = "progressColor")
    val animatedTextColor by animateColorAsState(targetValue = textColor, label = "textColor")

    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(id = R.string.success_probability_title), style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "${(animatedProbability * 100).toInt()}%",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = animatedTextColor
            )
        }
        LinearProgressIndicator(
            progress = { animatedProbability },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(AppShapes.sm),
            color = animatedProgressColor,
            trackColor = animatedProgressColor.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun FilterStatistics(activeFilters: List<FilterState>) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        if (activeFilters.isEmpty()) {
            Text(
                stringResource(id = R.string.no_active_filters),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            for (filter in activeFilters) {
                FilterStatRow(filter)
            }
        }
    }
}

@Composable
private fun FilterStatRow(filter: FilterState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(filter.type.titleRes), style = MaterialTheme.typography.bodyMedium)
        RestrictivenessChip(filter.restrictivenessCategory)
    }
}

@Composable
private fun RestrictivenessChip(category: RestrictivenessCategory) {
    val (color, text) = when (category) {
        RestrictivenessCategory.VERY_TIGHT -> MaterialTheme.colorScheme.error to stringResource(id = R.string.restrictiveness_very_tight)
        RestrictivenessCategory.TIGHT -> MaterialTheme.colorScheme.error.copy(alpha = 0.8f) to stringResource(id = R.string.restrictiveness_tight)
        RestrictivenessCategory.MODERATE -> MaterialTheme.colorScheme.tertiary to stringResource(id = R.string.restrictiveness_moderate)
        RestrictivenessCategory.LOOSE -> MaterialTheme.colorScheme.primary to stringResource(id = R.string.restrictiveness_loose)
        RestrictivenessCategory.VERY_LOOSE -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) to stringResource(id = R.string.restrictiveness_very_loose)
        RestrictivenessCategory.DISABLED -> MaterialTheme.colorScheme.outline to stringResource(id = R.string.restrictiveness_disabled)
    }
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs)
        )
    }
}
