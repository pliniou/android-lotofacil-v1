package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.CardVariant
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.iconMedium

private data class QuickLinkAction(
    val labelResId: Int,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun QuickNavSection(
    onNavigateToInsights: () -> Unit,
    onNavigateToGames: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val quickActions = listOf(
        QuickLinkAction(
            labelResId = R.string.nav_statistics_label,
            icon = Icons.Default.BarChart,
            onClick = onNavigateToInsights
        ),
        QuickLinkAction(
            labelResId = R.string.nav_my_games_label,
            icon = Icons.Default.SportsEsports,
            onClick = onNavigateToGames
        ),
        QuickLinkAction(
            labelResId = R.string.nav_about_label,
            icon = Icons.Default.Info,
            onClick = onNavigateToAbout
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Text(
            text = stringResource(id = R.string.quick_links_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        AppCard(
            modifier = Modifier.fillMaxWidth(),
            variant = CardVariant.Surfaced,
            backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.md),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                maxItemsInEachRow = 2
            ) {
                quickActions.forEach { action ->
                    AssistChip(
                        onClick = action.onClick,
                        label = {
                            Text(
                                text = stringResource(id = action.labelResId),
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = null,
                                modifier = Modifier.size(iconMedium())
                            )
                        }
                    )
                }
            }
        }
    }
}
