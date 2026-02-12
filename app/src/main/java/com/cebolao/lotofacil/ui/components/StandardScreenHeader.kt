package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.iconButtonSize
import com.cebolao.lotofacil.ui.theme.iconMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconPainter: Painter? = null,
    onBackClick: (() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val colors = MaterialTheme.colorScheme
    val isCompactWidth = LocalConfiguration.current.screenWidthDp < 360
    val titleStyle = if (isCompactWidth) {
        MaterialTheme.typography.titleMedium
    } else {
        MaterialTheme.typography.titleLarge
    }
    val subtitleStyle = if (isCompactWidth) {
        MaterialTheme.typography.bodySmall
    } else {
        MaterialTheme.typography.bodyMedium
    }

    Column(modifier = modifier.fillMaxWidth()) {
        TopAppBar(
            title = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = AppSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.semantics { heading() },
                        style = titleStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    subtitle?.let {
                        Text(
                            text = it,
                            style = subtitleStyle,
                            color = colors.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            },
            navigationIcon = {
                when {
                    onBackClick != null -> {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.cd_navigate_back),
                                tint = colors.primary
                            )
                        }
                    }

                    icon != null || iconPainter != null -> {
                        IconBadge(
                            icon = icon,
                            painter = iconPainter,
                            contentDescription = null,
                            size = iconButtonSize(),
                            iconSize = iconMedium()
                        )
                    }
                }
            },
            actions = { actions?.invoke(this) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colors.surface,
                scrolledContainerColor = colors.surfaceContainer,
                titleContentColor = colors.onSurface,
                navigationIconContentColor = colors.primary,
                actionIconContentColor = colors.primary
            ),
            scrollBehavior = scrollBehavior
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = AppSpacing.md),
            color = colors.outlineVariant
        )
    }
}
