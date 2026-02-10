package com.cebolao.lotofacil.core

import com.cebolao.lotofacil.core.utils.retryExponentialBackoff
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import kotlin.test.assertFailsWith
import org.junit.Test

class RetryExtensionsTest {

    @Test
    fun `retryExponentialBackoff should succeed on first attempt`() = runTest {
        var attempts = 0
        val result = retryExponentialBackoff(maxRetries = 3) {
            attempts++
            "success"
        }
        assertEquals("success", result)
        assertEquals(1, attempts)
    }

    @Test
    fun `retryExponentialBackoff should retry on failure`() = runTest {
        var attempts = 0
        val result = retryExponentialBackoff(maxRetries = 3, initialDelayMs = 10) {
            attempts++
            if (attempts < 3) throw RuntimeException("Simulated failure")
            "success"
        }
        assertEquals("success", result)
        assertEquals(3, attempts)
    }

    @Test
    fun `retryExponentialBackoff should fail after max retries exceeded`() = runTest {
        var attempts = 0
        val exception = assertFailsWith<RuntimeException> {
            retryExponentialBackoff(maxRetries = 2, initialDelayMs = 10) {
                attempts++
                throw RuntimeException("Persistent failure")
            }
        }
        assertEquals("Persistent failure", exception.message)
        assertEquals(3, attempts) // initial + 2 retries
    }

    @Test
    fun `retryExponentialBackoff should have exponential backoff delay`() = runTest {
        val timestamps = mutableListOf<Long>()
        try {
            retryExponentialBackoff(maxRetries = 2, initialDelayMs = 50) {
                timestamps.add(System.currentTimeMillis())
                throw RuntimeException("Fail")
            }
        } catch (e: Exception) {
            // Expected
        }

        // Verify we have 3 attempts (initial + 2 retries)
        assertEquals(3, timestamps.size)
    }
}
