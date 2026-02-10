package com.cebolao.lotofacil.domain.service

import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Simple ML-based predictor for Lotofácil numbers.
 * Uses statistical analysis and frequency patterns to suggest numbers.
 *
 * NOTE: This is for entertainment purposes only.
 * Lottery results are random and predictions have no real predictive power.
 */
class SimpleLotofacilPredictor @Inject constructor(
    private val dispatchersProvider: DispatchersProvider
) {

    /**
     * Predict likely numbers based on historical patterns.
     *
     * @param history Historical draw data
     * @param suggestionCount How many numbers to suggest
     * @return Set of suggested numbers
     */
    suspend fun predictNumbers(
        history: List<HistoricalDraw>,
        suggestionCount: Int = 15
    ): Set<Int> = withContext(dispatchersProvider.default) {
        if (history.isEmpty()) return@withContext (1..25).shuffled().take(suggestionCount).toSet()

        // Frequency analysis: which numbers appear most often
        val frequencyMap = mutableMapOf<Int, Int>()
        history.forEach { draw ->
            draw.numbers.forEach { number ->
                frequencyMap[number] = (frequencyMap[number] ?: 0) + 1
            }
        }

        // Calculate expected frequency (should be around history.size * 15 / 25)
        val expectedFrequency = (history.size * 15) / 25
        val threshold = expectedFrequency * 0.7

        // Score numbers based on deviation from expected frequency
        val numberScores = (1..25).associateWith { number ->
            val actualFreq = frequencyMap[number] ?: 0

            // Penalize very high frequency, reward underappearing numbers
            when {
                actualFreq > expectedFrequency * 1.5 -> -1000.0 // Too frequent, might be due
                actualFreq < threshold -> 100.0 + (expectedFrequency - actualFreq)
                else -> 50.0
            }
        }

        // Add small randomness to avoid always predicting same numbers
        val randomFactor = mutableMapOf<Int, Double>()
        (1..25).forEach { number ->
            randomFactor[number] = (Math.random() - 0.5) * 30
        }

        // Combine scores and select top suggestionCount numbers
        val finalScores = numberScores.mapValues { (number, score) ->
            score + (randomFactor[number] ?: 0.0)
        }

        return@withContext finalScores
            .entries
            .sortedByDescending { it.value }
            .take(suggestionCount)
            .map { it.key }
            .toSet()
    }

    /**
     * Calculate statistical patterns (for dashboard display).
     *
     * @param history Historical draw data
     * @return Map of statistical metrics
     */
    suspend fun calculatePatterns(history: List<HistoricalDraw>): Map<String, Any> =
        withContext(dispatchersProvider.default) {
            if (history.isEmpty()) return@withContext emptyMap()

            val frequencyMap = mutableMapOf<Int, Int>()
            var sumTotal = 0
            var minNumber = 25
            var maxNumber = 1

            history.forEach { draw ->
                draw.numbers.forEach { number ->
                    frequencyMap[number] = (frequencyMap[number] ?: 0) + 1
                    sumTotal += number
                    minNumber = minOf(minNumber, number)
                    maxNumber = maxOf(maxNumber, number)
                }
            }

            val totalDraws = history.size
            val totalNumbers = totalDraws * 15
            val average = sumTotal.toDouble() / totalNumbers

            mapOf(
                "averageDrawnNumber" to average,
                "minNumber" to minNumber,
                "maxNumber" to maxNumber,
                "totalAnalyzed" to totalDraws,
                "mostFrequent" to (frequencyMap.maxByOrNull { it.value }?.key ?: 0),
                "leastFrequent" to (frequencyMap.minByOrNull { it.value }?.key ?: 0)
            )
        }
}
