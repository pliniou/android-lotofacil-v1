package com.cebolao.lotofacil.core.security

import java.io.IOException

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

    private fun cleanupOldRequests(now: Long) {
        val cutoff = now - windowMillis
        requestTimestamps.removeAll { it < cutoff }
    }

    companion object {

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
