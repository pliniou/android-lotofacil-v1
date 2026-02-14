package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.testtags.AppTestTags
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    primaryActionText: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    secondaryActionText: String? = null,
    onSecondaryAction: (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = AppSpacing.lg)
            .testTag(AppTestTags.EmptyState),
        contentAlignment = Alignment.Center
    ) {
        AppCard(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth(),
            containerColor = colors.surfaceContainerLow
        ) {
            Column(
                modifier = Modifier.padding(horizontal = AppSpacing.xl, vertical = AppSpacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                if (icon != null) {
                    IconBadge(
                        icon = icon,
                        contentDescription = null,
                        size = 72.dp,
                        iconSize = 32.dp,
                        tint = colors.primary
                    )
                } else {
                    LazyImage(
                        painterResourceId = R.drawable.ic_cebolalogo,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        delayMillis = 75L,
                        showPlaceholder = false
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = colors.onSurface
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = colors.onSurfaceVariant
                )

                if (!primaryActionText.isNullOrBlank() && onPrimaryAction != null) {
                    AppButton(
                        text = primaryActionText,
                        onClick = onPrimaryAction,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (!secondaryActionText.isNullOrBlank() && onSecondaryAction != null) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        OutlinedButton(onClick = onSecondaryAction) {
                            Text(text = secondaryActionText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    EmptyState(
        title = message,
        description = "",
        modifier = modifier,
        icon = icon,
        primaryActionText = actionLabel,
        onPrimaryAction = onAction
    )
}

@Composable
fun EmptyState(
    messageResId: Int,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    actionLabelResId: Int? = null,
    onAction: (() -> Unit)? = null
) {
    EmptyState(
        title = stringResource(id = messageResId),
        description = "",
        modifier = modifier,
        icon = icon,
        primaryActionText = actionLabelResId?.let { stringResource(id = it) },
        onPrimaryAction = onAction
    )
}
