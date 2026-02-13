package com.cebolao.lotofacil.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cebolao.lotofacil.ui.screens.about.AboutScreen
import com.cebolao.lotofacil.ui.screens.checker.CheckerScreen
import com.cebolao.lotofacil.ui.screens.filters.FiltersScreen
import com.cebolao.lotofacil.ui.screens.generated.GeneratedGamesScreen
import com.cebolao.lotofacil.ui.screens.home.HomeScreen
import com.cebolao.lotofacil.ui.screens.statistics.StatisticsScreen
import com.cebolao.lotofacil.ui.screens.stats.UserStatsScreen

/**
 * Navigation utilities for type-safe navigation.
 */
fun NavController.navigateToDestination(destination: Destination) {
    navigate(destination) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Navigation graph setup using type-safe destinations.
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: androidx.navigation.NavHostController,
    startDestination: Destination = Destination.Home,
    onNavigateToGeneratedGames: () -> Unit = {}
) {
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(durationMillis = 280)
            ) + fadeIn(animationSpec = tween(durationMillis = 220))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(durationMillis = 240)
            ) + fadeOut(animationSpec = tween(durationMillis = 180))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(durationMillis = 280)
            ) + fadeIn(animationSpec = tween(durationMillis = 220))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(durationMillis = 240)
            ) + fadeOut(animationSpec = tween(durationMillis = 180))
        }
    ) {
        composable<Destination.Home> {
            HomeScreen(
                onNavigateToInsights = {
                    navController.navigateToDestination(Destination.Insights)
                }
            )
        }
        
        composable<Destination.Filters> {
            FiltersScreen {
                onNavigateToGeneratedGames()
            }
        }
        
        composable<Destination.GeneratedGames> {
            GeneratedGamesScreen()
        }
        
        composable<Destination.Checker> {
            CheckerScreen()
        }
        
        composable<Destination.About> {
            AboutScreen(onNavigateToUserStats = { navController.navigate(Destination.UserStats) })
        }

        composable<Destination.Insights> {
            StatisticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Destination.UserStats> {
            UserStatsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

