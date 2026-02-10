package com.cebolao.lotofacil.domain.repository

import com.cebolao.lotofacil.domain.model.StatisticsReport
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing persistent statistics caching.
 * Provides efficient access to computed statistics while avoiding expensive recalculations.
 */
interface StatisticsRepository {

    /**
     * Get cached statistics for a specific time window.
     * Returns null if no valid cached data exists for the window and policy.
     * 
     * @param windowSize The time window size (number of draws to analyze)
     * @param policy Cache freshness policy
     * @return Cached statistics report or null if not available/expired
     */
    suspend fun getCachedStatistics(
        windowSize: Int,
        policy: CachePolicy = CachePolicy.OnlyValid
    ): StatisticsReport?

    /**
     * Cache statistics for a specific time window with TTL.
     * 
     * @param windowSize The time window size
     * @param statistics The statistics report to cache
     * @param ttlMs Time to live in milliseconds (default: 5 minutes)
     */
    suspend fun cacheStatistics(
        windowSize: Int, 
        statistics: StatisticsReport, 
        ttlMs: Long = DEFAULT_TTL_MS
    )

    /**
     * Clear cached statistics.
     *
     * @param target Target invalidation scope
     */
    suspend fun clearCache(target: CacheInvalidationTarget = CacheInvalidationTarget.All)

    /**
     * Remove expired entries based on TTL.
     */
    suspend fun clearExpiredCache()

    /**
     * Get cache statistics for monitoring and debugging.
     * 
     * @return Flow of cache metadata (size, hit rates, etc.)
     */
    fun getCacheStatistics(): Flow<CacheStatistics>

    /**
     * Check if cached data exists and is valid for a window size.
     * 
     * @param windowSize The time window size
     * @return true if valid cached data exists
     */
    suspend fun hasValidCache(windowSize: Int): Boolean
}

enum class CachePolicy {
    OnlyValid,
    AllowStale
}

sealed interface CacheInvalidationTarget {
    data object All : CacheInvalidationTarget
    data class Window(val windowSize: Int) : CacheInvalidationTarget
}

/**
 * Metadata about the statistics cache for monitoring.
 */
data class CacheStatistics(
    val totalEntries: Int = 0,
    val validEntries: Int = 0,
    val expiredEntries: Int = 0,
    val cacheHits: Long = 0,
    val cacheMisses: Long = 0,
    val lastUpdated: Long = 0L
) {
    val hitRate: Float
        get() = if (cacheHits + cacheMisses > 0) {
            cacheHits.toFloat() / (cacheHits + cacheMisses).toFloat()
        } else 0f
}

const val DEFAULT_TTL_MS = 5 * 60 * 1000L // 5 minutes
