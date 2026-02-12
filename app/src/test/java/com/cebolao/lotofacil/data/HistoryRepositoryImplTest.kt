@file:Suppress("UNCHECKED_CAST")

package com.cebolao.lotofacil.data

import com.cebolao.lotofacil.core.error.UnknownError
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.utils.TestAppLogger
import com.cebolao.lotofacil.data.datasource.HistoryLocalDataSource
import com.cebolao.lotofacil.data.datasource.HistoryRemoteDataSource
import com.cebolao.lotofacil.data.repository.HistoryRepositoryImpl
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.repository.SyncStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class HistoryRepositoryImplTest {

    private lateinit var localDataSource: HistoryLocalDataSource
    private lateinit var remoteDataSource: HistoryRemoteDataSource
    private lateinit var applicationScope: CoroutineScope

    @Before
    fun setup() {
        localDataSource = mock()
        remoteDataSource = mock()
        applicationScope = CoroutineScope(Dispatchers.Unconfined)
    }

    @Test
    fun `init should populate local database and mark initialized`() = runTest {
        whenever(localDataSource.populateIfNeeded()).thenReturn(Unit)

        val repository = HistoryRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            applicationScope = applicationScope,
            logger = TestAppLogger()
        )

        verify(localDataSource).populateIfNeeded()
        assertTrue(repository.isInitialized.value)
    }

    @Test
    fun `getHistory should delegate to local data source`() = runTest {
        val draws = listOf(HistoricalDraw(contestNumber = 100, numbers = (1..15).toSet()))
        whenever(localDataSource.populateIfNeeded()).thenReturn(Unit)
        whenever(localDataSource.getHistory()).thenReturn(flowOf(draws))

        val repository = HistoryRepositoryImpl(
            localDataSource,
            remoteDataSource,
            applicationScope,
            TestAppLogger()
        )

        val result = repository.getHistory().first()

        assertEquals(draws, result)
        verify(localDataSource).getHistory()
    }

    @Test
    fun `getLastDraw should delegate to local data source`() = runTest {
        val latest = HistoricalDraw(contestNumber = 2200, numbers = (1..15).toSet())
        whenever(localDataSource.populateIfNeeded()).thenReturn(Unit)
        whenever(localDataSource.getLatestDraw()).thenReturn(latest)

        val repository = HistoryRepositoryImpl(
            localDataSource,
            remoteDataSource,
            applicationScope,
            TestAppLogger()
        )

        val result = repository.getLastDraw()

        assertEquals(latest, result)
        verify(localDataSource).getLatestDraw()
    }

    @Test
    fun `syncHistory should fetch and persist new contests when remote is newer`() = runTest {
        val remoteLatest = HistoricalDraw(contestNumber = 3002, numbers = (1..15).toSet())
        val newDraws = listOf(
            HistoricalDraw(contestNumber = 3001, numbers = (1..15).toSet()),
            HistoricalDraw(contestNumber = 3002, numbers = (2..16).toSet())
        )
        whenever(localDataSource.populateIfNeeded()).thenReturn(Unit)
        whenever(localDataSource.getLatestDraw()).thenReturn(
            HistoricalDraw(contestNumber = 3000, numbers = (1..15).toSet())
        )
        whenever(remoteDataSource.getLatestDraw()).thenReturn(remoteLatest)
        
        // Match the range and any progress callback
        whenever(remoteDataSource.getDrawsInRange(
            eq(3001..3002),
            any(),
            any()
        )).thenAnswer { invocation ->
            val callback = invocation.arguments[2] as suspend (List<HistoricalDraw>) -> Unit
            runBlocking {
               callback(newDraws)
            }
            newDraws
        }

        val repository = HistoryRepositoryImpl(
            localDataSource,
            remoteDataSource,
            applicationScope,
            TestAppLogger()
        )

        val result = repository.syncHistory()

        assertTrue(result is AppResult.Success)
        verify(localDataSource).saveNewContests(newDraws)
        assertTrue(repository.syncStatus.value is SyncStatus.Success)
    }

    @Test
    fun `syncHistory should return failure when remote throws`() = runTest {
        whenever(localDataSource.populateIfNeeded()).thenReturn(Unit)
        whenever(remoteDataSource.getLatestDraw()).thenThrow(RuntimeException("network down"))

        val repository = HistoryRepositoryImpl(
            localDataSource,
            remoteDataSource,
            applicationScope,
            TestAppLogger()
        )

        val result = repository.syncHistory()

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is UnknownError)
        assertTrue(repository.syncStatus.value is SyncStatus.Failed)
    }
}
