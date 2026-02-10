package com.cebolao.lotofacil.domain.model

import androidx.compose.runtime.Immutable

enum class GameStatisticType {
    SUM,
    EVENS,
    ODDS,
    PRIMES,
    FIBONACCI,
    FRAME,
    PORTRAIT,
    MULTIPLES_OF_3
}

@Immutable
data class GameStatistic(
    val type: GameStatisticType,
    val value: Int
)
