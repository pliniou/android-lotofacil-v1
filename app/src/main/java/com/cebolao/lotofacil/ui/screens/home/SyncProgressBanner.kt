package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.CardVariant
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.iconSmall

internal sealed interface SyncFeedbackState {
    data class Progress(
        val current: Int?,
        val total: Int?
    ) : SyncFeedbackState

    data class Failed(val message: String?) : SyncFeedbackState
}

@Composable
internal fun SyncProgressBanner(
    feedbackState: SyncFeedbackState
) {
    val colors = MaterialTheme.colorScheme

    val titleText = when (feedbackState) {
        is SyncFeedbackState.Progress -> stringResource(id = R.string.refresh_sync_label)
        is SyncFeedbackState.Failed -> stringResource(id = R.string.error_sync_failed)
    }

    val progress = when (feedbackState) {
        is SyncFeedbackState.Progress -> {
            val current = feedbackState.current
            val total = feedbackState.total
            if (current == null || total == null || total <= 0) {
                null
            } else {
                (current.toFloat() / total.coerceAtLeast(1)).coerceIn(0f, 1f)
            }
        }
        is SyncFeedbackState.Failed -> null
    }

    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = AppSpacing.md),
        variant = CardVariant.Surfaced,
        shape = MaterialTheme.shapes.medium,
        backgroundColor = colors.surfaceContainerHigh,
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
                        modifier = Modifier.size(iconSmall())
                    )
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurface
                    )
                }

                if (feedbackState is SyncFeedbackState.Progress && feedbackState.current != null && feedbackState.total != null) {
                    Text(
                        text = "${feedbackState.current} / ${feedbackState.total}",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                } else if (feedbackState is SyncFeedbackState.Failed) {
                    Text(
                        text = feedbackState.message ?: stringResource(id = R.string.sync_failed_message),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.error,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.syncing_data),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (feedbackState is SyncFeedbackState.Failed) {
                return@AppCard
            }

            if (progress != null) {
                LinearProgressIndicator(
                    progress = { progress },
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
