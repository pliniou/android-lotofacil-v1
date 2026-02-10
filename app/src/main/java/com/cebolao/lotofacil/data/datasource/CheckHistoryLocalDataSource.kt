package com.cebolao.lotofacil.data.datasource

import com.cebolao.lotofacil.domain.model.CheckHistory
import kotlinx.coroutines.flow.Flow

/**
 * Local data source interface for check history.
 * Abstracts the local persistence layer.
 */
interface CheckHistoryLocalDataSource {

    suspend fun insert(checkHistory: CheckHistory): Long

    suspend fun delete(checkHistory: CheckHistory)

    fun getAllChecks(): Flow<List<CheckHistory>>

    fun getChecksByContest(contestNumber: Int): Flow<List<CheckHistory>>

    fun getRecentChecks(limit: Int = 50): Flow<List<CheckHistory>>

    suspend fun deleteOlderThan(beforeIso8601: String)

    suspend fun deleteAll()

    suspend fun getCount(): Int
}
