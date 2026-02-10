package com.cebolao.lotofacil.ui.screens.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.NumberBall
import com.cebolao.lotofacil.ui.theme.AppSize
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun RecencySection(
    overdueNumbers: List<Pair<Int, Int>>,
    modifier: Modifier = Modifier,
    showCard: Boolean = true,
    showHeader: Boolean = true
) {
    val sortedOverdue = remember(overdueNumbers) {
        overdueNumbers.sortedByDescending { it.second }
    }

    val content: @Composable () -> Unit = {
        Column(modifier = Modifier.padding(if (showCard) AppSpacing.md else 0.dp)) {
            if (showHeader) {
                Text(
                    text = stringResource(id = R.string.recency_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.recency_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (showHeader) AppSpacing.md else AppSpacing.sm),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                items(sortedOverdue, key = { it.first }) { (number, count) ->
                    OverdueItem(number = number, count = count)
                }
            }
        }
    }

    if (showCard) {
        AppCard(
            modifier = modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colorScheme.surface
        ) {
            content()
        }
    } else {
        Column(modifier = modifier) { content() }
    }
}

@Composable
private fun OverdueItem(number: Int, count: Int) {
    val colors = MaterialTheme.colorScheme
    val (color, label) = when {
        count <= 1 -> Pair(colors.tertiary, "0-1")
        count <= 3 -> Pair(colors.secondary, "2-3")
        count <= 6 -> Pair(colors.primary, "4-6")
        else -> Pair(colors.error, "+7")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        NumberBall(
            number = number,
            size = AppSize.numberBallMedium
        )
        
        Box(
            modifier = Modifier
                .width(AppSize.numberBallMedium)
                .height(24.dp)
                .clip(MaterialTheme.shapes.small)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        
        Text(
            text = stringResource(id = R.string.drawn_ago),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
