package com.cebolao.lotofacil.ui.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalHapticFeedback
import android.util.Log
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.ThemeMode
import com.cebolao.lotofacil.ui.components.AppScreenDefaults
import com.cebolao.lotofacil.ui.components.AppScreenScaffold
import com.cebolao.lotofacil.ui.components.InfoDialog
import com.cebolao.lotofacil.ui.components.screenContentPadding
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    selectedThemeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeSelected: (ThemeMode) -> Unit = {},
    onNavigateToUserStats: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    var dialogContent by remember { mutableStateOf<InfoItem?>(null) }
    val hapticFeedback = LocalHapticFeedback.current

    AboutScreenContent(
        modifier = modifier,
        dialogContent = dialogContent,
        selectedThemeMode = selectedThemeMode,
        onThemeModeSelected = onThemeModeSelected,
        onNavigateToUserStats = onNavigateToUserStats,
        onBackClick = onBackClick,
        onAction = { action ->
            when (action) {
                is AboutAction.ShowDialog -> {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    dialogContent = action.item
                }

                AboutAction.DismissDialog -> {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    dialogContent = null
                }
            }
        }
    )
}

sealed class AboutAction {
    data class ShowDialog(val item: InfoItem) : AboutAction()
    object DismissDialog : AboutAction()
}

@Composable
fun AboutScreenContent(
    modifier: Modifier = Modifier,
    dialogContent: InfoItem? = null,
    selectedThemeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeSelected: (ThemeMode) -> Unit = {},
    onNavigateToUserStats: () -> Unit = {},
    onBackClick: (() -> Unit)? = null,
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
        val items = listOf(
            InfoItem.Tips(Icons.Default.TipsAndUpdates, content = { TipsInfoContent() }),
            InfoItem.Rules(Icons.Default.Gavel, content = { RulesInfoContent() }),
            InfoItem.Probabilities(Icons.Default.Calculate, content = { ProbabilitiesTable() }),
            InfoItem.Bolao(Icons.Default.Group, content = { BolaoInfoContent() }),
            InfoItem.Purpose(Icons.Default.Lightbulb, content = { PurposeInfoContent() })
        )
        
        // Validate uniqueness and log any issues
        try {
            InfoItem.validateUniqueItems(items)
        } catch (e: IllegalStateException) {
            Log.e("AboutScreen", "Duplicate InfoItems detected: ${e.message}")
        }
        
        // Ensure uniqueness by titleResId as fallback
        items.distinctBy { it.titleResId }
    }

    val legalItems = remember {
        val items = listOf(
            InfoItem.Legal(Icons.Default.Info, content = { LegalInfoContent() }),
            InfoItem.Privacy(Icons.Default.PrivacyTip, content = { PrivacyInfoContent() })
        )
        
        // Validate uniqueness
        try {
            InfoItem.validateUniqueItems(items)
        } catch (e: IllegalStateException) {
            Log.e("AboutScreen", "Duplicate legal InfoItems detected: ${e.message}")
        }
        
        items.distinctBy { it.titleResId }
    }

    val links = remember {
        listOf(
            AboutExternalLink(
                titleResId = R.string.link_caixa_title,
                subtitleResId = R.string.link_caixa_subtitle,
                icon = Icons.Default.Language,
                url = "https://loterias.caixa.gov.br/Paginas/Lotofacil.aspx"
            ),
            AboutExternalLink(
                titleResId = R.string.link_github_title,
                subtitleResId = R.string.link_github_subtitle,
                icon = Icons.Default.Code,
                url = "https://github.com/pliniou"
            )
        )
    }
    val guideSectionTitle = stringResource(id = R.string.about_section_guide)
    val legalSectionTitle = stringResource(id = R.string.about_section_legal)
    val linksSectionTitle = stringResource(id = R.string.about_section_links)

    AppScreenScaffold(
        modifier = modifier.fillMaxSize(),
        title = stringResource(id = R.string.about_section_guide),
        subtitle = stringResource(id = R.string.about_header_subtitle),
        icon = Icons.Default.Info,
        onBackClick = onBackClick
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .screenContentPadding(innerPadding),
            contentPadding = AppScreenDefaults.listContentPadding(
                horizontal = AppSpacing.lg,
                top = AppSpacing.lg,
                bottom = AppSpacing.xxxl
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            aboutInfoSection(
                sectionKey = "guide",
                title = guideSectionTitle,
                items = guideItems,
                onInfoClick = { onAction(AboutAction.ShowDialog(it)) }
            )

            item(key = "legal_spacing") {
                Spacer(modifier = Modifier.height(AppSpacing.md))
            }

            aboutInfoSection(
                sectionKey = "legal",
                title = legalSectionTitle,
                items = legalItems,
                onInfoClick = { onAction(AboutAction.ShowDialog(it)) }
            )

            item(key = "links_spacing") {
                Spacer(modifier = Modifier.height(AppSpacing.md))
            }

            aboutLinksSection(
                sectionKey = "links",
                title = linksSectionTitle,
                links = links
            )

            item(key = "user_stats_spacer") {
                Spacer(modifier = Modifier.height(AppSpacing.md))
            }

            item(key = "theme_settings_card") {
                ThemeSettingsCard(
                    selectedThemeMode = selectedThemeMode,
                    onThemeModeSelected = onThemeModeSelected
                )
            }

            item(key = "user_stats_card") {
                UserStatsCard(onNavigateToUserStats)
            }
        }
    }
}
