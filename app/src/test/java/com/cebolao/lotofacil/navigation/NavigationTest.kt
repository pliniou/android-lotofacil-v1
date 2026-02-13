package com.cebolao.lotofacil.navigation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NavigationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `bottom nav destinations list contains expected items`() {
        val expectedCount = 5  // Home, Filters, GeneratedGames, Checker, About
        assertEquals(expectedCount, bottomNavDestinations.size)
    }

    @Test
    fun `each destination has valid title resource`() {
        val destinations = listOf(
            Destination.Home,
            Destination.Filters,
            Destination.GeneratedGames,
            Destination.Checker(),
            Destination.About,
            Destination.Insights,
            Destination.UserStats
        )

        destinations.forEach { destination ->
            assertTrue("Invalid titleRes for $destination", destination.titleRes > 0)
        }
    }

    @Test
    fun `each destination has selected icon`() {
        val destinations = listOf(
            Destination.Home,
            Destination.Filters,
            Destination.GeneratedGames,
            Destination.Checker(),
            Destination.About,
            Destination.Insights,
            Destination.UserStats
        )

        destinations.forEach { destination ->
            val icon = destination.selectedIcon
            assertNotNull("Missing selectedIcon for $destination", icon)
        }
    }

    @Test
    fun `each destination has unselected icon`() {
        val destinations = listOf(
            Destination.Home,
            Destination.Filters,
            Destination.GeneratedGames,
            Destination.Checker(),
            Destination.About,
            Destination.Insights,
            Destination.UserStats
        )

        destinations.forEach { destination ->
            val icon = destination.unselectedIcon
            assertNotNull("Missing unselectedIcon for $destination", icon)
        }
    }

    @Test
    fun `checker destination preserves numbers parameter`() {
        val numbers = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15"
        val destination = Destination.Checker(numbers)
        assertEquals(numbers, destination.numbers)
    }

    @Test
    fun `checker destination default numbers is null`() {
        val destination = Destination.Checker()
        assertNull(destination.numbers)
    }
}
