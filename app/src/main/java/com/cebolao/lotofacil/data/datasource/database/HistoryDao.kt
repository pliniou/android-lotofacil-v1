package com.cebolao.lotofacil.data.datasource.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.cebolao.lotofacil.data.datasource.database.entity.HistoricalDrawEntity

@Dao
interface HistoryDao {

    @Query("SELECT * FROM historical_draws ORDER BY contestNumber DESC")
    fun getAll(): kotlinx.coroutines.flow.Flow<List<HistoricalDrawEntity>>

    @Upsert
    suspend fun upsertAll(draws: List<HistoricalDrawEntity>)

    @Query("SELECT COUNT(*) FROM historical_draws")
    suspend fun getCount(): Int

    @Query("SELECT * FROM historical_draws WHERE contestNumber = :contestNumber")
    suspend fun getDraw(contestNumber: Int): HistoricalDrawEntity?

    @Query("SELECT * FROM historical_draws ORDER BY contestNumber DESC LIMIT 1")
    suspend fun getLatestDraw(): HistoricalDrawEntity?
    
    @Query("DELETE FROM historical_draws")
    suspend fun clearAll()
}
