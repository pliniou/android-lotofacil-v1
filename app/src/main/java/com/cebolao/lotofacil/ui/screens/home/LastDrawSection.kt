package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.cebolao.lotofacil.ui.components.NumberBallInteractive
import com.cebolao.lotofacil.ui.theme.AppSize
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

                NumbersGrid(numbers = contest.numbers.sorted())
                NumberLegend()

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

@Composable
private fun NumbersGrid(numbers: List<Int>) {
    val ballSize = AppSize.numberBallTiny
    val spacing = AppSpacing.sm
    val rows = remember(numbers.size) { ((numbers.size - 1) / 5) + 1 }
    val gridHeight = remember(rows, ballSize, spacing) {
        (ballSize * rows) + (spacing * (rows - 1)) + AppSpacing.sm + AppSpacing.sm
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier
            .fillMaxWidth()
            .height(gridHeight),
        userScrollEnabled = false,
        contentPadding = PaddingValues(AppSpacing.sm),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        items(numbers.size) { index ->
            val number = numbers[index]
            val category = resolveCategory(number)
            val (containerColor, contentColor, borderColor) = category.colors()
            NumberBallInteractive(
                number = number,
                size = ballSize,
                containerColor = containerColor,
                contentColor = contentColor,
                borderColor = borderColor,
                isSelected = false,
                onClick = null,
                contentDescription = ""
            )
        }
    }
}

@Composable
private fun NumberLegend() {
    val categories = listOf(
        NumberCategory.Prime,
        NumberCategory.Fibonacci,
        NumberCategory.MultipleOfThree
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        categories.forEach { category ->
            val (containerColor, contentColor, _) = category.colors()
            SuggestionChip(
                onClick = {},
                label = {
                    Text(
                        text = stringResource(id = category.labelResId),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = containerColor,
                    labelColor = contentColor
                )
            )
        }
    }
}

@Composable
private fun QuickStatsRow(contest: HomeLastContest) {
    val primaryStats = listOf(
        stringResource(R.string.sum_label) to NumberFormatUtils.formatInteger(contest.sum),
        stringResource(R.string.home_even_odd_label) to NumberFormatUtils.formatInteger(contest.evens),
        stringResource(R.string.prime_label) to NumberFormatUtils.formatInteger(contest.primes),
        stringResource(R.string.frame_label) to NumberFormatUtils.formatInteger(contest.frame),
        stringResource(R.string.portrait_label) to NumberFormatUtils.formatInteger(contest.portrait)
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        primaryStats.forEach { (label, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun PrizeDetailsSection(
    prizes: List<HomePrizeTier>,
    locations: List<HomeWinnerLocation>
) {
    var showAllLocations by remember { mutableStateOf(false) }

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

            val visibleLocations = if (showAllLocations) locations else locations.take(3)
            visibleLocations.forEach { location ->
                WinnerLocationRow(location = location)
            }

            if (locations.size > 3) {
                TextButton(onClick = { showAllLocations = !showAllLocations }) {
                    Text(
                        text = if (showAllLocations) {
                            stringResource(id = R.string.home_view_less)
                        } else {
                            stringResource(id = R.string.common_view_all)
                        }
                    )
                }
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

    AppCard(
        variant = CardVariant.Filled,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = tierMedalColor(tier.faixa)
            )

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
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs), verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = locationText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = stringResource(id = R.string.home_prize_winners_label, winnersText),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private enum class NumberCategory(val labelResId: Int) {
    Prime(R.string.home_legend_prime),
    Fibonacci(R.string.home_legend_fibonacci),
    MultipleOfThree(R.string.home_legend_multiple_3),
    Default(R.string.home_legend_default)
}

@Composable
private fun NumberCategory.colors(): Triple<Color, Color, Color> {
    val colors = MaterialTheme.colorScheme
    return when (this) {
        NumberCategory.Prime -> Triple(colors.tertiaryContainer, colors.onTertiaryContainer, colors.tertiary)
        NumberCategory.Fibonacci -> Triple(colors.secondaryContainer, colors.onSecondaryContainer, colors.secondary)
        NumberCategory.MultipleOfThree -> Triple(colors.primaryContainer, colors.onPrimaryContainer, colors.primary)
        NumberCategory.Default -> Triple(colors.surfaceVariant, colors.onSurfaceVariant, colors.outline)
    }
}

private fun resolveCategory(number: Int): NumberCategory {
    val prime = setOf(2, 3, 5, 7, 11, 13, 17, 19, 23)
    val fibonacci = setOf(1, 2, 3, 5, 8, 13, 21)
    return when {
        number in prime -> NumberCategory.Prime
        number in fibonacci -> NumberCategory.Fibonacci
        number % 3 == 0 -> NumberCategory.MultipleOfThree
        else -> NumberCategory.Default
    }
}

@Composable
private fun tierMedalColor(faixa: Int?): Color {
    val colors = MaterialTheme.colorScheme
    return when (faixa) {
        1, 15 -> Color(0xFFFFB300)
        2, 14 -> Color(0xFFB0BEC5)
        3, 13 -> Color(0xFFBF8E63)
        else -> colors.primary
    }
}
