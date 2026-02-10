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
import androidx.navigation.navArgument
import androidx.navigation.NavType
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
    navigate(destination.route) {
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
        startDestination = startDestination.route,
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
        composable(Destination.Home.route) {
            HomeScreen(
                onExploreFilters = {
                    navController.navigateToDestination(Destination.Filters)
                },
                onOpenChecker = {
                    navController.navigateToDestination(Destination.Checker())
                },
                onNavigateToInsights = {
                    navController.navigateToDestination(Destination.Insights)
                },
                onNavigateToAbout = {
                    navController.navigateToDestination(Destination.About)
                },
                onNavigateToGames = {
                    navController.navigateToDestination(Destination.GeneratedGames)
                }
            )
        }
        
        composable(Destination.Filters.route) {
            FiltersScreen {
                onNavigateToGeneratedGames()
            }
        }
        
        composable(Destination.GeneratedGames.route) {
            GeneratedGamesScreen()
        }
        
        composable(
            route = Destination.Checker.ROUTE_PATTERN,
            arguments = listOf(
                navArgument("numbers") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            CheckerScreen()
        }
        
        composable(Destination.About.route) {
            AboutScreen(onNavigateToUserStats = { navController.navigate(Destination.UserStats.route) })
        }

        composable(Destination.Insights.route) {
            StatisticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Destination.UserStats.route) {
            UserStatsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
