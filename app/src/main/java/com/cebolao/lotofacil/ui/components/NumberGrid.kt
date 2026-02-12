package com.cebolao.lotofacil.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.testtags.AppTestTags

@Stable
data class NumberBallItem(
    val number: Int,
    val isSelected: Boolean,
    val isDisabled: Boolean,
    val isHighlighted: Boolean = false,
    override val key: Any = number
) : StableKey

@Stable
interface StableKey {
    val key: Any
}

@Stable
data class GridLayoutConfig(
    val spacing: Dp,
    val ballSize: Dp
)

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun NumberGrid(
    items: List<NumberBallItem>,
    modifier: Modifier = Modifier,
    columns: Int = 5,
    ballSize: Dp? = null,
    onNumberClicked: ((Int) -> Unit)? = null
) {
    val configuration = LocalConfiguration.current

    val spacing = AppSpacing.sm
    val horizontalPadding = AppSpacing.lg * 2

    val gridConfig = remember(configuration.screenWidthDp, columns, spacing, horizontalPadding, ballSize) {
        val screenWidthDp = configuration.screenWidthDp.dp
        
        val availableWidth = screenWidthDp - horizontalPadding
        val spacingTotal = spacing * (columns - 1)
        val calculatedBallSize = (availableWidth - spacingTotal) / columns
        
        val finalBallSize = ballSize ?: calculatedBallSize.coerceIn(32.dp, 48.dp)
        GridLayoutConfig(spacing, finalBallSize)
    }

    val rows = remember(items.size, columns) {
        if (items.isEmpty()) 0 else ((items.size - 1) / columns) + 1
    }
    val gridHeight = remember(gridConfig, rows) {
        if (rows == 0) 0.dp else {
            (gridConfig.ballSize * rows) +
                (gridConfig.spacing * (rows - 1)) +
                (gridConfig.spacing * 2)
        }
    }

    val boundedModifier = if (rows > 0) {
        modifier.fillMaxWidth().height(gridHeight)
    } else {
        modifier.fillMaxWidth()
    }

    val taggedModifier = boundedModifier.testTag(AppTestTags.NumberGrid)

    LazyVerticalGrid(
        modifier = taggedModifier,
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(gridConfig.spacing),
        horizontalArrangement = Arrangement.spacedBy(gridConfig.spacing),
        verticalArrangement = Arrangement.spacedBy(gridConfig.spacing)
    ) {
        items(
            items = items,
            key = { it.key },
            contentType = { "number_ball" }
        ) { item ->
            NumberBall(
                number = item.number,
                size = gridConfig.ballSize,
                isSelected = item.isSelected,
                isHighlighted = item.isHighlighted,
                isDisabled = item.isDisabled,
                onClick = { onNumberClicked?.invoke(item.number) }
            )
        }
    }
}
