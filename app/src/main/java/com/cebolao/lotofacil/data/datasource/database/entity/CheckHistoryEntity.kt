package com.cebolao.lotofacil.data.datasource.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cebolao.lotofacil.domain.model.CheckHistory
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = "check_history")
data class CheckHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameNumbers: Set<Int>,
    val contestNumber: Int,
    val checkedAt: String, // ISO 8601 format
    val hits: Int,
    val scoreCounts: String, // JSON serialized Map<Int, Int>
    val lastHitContest: Int? = null,
    val lastHitScore: Int? = null,
    val notes: String? = null // Optional user notes
)

// Mapping extension functions
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

// Serialization helpers
private val json = Json { ignoreUnknownKeys = true }

fun serializeScoreCounts(scores: Map<Int, Int>): String = json.encodeToString(scores)
fun deserializeScoreCounts(rawJson: String): Map<Int, Int> {
    return try {
        json.decodeFromString(rawJson)
    } catch (e: Exception) {
        emptyMap()
    }
}
