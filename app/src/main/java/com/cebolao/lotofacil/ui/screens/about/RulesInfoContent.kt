package com.cebolao.lotofacil.ui.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R

@Composable
fun RulesInfoContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.about_rules_desc_header), style = MaterialTheme.typography.titleMedium)
        InfoListItem(icon = Icons.AutoMirrored.Filled.Rule, text = stringResource(R.string.about_rules_item1), iconContentDescription = stringResource(R.string.icon_rule_description))
        InfoListItem(icon = Icons.AutoMirrored.Filled.Rule, text = stringResource(R.string.about_rules_item2), iconContentDescription = stringResource(R.string.icon_rule_description))
        InfoListItem(icon = Icons.AutoMirrored.Filled.Rule, text = stringResource(R.string.about_rules_item3), iconContentDescription = stringResource(R.string.icon_rule_description))
    }
}
