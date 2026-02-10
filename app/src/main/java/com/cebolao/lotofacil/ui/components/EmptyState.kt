package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.testtags.AppTestTags

@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconSize: androidx.compose.ui.unit.Dp = 64.dp
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(AppSpacing.xl)
            .testTag(AppTestTags.EmptyState),
        contentAlignment = Alignment.Center
    ) {
        AppCard(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth(),
            backgroundColor = colors.surface
        ) {
            Column(
                modifier = Modifier.padding(AppSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                if (icon != null) {
                    IconBadge(
                        icon = icon,
                        contentDescription = null,
                        size = iconSize + 8.dp,
                        iconSize = iconSize / 2,
                        tint = colors.primary
                    )
                } else {
                    // Use lazy-loaded image for better performance
                    LazyImage(
                        painterResourceId = R.drawable.ic_cebolalogo,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize + 12.dp),
                        delayMillis = 75L,
                        showPlaceholder = false
                    )
                }

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = colors.onSurface
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    messageResId: Int,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconSize: androidx.compose.ui.unit.Dp = 64.dp
) {
    EmptyState(
        message = stringResource(id = messageResId),
        modifier = modifier,
        icon = icon,
        iconSize = iconSize
    )
}
