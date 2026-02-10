package com.cebolao.lotofacil.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.cebolao.lotofacil.R
import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations that encapsulate routes and arguments.
 * This replaces string-based navigation with compile-time safety.
 */
@Stable
@Serializable
sealed interface Destination {
    val route: String

    @Serializable
    data object Home : Destination {
        override val route = "home"
    }

    @Serializable
    data object Filters : Destination {
        override val route = "filters"
    }

    @Serializable
    data object GeneratedGames : Destination {
        override val route = "games"
    }

    @Serializable
    data class Checker(val numbers: String? = null) : Destination {
        override val route = if (numbers.isNullOrBlank()) {
            BASE_ROUTE
        } else {
            "$BASE_ROUTE?numbers=$numbers"
        }

        companion object {
            const val BASE_ROUTE = "checker"
            const val ROUTE_PATTERN = "$BASE_ROUTE?numbers={numbers}"
        }
    }

    @Serializable
    data object About : Destination {
        override val route = "about"
    }

    @Serializable
    data object Insights : Destination {
        override val route = "insights"
    }

    @Serializable
    data object UserStats : Destination {
        override val route = "user_stats"
    }
}

// Destinos exibidos na barra de navegação inferior (4 abas)
val bottomNavDestinations = listOf(
    Destination.Home,
    Destination.Filters,
    Destination.GeneratedGames,
    Destination.Checker()
)

val Destination.titleRes: Int
    @StringRes
    get() = when (this) {
        Destination.Home -> R.string.nav_home
        Destination.Filters -> R.string.nav_filters
        Destination.GeneratedGames -> R.string.nav_games
        is Destination.Checker -> R.string.nav_checker
        Destination.About -> R.string.nav_about
        Destination.Insights -> R.string.insights_title
        Destination.UserStats -> R.string.nav_user_stats
    }

val Destination.selectedIcon: ImageVector
    get() = when (this) {
        Destination.Home -> Icons.Filled.Home
        Destination.Filters -> Icons.Filled.Tune
        Destination.GeneratedGames -> Icons.AutoMirrored.Filled.ListAlt
        is Destination.Checker -> Icons.Filled.Analytics
        Destination.About -> Icons.Filled.Info
        Destination.Insights -> Icons.Filled.Analytics
        Destination.UserStats -> Icons.Default.Person
    }

val Destination.unselectedIcon: ImageVector
    get() = when (this) {
        Destination.Home -> Icons.Outlined.Home
        Destination.Filters -> Icons.Outlined.Tune
        Destination.GeneratedGames -> Icons.AutoMirrored.Filled.ListAlt
        is Destination.Checker -> Icons.Outlined.Analytics
        Destination.About -> Icons.Outlined.Info
        Destination.Insights -> Icons.Outlined.Analytics
        Destination.UserStats -> Icons.Default.Person
    }
