package com.cebolao.lotofacil.core.constants

import java.math.BigDecimal
import java.math.RoundingMode

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
     * All valid numbers as a set for efficient lookups.
     */
    val ALL_NUMBERS: Set<Int> = VALID_NUMBER_RANGE.toSet()
    
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
    
    /**
     * Database name for the app.
     */
    const val DATABASE_NAME = "lotofacil_database"
    
    /**
     * Database version for migration tracking.
     */
    const val DATABASE_VERSION = 1
    
    // ==================== NETWORK CONFIGURATION ====================
    
    /**
     * Base URL for the Lotofácil API.
     */
    const val API_BASE_URL = "https://api.lotofacil.example.com/"
    
    /**
     * Timeout for network requests in milliseconds.
     */
    const val NETWORK_TIMEOUT_MS = 30_000L
    
    /**
     * Number of retry attempts for failed network requests.
     */
    const val NETWORK_RETRY_ATTEMPTS = 3
    
    // ==================== CACHE CONFIGURATION ====================
    
    /**
     * Default cache size for memory caches.
     */
    const val DEFAULT_CACHE_SIZE = 100
    
    /**
     * Default TTL for cache entries in milliseconds (5 minutes).
     */
    const val DEFAULT_CACHE_TTL_MS = 5 * 60 * 1000L
    
    /**
     * Maximum cache size for large datasets.
     */
    const val MAX_CACHE_SIZE = 1000
    
    // ==================== UI CONFIGURATION ====================
    
    /**
     * Maximum number of items to display in lazy lists before pagination.
     */
    const val MAX_LIST_ITEMS = 100
    
    /**
     * Default animation duration in milliseconds.
     */
    const val DEFAULT_ANIMATION_DURATION_MS = 300L
    
    /**
     * Debounce delay for search queries in milliseconds.
     */
    const val SEARCH_DEBOUNCE_MS = 300L
    
    // ==================== PERFORMANCE CONFIGURATION ====================
    
    /**
     * Memory usage threshold for emergency cleanup (percentage).
     */
    const val MEMORY_THRESHOLD_PERCENTAGE = 80
    
    /**
     * Batch size for database operations.
     */
    const val DATABASE_BATCH_SIZE = 100
    
    /**
     * Interval for performance monitoring in milliseconds.
     */
    const val PERFORMANCE_MONITORING_INTERVAL_MS = 5_000L
    
    // ==================== NOTIFICATION CONFIGURATION ====================
    
    /**
     * Channel ID for draw result notifications.
     */
    const val NOTIFICATION_CHANNEL_DRAW_RESULTS = "draw_results"
    
    /**
     * Channel ID for reminder notifications.
     */
    const val NOTIFICATION_CHANNEL_REMINDERS = "reminders"
    
    /**
     * Channel ID for promotional notifications.
     */
    const val NOTIFICATION_CHANNEL_PROMOTIONAL = "promotional"
    
    /**
     * Channel ID for system notifications.
     */
    const val NOTIFICATION_CHANNEL_SYSTEM = "system"
    
    // ==================== BIOMETRIC CONFIGURATION ====================
    
    /**
     * Title for biometric authentication prompts.
     */
    const val BIOMETRIC_TITLE = "Lotofácil Authentication"
    
    /**
     * Subtitle for biometric authentication prompts.
     */
    const val BIOMETRIC_SUBTITLE = "Secure Access"
    
    /**
     * Description for biometric authentication prompts.
     */
    const val BIOMETRIC_DESCRIPTION = "Use your fingerprint or face to authenticate"
    
    // ==================== UTILITY FUNCTIONS ====================
    
    /**
     * Counts how many numbers from the given set match the lookup set.
     * This is a frequently used operation in statistical calculations.
     */
    fun countMatches(numbers: Set<Int>, lookup: Set<Int>): Int {
        return numbers.count { it in lookup }
    }
    
    /**
     * Validates if a number is within the valid Lotofácil range.
     */
    fun isValidNumber(number: Int): Boolean {
        return number in VALID_NUMBER_RANGE
    }
    
    /**
     * Validates if a set of numbers is a valid Lotofácil game.
     */
    fun isValidGame(numbers: Set<Int>): Boolean {
        return numbers.size == GAME_SIZE && numbers.all { it in VALID_NUMBER_RANGE }
    }
    
    /**
     * Formats the game cost as a currency string.
     */
    fun formatGameCost(): String {
        return "R$ ${GAME_COST.setScale(2, RoundingMode.HALF_UP)}"
    }
    
    /**
     * Gets the optimal cache size based on current memory conditions.
     */
    fun getOptimalCacheSize(memoryUsagePercentage: Int): Int {
        return when {
            memoryUsagePercentage >= 80 -> 50
            memoryUsagePercentage >= 60 -> DEFAULT_CACHE_SIZE
            else -> MAX_CACHE_SIZE
        }
    }
    
    /**
     * Gets the optimal batch size for database operations.
     */
    fun getOptimalBatchSize(memoryUsagePercentage: Int): Int {
        return when {
            memoryUsagePercentage >= 80 -> 25
            memoryUsagePercentage >= 60 -> 50
            else -> DATABASE_BATCH_SIZE
        }
    }
}
