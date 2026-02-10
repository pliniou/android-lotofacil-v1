package com.cebolao.lotofacil.data.repository

import com.cebolao.lotofacil.data.datasource.CheckHistoryLocalDataSource
import com.cebolao.lotofacil.domain.model.CheckHistory
import com.cebolao.lotofacil.domain.repository.CheckHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of CheckHistoryRepository using local data source (Room).
 * Manages persistence and retrieval of game check records.
 */
class CheckHistoryRepositoryImpl @Inject constructor(
    private val localDataSource: CheckHistoryLocalDataSource
) : CheckHistoryRepository {

    override suspend fun saveCheck(checkHistory: CheckHistory): Long {
        return localDataSource.insert(checkHistory)
    }

    override suspend fun deleteCheck(checkHistory: CheckHistory) {
        localDataSource.delete(checkHistory)
    }

    override fun getAllChecks(): Flow<List<CheckHistory>> {
        return localDataSource.getAllChecks()
    }

    override fun getChecksByContest(contestNumber: Int): Flow<List<CheckHistory>> {
        return localDataSource.getChecksByContest(contestNumber)
    }

    override fun getRecentChecks(limit: Int): Flow<List<CheckHistory>> {
        return localDataSource.getRecentChecks(limit)
    }

    override suspend fun deleteOlderThan(beforeIso8601: String) {
        localDataSource.deleteOlderThan(beforeIso8601)
    }

    override suspend fun deleteAll() {
        localDataSource.deleteAll()
    }

    override suspend fun getCount(): Int {
        return localDataSource.getCount()
    }
}
