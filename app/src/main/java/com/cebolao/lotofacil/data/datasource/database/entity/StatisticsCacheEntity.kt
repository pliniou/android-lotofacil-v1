package com.cebolao.lotofacil.data.datasource.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "statistics_cache")
data class StatisticsCacheEntity(
    @PrimaryKey
    val windowSize: Int,
    val reportJson: String,
    val cachedAt: Long,
    val ttlMs: Long
)
