package com.cebolao.lotofacil.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableSet

@Immutable
data class HomePrizeTier(
    val faixa: Int?,
    val description: String?,
    val winners: Int?,
    val prizeValue: Double?
)

@Immutable
data class HomeWinnerLocation(
    val winnersCount: Int?,
    val city: String?,
    val state: String?
)

@Immutable
data class HomeLastContest(
    val contest: Int,
    val date: String?,
    val numbers: ImmutableSet<Int>,
    val sum: Int,
    val evens: Int,
    val odds: Int,
    val primes: Int,
    val frame: Int,
    val portrait: Int,
    val fibonacci: Int,
    val multiplesOf3: Int,
    val prizes: List<HomePrizeTier>,
    val winnerLocations: List<HomeWinnerLocation>,
    val accumulated: Boolean
)

@Immutable
data class HomeNextContest(
    val contestNumber: Int,
    val date: String?,
    val prizeEstimate: Double?,
    val isAccumulated: Boolean,
    val source: HomeNextContestSource
)

enum class HomeNextContestSource {
    OFFICIAL,
    DERIVED
}
