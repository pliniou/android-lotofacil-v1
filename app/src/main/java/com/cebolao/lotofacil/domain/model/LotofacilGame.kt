package com.cebolao.lotofacil.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * Represents a single Lotofácil game.
 * Optimized for performance in Jetpack Compose with @Immutable.
 */
@Immutable
@Serializable
data class LotofacilGame(
    val numbers: Set<Int>,
    val isPinned: Boolean = false,
    val creationTimestamp: Long = System.currentTimeMillis(),
    val usageCount: Int = 0,
    val lastPlayed: Long? = null,
    val id: String = java.util.UUID.randomUUID().toString()
) {
    init {
        require(numbers.size == LotofacilConstants.GAME_SIZE) { "A game must have 15 numbers." }
        require(numbers.all { it in LotofacilConstants.VALID_NUMBER_RANGE }) { "Invalid numbers found." }
    }

    // Computed properties for statistical analysis
    val sum: Int by lazy { numbers.sum() }
    val evens: Int by lazy { numbers.count { it % 2 == 0 } }
    val odds: Int by lazy { LotofacilConstants.GAME_SIZE - evens }
    val primes: Int by lazy { LotofacilConstants.countMatches(numbers, LotofacilConstants.PRIME_NUMBERS) }
    val frame: Int by lazy { LotofacilConstants.countMatches(numbers, LotofacilConstants.FRAME_NUMBERS) }
    val portrait: Int by lazy { LotofacilConstants.countMatches(numbers, LotofacilConstants.PORTRAIT_NUMBERS) }
    val fibonacci: Int by lazy { LotofacilConstants.countMatches(numbers, LotofacilConstants.FIBONACCI_NUMBERS) }
    val multiplesOf3: Int by lazy { LotofacilConstants.countMatches(numbers, LotofacilConstants.MULTIPLES_OF_3) }

    /** Calculates how many numbers from this game repeated from the previous draw. */
    fun repeatedFrom(lastDraw: Set<Int>?): Int {
        return lastDraw?.let { numbers.intersect(it).size } ?: 0
    }

    /** Converts the game to a compact string for efficient storage. */
    fun toCompactString(): String {
        return "${numbers.sorted().joinToString(",")}|$isPinned|$creationTimestamp|$id|$usageCount|$lastPlayed"
    }

    companion object {
        /** Creates a LotofacilGame from a compact string. */
        fun fromCompactString(compactString: String): LotofacilGame? {
            return try {
                val parts = compactString.split("|")
                val numbers = parts[0].split(",").map { it.toInt() }.toSet()
                val isPinned = parts.getOrNull(1)?.toBoolean() ?: false
                val timestamp = parts.getOrNull(2)?.toLong() ?: System.currentTimeMillis()
                val id = parts.getOrNull(3) ?: java.util.UUID.randomUUID().toString()
                val usageCount = parts.getOrNull(4)?.toInt() ?: 0
                val lastPlayed = parts.getOrNull(5)?.toLongOrNull()
                if (numbers.size == LotofacilConstants.GAME_SIZE) {
                    LotofacilGame(numbers, isPinned, timestamp, usageCount, lastPlayed, id)
                } else null
            } catch (_: Exception) {
                null
            }
        }
    }
}
