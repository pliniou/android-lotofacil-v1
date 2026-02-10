package com.cebolao.lotofacil.data

import com.cebolao.lotofacil.core.coroutine.TestDispatchersProvider
import com.cebolao.lotofacil.core.utils.TestTimeProvider
import com.cebolao.lotofacil.data.datasource.database.StatisticsCacheDao
import com.cebolao.lotofacil.data.datasource.database.entity.StatisticsCacheEntity
import com.cebolao.lotofacil.data.repository.StatisticsRepositoryImpl
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.domain.repository.CacheInvalidationTarget
import com.cebolao.lotofacil.domain.repository.CachePolicy
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class StatisticsRepositoryImplTest {

    private lateinit var dao: StatisticsCacheDao
    private lateinit var repository: StatisticsRepositoryImpl
    private lateinit var timeProvider: TestTimeProvider
    private lateinit var json: Json

    @Before
    fun setup() {
        dao = mock()
        timeProvider = TestTimeProvider(1_000L)
        json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        repository = StatisticsRepositoryImpl(dao, TestDispatchersProvider(), json, timeProvider)
    }

    @Test
    fun `cacheStatistics and getCachedStatistics should return persisted data`() = runTest {
        val report = StatisticsReport(averageSum = 190f, totalDrawsAnalyzed = 30)
        whenever(dao.countAll()).thenReturn(1)
        whenever(dao.countValid(any())).thenReturn(1)

        repository.cacheStatistics(windowSize = 30, statistics = report, ttlMs = 60_000L)

        val payloadCaptor = org.mockito.kotlin.argumentCaptor<StatisticsCacheEntity>()
        verify(dao).upsert(payloadCaptor.capture())
        val persisted = payloadCaptor.firstValue
        whenever(dao.getByWindow(30)).thenReturn(persisted)
        whenever(dao.countAll()).thenReturn(1)
        whenever(dao.countValid(any())).thenReturn(1)

        val result = repository.getCachedStatistics(30, CachePolicy.OnlyValid)

        assertNotNull(result)
        assertEquals(190f, result?.averageSum)
        assertEquals(30, result?.totalDrawsAnalyzed)
    }

    @Test
    fun `getCachedStatistics should return null for expired cache when policy is OnlyValid`() = runTest {
        val expiredEntry = StatisticsCacheEntity(
            windowSize = 7,
            reportJson = json.encodeToString(StatisticsReport(averageSum = 10f)),
            cachedAt = timeProvider.currentTimeMillis() - 10_000L,
            ttlMs = 1_000L
        )
        whenever(dao.getByWindow(7)).thenReturn(expiredEntry)
        whenever(dao.countAll()).thenReturn(0)
        whenever(dao.countValid(any())).thenReturn(0)

        val result = repository.getCachedStatistics(7, CachePolicy.OnlyValid)

        assertNull(result)
        verify(dao).clearWindow(7)
    }

    @Test
    fun `getCachedStatistics should return stale cache when policy allows stale`() = runTest {
        val staleEntry = StatisticsCacheEntity(
            windowSize = 7,
            reportJson = json.encodeToString(StatisticsReport(totalDrawsAnalyzed = 7)),
            cachedAt = timeProvider.currentTimeMillis() - 10_000L,
            ttlMs = 1_000L
        )
        whenever(dao.getByWindow(7)).thenReturn(staleEntry)
        whenever(dao.countAll()).thenReturn(1)
        whenever(dao.countValid(any())).thenReturn(0)

        val result = repository.getCachedStatistics(7, CachePolicy.AllowStale)

        assertNotNull(result)
        assertEquals(7, result?.totalDrawsAnalyzed)
    }

    @Test
    fun `clearCache should support window and full invalidation`() = runTest {
        whenever(dao.countAll()).thenReturn(0)
        whenever(dao.countValid(any())).thenReturn(0)

        repository.clearCache(CacheInvalidationTarget.Window(30))
        repository.clearCache(CacheInvalidationTarget.All)

        verify(dao).clearWindow(30)
        verify(dao).clearAll()
    }

    @Test
    fun `clearExpiredCache should delegate to dao`() = runTest {
        whenever(dao.clearExpired(any())).thenReturn(1)
        whenever(dao.countAll()).thenReturn(1, 0)
        whenever(dao.countValid(any())).thenReturn(0, 0)

        repository.clearExpiredCache()

        verify(dao).clearExpired(any())
    }

    @Test
    fun `hasValidCache should return true only for non expired entry`() = runTest {
        val freshEntry = StatisticsCacheEntity(
            windowSize = 90,
            reportJson = json.encodeToString(StatisticsReport()),
            cachedAt = timeProvider.currentTimeMillis(),
            ttlMs = 60_000L
        )
        whenever(dao.getByWindow(eq(90))).thenReturn(freshEntry)

        assertTrue(repository.hasValidCache(90))
    }
}
