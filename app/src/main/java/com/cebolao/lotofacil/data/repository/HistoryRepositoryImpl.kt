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
import com.cebolao.lotofacil.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.CancellationException
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
    private val userPreferencesRepository: UserPreferencesRepository,
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

    private val MIN_SYNC_INTERVAL = 60_000L // 1 minute
    private val REMOTE_CONTEST_CACHE_TTL = 90_000L
    @Volatile
    private var cachedLatestRemoteContest: Int? = null
    @Volatile
    private var cachedLatestRemoteContestAt: Long = 0L

    override suspend fun syncHistory(): AppResult<Unit> {
        // Wait for initialization (db population) to complete
        isInitialized.first { it }

        return syncMutex.withLock {
            val lastSyncTime = userPreferencesRepository.lastHistorySyncTimestamp.first()
            val now = System.currentTimeMillis()
            val shouldThrottleSync = now - lastSyncTime < MIN_SYNC_INTERVAL
            val hasPreviousFailure = _syncStatus.value is SyncStatus.Failed

            if (shouldThrottleSync && !hasPreviousFailure) {
                AppResult.Success(Unit)
            } else if (_syncStatus.value == SyncStatus.Syncing) {
                AppResult.Success(Unit)
            } else {
                _syncStatus.value = SyncStatus.Syncing
                try {
                    val currentLatest = localDataSource.getLatestDraw()?.contestNumber ?: 0
                    val latestRemoteContest = resolveLatestRemoteContest(
                        currentLatest = currentLatest,
                        nowMillis = now
                    )

                    if (latestRemoteContest != null && latestRemoteContest > currentLatest) {
                        val rangeToFetch = (currentLatest + 1)..latestRemoteContest
                        val totalToFetch = latestRemoteContest - currentLatest

                        remoteDataSource.getDrawsInRange(
                            range = rangeToFetch,
                            onProgress = { progressCount ->
                                _syncStatus.value = SyncStatus.Progress(progressCount, totalToFetch)
                            },
                            onBatchFetched = { batch ->
                                localDataSource.saveNewContests(batch)
                                val newest = batch.maxOfOrNull { it.contestNumber } ?: -1
                                val oldest = batch.minOfOrNull { it.contestNumber } ?: -1
                                logger.d(
                                    TAG,
                                    "Persisted batch size=${batch.size} contests=$oldest..$newest"
                                )
                            }
                        )
                    } else {
                        logger.d(TAG, "Local history already up to date or remote unavailable.")
                    }
                    val latestAfterSync = localDataSource.getLatestDraw()?.contestNumber ?: currentLatest
                    logger.i(
                        TAG,
                        "Sync complete. localLatestBefore=$currentLatest remoteLatest=${latestRemoteContest ?: "n/a"} localLatestAfter=$latestAfterSync"
                    )
                    val successTime = System.currentTimeMillis()
                    userPreferencesRepository.saveLastHistorySyncTimestamp(successTime)
                    _syncStatus.value = SyncStatus.Success
                    AppResult.Success(Unit)
                } catch (e: CancellationException) {
                    _syncStatus.value = SyncStatus.Idle
                    throw e
                } catch (e: Exception) {
                    // If network failure, permit retry sooner but not immediately
                    // We update timestamp to partially throttle retries
                    val failureTime = System.currentTimeMillis() - (MIN_SYNC_INTERVAL / 2)
                    userPreferencesRepository.saveLastHistorySyncTimestamp(failureTime)

                    val error = ErrorMapper.toAppError(e)
                    _syncStatus.value = SyncStatus.Failed(ErrorMapper.messageFor(error))
                    AppResult.Failure(error)
                }
            }
        }
    }

    private suspend fun resolveLatestRemoteContest(
        currentLatest: Int,
        nowMillis: Long
    ): Int? {
        val cachedContest = cachedLatestRemoteContest
        val cacheAge = nowMillis - cachedLatestRemoteContestAt
        if (cachedContest != null && cacheAge <= REMOTE_CONTEST_CACHE_TTL) {
            return cachedContest
        }

        val latestRemote = remoteDataSource.getLatestDraw(localLatestContest = currentLatest)
        val contest = latestRemote?.contestNumber
        if (contest != null) {
            cachedLatestRemoteContest = contest
            cachedLatestRemoteContestAt = nowMillis
        }
        return contest
    }
}
