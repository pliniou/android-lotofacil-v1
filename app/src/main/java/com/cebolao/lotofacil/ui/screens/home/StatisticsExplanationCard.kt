package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.cards.StatCard
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.iconMedium

@Composable
fun StatisticsExplanationCard() {
    StatCard(
        title = stringResource(id = R.string.stats_guide_title),
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
                modifier = Modifier.padding(AppSpacing.md)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(iconMedium())
                    )
                    Text(
                        text = stringResource(id = R.string.how_filters_work),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                
                FilterExplanationItem(
                    title = stringResource(id = R.string.filter_soma_dezenas_title),
                    description = stringResource(id = R.string.stats_explanation_sum)
                )
                
                FilterExplanationItem(
                    title = stringResource(id = R.string.filter_pares_title),
                    description = stringResource(id = R.string.stats_explanation_even_odd)
                )

                FilterExplanationItem(
                    title = stringResource(id = R.string.filter_primos_title),
                    description = stringResource(id = R.string.stats_explanation_primes)
                )

                FilterExplanationItem(
                    title = stringResource(id = R.string.filter_moldura_title),
                    description = stringResource(id = R.string.stats_explanation_frame_portrait)
                )

                FilterExplanationItem(
                    title = stringResource(id = R.string.stats_explanation_frequency_title),
                    description = stringResource(id = R.string.stats_explanation_frequency)
                )
            }
        }
    )
}

@Composable
private fun FilterExplanationItem(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
