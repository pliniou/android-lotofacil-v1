package com.cebolao.lotofacil.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * Represents a single Lotofácil draw.  Derived statistics (sum, evens,
 * primes, etc.) are computed eagerly in the constructor to avoid
 * recomputation and potential memory leaks.
 */
@Immutable
@Serializable
data class HistoricalDraw(
    val contestNumber: Int,
    val numbers: Set<Int>,
    val date: String? = null,
    val prizes: List<PrizeTier> = emptyList(),
    val winners: List<WinnerLocation> = emptyList(),
    val nextContest: Int? = null,
    val nextDate: String? = null,
    val nextEstimate: Double? = null,
    val accumulated: Boolean = false
) {
    val sum: Int = numbers.sum()
    val evens: Int = numbers.count { it % 2 == 0 }
    val odds: Int = LotofacilConstants.GAME_SIZE - evens
    val primes: Int = LotofacilConstants.countMatches(numbers, LotofacilConstants.PRIME_NUMBERS)
    val fibonacci: Int = LotofacilConstants.countMatches(numbers, LotofacilConstants.FIBONACCI_NUMBERS)
    val frame: Int = LotofacilConstants.countMatches(numbers, LotofacilConstants.FRAME_NUMBERS)
    val portrait: Int = LotofacilConstants.countMatches(numbers, LotofacilConstants.PORTRAIT_NUMBERS)
    val multiplesOf3: Int = LotofacilConstants.countMatches(numbers, LotofacilConstants.MULTIPLES_OF_3)
}

@Immutable
@Serializable
data class PrizeTier(
    val description: String,
    val winners: Int,
    val prizeValue: Double
)

@Immutable
@Serializable
data class WinnerLocation(
    val winnersCount: Int,
    val city: String,
    val state: String
)
