package com.cebolao.lotofacil.data.repository

import com.cebolao.lotofacil.data.datasource.database.StatisticsCacheDao
import com.cebolao.lotofacil.data.datasource.database.entity.StatisticsCacheEntity
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.utils.TimeProvider
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.domain.repository.CacheInvalidationTarget
import com.cebolao.lotofacil.domain.repository.CachePolicy
import com.cebolao.lotofacil.domain.repository.CacheStatistics
import com.cebolao.lotofacil.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsRepositoryImpl @Inject constructor(
    private val cacheDao: StatisticsCacheDao,
    private val dispatchersProvider: DispatchersProvider,
    private val json: Json,
    private val timeProvider: TimeProvider
) : StatisticsRepository {

    private val cacheMutex = Mutex()
    private val cacheStats = MutableStateFlow(CacheStatistics())

    override suspend fun getCachedStatistics(
        windowSize: Int,
        policy: CachePolicy
    ): StatisticsReport? = withContext(dispatchersProvider.io) {
        cacheMutex.withLock {
            val now = timeProvider.currentTimeMillis()
            val entry = cacheDao.getByWindow(windowSize)
            if (entry == null) {
                cacheStats.value = cacheStats.value.copy(
                    cacheMisses = cacheStats.value.cacheMisses + 1,
                    lastUpdated = now
                )
                updateEntryCountersLocked(now)
                return@withLock null
            }
            val isExpired = now - entry.cachedAt > entry.ttlMs

            if (!isExpired || policy == CachePolicy.AllowStale) {
                val decoded = decode(entry.reportJson)
                if (decoded != null) {
                    cacheStats.value = cacheStats.value.copy(
                        cacheHits = cacheStats.value.cacheHits + 1,
                        lastUpdated = now
                    )
                    updateEntryCountersLocked(now)
                    decoded
                } else {
                    cacheDao.clearWindow(windowSize)
                    cacheStats.value = cacheStats.value.copy(
                        cacheMisses = cacheStats.value.cacheMisses + 1,
                        lastUpdated = now
                    )
                    updateEntryCountersLocked(now)
                    null
                }
            } else {
                cacheDao.clearWindow(windowSize)
                cacheStats.value = cacheStats.value.copy(
                    cacheMisses = cacheStats.value.cacheMisses + 1,
                    lastUpdated = now
                )
                updateEntryCountersLocked(now)
                null
            }
        }
    }

    override suspend fun cacheStatistics(windowSize: Int, statistics: StatisticsReport, ttlMs: Long) {
        withContext(dispatchersProvider.io) {
            cacheMutex.withLock {
                cacheDao.upsert(
                    StatisticsCacheEntity(
                        windowSize = windowSize,
                        reportJson = json.encodeToString(statistics),
                        cachedAt = timeProvider.currentTimeMillis(),
                        ttlMs = ttlMs
                    )
                )
                val now = timeProvider.currentTimeMillis()
                cacheStats.value = cacheStats.value.copy(lastUpdated = now)
                updateEntryCountersLocked(now)
            }
        }
    }

    override suspend fun clearCache(target: CacheInvalidationTarget) {
        withContext(dispatchersProvider.io) {
            cacheMutex.withLock {
                when (target) {
                    CacheInvalidationTarget.All -> cacheDao.clearAll()
                    is CacheInvalidationTarget.Window -> cacheDao.clearWindow(target.windowSize)
                }
                val now = timeProvider.currentTimeMillis()
                cacheStats.value = cacheStats.value.copy(lastUpdated = now)
                updateEntryCountersLocked(now)
            }
        }
    }

    override suspend fun clearExpiredCache() {
        withContext(dispatchersProvider.io) {
            cacheMutex.withLock {
                cacheDao.clearExpired(timeProvider.currentTimeMillis())
                val now = timeProvider.currentTimeMillis()
                cacheStats.value = cacheStats.value.copy(lastUpdated = now)
                updateEntryCountersLocked(now)
            }
        }
    }

    override suspend fun hasValidCache(windowSize: Int): Boolean = withContext(dispatchersProvider.io) {
        cacheMutex.withLock {
            val now = timeProvider.currentTimeMillis()
            val entry = cacheDao.getByWindow(windowSize) ?: return@withLock false
            now - entry.cachedAt <= entry.ttlMs
        }
    }

    override fun getCacheStatistics(): Flow<CacheStatistics> {
        return cacheStats.asStateFlow()
    }

    private suspend fun updateEntryCountersLocked(now: Long) {
        val totalEntries = cacheDao.countAll()
        val validEntries = cacheDao.countValid(now)
        cacheStats.value = cacheStats.value.copy(
            totalEntries = totalEntries,
            validEntries = validEntries,
            expiredEntries = (totalEntries - validEntries).coerceAtLeast(0),
            lastUpdated = now
        )
    }

    private fun decode(payload: String): StatisticsReport? {
        return runCatching {
            json.decodeFromString<StatisticsReport>(payload)
        }.getOrNull()
    }
}
