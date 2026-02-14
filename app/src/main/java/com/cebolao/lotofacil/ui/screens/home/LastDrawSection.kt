package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.SuggestionChip
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.utils.NumberFormatUtils
import com.cebolao.lotofacil.domain.model.LastDrawStats
import com.cebolao.lotofacil.domain.model.PrizeTier
import com.cebolao.lotofacil.domain.model.WinnerLocation
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.CardVariant
import com.cebolao.lotofacil.ui.components.NumberBall
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.theme.AppSize
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppTheme

@Composable
fun LastDrawSection(stats: LastDrawStats) {

    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)) {
        // Main Result Card
        LatestResultCard(stats = stats)

        // Next Contest Card
        // Next Draw Card moved to HomeScreen

    }
}

@Composable
private fun LatestResultCard(
    stats: LastDrawStats
) {
    val colors = MaterialTheme.colorScheme
    
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        variant = CardVariant.Elevated,
        isGlassmorphic = true
    ) {
        Column {
            // Secondary title label
            Text(
                text = "${stringResource(id = R.string.last_contest)} #${NumberFormatUtils.formatInteger(stats.contest)}",
                style = MaterialTheme.typography.labelMedium,
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(AppSpacing.md))

            // Numbers Grid
            val sortedNumbers = remember(stats.numbers) { stats.numbers.sorted() }
            FlowNumbersGrid(numbers = sortedNumbers)

            Spacer(modifier = Modifier.height(AppSpacing.lg))
            HorizontalDivider(color = colors.outline.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(AppSpacing.md))

            // Quick Stats
            QuickStatsRow(stats)

            // Prizes Expandable
            if (stats.prizes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(AppSpacing.md))
                PrizeDetailsSection(stats.prizes, stats.winners)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowNumbersGrid(numbers: List<Int>) {
    val sortedNumbers = remember(numbers) { numbers.sorted() }
    
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        maxItemsInEachRow = 5
    ) {
        for (number in sortedNumbers) {
            NumberBall(
                number = number
            )
        }
    }
}

@Composable
private fun QuickStatsRow(stats: LastDrawStats) {
    val cachedSum = remember(stats.sum) { NumberFormatUtils.formatInteger(stats.sum) }
    val primaryStats = listOf(
        stringResource(R.string.sum_label) to cachedSum,
        stringResource(R.string.even_label) to NumberFormatUtils.formatInteger(stats.evens),
        stringResource(R.string.prime_label) to NumberFormatUtils.formatInteger(stats.primes)
    )
    val secondaryStats = listOf(
        stringResource(R.string.frame_label) to NumberFormatUtils.formatInteger(stats.frame),
        stringResource(R.string.portrait_label) to NumberFormatUtils.formatInteger(stats.portrait)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = AppSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            items(primaryStats) { (label, value) ->
                StatChip(label = label, value = value)
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = AppSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            items(secondaryStats) { (label, value) ->
                StatChip(label = label, value = value)
            }
        }
    }
}

@Composable
private fun PrizeDetailsSection(
    prizes: List<PrizeTier>,
    winners: List<WinnerLocation>
) {
    var showPrizes by remember { mutableStateOf(true) }
    val colors = MaterialTheme.colorScheme

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .clickable { showPrizes = !showPrizes }
                .padding(vertical = AppSpacing.sm, horizontal = AppSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.prize_details_title),
                style = MaterialTheme.typography.labelLarge,
                color = colors.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(AppSpacing.xs))
            Icon(
                imageVector = if (showPrizes) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(AppSize.iconSmall)
            )
        }

        AnimatedVisibility(
            visible = showPrizes,
            enter = expandVertically(animationSpec = tween(durationMillis = 220)),
            exit = shrinkVertically(animationSpec = tween(durationMillis = 200))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                prizes.forEachIndexed { index, prize ->
                    PrizeTierCard(
                        prize = prize,
                        tier = index + 1
                    )
                }

                if (winners.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(AppSpacing.md))
                    Text(
                        text = stringResource(id = R.string.winners_by_state),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurface,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = AppSpacing.xs, start = AppSpacing.xs)
                    )
                    WinnersByStateGrid(winners = winners)
                }
            }
        }
    }
}

@Composable
private fun PrizeTierCard(prize: PrizeTier, tier: Int) {
    val colors = MaterialTheme.colorScheme
    val elevations = AppTheme.elevation
    val tierDescription = stringResource(
        R.string.prize_tier_description,
        tier,
        NumberFormatUtils.formatCurrency(prize.prizeValue)
    )
    
    // Determine tier-specific styling
    val (tierColor, tierElevation, tierIcon) = when (tier) {
            1 -> Triple(
                colors.primary,
                elevations.lg,
                "🥇"
            )
            2 -> Triple(
                colors.secondary,
                elevations.md,
                "🥈"
            )
            3 -> Triple(
                colors.tertiary,
                elevations.sm,
                "🥉"
            )
            else -> Triple(
                colors.outline,
                elevations.xs,
                "${tier}º"
            )
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = tierDescription
                role = Role.Image
            },
        elevation = CardDefaults.cardElevation(defaultElevation = tierElevation),
        colors = CardDefaults.cardColors(
            containerColor = tierColor.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.5.dp, tierColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            // Header with tier indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
                    modifier = Modifier.weight(1f)
                ) {
                    // Tier badge
                    Box(
                        modifier = Modifier
                            .size(AppSize.numberBallSmall)
                            .background(tierColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tierIcon,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    
                    // Prize description
                    Column {
                        Text(
                            text = prize.description,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = tierColor
                        )
                        Text(
                            text = "${NumberFormatUtils.formatInteger(prize.winners)} ${stringResource(id = R.string.winners)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
            
            // Prize value - highlighted
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tierColor.copy(alpha = 0.1f), MaterialTheme.shapes.small)
                    .padding(AppSpacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.prize_per_winner),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant
                )
                Text(
                    text = NumberFormatUtils.formatCurrency(prize.prizeValue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = tierColor
                )
            }
        }
    }
}

@Composable
private fun WinnersByStateGrid(winners: List<WinnerLocation>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        contentPadding = PaddingValues(horizontal = AppSpacing.xs)
    ) {
        items(winners.take(10), key = { "${it.city}-${it.state}" }) { winner ->
            WinnerBadge(winner)
        }
    }
}

@Composable
private fun WinnerBadge(winner: WinnerLocation) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = CircleShape,
        color = colors.secondaryContainer,
        modifier = Modifier.height(AppSize.chipHeight)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = AppSpacing.md)
        ) {
            Text(
                text = winner.state,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.onSecondaryContainer
            )
            if (winner.winnersCount > 1) {
                Spacer(modifier = Modifier.width(AppSpacing.xs))
                Box(
                    modifier = Modifier
                        .background(colors.primary, CircleShape)
                        .padding(horizontal = AppSpacing.xs, vertical = 2.dp)
                ) {
                    Text(
                        text = NumberFormatUtils.formatInteger(winner.winnersCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onPrimary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
 
@Composable
private fun StatChip(label: String, value: String) {
    SuggestionChip(
        onClick = { /* no op */ },
        label = {
            Text(
                text = buildAnnotatedString {
                    append("$label: ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(value)
                    }
                },
                style = MaterialTheme.typography.bodyMedium
            )
        },
        modifier = Modifier.defaultMinSize(minWidth = 72.dp)
    )
}

@Composable
private fun StatItem(label: String, value: String) {
    val colors = MaterialTheme.colorScheme
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.primary
        )
    }
}
