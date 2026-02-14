package com.cebolao.lotofacil.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.ThemeMode
import com.cebolao.lotofacil.navigation.AppNavigation
import com.cebolao.lotofacil.navigation.Destination
import com.cebolao.lotofacil.navigation.bottomNavDestinations
import com.cebolao.lotofacil.navigation.navigateToDestination
import com.cebolao.lotofacil.ui.components.AppBottomBar
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeSelected: (ThemeMode) -> Unit = {},
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val selectedBottomDestination = when {
        currentDestination?.hierarchy?.any { navDestination ->
            navDestination.hasRoute(Destination.Analysis::class)
        } == true -> Destination.GeneratedGames

        else -> bottomNavDestinations.find { destination ->
            currentDestination?.hierarchy?.any { navDestination ->
                navDestination.hasRoute(destination::class)
            } == true
        } ?: bottomNavDestinations.first()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                TextButton(onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    navController.navigateToDestination(Destination.Home)
                }) {
                    Text(text = stringResource(id = R.string.nav_home))
                }
                TextButton(onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    navController.navigateToDestination(Destination.Filters())
                }) {
                    Text(text = stringResource(id = R.string.nav_filters))
                }
                TextButton(onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    navController.navigateToDestination(Destination.GeneratedGames)
                }) {
                    Text(text = stringResource(id = R.string.nav_games))
                }
                TextButton(onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    navController.navigate(Destination.Insights)
                }) {
                    Text(text = stringResource(id = R.string.insights_title))
                }
            }
        }
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = {
                AppBottomBar(
                    destinations = bottomNavDestinations,
                    selectedDestination = selectedBottomDestination,
                    onDestinationSelected = { destination ->
                        val isSelected = currentDestination?.hierarchy?.any { navDestination ->
                            navDestination.hasRoute(destination::class)
                        } == true
                        if (!isSelected) {
                            navController.navigateToDestination(destination)
                        }
                    }
                )
            }
        ) { paddingValues ->
            AppNavigation(
                modifier = Modifier.padding(paddingValues),
                navController = navController,
                themeMode = themeMode,
                onThemeModeSelected = onThemeModeSelected,
                onNavigateToGeneratedGames = {
                    navController.navigateToDestination(Destination.GeneratedGames)
                }
            )
        }
    }
}
