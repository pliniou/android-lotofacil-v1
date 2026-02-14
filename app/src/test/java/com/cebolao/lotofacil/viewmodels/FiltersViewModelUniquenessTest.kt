package com.cebolao.lotofacil.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.domain.model.FilterPreset
import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import com.cebolao.lotofacil.domain.usecase.GenerateGamesUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FiltersViewModelUniquenessTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: FiltersViewModel
    private val mockGameRepository = mockk<GameRepository>(relaxed = true)
    private val mockGenerateGamesUseCase = mockk<GenerateGamesUseCase>(relaxed = true)
    private val mockHistoryRepository = mockk<HistoryRepository>()
    private val mockDispatchersProvider = mockk<DispatchersProvider>()
    private val mockLogger = mockk<AppLogger>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { mockHistoryRepository.getLastDraw() } returns null
        every { mockHistoryRepository.syncStatus } returns MutableStateFlow(SyncStatus.Idle)
        every { mockHistoryRepository.isInitialized } returns MutableStateFlow(true)
        every { mockDispatchersProvider.main } returns testDispatcher
        every { mockDispatchersProvider.io } returns testDispatcher
        every { mockDispatchersProvider.default } returns testDispatcher
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `default filter states should be unique`() = runTest(testDispatcher) {
        viewModel = FiltersViewModel(
            mockGameRepository,
            mockGenerateGamesUseCase,
            mockHistoryRepository,
            mockDispatchersProvider,
            mockLogger
        )
        advanceUntilIdle()

        val filterStates = viewModel.uiState.value.filterStates
        assertEquals(FilterType.entries.size, filterStates.size)
        assertEquals(filterStates.size, filterStates.map { it.type.name }.distinct().size)
        assertEquals(filterStates.size, filterStates.map { it.type }.distinct().size)
    }

    @Test
    fun `apply preset should maintain uniqueness`() = runTest(testDispatcher) {
        viewModel = FiltersViewModel(
            mockGameRepository,
            mockGenerateGamesUseCase,
            mockHistoryRepository,
            mockDispatchersProvider,
            mockLogger
        )
        advanceUntilIdle()

        FilterPreset.entries.forEach { preset ->
            viewModel.applyPreset(preset)
            val filterStates = viewModel.uiState.value.filterStates
            assertEquals(filterStates.size, filterStates.map { it.type.name }.distinct().size)
            assertTrue(filterStates.all { it.isEnabled })
        }
    }

    @Test
    fun `reset filters should maintain uniqueness`() = runTest(testDispatcher) {
        viewModel = FiltersViewModel(
            mockGameRepository,
            mockGenerateGamesUseCase,
            mockHistoryRepository,
            mockDispatchersProvider,
            mockLogger
        )
        advanceUntilIdle()

        viewModel.onFilterToggle(FilterType.PARES, true)
        viewModel.onFilterToggle(FilterType.PRIMOS, true)
        viewModel.resetFilters()

        val filterStates = viewModel.uiState.value.filterStates
        assertEquals(filterStates.size, filterStates.map { it.type.name }.distinct().size)
        assertEquals(0, viewModel.uiState.value.activeFiltersCount)
    }

    @Test
    fun `filter toggle operations should maintain uniqueness`() = runTest(testDispatcher) {
        viewModel = FiltersViewModel(
            mockGameRepository,
            mockGenerateGamesUseCase,
            mockHistoryRepository,
            mockDispatchersProvider,
            mockLogger
        )
        advanceUntilIdle()

        FilterType.entries.take(5).forEach { filterType ->
            viewModel.onFilterToggle(filterType, true)
        }

        val filterStates = viewModel.uiState.value.filterStates
        assertEquals(filterStates.size, filterStates.map { it.type.name }.distinct().size)
        assertEquals(5, viewModel.uiState.value.activeFiltersCount)
    }

    @Test
    fun `range adjustments should maintain uniqueness`() = runTest(testDispatcher) {
        viewModel = FiltersViewModel(
            mockGameRepository,
            mockGenerateGamesUseCase,
            mockHistoryRepository,
            mockDispatchersProvider,
            mockLogger
        )
        advanceUntilIdle()

        FilterType.entries.take(3).forEach { filterType ->
            val newRange = filterType.defaultRange.start + 1f..filterType.defaultRange.endInclusive - 1f
            viewModel.onRangeAdjust(filterType, newRange)
        }

        val filterStates = viewModel.uiState.value.filterStates
        assertEquals(filterStates.size, filterStates.map { it.type.name }.distinct().size)
    }

    @Test
    fun `detect duplicate filter types in custom list`() {
        val duplicateFilters = listOf(
            FilterState(type = FilterType.PARES),
            FilterState(type = FilterType.PRIMOS),
            FilterState(type = FilterType.PARES),
            FilterState(type = FilterType.MOLDURA)
        )

        val duplicateNames = duplicateFilters.map { it.type.name }
            .groupBy { it }
            .filter { it.value.size > 1 }
            .keys

        assertTrue(duplicateNames.contains("PARES"))
    }

    @Test
    fun `validate filter type enum uniqueness`() {
        val filterTypeNames = FilterType.entries.map { it.name }
        assertEquals(filterTypeNames.size, filterTypeNames.distinct().size)
        assertTrue(filterTypeNames.all { it.isNotBlank() })
    }

    @Test
    fun `should expose total combinations when all filters are disabled`() = runTest(testDispatcher) {
        viewModel = FiltersViewModel(
            mockGameRepository,
            mockGenerateGamesUseCase,
            mockHistoryRepository,
            mockDispatchersProvider,
            mockLogger
        )
        advanceUntilIdle()

        assertEquals(3_268_760L, viewModel.uiState.value.possibleCombinationsCount)
        assertTrue(!viewModel.uiState.value.isCombinationImpossible)
    }

    @Test
    fun `should mark combination as impossible when filters conflict`() = runTest(testDispatcher) {
        viewModel = FiltersViewModel(
            mockGameRepository,
            mockGenerateGamesUseCase,
            mockHistoryRepository,
            mockDispatchersProvider,
            mockLogger
        )
        advanceUntilIdle()

        viewModel.onRangeAdjust(FilterType.PARES, 0f..0f)
        viewModel.onRangeAdjust(FilterType.PRIMOS, 9f..9f)
        viewModel.onFilterToggle(FilterType.PARES, true)
        viewModel.onFilterToggle(FilterType.PRIMOS, true)
        advanceUntilIdle()

        assertEquals(0L, viewModel.uiState.value.possibleCombinationsCount)
        assertTrue(viewModel.uiState.value.isCombinationImpossible)
    }
}
