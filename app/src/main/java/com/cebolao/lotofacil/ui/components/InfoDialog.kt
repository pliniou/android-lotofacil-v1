package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.testtags.AppTestTags

@Composable
fun InfoDialog(
    onDismissRequest: () -> Unit,
    dialogTitle: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Info,
    dismissButtonText: String = "",
    content: @Composable ColumnScope.() -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val colors = MaterialTheme.colorScheme
    val resolvedDismissText = dismissButtonText.ifBlank {
        stringResource(id = R.string.common_ok)
    }
    
    Dialog(
        onDismissRequest = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onDismissRequest()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = modifier
                .widthIn(max = 480.dp)
                .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xl)
                .testTag(AppTestTags.InfoDialog)
        ) {
            AppCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = AppCardDefaults.elevation
            ) {
                Column(
                    modifier = Modifier.padding(
                        top = AppSpacing.lg,
                        start = AppSpacing.lg,
                        end = AppSpacing.lg,
                        bottom = AppSpacing.md
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
                    ) {
                        IconBadge(
                            icon = icon,
                            contentDescription = stringResource(R.string.info_dialog_icon_description),
                            size = 40.dp,
                            iconSize = 20.dp
                        )
                        Text(
                            text = dialogTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.onSurface
                        )
                    }

                    Spacer(Modifier.height(AppSpacing.md))
                    HorizontalDivider(
                        color = colors.outline.copy(alpha = 0.75f),
                        thickness = 1.dp
                    )
                    Spacer(Modifier.height(AppSpacing.md))

                    Box(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                            content = content
                        )
                    }

                    Spacer(Modifier.height(AppSpacing.lg))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                onDismissRequest()
                            },
                            modifier = Modifier.testTag(AppTestTags.InfoDialogDismiss)
                        ) {
                            Text(
                                text = resolvedDismissText,
                                style = MaterialTheme.typography.labelLarge,
                                color = colors.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
