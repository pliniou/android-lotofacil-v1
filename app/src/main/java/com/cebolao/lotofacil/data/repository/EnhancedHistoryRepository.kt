package com.cebolao.lotofacil.data.repository

import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.data.datasource.HistoryLocalDataSource
import com.cebolao.lotofacil.data.datasource.HistoryRemoteDataSource
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Compatibility repository kept during migration.
 * It follows the current HistoryRepository contract and mirrors the sync behavior.
 */
@Singleton
class EnhancedHistoryRepository @Inject constructor(
    private val localDataSource: HistoryLocalDataSource,
    private val remoteDataSource: HistoryRemoteDataSource,
    private val logger: AppLogger
) : HistoryRepository {

    private val syncMutex = Mutex()
    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    override val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val _isInitialized = MutableStateFlow(true)
    override val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    override fun getHistory(): Flow<List<HistoricalDraw>> = localDataSource.getHistory()

    override suspend fun getLastDraw(): HistoricalDraw? = localDataSource.getLatestDraw()

    override suspend fun syncHistory(): AppResult<Unit> = syncMutex.withLock {
        if (_syncStatus.value == SyncStatus.Syncing) {
            return AppResult.Success(Unit)
        }

        _syncStatus.value = SyncStatus.Syncing
        return try {
            val latestRemote = remoteDataSource.getLatestDraw()
            val currentLatest = localDataSource.getLatestDraw()?.contestNumber ?: 0

            if (latestRemote != null && latestRemote.contestNumber > currentLatest) {
                val newDraws = remoteDataSource.getDrawsInRange((currentLatest + 1)..latestRemote.contestNumber)
                if (newDraws.isNotEmpty()) {
                    localDataSource.saveNewContests(newDraws)
                }
            }

            _syncStatus.value = SyncStatus.Success
            AppResult.Success(Unit)
        } catch (e: Exception) {
            logger.e("EnhancedHistoryRepository", "syncHistory failed", e)
            _syncStatus.value = SyncStatus.Failed(e.message ?: "Unknown sync error")
            AppResult.Failure(e)
        }
    }
}
