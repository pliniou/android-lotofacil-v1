package com.cebolao.lotofacil.ui.components

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
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppSpacing

/**
 * Cabeçalho padrão de tela usando MediumTopAppBar do Material 3.
 *
 * Colapsa suavemente ao rolar, mostrando o título grande no estado expandido
 * e título compacto no estado colapsado.
 *
 * @param title Título principal da tela
 * @param subtitle Subtítulo opcional exibido abaixo do título
 * @param icon Ícone opcional exibido na navegação (quando sem botão voltar)
 * @param iconPainter Painter alternativo para o ícone de navegação
 * @param onBackClick Callback do botão voltar (quando não-nulo, exibe seta de retorno)
 * @param actions Ações da barra de ferramentas
 * @param scrollBehavior Comportamento de rolagem para uso com nestedScroll
 */
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

    Column(modifier = modifier.fillMaxWidth()) {
        MediumTopAppBar(
            title = {
                Column {
                    Text(
                        text = title,
                        modifier = Modifier.semantics { heading() },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant,
                            maxLines = 2,
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
                            size = com.cebolao.lotofacil.ui.theme.AppSize.touchTargetMinimum,
                            iconSize = com.cebolao.lotofacil.ui.theme.AppSize.iconSmallMedium
                        )
                    }
                }
            },
            actions = { actions?.invoke(this) },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = colors.surface,
                scrolledContainerColor = colors.surfaceContainer,
                titleContentColor = colors.onSurface,
                navigationIconContentColor = colors.primary,
                actionIconContentColor = colors.primary
            ),
            scrollBehavior = scrollBehavior
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = AppSpacing.lg),
            color = colors.outlineVariant
        )
    }
}
