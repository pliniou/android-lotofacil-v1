package com.cebolao.lotofacil.data.datasource.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cebolao.lotofacil.data.datasource.database.entity.StatisticsCacheEntity

@Dao
interface StatisticsCacheDao {
    @Query("SELECT * FROM statistics_cache WHERE windowSize = :windowSize LIMIT 1")
    suspend fun getByWindow(windowSize: Int): StatisticsCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: StatisticsCacheEntity)

    @Query("DELETE FROM statistics_cache")
    suspend fun clearAll()

    @Query("DELETE FROM statistics_cache WHERE windowSize = :windowSize")
    suspend fun clearWindow(windowSize: Int)

    @Query("DELETE FROM statistics_cache WHERE (:nowMillis - cachedAt) > ttlMs")
    suspend fun clearExpired(nowMillis: Long): Int

    @Query("SELECT COUNT(*) FROM statistics_cache")
    suspend fun countAll(): Int

    @Query("SELECT COUNT(*) FROM statistics_cache WHERE (:nowMillis - cachedAt) <= ttlMs")
    suspend fun countValid(nowMillis: Long): Int
}
