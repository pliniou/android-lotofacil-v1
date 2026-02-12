package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.cebolao.lotofacil.ui.testtags.AppTestTags
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = modifier
                .widthIn(max = 520.dp)
                .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xl)
                .testTag(AppTestTags.ConfirmationDialog)
        ) {
            AppCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = colors.surface
            ) {
                Column(
                    modifier = Modifier.padding(AppSpacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = colors.onSurface
                    )

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = colors.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.sm))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md, Alignment.End)
                    ) {
                        if (dismissText.isNotBlank()) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.testTag(AppTestTags.ConfirmationDialogDismiss)
                            ) {
                                Text(text = dismissText)
                            }
                        }

                        Button(
                            onClick = onConfirm,
                            modifier = Modifier.testTag(AppTestTags.ConfirmationDialogConfirm)
                        ) {
                            Text(text = confirmText)
                        }
                    }
                }
            }
        }
    }
}
