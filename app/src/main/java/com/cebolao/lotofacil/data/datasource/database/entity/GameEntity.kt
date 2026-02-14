package com.cebolao.lotofacil.data.datasource.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.cebolao.lotofacil.domain.model.LotofacilConstants

@Entity(
    tableName = "games",
    indices = [
        Index(value = ["isPinned"]),
        Index(value = ["creationTimestamp"])
    ]
)
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
}
