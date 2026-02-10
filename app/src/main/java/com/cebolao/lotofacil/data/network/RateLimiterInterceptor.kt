package com.cebolao.lotofacil.data.network

import com.cebolao.lotofacil.core.security.RateLimitExceededException
import com.cebolao.lotofacil.core.security.RateLimiter
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RateLimiterInterceptor(
    private val rateLimiter: RateLimiter,
    private val maxWaitTimeMs: Long = DEFAULT_MAX_WAIT_TIME_MS
) : Interceptor {

    companion object {
        private const val DEFAULT_MAX_WAIT_TIME_MS = 120_000L // 2 minutes max wait
        private const val RETRY_CHECK_INTERVAL_MS = 100L
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val startTime = System.currentTimeMillis()
        
        // Wait for rate limit slot instead of immediately failing
        while (!rateLimiter.allowRequest()) {
            val elapsed = System.currentTimeMillis() - startTime
            val waitTime = rateLimiter.getTimeUntilNextRequest()
            
            // Fail if max wait time exceeded
            if (elapsed + waitTime > maxWaitTimeMs) {
                throw RateLimitExceededException(
                    retryAfterMillis = waitTime,
                    message = "Rate limit exceeded. Max wait time (${maxWaitTimeMs}ms) would be exceeded."
                )
            }
            
            // Wait a bit before retrying
            try {
                Thread.sleep(minOf(waitTime, RETRY_CHECK_INTERVAL_MS))
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                throw IOException("Request interrupted while waiting for rate limit", e)
            }
        }

        return chain.proceed(chain.request())
    }
}
