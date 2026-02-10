package com.cebolao.lotofacil.domain.service

import androidx.compose.runtime.Immutable
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.LotofacilGame
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject
import javax.inject.Singleton

@Immutable
data class UserStats(
    val totalGamesGenerated: Int,
    val totalGamesPlayed: Int,
    val mostPlayedNumbers: ImmutableList<Pair<Int, Int>>,
    val averageHits: Float,
    val lastGamePlayed: LotofacilGame?
)

@Singleton
class UserStatisticsService @Inject constructor() {

    fun calculateUserStats(
        games: List<LotofacilGame>,
        history: List<HistoricalDraw>
    ): UserStats {
        val playedGames = games.filter { it.usageCount > 0 }
        
        // Count number usage across all generated games (weighted by usageCount)
        val numberUsage = IntArray(26)
        games.forEach { game ->
            val weight = if (game.usageCount > 0) game.usageCount else 1
            game.numbers.forEach { num ->
                numberUsage[num] += weight
            }
        }
        
        val topNumbers = (1..25)
            .map { it to numberUsage[it] }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .take(5)
            .toImmutableList()

        // Calculate average hits against all history for played games
        var totalHits = 0
        var totalComparisons = 0
        
        if (playedGames.isNotEmpty() && history.isNotEmpty()) {
            playedGames.forEach { game ->
                history.forEach { draw ->
                    totalHits += game.numbers.intersect(draw.numbers).size
                    totalComparisons++
                }
            }
        }
        
        val avgHits = if (totalComparisons > 0) totalHits.toFloat() / totalComparisons else 0f
        val lastPlayed = playedGames.maxByOrNull { it.lastPlayed ?: 0L }

        return UserStats(
            totalGamesGenerated = games.size,
            totalGamesPlayed = playedGames.sumOf { it.usageCount },
            mostPlayedNumbers = topNumbers,
            averageHits = avgHits,
            lastGamePlayed = lastPlayed
        )
    }
}
