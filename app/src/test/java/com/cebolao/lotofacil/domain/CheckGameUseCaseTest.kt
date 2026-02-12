package com.cebolao.lotofacil.domain

import app.cash.turbine.test
import com.cebolao.lotofacil.core.coroutine.TestDispatchersProvider
import com.cebolao.lotofacil.core.error.EmptyHistoryError
import com.cebolao.lotofacil.domain.model.GameStatistic
import com.cebolao.lotofacil.domain.model.GameStatisticType
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.service.StatisticsEngine
import com.cebolao.lotofacil.domain.usecase.CheckGameUseCase
import com.cebolao.lotofacil.domain.usecase.GameCheckState
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class CheckGameUseCaseTest {

    private lateinit var historyRepository: HistoryRepository
    private lateinit var statisticsEngine: StatisticsEngine
    private lateinit var dispatchersProvider: TestDispatchersProvider
    private lateinit var useCase: CheckGameUseCase

    @Before
    fun setup() {
        historyRepository = mock()
        statisticsEngine = mock()
        dispatchersProvider = TestDispatchersProvider()
        useCase = CheckGameUseCase(historyRepository, statisticsEngine, dispatchersProvider)
    }

    @Test
    fun `invoke should emit success with expected check result`() = runTest {
        val gameNumbers = (1..15).toSet()
        val history = listOf(
            HistoricalDraw(
                contestNumber = 101,
                numbers = gameNumbers
            ),
            HistoricalDraw(
                contestNumber = 100,
                numbers = setOf(1, 2, 3, 4, 5, 6, 7, 16, 17, 18, 19, 20, 21, 22, 23)
            )
        )

        whenever(historyRepository.getHistory()).thenReturn(flowOf(history))
        whenever(statisticsEngine.analyzeGame(any())).thenReturn(
            listOf(GameStatistic(GameStatisticType.SUM, 120))
        )

        useCase(gameNumbers).test {
            assertTrue(awaitItem() is GameCheckState.InProgress)
            assertTrue(awaitItem() is GameCheckState.InProgress)
            assertTrue(awaitItem() is GameCheckState.InProgress)

            val success = awaitItem() as GameCheckState.Success
            assertEquals(101, success.result.lastHitContest)
            assertEquals(15, success.result.lastHitScore)
            assertEquals(1, success.result.scoreCounts.size)
            assertEquals(1, success.result.scoreCounts[15])
            assertEquals(1, success.stats.size)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should emit failure when history is empty`() = runTest {
        whenever(historyRepository.getHistory()).thenReturn(flowOf(emptyList()))

        useCase((1..15).toSet()).test {
            assertTrue(awaitItem() is GameCheckState.InProgress)
            val failure = awaitItem() as GameCheckState.Failure
            assertEquals(EmptyHistoryError, failure.error)
            awaitComplete()
        }
    }
}
