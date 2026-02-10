package com.cebolao.lotofacil.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.DomainError
import com.cebolao.lotofacil.domain.usecase.FrequencyAnalysis
import com.cebolao.lotofacil.domain.usecase.GetFrequencyAnalysisUseCase
import com.cebolao.lotofacil.domain.usecase.GetPatternAnalysisUseCase
import com.cebolao.lotofacil.domain.usecase.GetTrendAnalysisUseCase
import com.cebolao.lotofacil.domain.usecase.PatternAnalysis
import com.cebolao.lotofacil.domain.usecase.TrendAnalysis
import com.cebolao.lotofacil.domain.usecase.TrendType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class InsightsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getFrequencyAnalysisUseCase: GetFrequencyAnalysisUseCase
    private lateinit var getPatternAnalysisUseCase: GetPatternAnalysisUseCase
    private lateinit var getTrendAnalysisUseCase: GetTrendAnalysisUseCase
    private lateinit var viewModel: InsightsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getFrequencyAnalysisUseCase = mock()
        getPatternAnalysisUseCase = mock()
        getTrendAnalysisUseCase = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads all insights sections successfully`() = runTest {
        whenever(getFrequencyAnalysisUseCase.invoke()).thenReturn(AppResult.Success(fakeFrequency()))
        whenever(getPatternAnalysisUseCase.invoke(any())).thenReturn(AppResult.Success(fakePattern()))
        whenever(getTrendAnalysisUseCase.invoke(any(), any())).thenReturn(AppResult.Success(fakeTrend()))

        viewModel = InsightsViewModel(
            getFrequencyAnalysisUseCase,
            getPatternAnalysisUseCase,
            getTrendAnalysisUseCase
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.frequencyAnalysis)
        assertNotNull(state.patternAnalysis)
        assertNotNull(state.trendAnalysis)
        assertNull(state.errorMessageResId)
        assertNull(state.patternErrorMessageResId)
        assertNull(state.trendErrorMessageResId)
        assertFalse(state.isLoading)
        assertFalse(state.isPatternLoading)
        assertFalse(state.isTrendLoading)
    }

    @Test
    fun `pattern failure should not set global error`() = runTest {
        whenever(getFrequencyAnalysisUseCase.invoke()).thenReturn(AppResult.Success(fakeFrequency()))
        whenever(getPatternAnalysisUseCase.invoke(any()))
            .thenReturn(AppResult.Failure(DomainError.Unknown(RuntimeException("pattern error"))))
        whenever(getTrendAnalysisUseCase.invoke(any(), any())).thenReturn(AppResult.Success(fakeTrend()))

        viewModel = InsightsViewModel(
            getFrequencyAnalysisUseCase,
            getPatternAnalysisUseCase,
            getTrendAnalysisUseCase
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.frequencyAnalysis)
        assertNotNull(state.trendAnalysis)
        assertNull(state.errorMessageResId)
        assertEquals(R.string.error_load_data_failed, state.patternErrorMessageResId)
    }

    private fun fakeFrequency() = FrequencyAnalysis(
        frequencies = mapOf(1 to 10, 2 to 12, 3 to 8),
        topNumbers = listOf(2, 1, 3, 4, 5),
        overdueNumbers = listOf(10 to 2, 11 to 4, 12 to 1),
        totalDraws = 100
    )

    private fun fakePattern() = PatternAnalysis(
        size = 2,
        patterns = listOf(setOf(1, 2) to 12, setOf(3, 4) to 9),
        totalDraws = 100
    )

    private fun fakeTrend() = TrendAnalysis(
        type = TrendType.SUM,
        timeline = listOf(1 to 190f, 2 to 195f, 3 to 200f),
        averageValue = 195f
    )
}
