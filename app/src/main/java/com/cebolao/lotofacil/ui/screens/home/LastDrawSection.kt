package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.utils.NumberFormatUtils
import com.cebolao.lotofacil.domain.model.HomeLastContest
import com.cebolao.lotofacil.domain.model.HomePrizeTier
import com.cebolao.lotofacil.domain.model.HomeWinnerLocation
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.CardVariant
import com.cebolao.lotofacil.ui.components.NumberBall
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun LastDrawSection(contest: HomeLastContest) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)) {
        AppCard(
            modifier = Modifier.fillMaxWidth(),
            variant = CardVariant.Elevated,
            isGlassmorphic = true
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)) {
                Text(
                    text = "${stringResource(id = R.string.last_contest)} #${NumberFormatUtils.formatInteger(contest.contest)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                FlowNumbersGrid(numbers = remember(contest.numbers) { contest.numbers.sorted() })

                QuickStatsRow(contest = contest)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.14f))
                PrizeDetailsSection(
                    prizes = contest.prizes,
                    locations = contest.winnerLocations
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowNumbersGrid(numbers: List<Int>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        maxItemsInEachRow = 5
    ) {
        numbers.forEach { number ->
            NumberBall(number = number)
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun QuickStatsRow(contest: HomeLastContest) {
    val primaryStats = listOf(
        stringResource(R.string.sum_label) to NumberFormatUtils.formatInteger(contest.sum),
        stringResource(R.string.even_label) to NumberFormatUtils.formatInteger(contest.evens),
        stringResource(R.string.prime_label) to NumberFormatUtils.formatInteger(contest.primes),
        stringResource(R.string.frame_label) to NumberFormatUtils.formatInteger(contest.frame),
        stringResource(R.string.portrait_label) to NumberFormatUtils.formatInteger(contest.portrait)
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        maxItemsInEachRow = 3
    ) {
        primaryStats.forEach { (label, value) ->
            SuggestionChip(
                onClick = {},
                label = {
                    Text(
                        text = "$label: $value",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            )
        }
    }
}

@Composable
private fun PrizeDetailsSection(
    prizes: List<HomePrizeTier>,
    locations: List<HomeWinnerLocation>
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        Text(
            text = stringResource(id = R.string.home_prize_section_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        if (prizes.isEmpty()) {
            Text(
                text = stringResource(id = R.string.home_prize_data_unavailable),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            prizes.sortedBy { it.faixa ?: Int.MAX_VALUE }.forEach { tier ->
                PrizeTierRow(tier)
            }
        }

        if (locations.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.home_winner_locations_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            locations.take(8).forEach { location ->
                WinnerLocationRow(location = location)
            }
        }
    }
}

@Composable
private fun PrizeTierRow(tier: HomePrizeTier) {
    val description = tier.description
        ?.takeIf { it.isNotBlank() }
        ?: tier.faixa?.let { stringResource(id = R.string.home_prize_faixa_label, it) }
        ?: stringResource(id = R.string.home_prize_generic_faixa)

    val winnersText = tier.winners
        ?.let { NumberFormatUtils.formatInteger(it) }
        ?: stringResource(id = R.string.home_value_not_available_short)

    val prizeText = tier.prizeValue
        ?.let { NumberFormatUtils.formatCurrency(it) }
        ?: stringResource(id = R.string.home_value_not_available)

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(id = R.string.home_prize_winners_label, winnersText),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = prizeText,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun WinnerLocationRow(location: HomeWinnerLocation) {
    val locationText = buildList {
        location.city?.takeIf { it.isNotBlank() }?.let { add(it) }
        location.state?.takeIf { it.isNotBlank() }?.let { add(it) }
    }.joinToString(separator = " / ")
        .ifBlank { stringResource(id = R.string.home_location_not_informed) }

    val winnersText = location.winnersCount
        ?.let { NumberFormatUtils.formatInteger(it) }
        ?: stringResource(id = R.string.home_value_not_available_short)

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = locationText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(id = R.string.home_prize_winners_label, winnersText),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
