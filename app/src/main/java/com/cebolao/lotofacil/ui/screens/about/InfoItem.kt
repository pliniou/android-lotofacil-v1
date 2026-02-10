package com.cebolao.lotofacil.ui.screens.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

sealed class InfoItem(
    val titleResId: Int,
    val subtitleResId: Int,
    val icon: ImageVector,
    val content: @Composable () -> Unit
) {
    class Rules(
        icon: ImageVector,
        content: @Composable () -> Unit
    ) : InfoItem(
        titleResId = com.cebolao.lotofacil.R.string.info_item_rules_title,
        subtitleResId = com.cebolao.lotofacil.R.string.info_item_rules_subtitle,
        icon = icon,
        content = content
    )

    class Probabilities(
        icon: ImageVector,
        content: @Composable () -> Unit
    ) : InfoItem(
        titleResId = com.cebolao.lotofacil.R.string.info_item_probabilities_title,
        subtitleResId = com.cebolao.lotofacil.R.string.info_item_probabilities_subtitle,
        icon = icon,
        content = content
    )

    class Bolao(
        icon: ImageVector,
        content: @Composable () -> Unit
    ) : InfoItem(
        titleResId = com.cebolao.lotofacil.R.string.info_item_bolao_title,
        subtitleResId = com.cebolao.lotofacil.R.string.info_item_bolao_subtitle,
        icon = icon,
        content = content
    )

    class Purpose(
        icon: ImageVector,
        content: @Composable () -> Unit
    ) : InfoItem(
        titleResId = com.cebolao.lotofacil.R.string.info_item_purpose_title,
        subtitleResId = com.cebolao.lotofacil.R.string.info_item_purpose_subtitle,
        icon = icon,
        content = content
    )

    class Legal(
        icon: ImageVector,
        content: @Composable () -> Unit
    ) : InfoItem(
        titleResId = com.cebolao.lotofacil.R.string.info_item_legal_title,
        subtitleResId = com.cebolao.lotofacil.R.string.info_item_legal_subtitle,
        icon = icon,
        content = content
    )

    class Privacy(
        icon: ImageVector,
        content: @Composable () -> Unit
    ) : InfoItem(
        titleResId = com.cebolao.lotofacil.R.string.info_item_privacy_title,
        subtitleResId = com.cebolao.lotofacil.R.string.info_item_privacy_subtitle,
        icon = icon,
        content = content
    )

    class Tips(
        icon: ImageVector,
        content: @Composable () -> Unit
    ) : InfoItem(
        titleResId = com.cebolao.lotofacil.R.string.info_item_tips_title,
        subtitleResId = com.cebolao.lotofacil.R.string.info_item_tips_subtitle,
        icon = icon,
        content = content
    )
}
