package com.cebolao.lotofacil.viewmodels

import app.cash.turbine.test
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.result.ErrorMessageMapper
import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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
    private val errorMessageMapper: ErrorMessageMapper = mockk(relaxed = true)
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
        val viewModel = MainViewModel(historyRepository, logger, errorMessageMapper)

        // Then
        viewModel.uiState.test {
            // Initial state
            val initialState = awaitItem()
            // Loading state (triggered by initializeApp inside init block)
            // Depending on how fast the coroutine runs, we might see loading=true
            
            // Wait for the final state which should be ready
            // We might need to skip intermediate states if they are emitted quickly
            
            // The fix ensures initializationResult is set to Success
            
            var state = if (initialState.isLoading) awaitItem() else initialState
            
            // If the first state wasn't the result, keep waiting until we get a result or timeout
             while (!state.isReady && !state.hasError) {
                state = awaitItem()
            }

            assertTrue("State should be ready", state.isReady)
            assertTrue("Initialization result should be success", state.initializationResult?.isSuccess == true)
        }
    }
}
