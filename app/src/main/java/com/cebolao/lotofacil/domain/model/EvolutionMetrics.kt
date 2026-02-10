package com.cebolao.lotofacil.domain.model

import androidx.compose.runtime.Immutable

/**
 * Represents a chart data point for visualizations.
 */
@Immutable
data class ChartDataPoint(
    val label: String,
    val value: Float,
    val timestamp: Long? = null
)

/**
 * Evolution data for tracking metrics over time.
 */
@Immutable
data class EvolutionMetrics(
    val numberFrequency: List<ChartDataPoint> = emptyList(), // (1-25, frequency)
    val timelineMetrics: List<ChartDataPoint> = emptyList(), // (contest, hits)
    val winDistribution: Map<Int, Int> = emptyMap(), // (hits count, occurrences)
    val avgNumberPerContest: Double = 0.0,
    val trendsAnalysis: String = "" // Text summary of trends
)
