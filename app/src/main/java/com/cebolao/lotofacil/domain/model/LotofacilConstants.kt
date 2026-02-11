package com.cebolao.lotofacil.domain.model

import com.cebolao.lotofacil.core.constants.AppConstants
import java.math.BigDecimal

/**
 * Compatibility layer for legacy references.
 * Canonical constants now live in AppConstants.
 */
object LotofacilConstants {
    const val GAME_SIZE: Int = AppConstants.GAME_SIZE
    val GAME_COST: BigDecimal = AppConstants.GAME_COST
    val VALID_NUMBER_RANGE: IntRange = AppConstants.VALID_NUMBER_RANGE
    val PRIME_NUMBERS: Set<Int> = AppConstants.PRIME_NUMBERS
    val FRAME_NUMBERS: Set<Int> = AppConstants.FRAME_NUMBERS
    val PORTRAIT_NUMBERS: Set<Int> = AppConstants.PORTRAIT_NUMBERS
    val FIBONACCI_NUMBERS: Set<Int> = AppConstants.FIBONACCI_NUMBERS
    val MULTIPLES_OF_3: Set<Int> = AppConstants.MULTIPLES_OF_3

    fun countMatches(numbers: Set<Int>, lookup: Set<Int>): Int =
        AppConstants.countMatches(numbers, lookup)
}
