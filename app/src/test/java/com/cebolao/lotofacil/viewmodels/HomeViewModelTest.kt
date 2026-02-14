package com.cebolao.lotofacil.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import com.cebolao.lotofacil.domain.usecase.GetHomeScreenDataUseCase
import com.cebolao.lotofacil.domain.usecase.GetStatisticsDataUseCase
import com.cebolao.lotofacil.domain.usecase.HomeScreenData
import com.cebolao.lotofacil.domain.usecase.StatisticsDataSource
import com.cebolao.lotofacil.domain.usecase.StatisticsReportSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getHomeScreenDataUseCase: GetHomeScreenDataUseCase
    private lateinit var getStatisticsDataUseCase: GetStatisticsDataUseCase
    private lateinit var historyRepository: HistoryRepository
    private lateinit var dispatchersProvider: DispatchersProvider
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getHomeScreenDataUseCase = mock()
        getStatisticsDataUseCase = mock()
        historyRepository = mock()
        dispatchersProvider = mock()

        whenever(dispatchersProvider.default).thenReturn(testDispatcher)
        whenever(dispatchersProvider.main).thenReturn(testDispatcher)
        whenever(dispatchersProvider.io).thenReturn(testDispatcher)
        whenever(historyRepository.syncStatus).thenReturn(MutableStateFlow(SyncStatus.Idle))
        runTest {
            whenever(historyRepository.syncHistory()).thenReturn(AppResult.Success(Unit))
            whenever(historyRepository.getHistory()).thenReturn(flowOf(emptyList()))
            whenever(getStatisticsDataUseCase.clearCache(any())).thenReturn(Unit)
            whenever(getStatisticsDataUseCase.loadReportForHistory(any(), any(), any()))
                .thenReturn(
                    AppResult.Success(
                        StatisticsReportSnapshot(
                            report = StatisticsReport(),
                            source = StatisticsDataSource.CACHE,
                            isStale = false,
                            totalHistorySize = 0,
                            draws = emptyList()
                        )
                    )
                )
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load home data and clear loading flag`() = runTest {
        whenever(getHomeScreenDataUseCase.invoke()).thenReturn(
            flowOf(
                AppResult.Success(
                    HomeScreenData(
                        history = emptyList(),
                        lastContest = null,
                        nextContest = null,
                        initialStats = StatisticsReport(),
                        statisticsSource = StatisticsDataSource.COMPUTED,
                        isShowingStaleStatistics = false
                    )
                )
            )
        )

        viewModel = HomeViewModel(
            getHomeScreenDataUseCase = getHomeScreenDataUseCase,
            getStatisticsDataUseCase = getStatisticsDataUseCase,
            historyRepository = historyRepository,
            dispatchersProvider = dispatchersProvider
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isScreenLoading)
        assertNull(state.errorMessageResId)
    }

    @Test
    fun `onTimeWindowSelected should update selectedTimeWindow and source`() = runTest {
        val history = listOf(
            HistoricalDraw(
                contestNumber = 3200,
                numbers = (1..15).toSet(),
                date = "10/02/2026"
            )
        )
        whenever(getHomeScreenDataUseCase.invoke()).thenReturn(
            flowOf(
                AppResult.Success(
                    HomeScreenData(
                        history = history,
                        lastContest = null,
                        nextContest = null,
                        initialStats = StatisticsReport(),
                        statisticsSource = StatisticsDataSource.COMPUTED,
                        isShowingStaleStatistics = false
                    )
                )
            )
        )

        whenever(
            getStatisticsDataUseCase.loadReportForHistory(
                any(),
                eq(100),
                eq(false)
            )
        ).thenReturn(
            AppResult.Success(
                StatisticsReportSnapshot(
                    report = StatisticsReport(),
                    source = StatisticsDataSource.CACHE,
                    isStale = false,
                    totalHistorySize = history.size,
                    draws = history
                )
            )
        )

        viewModel = HomeViewModel(
            getHomeScreenDataUseCase = getHomeScreenDataUseCase,
            getStatisticsDataUseCase = getStatisticsDataUseCase,
            historyRepository = historyRepository,
            dispatchersProvider = dispatchersProvider
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onTimeWindowSelected(100)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(100, viewModel.uiState.value.selectedTimeWindow)
        assertEquals(DataLoadSource.CACHE, viewModel.uiState.value.statisticsSource)
    }

    @Test
    fun `onPatternSelected should update selectedPattern`() = runTest {
        whenever(getHomeScreenDataUseCase.invoke()).thenReturn(
            flowOf(
                AppResult.Success(
                    HomeScreenData(
                        history = emptyList(),
                        lastContest = null,
                        nextContest = null,
                        initialStats = StatisticsReport(),
                        statisticsSource = StatisticsDataSource.COMPUTED,
                        isShowingStaleStatistics = false
                    )
                )
            )
        )

        viewModel = HomeViewModel(
            getHomeScreenDataUseCase = getHomeScreenDataUseCase,
            getStatisticsDataUseCase = getStatisticsDataUseCase,
            historyRepository = historyRepository,
            dispatchersProvider = dispatchersProvider
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onPatternSelected(StatisticPattern.EVENS)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(StatisticPattern.EVENS, viewModel.uiState.value.selectedPattern)
    }

    @Test
    fun `refreshData should reload data and mark history source as network on success`() = runTest {
        val homeData = HomeScreenData(
            history = emptyList(),
            lastContest = null,
            nextContest = null,
            initialStats = StatisticsReport(),
            statisticsSource = StatisticsDataSource.COMPUTED,
            isShowingStaleStatistics = false
        )
        whenever(getHomeScreenDataUseCase.invoke()).thenReturn(flowOf(AppResult.Success(homeData)))
        whenever(historyRepository.syncHistory()).thenReturn(AppResult.Success(Unit))

        viewModel = HomeViewModel(
            getHomeScreenDataUseCase = getHomeScreenDataUseCase,
            getStatisticsDataUseCase = getStatisticsDataUseCase,
            historyRepository = historyRepository,
            dispatchersProvider = dispatchersProvider
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.refreshData()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isScreenLoading)
        assertNotNull(viewModel.uiState.value.statistics)
        assertEquals(DataLoadSource.NETWORK, viewModel.uiState.value.historySource)
    }
}
