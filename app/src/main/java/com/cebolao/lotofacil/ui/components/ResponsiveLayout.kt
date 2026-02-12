package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppTheme

/**
 * Responsive layout utilities for different screen sizes
 */

@Composable
fun ResponsiveGrid(
    items: List<Any>,
    itemCount: Int,
    modifier: Modifier = Modifier,
    minItemWidth: androidx.compose.ui.unit.Dp = 300.dp,
    content: @Composable (item: Any) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val screenWidth = maxWidth
        val columns = (screenWidth / minItemWidth).toInt().coerceAtLeast(1)
        val itemWidth = screenWidth / columns
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            items(items.chunked(columns)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
                ) {
                    rowItems.forEach { item ->
                        Box(
                            modifier = Modifier
                                .width(itemWidth)
                                .weight(1f)
                        ) {
                            content(item)
                        }
                    }
                    // Fill remaining space with spacers if needed
                    repeat(columns - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ResponsiveRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        
        if (screenWidth >= AppTheme.sizes.breakpointTablet) {
            // Tablet layout - horizontal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.lg)
            ) {
                content()
            }
        } else {
            // Phone layout - vertical
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                content()
            }
        }
    }
}

@Composable
fun AdaptiveLazyRow(
    items: List<Any>,
    modifier: Modifier = Modifier,
    content: @Composable (item: Any) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        
        if (screenWidth >= AppTheme.sizes.breakpointTablet) {
            // Show more items on tablet
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                items(items.take(8)) { item ->
                    content(item)
                }
            }
        } else {
            // Show fewer items on phone
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                items(items.take(4)) { item ->
                    content(item)
                }
            }
        }
    }
}

/**
 * Screen size utilities
 */
@Composable
fun isTablet(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp.dp >= AppTheme.sizes.breakpointTablet
}

@Composable
fun isPortrait(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
}
