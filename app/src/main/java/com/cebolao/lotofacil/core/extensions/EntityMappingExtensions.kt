package com.cebolao.lotofacil.core.extensions

import com.cebolao.lotofacil.data.datasource.database.entity.CheckHistoryEntity
import com.cebolao.lotofacil.data.datasource.database.entity.GameEntity
import com.cebolao.lotofacil.data.datasource.database.entity.HistoricalDrawEntity
import com.cebolao.lotofacil.domain.model.CheckHistory
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.LotofacilConstants
import com.cebolao.lotofacil.domain.model.LotofacilGame
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Centralized entity mapping extensions to reduce boilerplate
 * and ensure consistency across the application.
 */

// HistoricalDraw Entity Mappings
fun HistoricalDrawEntity.toDomain() = HistoricalDraw(
    contestNumber = contestNumber,
    numbers = numbers,
    date = date,
    prizes = prizes,
    winners = winners,
    nextContest = nextContest,
    nextDate = nextDate,
    nextEstimate = nextEstimate,
    accumulated = accumulated
)

fun HistoricalDraw.toEntity() = HistoricalDrawEntity(
    contestNumber = contestNumber,
    numbers = numbers,
    date = date,
    prizes = prizes,
    winners = winners,
    nextContest = nextContest,
    nextDate = nextDate,
    nextEstimate = nextEstimate,
    accumulated = accumulated
)

// Game Entity Mappings
fun GameEntity.toDomain(): LotofacilGame? {
    val parsedNumbers = getNumbersList().toSet()
    if (parsedNumbers.size != LotofacilConstants.GAME_SIZE) return null
    
    return LotofacilGame(
        numbers = parsedNumbers,
        isPinned = isPinned,
        creationTimestamp = creationTimestamp,
        usageCount = usageCount,
        lastPlayed = lastPlayed,
        id = id
    )
}

fun LotofacilGame.toEntity() = GameEntity(
    id = id,
    numbers = numbers.sorted().joinToString(","),
    isPinned = isPinned,
    creationTimestamp = creationTimestamp,
    usageCount = usageCount,
    lastPlayed = lastPlayed
)

// CheckHistory Entity Mappings
fun CheckHistoryEntity.toDomain() = CheckHistory(
    id = id,
    gameNumbers = gameNumbers,
    contestNumber = contestNumber,
    checkedAt = checkedAt,
    hits = hits,
    scoreCounts = deserializeScoreCounts(scoreCounts),
    lastHitContest = lastHitContest,
    lastHitScore = lastHitScore,
    notes = notes
)

fun CheckHistory.toEntity() = CheckHistoryEntity(
    id = id,
    gameNumbers = gameNumbers,
    contestNumber = contestNumber,
    checkedAt = checkedAt,
    hits = hits,
    scoreCounts = serializeScoreCounts(scoreCounts),
    lastHitContest = lastHitContest,
    lastHitScore = lastHitScore,
    notes = notes
)

private val scoreCountsJson = Json { ignoreUnknownKeys = true }

private fun serializeScoreCounts(scores: Map<Int, Int>): String =
    scoreCountsJson.encodeToString(scores)

private fun deserializeScoreCounts(rawJson: String): Map<Int, Int> {
    return runCatching {
        scoreCountsJson.decodeFromString<Map<Int, Int>>(rawJson)
    }.getOrDefault(emptyMap())
}
