package com.cebolao.lotofacil.core.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generic in-memory cache implementation with thread safety.
 * Provides caching with TTL (Time To Live) support.
 */
@Singleton
class MemoryCache @Inject constructor() {
    
    private val cache = mutableMapOf<String, CacheEntry<*>>()
    private val mutex = Mutex()
    
    /**
     * Stores a value in the cache with optional TTL.
     */
    suspend fun <T> put(
        key: String,
        value: T,
        ttlMs: Long? = null
    ) {
        mutex.withLock {
            val expirationTime = ttlMs?.let { System.currentTimeMillis() + it }
            cache[key] = CacheEntry(value, expirationTime)
        }
    }
    
    /**
     * Retrieves a value from the cache.
     */
    suspend fun <T> get(key: String): T? {
        return mutex.withLock {
            @Suppress("UNCHECKED_CAST")
            val entry = cache[key] as? CacheEntry<T>
            if (entry != null && !entry.isExpired()) {
                entry.value
            } else {
                cache.remove(key)
                null
            }
        }
    }
    
    /**
     * Retrieves a value or computes it if not present.
     */
    suspend fun <T> getOrPut(
        key: String,
        ttlMs: Long? = null,
        compute: suspend () -> T
    ): T {
        return get(key) ?: run {
            val value = compute()
            put(key, value, ttlMs)
            value
        }
    }
    
    /**
     * Removes a value from the cache.
     */
    suspend fun remove(key: String) {
        mutex.withLock {
            cache.remove(key)
        }
    }
    
    /**
     * Clears all expired entries from the cache.
     */
    suspend fun clearExpired() {
        mutex.withLock {
            cache.entries.removeAll { it.value.isExpired() }
        }
    }
    
    /**
     * Clears all entries from the cache.
     */
    suspend fun clear() {
        mutex.withLock {
            cache.clear()
        }
    }
    
    private data class CacheEntry<T>(
        val value: T,
        val expirationTime: Long?
    ) {
        fun isExpired(): Boolean {
            return expirationTime?.let { System.currentTimeMillis() > it } ?: false
        }
    }
}

/**
 * Reactive cache that provides StateFlow for observing changes.
 */
@Singleton
class ReactiveCache @Inject constructor() {
    
    private val cache = mutableMapOf<String, MutableStateFlow<*>>()
    private val mutex = Mutex()
    
    /**
     * Gets or creates a StateFlow for the given key.
     */
    suspend fun <T> getFlow(key: String, initialValue: T): StateFlow<T> {
        return mutex.withLock {
            @Suppress("UNCHECKED_CAST")
            (cache.getOrPut(key) { MutableStateFlow(initialValue) } as MutableStateFlow<T>)
                .asStateFlow()
        }
    }
    
    /**
     * Updates the value of a StateFlow.
     */
    suspend fun <T> update(key: String, value: T) {
        mutex.withLock {
            @Suppress("UNCHECKED_CAST")
            (cache[key] as? MutableStateFlow<T>)?.value = value
        }
    }
    
    /**
     * Removes a StateFlow from the cache.
     */
    suspend fun remove(key: String) {
        mutex.withLock {
            cache.remove(key)
        }
    }
    
    /**
     * Clears all StateFlows from the cache.
     */
    suspend fun clear() {
        mutex.withLock {
            cache.clear()
        }
    }
}
