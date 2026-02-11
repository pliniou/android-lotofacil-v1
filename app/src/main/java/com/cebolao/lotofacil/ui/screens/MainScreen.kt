package com.cebolao.lotofacil.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cebolao.lotofacil.navigation.AppNavigation
import com.cebolao.lotofacil.navigation.Destination
import com.cebolao.lotofacil.navigation.bottomNavDestinations
import com.cebolao.lotofacil.navigation.navigateToDestination
import com.cebolao.lotofacil.ui.components.AppBottomBar
import com.cebolao.lotofacil.viewmodels.MainViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    if (!uiState.isReady) return

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            AppBottomBar(
                destinations = bottomNavDestinations,
                selectedDestination = bottomNavDestinations.find { destination ->
                    currentDestination?.hierarchy?.any { navDestination ->
                        navDestination.route?.substringBefore("?") == destination.route.substringBefore("?")
                    } == true
                } ?: bottomNavDestinations.first(),
                onDestinationSelected = { destination ->
                    val isSelected = currentDestination?.hierarchy?.any { navDestination ->
                        navDestination.route?.substringBefore("?") == destination.route.substringBefore("?")
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
            onNavigateToGeneratedGames = {
                navController.navigateToDestination(Destination.GeneratedGames)
            }
        )
    }
}
