package com.cebolao.lotofacil.ui.screens.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.components.AppScreenDefaults
import com.cebolao.lotofacil.ui.components.AppScreenScaffold
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.CardVariant
import com.cebolao.lotofacil.ui.components.InfoDialog
import com.cebolao.lotofacil.ui.components.LazyImage
import com.cebolao.lotofacil.ui.components.screenContentPadding
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.iconLarge

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    onNavigateToUserStats: () -> Unit
) {
    var dialogContent by remember { mutableStateOf<InfoItem?>(null) }
    val hapticFeedback = LocalHapticFeedback.current

    // Delegate to Content composable
    AboutScreenContent(
        modifier = modifier,
        dialogContent = dialogContent,
        onNavigateToUserStats = onNavigateToUserStats,
        onAction = { action ->
            when (action) {
                is AboutAction.ShowDialog -> {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    dialogContent = action.item
                }
                is AboutAction.DismissDialog -> {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    dialogContent = null
                }
            }
        }
    )
}

// ==================== SEALED ACTIONS ====================
sealed class AboutAction {
    data class ShowDialog(val item: InfoItem) : AboutAction()
    object DismissDialog : AboutAction()
}

// ==================== STATELESS CONTENT ====================
@Composable
fun AboutScreenContent(
    modifier: Modifier = Modifier,
    dialogContent: InfoItem? = null,
    onNavigateToUserStats: () -> Unit = {},
    onAction: (AboutAction) -> Unit = {}
) {
    dialogContent?.let { item ->
        InfoDialog(
            onDismissRequest = { onAction(AboutAction.DismissDialog) },
            dialogTitle = stringResource(id = item.titleResId),
            icon = item.icon
        ) { item.content() }
    }

    val guideItems = remember {
        listOf(
            InfoItem.Tips(Icons.Default.TipsAndUpdates, content = { TipsInfoContent() }),
            InfoItem.Rules(Icons.Default.Gavel, content = { RulesInfoContent() }),
            InfoItem.Probabilities(Icons.Default.Calculate, content = { ProbabilitiesTable() }),
            InfoItem.Bolao(Icons.Default.Group, content = { BolaoInfoContent() }),
            InfoItem.Purpose(Icons.Default.Lightbulb, content = { PurposeInfoContent() })
        )
    }

    val legalItems = remember {
        listOf(
            InfoItem.Legal(Icons.Default.Info, content = { LegalInfoContent() }),
            InfoItem.Privacy(Icons.Default.PrivacyTip, content = { PrivacyInfoContent() })
        )
    }

    AppScreenScaffold(
        modifier = modifier.fillMaxSize(),
        title = stringResource(id = R.string.studio_name),
        subtitle = stringResource(id = R.string.studio_slogan),
        icon = Icons.Default.Info
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .screenContentPadding(innerPadding),
            contentPadding = AppScreenDefaults.listContentPadding(
                horizontal = 0.dp,
                top = 0.dp,
                bottom = AppSpacing.xxxl
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            item(key = "studio_hero") {
                StudioHero()
            }
            
            item(key = "guide_section_header") {
                SectionHeader(stringResource(R.string.about_section_guide))
            }
            
            items(guideItems, key = { it.titleResId }) { info ->
                AnimateOnEntry {
                    InfoCard(info) {
                        onAction(AboutAction.ShowDialog(info))
                    }
                }
            }

            item(key = "legal_section_header") {
                Spacer(modifier = Modifier.height(AppSpacing.md))
                SectionHeader(stringResource(R.string.about_section_legal))
            }

            items(legalItems, key = { it.titleResId }) { info ->
                AnimateOnEntry {
                    InfoCard(info) {
                        onAction(AboutAction.ShowDialog(info))
                    }
                }
            }

            item(key = "links_section_header") {
                Spacer(modifier = Modifier.height(AppSpacing.md))
                SectionHeader(stringResource(R.string.about_section_links))
            }

            item(key = "link_caixa") {
                AnimateOnEntry {
                    ExternalLinkCard(
                        titleResId = R.string.link_caixa_title,
                        subtitleResId = R.string.link_caixa_subtitle,
                        icon = Icons.Default.Language,
                        url = "https://loterias.caixa.gov.br/Paginas/Lotofacil.aspx"
                    )
                }
            }

            item(key = "link_github") {
                AnimateOnEntry {
                    ExternalLinkCard(
                        titleResId = R.string.link_github_title,
                        subtitleResId = R.string.link_github_subtitle,
                        icon = Icons.Default.Code,
                        url = "https://github.com/pliniou"
                    )
                }
            }

            item(key = "user_stats_spacer") {
                Spacer(modifier = Modifier.height(AppSpacing.md))
            }

            item(key = "user_stats_card") {
                UserStatsCard(onNavigateToUserStats)
            }
        }
    }
}

@Composable
private fun UserStatsCard(onClick: () -> Unit) {
    AppCard(
        variant = CardVariant.Clickable,
        onClick = onClick,
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,  // Usar cor sólida
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
                modifier = Modifier.size(32.dp)
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
private fun SectionHeader(title: String) {
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
private fun StudioHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = AppSpacing.xxl),
        contentAlignment = Alignment.Center
    ) {
        // Studio Hero as background/decorative element - lazy loaded
        LazyImage(
            painterResourceId = R.drawable.ic_studiohero,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .alpha(0.15f),
            contentScale = ContentScale.Crop,
            delayMillis = 100L,
            showPlaceholder = false
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cebola logo with optimized size
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

@Composable
private fun InfoCard(item: InfoItem, onClick: () -> Unit) {
    AppCard(
        variant = CardVariant.Clickable,
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        elevation = AppCardDefaults.elevation,
        modifier = Modifier.padding(horizontal = AppSpacing.lg)
    ) {
        Row(
            modifier = Modifier.padding(AppCardDefaults.defaultPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                item.icon, 
                contentDescription = stringResource(id = item.titleResId), 
                tint = MaterialTheme.colorScheme.primary, 
                modifier = Modifier.size(iconLarge())
            )
            Spacer(Modifier.width(AppSpacing.md))
            Column(Modifier.weight(1f)) {
                Text(stringResource(id = item.titleResId), style = MaterialTheme.typography.titleMedium)
                Text(
                    stringResource(id = item.subtitleResId),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ExternalLinkCard(
    titleResId: Int,
    subtitleResId: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    url: String
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    AppCard(
        variant = CardVariant.Clickable,
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        },
        shape = MaterialTheme.shapes.medium,
        elevation = AppCardDefaults.elevation,
        modifier = Modifier.padding(horizontal = AppSpacing.lg)
    ) {
        Row(
            modifier = Modifier.padding(AppCardDefaults.defaultPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = stringResource(id = titleResId),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(iconLarge())
            )
            Spacer(Modifier.width(AppSpacing.md))
            Column(Modifier.weight(1f)) {
                Text(stringResource(id = titleResId), style = MaterialTheme.typography.titleMedium)
                Text(
                    stringResource(id = subtitleResId),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
