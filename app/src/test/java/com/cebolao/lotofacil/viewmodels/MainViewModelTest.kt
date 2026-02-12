package com.cebolao.lotofacil.viewmodels

import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
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
        coEvery { historyRepository.syncHistory() } returns AppResult.Success(Unit)

        // When
        val viewModel = MainViewModel(historyRepository, logger)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue("State should be ready", state.isReady)
    }
}
