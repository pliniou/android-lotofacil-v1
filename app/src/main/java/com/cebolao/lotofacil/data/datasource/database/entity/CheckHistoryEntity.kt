package com.cebolao.lotofacil.data.datasource.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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
