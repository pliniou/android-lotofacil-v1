package com.cebolao.lotofacil.core.constants

import java.math.BigDecimal

/**
 * Unified application constants for the Lotofácil app.
 * Consolidates all constants from AppConstants and LotofacilConstants.
 * This eliminates duplication and provides a single source of truth.
 */
object AppConstants {
    
    // ==================== GAME CONFIGURATION ====================
    
    /**
     * Number of numbers in a Lotofácil game.
     */
    const val GAME_SIZE = 15
    
    /**
     * Cost of a single Lotofácil game.
     */
    val GAME_COST: BigDecimal = BigDecimal("3.50")
    
    /**
     * Valid number range for Lotofácil (1-25).
     */
    val VALID_NUMBER_RANGE = 1..25

    /**
     * Maximum attempts for game generation to prevent infinite loops.
     */
    const val MAX_GAME_GENERATION_ATTEMPTS = 50_000
    
    // ==================== STATISTICAL SUBSETS ====================
    
    /**
     * Prime numbers within the valid range.
     */
    val PRIME_NUMBERS: Set<Int> = setOf(2, 3, 5, 7, 11, 13, 17, 19, 23)
    
    /**
     * Frame numbers (border numbers in the grid).
     */
    val FRAME_NUMBERS: Set<Int> = setOf(
        1, 2, 3, 4, 5, 6,      // Top row
        10, 11,                  // Middle sides
        15, 16, 20, 21,         // Middle sides
        22, 23, 24, 25          // Bottom row
    )
    
    /**
     * Portrait numbers (middle column).
     */
    val PORTRAIT_NUMBERS: Set<Int> = setOf(7, 8, 9, 12, 13, 14, 17, 18, 19)
    
    /**
     * Fibonacci numbers within the valid range.
     */
    val FIBONACCI_NUMBERS: Set<Int> = setOf(1, 2, 3, 5, 8, 13, 21)
    
    /**
     * Multiples of 3 within the valid range.
     */
    val MULTIPLES_OF_3: Set<Int> = setOf(3, 6, 9, 12, 15, 18, 21, 24)
    
    // ==================== FILE CONFIGURATION ====================
    
    /**
     * Asset file containing historical draw results.
     */
    const val HISTORY_ASSET_FILE = "lotofacil_resultados.txt"

    // ==================== UTILITY FUNCTIONS ====================
    
    /**
     * Counts how many numbers from the given set match the lookup set.
     * This is a frequently used operation in statistical calculations.
     */
    fun countMatches(numbers: Set<Int>, lookup: Set<Int>): Int {
        return numbers.count { it in lookup }
    }

}
