package com.cebolao.lotofacil.domain.service

import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.domain.model.GameStatistic
import com.cebolao.lotofacil.domain.model.GameStatisticType
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.domain.model.StatisticsReport
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This class receives raw lottery data ([HistoricalDraw] or [LotofacilGame])
 * and returns computed statistics models. It has **no coupling** to UI,
 * database, or repository layers.
 */
@Singleton
class StatisticsEngine @Inject constructor(
    private val dispatchersProvider: DispatchersProvider
) {
    companion object {
        private const val TOP_NUMBERS_COUNT = 5
    }

    // ── Full Report ─────────────────────────────────────────────────────

    /**
     * Produces a comprehensive [StatisticsReport] from a list of historical
     * draws. Heavy work is dispatched to [DispatchersProvider.default] and
     * parallelised with structured concurrency.
     */
    suspend fun analyze(draws: List<HistoricalDraw>): StatisticsReport =
        withContext(dispatchersProvider.default) {
            if (draws.isEmpty()) return@withContext StatisticsReport()

            coroutineScope {
                val mostFrequentDeferred = async { calculateMostFrequent(draws) }
                val mostOverdueDeferred = async { calculateMostOverdue(draws) }
                val distributionsDeferred = async { calculateAllDistributions(draws) }
                val averageSumDeferred = async { calculateAverageSum(draws) }

                val distributions = distributionsDeferred.await()

                StatisticsReport(
                    mostFrequentNumbers = mostFrequentDeferred.await(),
                    mostOverdueNumbers = mostOverdueDeferred.await(),
                    evenDistribution = distributions.evenDistribution,
                    primeDistribution = distributions.primeDistribution,
                    frameDistribution = distributions.frameDistribution,
                    portraitDistribution = distributions.portraitDistribution,
                    fibonacciDistribution = distributions.fibonacciDistribution,
                    multiplesOf3Distribution = distributions.multiplesOf3Distribution,
                    sumDistribution = distributions.sumDistribution,
                    averageSum = averageSumDeferred.await(),
                    totalDrawsAnalyzed = draws.size,
                    analysisDate = System.currentTimeMillis()
                )
            }
        }

    // ── Frequency Analysis ──────────────────────────────────────────────

    /**
     * Returns a map of number → appearance count across all draws.
     */
    fun getNumberFrequencies(draws: List<HistoricalDraw>): Map<Int, Int> {
        val frequencies = IntArray(26)
        draws.forEach { draw ->
            draw.numbers.forEach { number ->
                if (number in 1..25) {
                    frequencies[number]++
                }
            }
        }
        return (1..25).associateWith { frequencies[it] }
    }

    /**
     * Returns the [count] most-frequent numbers, sorted ascending by number.
     */
    fun getTopNumbers(frequencies: Map<Int, Int>, count: Int = TOP_NUMBERS_COUNT): List<Int> {
        return frequencies.entries
            .sortedByDescending { it.value }
            .take(count)
            .map { it.key }
            .sorted()
    }

    // ── Overdue / Recency ───────────────────────────────────────────────

    /**
     * Returns all 25 numbers ranked by how many draws since they last appeared.
     */
    fun getOverdueNumbers(draws: List<HistoricalDraw>): List<Pair<Int, Int>> {
        if (draws.isEmpty()) return emptyList()

        val lastContestNumber = draws.first().contestNumber
        val lastSeenMap = IntArray(26)

        draws.forEach { draw ->
            draw.numbers.forEach { number ->
                if (number in 1..25 && lastSeenMap[number] == 0) {
                    lastSeenMap[number] = draw.contestNumber
                }
            }
        }

        return (1..25).map { number ->
            val lastSeen = lastSeenMap[number]
            val overdue = if (lastSeen > 0) {
                (lastContestNumber - lastSeen).coerceAtLeast(0)
            } else {
                draws.size
            }
            number to overdue
        }.sortedByDescending { it.second }
    }

    // ── Pattern Detection ───────────────────────────────────────────────

    /**
     * Detects common combinations of numbers (pairs, triplets, etc.)
     * that appear most frequently.
     *
     * @param draws  Draws to analyse.
     * @param size   Combination size (2 for pairs, 3 for triplets).
     * @param limit  Maximum number of patterns to return.
     */
    fun getCommonPatterns(
        draws: List<HistoricalDraw>,
        size: Int,
        limit: Int = 10
    ): List<Pair<Set<Int>, Int>> {
        if (draws.isEmpty()) return emptyList()

        val patternCounts = mutableMapOf<Set<Int>, Int>()

        draws.forEach { draw ->
            val numbersList = draw.numbers.toList().sorted()
            generateCombinations(numbersList, size).forEach { combination ->
                patternCounts[combination] = patternCounts.getOrDefault(combination, 0) + 1
            }
        }

        return patternCounts.entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key to it.value }
    }

    // ── Timelines ───────────────────────────────────────────────────────

    /**
     * Rolling average of the sum over a sliding window.
     */
    fun getAverageSumTimeline(
        draws: List<HistoricalDraw>,
        windowSize: Int = 10
    ): List<Pair<Int, Float>> {
        if (draws.isEmpty()) return emptyList()

        val sortedDraws = draws.sortedBy { it.contestNumber }

        return sortedDraws.mapIndexed { index, draw ->
            val start = (index - windowSize + 1).coerceAtLeast(0)
            val window = sortedDraws.subList(start, index + 1)
            val average = window.map { it.sum }.average().toFloat()
            draw.contestNumber to average
        }
    }

    /**
     * Rolling average of an arbitrary distribution value.
     */
    fun getDistributionTimeline(
        draws: List<HistoricalDraw>,
        windowSize: Int = 10,
        valueExtractor: (HistoricalDraw) -> Int
    ): List<Pair<Int, Float>> {
        if (draws.isEmpty()) return emptyList()

        val sortedDraws = draws.sortedBy { it.contestNumber }

        return sortedDraws.mapIndexed { index, draw ->
            val start = (index - windowSize + 1).coerceAtLeast(0)
            val window = sortedDraws.subList(start, index + 1)
            val average = window.map { valueExtractor(it) }.average().toFloat()
            draw.contestNumber to average
        }
    }

    // ── Single-game statistics ──────────────────────────────────────────

    /**
     * Extracts computed statistics for a single [LotofacilGame].
     */
    suspend fun analyzeGame(game: LotofacilGame): List<GameStatistic> =
        withContext(dispatchersProvider.default) {
            listOf(
                GameStatistic(GameStatisticType.SUM, game.sum),
                GameStatistic(GameStatisticType.EVENS, game.evens),
                GameStatistic(GameStatisticType.ODDS, game.odds),
                GameStatistic(GameStatisticType.PRIMES, game.primes),
                GameStatistic(GameStatisticType.FIBONACCI, game.fibonacci),
                GameStatistic(GameStatisticType.FRAME, game.frame),
                GameStatistic(GameStatisticType.PORTRAIT, game.portrait),
                GameStatistic(GameStatisticType.MULTIPLES_OF_3, game.multiplesOf3)
            )
        }

    // ── Report helpers (private) ────────────────────────────────────────

    fun calculateAverageSum(draws: List<HistoricalDraw>): Float {
        if (draws.isEmpty()) return 0f
        return draws.map { it.sum }.average().toFloat()
    }

    private fun calculateMostFrequent(draws: List<HistoricalDraw>): List<Pair<Int, Int>> {
        val frequencies = IntArray(26)
        draws.forEach { draw ->
            draw.numbers.forEach { number ->
                if (number in 1..25) frequencies[number]++
            }
        }
        return (1..25).asSequence()
            .map { it to frequencies[it] }
            .sortedByDescending { it.second }
            .take(TOP_NUMBERS_COUNT)
            .toList()
    }

    private fun calculateMostOverdue(draws: List<HistoricalDraw>): List<Pair<Int, Int>> {
        if (draws.isEmpty()) return emptyList()

        val lastContestNumber = draws.first().contestNumber
        val lastSeenMap = IntArray(26)

        draws.forEach { draw ->
            draw.numbers.forEach { number ->
                if (number in 1..25 && lastSeenMap[number] == 0) {
                    lastSeenMap[number] = draw.contestNumber
                }
            }
        }

        return (1..25).asSequence()
            .map { number ->
                val lastSeen = lastSeenMap[number]
                val overdue = if (lastSeen > 0) {
                    (lastContestNumber - lastSeen).coerceAtLeast(1)
                } else {
                    draws.size + 1
                }
                number to overdue
            }
            .sortedByDescending { it.second }
            .take(TOP_NUMBERS_COUNT)
            .toList()
    }

    private suspend fun calculateAllDistributions(
        draws: List<HistoricalDraw>
    ): DistributionResults = coroutineScope {
        val even = async { calculateDistribution(draws) { it.evens } }
        val prime = async { calculateDistribution(draws) { it.primes } }
        val frame = async { calculateDistribution(draws) { it.frame } }
        val portrait = async { calculateDistribution(draws) { it.portrait } }
        val fibonacci = async { calculateDistribution(draws) { it.fibonacci } }
        val multiplesOf3 = async { calculateDistribution(draws) { it.multiplesOf3 } }
        val sum = async { calculateDistribution(draws, 10) { it.sum } }

        DistributionResults(
            evenDistribution = even.await(),
            primeDistribution = prime.await(),
            frameDistribution = frame.await(),
            portraitDistribution = portrait.await(),
            fibonacciDistribution = fibonacci.await(),
            multiplesOf3Distribution = multiplesOf3.await(),
            sumDistribution = sum.await()
        )
    }

    private fun calculateDistribution(
        draws: List<HistoricalDraw>,
        grouping: Int = 1,
        valueExtractor: (HistoricalDraw) -> Int
    ): Map<Int, Int> {
        return draws.groupingBy { draw ->
            (valueExtractor(draw) / grouping) * grouping
        }.eachCount()
    }

    private fun generateCombinations(numbers: List<Int>, size: Int): List<Set<Int>> {
        val combinations = mutableListOf<Set<Int>>()

        fun combine(start: Int, current: MutableList<Int>) {
            if (current.size == size) {
                combinations.add(current.toSet())
                return
            }
            for (i in start until numbers.size) {
                current.add(numbers[i])
                combine(i + 1, current)
                current.removeAt(current.size - 1)
            }
        }

        combine(0, mutableListOf())
        return combinations
    }

    private data class DistributionResults(
        val evenDistribution: Map<Int, Int>,
        val primeDistribution: Map<Int, Int>,
        val frameDistribution: Map<Int, Int>,
        val portraitDistribution: Map<Int, Int>,
        val fibonacciDistribution: Map<Int, Int>,
        val multiplesOf3Distribution: Map<Int, Int>,
        val sumDistribution: Map<Int, Int>
    )
}
