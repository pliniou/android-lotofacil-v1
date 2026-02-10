package com.cebolao.lotofacil.ui.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.iconSmall

@Composable
fun TipsInfoContent() {
    val colors = MaterialTheme.colorScheme

    Column(
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        modifier = Modifier.padding(AppSpacing.md)
    ) {
        Text(
            text = stringResource(id = R.string.tips_intro),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant
        )

        HorizontalDivider(color = colors.outline.copy(alpha = 0.15f))

        TipItem(
            title = stringResource(id = R.string.tips_sum_title),
            description = stringResource(id = R.string.tips_sum_desc)
        )
        TipItem(
            title = stringResource(id = R.string.tips_even_odd_title),
            description = stringResource(id = R.string.tips_even_odd_desc)
        )
        TipItem(
            title = stringResource(id = R.string.tips_primes_title),
            description = stringResource(id = R.string.tips_primes_desc)
        )
        TipItem(
            title = stringResource(id = R.string.tips_frame_title),
            description = stringResource(id = R.string.tips_frame_desc)
        )
        TipItem(
            title = stringResource(id = R.string.tips_repeat_title),
            description = stringResource(id = R.string.tips_repeat_desc)
        )
    }
}

@Composable
private fun TipItem(title: String, description: String) {
    val colors = MaterialTheme.colorScheme

    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(iconSmall())
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colors.primary
            )
        }
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(start = AppSpacing.xl)
        )
    }
}
