package com.cebolao.lotofacil.ui.screens.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.CardVariant
import com.cebolao.lotofacil.ui.components.LazyImage
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.iconLarge

internal data class AboutExternalLink(
    val titleResId: Int,
    val subtitleResId: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val url: String
)

internal fun LazyListScope.aboutInfoSection(
    sectionKey: String,
    title: String,
    items: List<InfoItem>,
    onInfoClick: (InfoItem) -> Unit
) {
    item(key = "${sectionKey}_header") {
        SectionHeader(title = title)
    }

    itemsIndexed(
        items = items,
        key = { _, item -> "${sectionKey}_${item.titleResId}" },
        contentType = { _, _ -> "about_info_card" }
    ) { index, info ->
        AnimateOnEntry(delayMillis = index * 35L) {
            InfoCard(
                item = info,
                modifier = Modifier.padding(horizontal = AppSpacing.lg)
            ) {
                onInfoClick(info)
            }
        }
    }
}

internal fun LazyListScope.aboutLinksSection(
    sectionKey: String,
    title: String,
    links: List<AboutExternalLink>
) {
    item(key = "${sectionKey}_header") {
        SectionHeader(title = title)
    }

    itemsIndexed(
        items = links,
        key = { _, link -> "${sectionKey}_${link.titleResId}" },
        contentType = { _, _ -> "about_link_card" }
    ) { index, link ->
        AnimateOnEntry(delayMillis = index * 35L) {
            ExternalLinkCard(
                titleResId = link.titleResId,
                subtitleResId = link.subtitleResId,
                icon = link.icon,
                url = link.url,
                modifier = Modifier.padding(horizontal = AppSpacing.lg)
            )
        }
    }
}

@Composable
internal fun UserStatsCard(onClick: () -> Unit) {
    AppCard(
        variant = CardVariant.Elevated,
        onClick = onClick,
        isGlassmorphic = true,
        modifier = Modifier.padding(horizontal = AppSpacing.lg)
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(iconLarge())
            )
            Spacer(modifier = Modifier.width(AppSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.user_stats_card_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.user_stats_card_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
internal fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xs)
    )
}

@Composable
internal fun StudioHero() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = AppSpacing.xxl),
        contentAlignment = Alignment.Center
    ) {
        LazyImage(
            painterResourceId = R.drawable.ic_studiohero,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(com.cebolao.lotofacil.ui.theme.AppSize.chartHeightSmall)
                .alpha(0.15f),
            contentScale = ContentScale.Crop,
            delayMillis = 100L,
            showPlaceholder = false
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LazyImage(
                painterResourceId = R.drawable.ic_cebolalogo,
                contentDescription = stringResource(id = R.string.studio_logo_description),
                modifier = Modifier.size(80.dp),
                delayMillis = 50L,
                showPlaceholder = false
            )
        }
    }
}
