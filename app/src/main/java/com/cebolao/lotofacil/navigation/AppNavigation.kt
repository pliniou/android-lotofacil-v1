package com.cebolao.lotofacil.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navDeepLink
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cebolao.lotofacil.ui.screens.about.AboutScreen
import com.cebolao.lotofacil.ui.screens.checker.CheckerScreen
import com.cebolao.lotofacil.ui.screens.filters.FiltersScreen
import com.cebolao.lotofacil.ui.screens.generated.GameAnalysisScreen
import com.cebolao.lotofacil.ui.screens.generated.GeneratedGamesScreen
import com.cebolao.lotofacil.ui.screens.home.HomeScreen
import com.cebolao.lotofacil.ui.screens.statistics.StatisticsScreen
import com.cebolao.lotofacil.ui.screens.user_stats.UserStatsScreen
import com.cebolao.lotofacil.domain.model.ThemeMode

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

private fun NavController.navigateUpOrDestination(destination: Destination) {
    if (!popBackStack()) {
        navigateToDestination(destination)
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
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeSelected: (ThemeMode) -> Unit = {},
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
        
        composable<Destination.Filters> { backStackEntry ->
            val destination = backStackEntry.toRoute<Destination.Filters>()
            FiltersScreen(
                initialPreset = destination.preset,
                onNavigateToGeneratedGames = onNavigateToGeneratedGames,
                onBackClick = {
                    navController.navigateToDestination(Destination.Home)
                }
            )
        }
        
        composable<Destination.GeneratedGames> {
            GeneratedGamesScreen(
                onNavigateToFilters = {
                    navController.navigateToDestination(Destination.Filters())
                },
                onNavigateToAnalysis = { game ->
                    navController.navigate(Destination.Analysis(gameId = game.id))
                },
                onDuplicateAndEdit = { game ->
                    navController.navigate(Destination.Filters(preset = game.toGeneratorPreset()))
                },
                onBackClick = {
                    navController.navigateToDestination(Destination.Filters())
                }
            )
        }
        
        composable<Destination.Checker> {
            CheckerScreen(
                onBackClick = {
                    navController.navigateToDestination(Destination.GeneratedGames)
                },
                onGenerateNewGame = {
                    navController.navigateToDestination(Destination.Filters())
                },
                onRefineWithPattern = { numbers ->
                    navController.navigate(Destination.Filters(preset = numbers.toGeneratorPreset()))
                }
            )
        }

        composable<Destination.Analysis>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "cebolao://game/{gameId}" }
            )
        ) { backStackEntry ->
            val destination = backStackEntry.toRoute<Destination.Analysis>()
            GameAnalysisScreen(
                gameId = destination.gameId,
                onBackClick = {
                    navController.navigateUpOrDestination(Destination.GeneratedGames)
                },
                onGenerateVariation = { game ->
                    navController.navigate(Destination.Filters(preset = game.toGeneratorPreset()))
                }
            )
        }
        
        composable<Destination.About> {
            AboutScreen(
                selectedThemeMode = themeMode,
                onThemeModeSelected = onThemeModeSelected,
                onNavigateToUserStats = { navController.navigate(Destination.UserStats) },
                onBackClick = {
                    navController.navigateToDestination(Destination.Home)
                }
            )
        }

        composable<Destination.Insights> {
            StatisticsScreen(
                onNavigateBack = { navController.navigateUpOrDestination(Destination.Home) }
            )
        }

        composable<Destination.UserStats> {
            UserStatsScreen(
                onNavigateBack = { navController.navigateUpOrDestination(Destination.About) }
            )
        }
    }
}
