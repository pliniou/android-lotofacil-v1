package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.utils.NumberFormatUtils
import com.cebolao.lotofacil.domain.model.HomeNextContest
import com.cebolao.lotofacil.domain.model.HomeNextContestSource
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.CardVariant
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.AppSize
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun NextDrawSection(
    nextContest: HomeNextContest,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    AppCard(
        modifier = modifier.fillMaxWidth(),
        elevation = AppElevation.sm,
        containerColor = if (nextContest.isAccumulated) {
            colors.errorContainer.copy(alpha = 0.12f)
        } else {
            colors.surface
        },
        variant = CardVariant.Elevated
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.next_draw_card_title),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                if (nextContest.isAccumulated) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = colors.error,
                            modifier = Modifier.size(AppSize.iconSmall)
                        )
                        Text(
                            text = stringResource(R.string.accumulated),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = nextContest.prizeEstimate
                    ?.let(NumberFormatUtils::formatCurrency)
                    ?: stringResource(id = R.string.home_value_not_available),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = if (nextContest.isAccumulated) colors.error else colors.primary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(AppSize.iconSmall)
                )
                Spacer(modifier = Modifier.width(AppSpacing.xs))
                Text(
                    text = stringResource(
                        R.string.next_contest_format,
                        NumberFormatUtils.formatInteger(nextContest.contestNumber)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(AppSpacing.xs))
                Text(text = "|", color = colors.outline)
                Spacer(modifier = Modifier.width(AppSpacing.xs))
                Text(
                    text = nextContest.date ?: stringResource(id = R.string.home_next_date_not_available),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            }

            if (nextContest.source == HomeNextContestSource.DERIVED) {
                Text(
                    text = stringResource(id = R.string.home_next_contest_derived_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}

