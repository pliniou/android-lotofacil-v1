package com.cebolao.lotofacil.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
data class FrequencyAnalysis(
    val frequencies: Map<Int, Int>,
    val topNumbers: List<Int>,
    val overdueNumbers: List<Pair<Int, Int>>,
    val totalDraws: Int
)

@Immutable
data class PatternAnalysis(
    val size: Int,
    val patterns: List<Pair<Set<Int>, Int>>,
    val totalDraws: Int
)

@Immutable
data class TrendAnalysis(
    val type: TrendType,
    val timeline: List<Pair<Int, Float>>,
    val averageValue: Float
)

@Stable
enum class TrendType {
    SUM, EVENS, PRIMES, FRAME, PORTRAIT, FIBONACCI, MULTIPLES_OF_3
}
