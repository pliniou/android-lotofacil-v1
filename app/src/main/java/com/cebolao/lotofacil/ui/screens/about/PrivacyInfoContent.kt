package com.cebolao.lotofacil.ui.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun PrivacyInfoContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
    ) {
        Text(stringResource(R.string.about_privacy_desc_header), style = MaterialTheme.typography.titleMedium)
        FormattedText(text = stringResource(R.string.about_privacy_desc_body))
        HorizontalDivider(modifier = Modifier.padding(vertical = AppSpacing.sm))
        InfoListItem(icon = Icons.Default.Shield, text = stringResource(R.string.about_privacy_item1), iconTint = MaterialTheme.colorScheme.tertiary, iconContentDescription = stringResource(R.string.icon_shield_description))
        InfoListItem(icon = Icons.Default.Shield, text = stringResource(R.string.about_privacy_item2), iconTint = MaterialTheme.colorScheme.tertiary, iconContentDescription = stringResource(R.string.icon_shield_description))
        InfoListItem(icon = Icons.Default.Shield, text = stringResource(R.string.about_privacy_item3), iconTint = MaterialTheme.colorScheme.tertiary, iconContentDescription = stringResource(R.string.icon_shield_description))
    }
}
