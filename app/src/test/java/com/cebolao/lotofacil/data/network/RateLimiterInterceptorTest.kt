package com.cebolao.lotofacil.data.network

import com.cebolao.lotofacil.core.security.RateLimitExceededException
import com.cebolao.lotofacil.core.security.RateLimiter
import okhttp3.Call
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Timeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.util.concurrent.TimeUnit

class RateLimiterInterceptorTest {

    @Test
    fun `interceptor throws IOException-compatible rate limit exception when blocked`() {
        val interceptor = RateLimiterInterceptor(
            rateLimiter = RateLimiter(maxRequests = 0, windowMillis = 60_000L),
            maxWaitTimeMs = 0L // Fail immediately for testing
        )
        val chain = FakeChain()

        val exception = assertThrows(RateLimitExceededException::class.java) {
            interceptor.intercept(chain)
        }

        assertTrue(exception is IOException)
        assertFalse(chain.proceedCalled)
        assertTrue(exception.retryAfterMillis >= 0L)
    }

    @Test
    fun `interceptor proceeds request when under limit`() {
        val interceptor = RateLimiterInterceptor(
            rateLimiter = RateLimiter(maxRequests = 1, windowMillis = 60_000L)
        )
        val chain = FakeChain()

        val response = interceptor.intercept(chain)

        assertTrue(chain.proceedCalled)
        assertEquals(200, response.code)
    }

    private class FakeChain : Interceptor.Chain {
        val request: Request = Request.Builder()
            .url("https://example.com/latest")
            .build()

        var proceedCalled: Boolean = false

        override fun request(): Request = request

        override fun proceed(request: Request): Response {
            proceedCalled = true
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("{}".toResponseBody("application/json".toMediaType()))
                .build()
        }

        override fun connection(): Connection? = null

        override fun call(): Call = object : Call {
            override fun request(): Request = this@FakeChain.request
            override fun execute(): Response = throw UnsupportedOperationException("Not used in test")
            override fun enqueue(responseCallback: okhttp3.Callback) = Unit
            override fun cancel() = Unit
            override fun isExecuted(): Boolean = false
            override fun isCanceled(): Boolean = false
            override fun timeout(): Timeout = Timeout.NONE
            override fun clone(): Call = this
        }

        override fun connectTimeoutMillis(): Int = 10_000

        override fun withConnectTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this

        override fun readTimeoutMillis(): Int = 10_000

        override fun withReadTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this

        override fun writeTimeoutMillis(): Int = 10_000

        override fun withWriteTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
    }
}
