package com.cebolao.lotofacil.data.datasource

import com.cebolao.lotofacil.data.datasource.database.CheckHistoryDao
import com.cebolao.lotofacil.data.datasource.database.entity.CheckHistoryEntity
import com.cebolao.lotofacil.data.datasource.database.entity.toDomain
import com.cebolao.lotofacil.data.datasource.database.entity.toEntity
import com.cebolao.lotofacil.domain.model.CheckHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Local data source implementation for check history using Room DAO.
 * Handles mapping between entities and domain models.
 */
class CheckHistoryLocalDataSourceImpl @Inject constructor(
    private val dao: CheckHistoryDao
) : CheckHistoryLocalDataSource {

    override suspend fun insert(checkHistory: CheckHistory): Long {
        val entity = checkHistory.toEntity()
        return dao.insert(entity)
    }

    override suspend fun delete(checkHistory: CheckHistory) {
        val entity = checkHistory.toEntity()
        dao.delete(entity)
    }

    override fun getAllChecks(): Flow<List<CheckHistory>> {
        return dao.getAllFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getChecksByContest(contestNumber: Int): Flow<List<CheckHistory>> {
        return dao.getByContestFlow(contestNumber).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecentChecks(limit: Int): Flow<List<CheckHistory>> {
        return dao.getRecentFlow(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteOlderThan(beforeIso8601: String) {
        dao.deleteOlderThan(beforeIso8601)
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

    override suspend fun getCount(): Int {
        return dao.getCount()
    }
}
