package com.cebolao.lotofacil.viewmodels

import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val historyRepository: HistoryRepository = mockk()
    private val logger: AppLogger = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initializeApp success updates state to ready`() = runTest(testDispatcher) {
        // Given
        every { historyRepository.isInitialized } returns MutableStateFlow(true)
        every { historyRepository.getHistory() } returns flowOf(
            listOf(
                HistoricalDraw(
                    contestNumber = 3200,
                    numbers = (1..15).toSet(),
                    date = "10/02/2026"
                )
            )
        )
        coEvery { historyRepository.syncHistory() } returns AppResult.Success(Unit)

        // When
        val viewModel = MainViewModel(historyRepository, logger)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue("State should be ready", state.isReady)
    }

    @Test
    fun `initializeApp should keep app ready and expose sync feedback when sync fails`() = runTest(testDispatcher) {
        every { historyRepository.isInitialized } returns MutableStateFlow(true)
        every { historyRepository.getHistory() } returns flowOf(emptyList())
        coEvery { historyRepository.syncHistory() } returns AppResult.Failure(com.cebolao.lotofacil.core.error.EmptyHistoryError)

        val viewModel = MainViewModel(historyRepository, logger)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.hasError)
        assertTrue(state.isReady)
        assertEquals(com.cebolao.lotofacil.R.string.error_sync_failed, state.startupSyncErrorMessageResId)
    }
}
