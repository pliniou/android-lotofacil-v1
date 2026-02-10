package com.cebolao.lotofacil.ui.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun ProbabilitiesTable(modifier: Modifier = Modifier) {
    val data = listOf(
        stringResource(R.string.probability_15_hits) to stringResource(R.string.probability_15_hits_value),
        stringResource(R.string.probability_14_hits) to stringResource(R.string.probability_14_hits_value),
        stringResource(R.string.probability_13_hits) to stringResource(R.string.probability_13_hits_value),
        stringResource(R.string.probability_12_hits) to stringResource(R.string.probability_12_hits_value),
        stringResource(R.string.probability_11_hits) to stringResource(R.string.probability_11_hits_value)
    )
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TableHeader()
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        data.forEachIndexed { idx, (prize, prob) ->
            TableRow(prize, prob, isHighlighted = idx == 0)
            if (idx < data.lastIndex) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            }
        }
    }
}

@Composable
private fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.md),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.probability_table_header_prize), 
            style = MaterialTheme.typography.titleSmall, 
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.probability_table_header_probability), 
            style = MaterialTheme.typography.titleSmall, 
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TableRow(prize: String, probability: String, isHighlighted: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            prize,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isHighlighted) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isHighlighted) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
        Text(
            probability,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
