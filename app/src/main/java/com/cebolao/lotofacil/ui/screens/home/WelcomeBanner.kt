package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppTheme
import com.cebolao.lotofacil.ui.theme.iconMedium
import com.cebolao.lotofacil.ui.theme.iconSmall
import com.cebolao.lotofacil.viewmodels.DataLoadSource

@Composable
fun WelcomeBanner(
    modifier: Modifier = Modifier,
    lastUpdateTime: String? = null,
    nextDrawDate: String? = null,
    nextDrawContest: Int? = null,
    isTodayDrawDay: Boolean = false,
    historySource: DataLoadSource = DataLoadSource.CACHE,
    statisticsSource: DataLoadSource = DataLoadSource.CACHE,
    isShowingStaleData: Boolean = false,
    onExploreFilters: () -> Unit = {},
    onOpenChecker: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    AnimateOnEntry(
        delayMillis = AppTheme.motion.delayEntryMs,
        modifier = modifier.fillMaxWidth()
    ) {
        AppCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = colors.secondaryContainer
        ) {
            Column(
                modifier = Modifier.padding(AppSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Default.AppRegistration,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(iconMedium())
                    )
                }

                Text(
                    text = stringResource(id = R.string.welcome_banner_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(id = R.string.welcome_banner_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )

                DrawScheduleInfo(
                    nextDrawDate = nextDrawDate,
                    nextDrawContest = nextDrawContest,
                    isTodayDrawDay = isTodayDrawDay
                )

                lastUpdateTime?.let { time ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = colors.outline,
                            modifier = Modifier.size(iconSmall())
                        )
                        Text(
                            text = stringResource(id = R.string.last_update_status, time),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.outline
                        )
                    }
                }

                CacheSourceInfo(
                    historySource = historySource,
                    statisticsSource = statisticsSource,
                    isShowingStaleData = isShowingStaleData
                )

                Row(
                    modifier = Modifier.padding(top = AppSpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.lg)
                ) {
                    QuickActionChip(
                        text = stringResource(id = R.string.welcome_tip_filters),
                        icon = Icons.Default.Tune,
                        onClick = onExploreFilters,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionChip(
                        text = stringResource(id = R.string.welcome_tip_checker),
                        icon = Icons.Default.CheckCircle,
                        onClick = onOpenChecker,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CacheSourceInfo(
    historySource: DataLoadSource,
    statisticsSource: DataLoadSource,
    isShowingStaleData: Boolean
) {
    val colors = MaterialTheme.colorScheme
    val historyLabel = when (historySource) {
        DataLoadSource.NETWORK -> stringResource(R.string.home_source_history_network)
        DataLoadSource.CACHE -> stringResource(R.string.home_source_history_cache)
        DataLoadSource.COMPUTED -> stringResource(R.string.home_source_history_cache)
    }
    val statsLabel = when (statisticsSource) {
        DataLoadSource.CACHE -> stringResource(R.string.home_source_stats_cache)
        DataLoadSource.NETWORK -> stringResource(R.string.home_source_stats_network)
        DataLoadSource.COMPUTED -> stringResource(R.string.home_source_stats_computed)
    }
    val combined = stringResource(R.string.home_source_status_format, historyLabel, statsLabel)

    Text(
        text = combined,
        style = MaterialTheme.typography.labelSmall,
        color = colors.onSurfaceVariant
    )
    if (isShowingStaleData) {
        Text(
            text = stringResource(id = R.string.home_stale_data_warning),
            style = MaterialTheme.typography.labelSmall,
            color = colors.error,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun DrawScheduleInfo(
    nextDrawDate: String?,
    nextDrawContest: Int?,
    isTodayDrawDay: Boolean
) {
    val colors = MaterialTheme.colorScheme
    val todayDate = getTodayFormattedDate()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        modifier = Modifier.padding(vertical = AppSpacing.xs)
    ) {
        Icon(
            imageVector = if (isTodayDrawDay) Icons.Default.Today else Icons.Default.CalendarMonth,
            contentDescription = null,
            tint = if (isTodayDrawDay) colors.error else colors.primary,
            modifier = Modifier.size(iconSmall())
        )
        when {
            isTodayDrawDay && nextDrawContest != null -> {
                Text(
                    text = stringResource(id = R.string.today_draw_day, nextDrawContest),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.error,
                    fontWeight = FontWeight.Bold
                )
            }

            nextDrawDate != null && nextDrawContest != null -> {
                Text(
                    text = stringResource(id = R.string.next_draw_info, nextDrawContest, nextDrawDate),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.primary
                )
            }

            else -> {
                Text(
                    text = stringResource(id = R.string.last_draw_info, todayDate),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}

private fun getTodayFormattedDate(): String {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return java.time.LocalDate.now().format(formatter)
}

@Composable
private fun QuickActionChip(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    androidx.compose.material3.AssistChip(
        onClick = onClick,
        modifier = modifier,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                color = colors.onSurface
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(iconMedium()),
                tint = colors.primary
            )
        },
        colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
            containerColor = colors.surface,
            labelColor = colors.onSurface,
            leadingIconContentColor = colors.primary
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = colors.outline
        )
    )
}
