package com.cebolao.lotofacil.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.cebolao.lotofacil.navigation.Destination
import com.cebolao.lotofacil.navigation.selectedIcon
import com.cebolao.lotofacil.navigation.titleRes
import com.cebolao.lotofacil.navigation.unselectedIcon
import com.cebolao.lotofacil.ui.theme.AppElevation
import com.cebolao.lotofacil.ui.testtags.AppTestTags

/**
 * Custom bottom navigation bar with modern styling and optimized interactions.
 * Uses Material3 NavigationBar with consistent elevation and spacing.
 */
@Composable
fun AppBottomBar(
    destinations: List<Destination>,
    selectedDestination: Destination,
    onDestinationSelected: (Destination) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current

    NavigationBar(
        modifier = modifier.testTag(AppTestTags.BottomNav),
        containerColor = colors.surfaceContainer,
        tonalElevation = AppElevation.sm,
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        destinations.forEach { destination ->
            val selected = destination == selectedDestination
            val itemTag = "${AppTestTags.BottomNavItemPrefix}${destination.route.substringBefore("?")}"
            
            NavigationBarItem(
                modifier = Modifier.testTag(itemTag),
                selected = selected,
                onClick = {
                    if (!selected) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDestinationSelected(destination)
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = stringResource(id = destination.titleRes)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = destination.titleRes),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colors.primary,
                    selectedTextColor = colors.primary,
                    unselectedIconColor = colors.onSurfaceVariant,
                    unselectedTextColor = colors.onSurfaceVariant,
                    indicatorColor = colors.primaryContainer
                )
            )
        }
    }
}
