package com.cebolao.lotofacil.data.repository

import com.cebolao.lotofacil.core.error.ErrorMapper
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.data.datasource.HistoryLocalDataSource
import com.cebolao.lotofacil.data.datasource.HistoryRemoteDataSource
import com.cebolao.lotofacil.di.ApplicationScope
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val localDataSource: HistoryLocalDataSource,
    private val remoteDataSource: HistoryRemoteDataSource,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val logger: AppLogger
) : HistoryRepository {

    companion object {
        private const val TAG = "HistoryRepository"
    }

    private val syncMutex = Mutex()
    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    override val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    override val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    init {
        applicationScope.launch {
            try {
                localDataSource.populateIfNeeded()
            } catch (e: Exception) {
                logger.e(TAG, "Failed to initialize local history database", e, logInRelease = true)
            } finally {
                // Seeding finished, app can now show current (offline) data.
                _isInitialized.value = true
            }
        }
    }

    override fun getHistory(): Flow<List<HistoricalDraw>> = localDataSource.getHistory()

    override suspend fun getLastDraw(): HistoricalDraw? {
        return localDataSource.getLatestDraw()
    }

    private var lastSyncTime = 0L
    private val MIN_SYNC_INTERVAL = 60_000L // 1 minute

    override suspend fun syncHistory(): AppResult<Unit> {
        // Wait for initialization (db population) to complete
        isInitialized.first { it }

        return syncMutex.withLock {
            val now = System.currentTimeMillis()
            if (now - lastSyncTime < MIN_SYNC_INTERVAL && _syncStatus.value != SyncStatus.Failed("")) {
                 return AppResult.Success(Unit)
            }

            if (_syncStatus.value == SyncStatus.Syncing) return AppResult.Success(Unit)
            _syncStatus.value = SyncStatus.Syncing
            return try {
                val latestRemote = remoteDataSource.getLatestDraw()
                val currentLatest = localDataSource.getLatestDraw()?.contestNumber ?: 0

                if (latestRemote != null && latestRemote.contestNumber > currentLatest) {
                    val rangeToFetch = (currentLatest + 1)..latestRemote.contestNumber
                    val totalToFetch = rangeToFetch.count()
                    
                    remoteDataSource.getDrawsInRange(
                        range = rangeToFetch,
                        onProgress = { progressCount ->
                            _syncStatus.value = SyncStatus.Progress(progressCount, totalToFetch)
                        },
                        onBatchFetched = { batch ->
                            localDataSource.saveNewContests(batch)
                            logger.d(TAG, "Incrementally saved ${batch.size} draws.")
                        }
                    )
                } else {
                    logger.d(TAG, "Local history already up to date or remote unavailable.")
                }
                lastSyncTime = System.currentTimeMillis()
                _syncStatus.value = SyncStatus.Success
                AppResult.Success(Unit)
            } catch (e: Exception) {
                // If network failure, permit retry sooner but not immediately
                lastSyncTime = System.currentTimeMillis() - (MIN_SYNC_INTERVAL / 2) 
                
                val error = ErrorMapper.toAppError(e)
                _syncStatus.value = SyncStatus.Failed(ErrorMapper.messageFor(error))
                AppResult.Failure(error)
            }
        }
    }
}
