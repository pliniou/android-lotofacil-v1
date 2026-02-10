package com.cebolao.lotofacil.domain.model

import java.math.BigDecimal

/**
 * Centraliza todas as constantes relacionadas à lógica do jogo Lotofácil.
 * Isso evita a duplicação de "números mágicos" e facilita a manutenção.
 */
object LotofacilConstants {
    const val GAME_SIZE = 15
    val GAME_COST: BigDecimal = BigDecimal("3.50")
    val VALID_NUMBER_RANGE = 1..25
    val ALL_NUMBERS: Set<Int> = VALID_NUMBER_RANGE.toSet()

    // Statistical subsets reused across the app to avoid repeated set allocations.
    val PRIME_NUMBERS: Set<Int> = setOf(2, 3, 5, 7, 11, 13, 17, 19, 23)
    val FRAME_NUMBERS: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 10, 11, 15, 16, 20, 21, 22, 23, 24, 25)
    val PORTRAIT_NUMBERS: Set<Int> = setOf(7, 8, 9, 12, 13, 14, 17, 18, 19)
    val FIBONACCI_NUMBERS: Set<Int> = setOf(1, 2, 3, 5, 8, 13, 21)
    val MULTIPLES_OF_3: Set<Int> = setOf(3, 6, 9, 12, 15, 18, 21, 24)

    fun countMatches(numbers: Set<Int>, lookup: Set<Int>): Int {
        return numbers.count { it in lookup }
    }
}
