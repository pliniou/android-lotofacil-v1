package com.cebolao.lotofacil.domain.model

import androidx.compose.runtime.Immutable

/**
 * Represents a check history record: a game checked against a specific contest.
 * Immutable to ensure thread safety and predictability.
 */
@Immutable
data class CheckHistory(
    val id: Long = 0,
    val gameNumbers: Set<Int>,
    val contestNumber: Int,
    val checkedAt: String, // ISO 8601 format, e.g., "2026-02-06T10:30:00Z"
    val hits: Int, // Best hit from this check
    val scoreCounts: Map<Int, Int>, // Distribution: hits (15,14,13...) -> count
    val lastHitContest: Int? = null, // Last contest where game had hits
    val lastHitScore: Int? = null, // Hits count at lastHitContest
    val notes: String? = null // Optional notes by user
)
