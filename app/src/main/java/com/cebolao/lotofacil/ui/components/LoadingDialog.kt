package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun LoadingDialog(
    text: String,
    onCancel: (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme

    Dialog(
        onDismissRequest = { onCancel?.invoke() },
        properties = DialogProperties(
            dismissOnBackPress = onCancel != null,
            dismissOnClickOutside = false
        )
    ) {
        AppCard(
            shape = MaterialTheme.shapes.medium,
            elevation = AppCardDefaults.elevation
        ) {
            Column(modifier = Modifier.padding(AppSpacing.xl), verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
                Row(
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

                if (onCancel != null) {
                    Row(modifier = Modifier.align(Alignment.End)) {
                        TextButton(onClick = onCancel) {
                            Text(text = stringResource(id = R.string.cancel_button))
                        }
                    }
                }
            }
        }
    }
}
