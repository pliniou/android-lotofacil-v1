package com.cebolao.lotofacil.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

/**
 * Result of checking a player's game against historical draws.  Contains
 * counts of hits, information about recent contests and the last hit.
 */
@Immutable
data class CheckResult(
    val scoreCounts: ImmutableMap<Int, Int> = persistentMapOf(),
    val lastHitContest: Int? = null,
    val lastHitScore: Int? = null,
    val lastCheckedContest: Int = 0,
    val recentHits: ImmutableList<Pair<Int, Int>> = persistentListOf()
)
