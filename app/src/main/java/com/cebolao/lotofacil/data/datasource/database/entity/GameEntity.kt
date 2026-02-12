package com.cebolao.lotofacil.data.datasource.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cebolao.lotofacil.domain.model.LotofacilConstants

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: String,
    val numbers: String, // Stored as comma-separated string
    val isPinned: Boolean,
    val creationTimestamp: Long,
    val usageCount: Int,
    val lastPlayed: Long?
) {
    // Helper to convert to list of ints
    fun getNumbersList(): List<Int> {
        return if (numbers.isBlank()) emptyList()
        else numbers.split(",").map { it.toInt() }
    }

    companion object {
        fun fromDomain(game: com.cebolao.lotofacil.domain.model.LotofacilGame): GameEntity {
            return GameEntity(
                id = game.id,
                numbers = game.numbers.sorted().joinToString(","),
                isPinned = game.isPinned,
                creationTimestamp = game.creationTimestamp,
                usageCount = game.usageCount,
                lastPlayed = game.lastPlayed
            )
        }
    }

    fun toDomain(): com.cebolao.lotofacil.domain.model.LotofacilGame? {
        val parsedNumbers = getNumbersList().toSet()
        if (parsedNumbers.size != LotofacilConstants.GAME_SIZE) return null
        return com.cebolao.lotofacil.domain.model.LotofacilGame(
            numbers = parsedNumbers,
            isPinned = isPinned,
            creationTimestamp = creationTimestamp,
            usageCount = usageCount,
            lastPlayed = lastPlayed,
            id = id
        )
    }
}
