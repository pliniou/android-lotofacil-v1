package com.cebolao.lotofacil.data.datasource.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.cebolao.lotofacil.data.datasource.database.entity.CheckHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckHistoryDao {

    @Insert
    suspend fun insert(entity: CheckHistoryEntity): Long

    @Delete
    suspend fun delete(entity: CheckHistoryEntity)

    @Query("SELECT * FROM check_history ORDER BY checkedAt DESC")
    fun getAllFlow(): Flow<List<CheckHistoryEntity>>

    @Query("SELECT * FROM check_history WHERE gameNumbers = :numbers ORDER BY checkedAt DESC LIMIT 1")
    suspend fun getLastCheckForNumbers(numbers: String): CheckHistoryEntity?

    @Query("SELECT * FROM check_history WHERE contestNumber = :contestNumber ORDER BY checkedAt DESC")
    fun getByContestFlow(contestNumber: Int): Flow<List<CheckHistoryEntity>>

    @Query("SELECT * FROM check_history ORDER BY checkedAt DESC LIMIT :limit")
    fun getRecentFlow(limit: Int = 50): Flow<List<CheckHistoryEntity>>

    @Query("DELETE FROM check_history WHERE checkedAt < datetime(:beforeIso8601)")
    suspend fun deleteOlderThan(beforeIso8601: String)

    @Query("DELETE FROM check_history")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM check_history")
    suspend fun getCount(): Int
}
