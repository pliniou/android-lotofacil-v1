package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import com.cebolao.lotofacil.ui.theme.AppSpacing

/**
 * Pull-to-refresh com indicador apenas para gesto manual.
 * Refresh programatico (botao/background) deve usar feedback dedicado da tela.
 */
@Composable
fun PullToRefreshScreen(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String? = null,
    content: @Composable () -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            pullToRefreshState.endRefresh()
        }
    }

    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing && !isRefreshing) {
            onRefresh()
        }
    }

    val shouldShowIndicator = remember(pullToRefreshState.progress, isRefreshing, pullToRefreshState.isRefreshing) {
        pullToRefreshState.progress > 0f || pullToRefreshState.isRefreshing || isRefreshing
    }
    val indicatorAlpha = if (shouldShowIndicator) 1f else 0f

    val rootModifier = modifier
        .fillMaxSize()
        .nestedScroll(pullToRefreshState.nestedScrollConnection)
        .let { current ->
            if (testTag.isNullOrBlank()) current else current.testTag(testTag)
        }

    Box(modifier = rootModifier) {
        content()

        if (shouldShowIndicator) {
            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = AppSpacing.sm)
                    .graphicsLayer { alpha = indicatorAlpha },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
