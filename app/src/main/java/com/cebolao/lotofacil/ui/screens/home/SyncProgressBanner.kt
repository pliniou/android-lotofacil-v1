package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.CardVariant
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.iconSmall
import com.cebolao.lotofacil.viewmodels.UpdateState

@Composable
internal fun SyncProgressBanner(
    updateState: UpdateState,
    onCancel: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (updateState !is UpdateState.Loading) return

    val colors = MaterialTheme.colorScheme

    val infiniteTransition = rememberInfiniteTransition(label = "sync_icon_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    val progressFraction = remember(updateState.current, updateState.total) {
        val current = updateState.current
        val total = updateState.total
        if (current != null && total != null && total > 0) {
            (current.toFloat() / total).coerceIn(0f, 1f)
        } else {
            null
        }
    }

    val progressLabel = when {
        updateState.current != null && updateState.total != null && updateState.total > 0 -> {
            stringResource(
                id = R.string.sync_stats_progress,
                updateState.current,
                updateState.total
            )
        }

        !updateState.message.isNullOrBlank() -> updateState.message

        else -> stringResource(id = R.string.sync_stats_message)
    }

    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = AppSpacing.md),
        variant = CardVariant.Surfaced,
        shape = MaterialTheme.shapes.medium,
        containerColor = colors.surfaceContainerHigh,
        border = BorderStroke(
            width = 1.dp,
            color = colors.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier
                            .size(iconSmall())
                            .graphicsLayer { rotationZ = rotation }
                    )
                    Text(
                        text = stringResource(id = R.string.refresh_sync_label),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurface
                    )
                }

                if (updateState.isCancellable) {
                    TextButton(onClick = onCancel) {
                        Text(stringResource(id = R.string.cancel_button))
                    }
                }
            }

            Text(
                text = progressLabel,
                style = MaterialTheme.typography.labelMedium,
                color = colors.primary
            )

            if (progressFraction != null) {
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small),
                    color = colors.primary,
                    trackColor = colors.primaryContainer
                )
            } else {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small),
                    color = colors.primary,
                    trackColor = colors.primaryContainer
                )
            }
        }
    }
}
