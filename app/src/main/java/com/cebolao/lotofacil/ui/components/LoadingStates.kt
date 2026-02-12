package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.theme.AppShapes
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppTheme
import com.cebolao.lotofacil.ui.theme.LotofacilTheme

/**
 * Skeleton card for content loading
 */
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 120.dp
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.md,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.elevation.xs)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.md)
        ) {
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .width(120.dp)
                    .clip(AppShapes.sm)
                    .shimmer()
            )
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.7f)
                    .clip(AppShapes.sm)
                    .shimmer()
            )
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.5f)
                    .clip(AppShapes.sm)
                    .shimmer()
            )
        }
    }
}

/**
 * Loading skeleton for number balls
 */
@Composable
fun NumberBallSkeleton(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = AppTheme.sizes.numberBallMedium
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .shimmer()
    )
}

/**
 * Row of number ball skeletons
 */
@Composable
fun NumberBallRowSkeleton(
    count: Int = 15,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        repeat(count) {
            NumberBallSkeleton()
        }
    }
}

/**
 * Full screen loading state
 */
@Composable
fun FullScreenLoading(
    message: String = "Carregando...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(AppTheme.sizes.progressIndicatorMedium),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Linear progress with label
 */
@Composable
fun LabeledLinearProgress(
    progress: Float,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

/**
 * Extension for LazyColumn skeleton items
 */
fun LazyListScope.skeletonItems(
    count: Int,
    itemContent: @Composable () -> Unit
) {
    items(count) {
        itemContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun SkeletonCardPreview() {
    LotofacilTheme {
        SkeletonCard()
    }
}

@Preview(showBackground = true)
@Composable
private fun NumberBallSkeletonPreview() {
    LotofacilTheme {
        NumberBallRowSkeleton(count = 5)
    }
}
