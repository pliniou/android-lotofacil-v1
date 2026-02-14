package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import com.cebolao.lotofacil.ui.theme.AppSpacing

object AppScreenDefaults {
    @Composable
    fun listContentPadding(
        horizontal: Dp = AppSpacing.lg,
        top: Dp = AppSpacing.lg,
        bottom: Dp = AppSpacing.xxxl
    ): PaddingValues {
        return PaddingValues(
            start = horizontal,
            top = top,
            end = horizontal,
            bottom = bottom
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppScreenScaffold(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconPainter: Painter? = null,
    onBackClick: (() -> Unit)? = null,
    actions: (@Composable androidx.compose.foundation.layout.RowScope.() -> Unit)? = null,
    floatingActionButton: (@Composable () -> Unit)? = null,
    snackbarHostState: SnackbarHostState? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val scaffoldModifier = if (scrollBehavior != null) {
        modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    } else {
        modifier.fillMaxSize()
    }

    Scaffold(
        modifier = scaffoldModifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            StandardScreenHeader(
                title = title,
                subtitle = subtitle,
                icon = icon,
                iconPainter = iconPainter,
                onBackClick = onBackClick,
                actions = actions,
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            snackbarHostState?.let { SnackbarHost(hostState = it) }
        },
        floatingActionButton = {
            floatingActionButton?.invoke()
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

fun Modifier.screenContentPadding(innerPadding: PaddingValues): Modifier {
    return this.padding(innerPadding)
}
