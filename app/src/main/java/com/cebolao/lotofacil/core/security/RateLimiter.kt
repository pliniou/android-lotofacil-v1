package com.cebolao.lotofacil.core.security

import java.io.IOException
import java.util.concurrent.atomic.AtomicLong

/**
 * Simple in-memory rate limiter for protecting against local force-brute/DDoS attacks.
 * Thread-safe implementation using AtomicLong for timestamp tracking.
 *
 * Tracks requests per window and enforces rate limits.
 */
class RateLimiter(
    private val maxRequests: Int,
    private val windowMillis: Long
) {
    private val requestTimestamps = mutableListOf<Long>()
    private val lastCleanupTime = AtomicLong(System.currentTimeMillis())
    private val lock = Any()

    /**
     * Check if a request is allowed under the rate limit.
     *
     * @return true if request is allowed, false if rate limit exceeded
     */
    fun allowRequest(): Boolean = synchronized(lock) {
        val now = System.currentTimeMillis()
        cleanupOldRequests(now)

        return if (requestTimestamps.size < maxRequests) {
            requestTimestamps.add(now)
            true
        } else {
            false
        }
    }

    /**
     * Get current request count in window.
     */
    fun getCurrentRequestCount(): Int = synchronized(lock) {
        cleanupOldRequests(System.currentTimeMillis())
        return requestTimestamps.size
    }

    /**
     * Get remaining requests allowed in current window.
     */
    fun getRemainingRequests(): Int {
        val current = getCurrentRequestCount()
        return maxOf(0, maxRequests - current)
    }

    /**
     * Get time until next request is allowed (in milliseconds).
     * Returns 0 if request can be made immediately.
     */
    fun getTimeUntilNextRequest(): Long = synchronized(lock) {
        if (requestTimestamps.size < maxRequests) return 0L

        val now = System.currentTimeMillis()
        val oldestRequest = requestTimestamps.firstOrNull() ?: return 0L
        val timeUntilOldestExpires = oldestRequest + windowMillis - now

        return maxOf(0L, timeUntilOldestExpires)
    }

    /**
     * Reset the limiter.
     */
    fun reset() = synchronized(lock) {
        requestTimestamps.clear()
        lastCleanupTime.set(System.currentTimeMillis())
    }

    private fun cleanupOldRequests(now: Long) {
        val cutoff = now - windowMillis
        requestTimestamps.removeAll { it < cutoff }
    }

    companion object {
        // Recommended: max 10 requests per 60 seconds (typical API rate limit)
        fun createDefault(): RateLimiter = RateLimiter(maxRequests = 10, windowMillis = 60_000)

        // Strict: max 3 requests per 10 seconds
        fun createStrict(): RateLimiter = RateLimiter(maxRequests = 3, windowMillis = 10_000)

        // Lenient: max 30 requests per 60 seconds
        fun createLenient(): RateLimiter = RateLimiter(maxRequests = 30, windowMillis = 60_000)
    }
}

/**
 * Exception thrown when rate limit is exceeded.
 */
class RateLimitExceededException(
    val retryAfterMillis: Long,
    message: String = "Rate limit exceeded. Retry after ${retryAfterMillis}ms"
) : IOException(message)
