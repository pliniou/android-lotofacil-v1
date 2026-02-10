package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun LoadingDialog(
    text: String
) {
    val colors = MaterialTheme.colorScheme
    Dialog(
        onDismissRequest = { /* O diálogo não pode ser dispensado pelo usuário */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        AppCard(
            shape = MaterialTheme.shapes.medium,
            elevation = AppCardDefaults.elevation
        ) {
            Row(
                modifier = Modifier.padding(AppSpacing.xl),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.lg)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 3.dp,
                    color = colors.primary
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onSurface
                )
            }
        }
    }
}
