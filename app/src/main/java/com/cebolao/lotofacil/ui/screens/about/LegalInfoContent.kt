package com.cebolao.lotofacil.ui.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R

@Composable
fun LegalInfoContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.about_legal_desc_header), style = MaterialTheme.typography.titleMedium)
        InfoListItem(icon = Icons.Default.Gavel, text = stringResource(R.string.about_legal_item1), iconContentDescription = stringResource(R.string.icon_gavel_description))
        InfoListItem(icon = Icons.Default.Gavel, text = stringResource(R.string.about_legal_item2), iconContentDescription = stringResource(R.string.icon_gavel_description))
        InfoListItem(icon = Icons.Default.Gavel, text = stringResource(R.string.about_legal_item3), iconContentDescription = stringResource(R.string.icon_gavel_description))
        Spacer(Modifier.height(8.dp))
        FormattedText(text = stringResource(R.string.about_legal_footer), style = SpanStyle(fontWeight = FontWeight.Bold))
    }
}
