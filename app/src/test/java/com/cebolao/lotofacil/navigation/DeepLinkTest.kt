package com.cebolao.lotofacil.navigation

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DeepLinkTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `home destination is serializable`() {
        val destination = Destination.Home
        val serialized = json.encodeToString(Destination.serializer(), destination)
        val deserialized = json.decodeFromString(Destination.serializer(), serialized)
        assertEquals(destination, deserialized)
    }

    @Test
    fun `checker destination with numbers is serializable`() {
        val numbers = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15"
        val destination = Destination.Checker(numbers)
        val serialized = json.encodeToString(Destination.serializer(), destination)
        val deserialized = json.decodeFromString(Destination.serializer(), serialized)
        assertTrue(deserialized is Destination.Checker)
        assertEquals(numbers, (deserialized as Destination.Checker).numbers)
    }

    @Test
    fun `checker destination without numbers is serializable`() {
        val destination = Destination.Checker()
        val serialized = json.encodeToString(Destination.serializer(), destination)
        val deserialized = json.decodeFromString(Destination.serializer(), serialized)
        assertTrue(deserialized is Destination.Checker)
        assertEquals(null, (deserialized as Destination.Checker).numbers)
    }

    @Test
    fun `all destinations have navigation properties`() {
        val destinations = listOf(
            Destination.Home,
            Destination.Filters,
            Destination.GeneratedGames,
            Destination.Checker(),
            Destination.About,
            Destination.Insights,
            Destination.UserStats
        )

        for (destination in destinations) {
            assertNotNull("Missing titleRes for $destination", destination.titleRes)
            assertNotNull("Missing selectedIcon for $destination", destination.selectedIcon)
            assertNotNull("Missing unselectedIcon for $destination", destination.unselectedIcon)
        }
    }

    @Test
    fun `bottom nav destinations are configured`() {
        assertTrue(bottomNavDestinations.isNotEmpty())
        assertTrue(bottomNavDestinations.contains(Destination.Home))
        assertTrue(bottomNavDestinations.contains(Destination.Filters))
        assertTrue(bottomNavDestinations.contains(Destination.GeneratedGames))
    }

    @Test
    fun `checker destination numbers can be parsed`() {
        val numbersStr = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15"
        val numbers = numbersStr.split(',').mapNotNull { it.toIntOrNull() }.toSet()
        assertEquals(15, numbers.size)
        assertTrue(numbers.contains(1))
        assertTrue(numbers.contains(15))
    }

    @Test
    fun `invalid numbers in checker destination are handled gracefully`() {
        val malformedNumbers = "1,invalid,3,4,5,6,7,8,9,10,11,12,13,14,15"
        val numbers = malformedNumbers.split(',').mapNotNull { it.toIntOrNull() }.toSet()
        assertEquals(14, numbers.size)  // "invalid" is skipped
    }
}
