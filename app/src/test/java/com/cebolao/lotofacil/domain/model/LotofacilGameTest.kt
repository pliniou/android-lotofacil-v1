package com.cebolao.lotofacil.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LotofacilGameTest {

    @Test
    fun `game with valid numbers should be created successfully`() {
        val numbers = (1..15).toSet()
        val game = LotofacilGame(numbers)

        assertEquals(15, game.numbers.size)
        assertEquals(120, game.sum)
        assertEquals(7, game.evens)
        assertEquals(8, game.odds)
    }

    @Test
    fun `game should correctly count prime numbers`() {
        val numbers = setOf(2, 3, 5, 7, 11, 13, 17, 19, 23, 1, 4, 6, 8, 10, 12)
        val game = LotofacilGame(numbers)

        assertEquals(9, game.primes)
    }

    @Test
    fun `game should correctly count frame numbers`() {
        val frameNumbers = setOf(1, 2, 3, 4, 5, 6, 10, 11, 15, 16, 20, 21, 22, 23, 24)
        val game = LotofacilGame(frameNumbers)

        assertEquals(15, game.frame)
    }

    @Test
    fun `game should correctly count portrait numbers`() {
        val portraitNumbers = setOf(7, 8, 9, 12, 13, 14, 17, 18, 19, 1, 2, 3, 4, 5, 6)
        val game = LotofacilGame(portraitNumbers)

        assertEquals(9, game.portrait)
    }

    @Test
    fun `game should correctly count fibonacci numbers`() {
        val fibonacciNumbers = setOf(1, 2, 3, 5, 8, 13, 21, 4, 6, 7, 9, 10, 11, 12, 14)
        val game = LotofacilGame(fibonacciNumbers)

        assertEquals(7, game.fibonacci)
    }

    @Test
    fun `game should correctly count multiples of 3`() {
        val multiplesOf3 = setOf(3, 6, 9, 12, 15, 18, 21, 24, 1, 2, 4, 5, 7, 8, 10)
        val game = LotofacilGame(multiplesOf3)

        assertEquals(8, game.multiplesOf3)
    }

    @Test
    fun `repeatedFrom should return correct intersection count`() {
        val gameNumbers = setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
        val lastDraw = setOf(5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19)
        val game = LotofacilGame(gameNumbers)

        assertEquals(11, game.repeatedFrom(lastDraw))
    }

    @Test
    fun `repeatedFrom with null lastDraw should return 0`() {
        val numbers = (1..15).toSet()
        val game = LotofacilGame(numbers)

        assertEquals(0, game.repeatedFrom(null))
    }

    @Test
    fun `toCompactString should produce parseable string`() {
        val numbers = (1..15).toSet()
        val game = LotofacilGame(numbers, isPinned = true, creationTimestamp = 1234567890L)

        val compactString = game.toCompactString()
        val parsed = LotofacilGame.fromCompactString(compactString)

        assertTrue(parsed != null)
        assertEquals(numbers, parsed!!.numbers)
        assertEquals(true, parsed.isPinned)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `game with less than 15 numbers should throw exception`() {
        val numbers = (1..14).toSet()
        LotofacilGame(numbers)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `game with numbers outside 1-25 range should throw exception`() {
        val numbers = setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 26)
        LotofacilGame(numbers)
    }
}
