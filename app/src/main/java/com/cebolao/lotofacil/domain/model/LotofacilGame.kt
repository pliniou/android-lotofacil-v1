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

    /**
     * Compact representation used for local sharing/tests.
     * Format: numbers|pinned|createdAt|usageCount|lastPlayed|id
     */
    fun toCompactString(): String {
        val numbersPart = numbers.sorted().joinToString(",")
        val pinnedPart = if (isPinned) "1" else "0"
        val lastPlayedPart = lastPlayed?.toString().orEmpty()
        return listOf(
            numbersPart,
            pinnedPart,
            creationTimestamp.toString(),
            usageCount.toString(),
            lastPlayedPart,
            id
        ).joinToString("|")
    }

    companion object {
        fun fromCompactString(compact: String): LotofacilGame? {
            return runCatching {
                val parts = compact.split("|")
                require(parts.size >= 2)

                val numbers = parts[0]
                    .split(",")
                    .mapNotNull { it.toIntOrNull() }
                    .toSet()

                val pinned = parts[1] == "1" || parts[1].equals("true", ignoreCase = true)
                val createdAt = parts.getOrNull(2)?.toLongOrNull() ?: System.currentTimeMillis()
                val usageCount = parts.getOrNull(3)?.toIntOrNull() ?: 0
                val lastPlayed = parts.getOrNull(4)?.toLongOrNull()
                val id = parts.getOrNull(5).orEmpty().ifBlank { java.util.UUID.randomUUID().toString() }

                LotofacilGame(
                    numbers = numbers,
                    isPinned = pinned,
                    creationTimestamp = createdAt,
                    usageCount = usageCount,
                    lastPlayed = lastPlayed,
                    id = id
                )
            }.getOrNull()
        }
    }
}
