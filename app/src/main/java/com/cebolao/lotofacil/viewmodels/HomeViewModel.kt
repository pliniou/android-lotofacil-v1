package com.cebolao.lotofacil.viewmodels

import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.repository.CacheInvalidationTarget
import com.cebolao.lotofacil.domain.repository.CachePolicy
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.StatisticsRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import com.cebolao.lotofacil.domain.service.StatisticsEngine
import com.cebolao.lotofacil.domain.usecase.GetHomeScreenDataUseCase
import com.cebolao.lotofacil.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeScreenDataUseCase: GetHomeScreenDataUseCase,
    private val historyRepository: HistoryRepository,
    private val statisticsEngine: StatisticsEngine,
    private val statisticsRepository: StatisticsRepository,
    private val dispatchersProvider: DispatchersProvider
) : StateViewModel<HomeUiState>(HomeUiState()) {

    private var syncStatusJob: Job? = null
    private var statsJob: Job? = null
    private var cachedHistory: List<HistoricalDraw> = emptyList()

    init {
        observeSyncStatus()
        loadInitialData()
    }

    override fun onCleared() {
        super.onCleared()
        syncStatusJob?.cancel()
        statsJob?.cancel()
        jobTracker.cancelAll()
    }

    private fun observeSyncStatus() {
        syncStatusJob = viewModelScope.launch {
            historyRepository.syncStatus.collect { status ->
                when (status) {
                    is SyncStatus.Progress -> {
                        updateState { it.copy(syncProgress = status.current to status.total) }
                    }
                    is SyncStatus.Success -> {
                        updateState { it.copy(syncProgress = null, isInitialSync = false) }
                    }
                    is SyncStatus.Failed -> {
                        updateState { it.copy(syncProgress = null) }
                        sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_sync_failed))
                    }
                    else -> {
                        updateState { it.copy(syncProgress = null) }
                    }
                }
            }
        }
    }

    private fun loadInitialData() {
        updateState { it.copy(isScreenLoading = true, errorMessageResId = null) }

        viewModelScope.launch {
            // Reduced timeout to prevent UI blocking
            val result = withTimeoutOrNull(3000L) {
                getHomeScreenDataUseCase().first { it is AppResult.Success }
            }

            if (result is AppResult.Success) {
                val data = result.value
                val lastDraw = data.history.firstOrNull()
                cachedHistory = data.history

                val nextDrawStats = data.lastDrawStats
                updateState {
                    it.copy(
                        isScreenLoading = false,
                        errorMessageResId = null,
                        lastDrawStats = data.lastDrawStats,
                        statistics = data.initialStats,
                        historySource = DataLoadSource.CACHE,
                        statisticsSource = DataLoadSource.COMPUTED,
                        isShowingStaleData = false,
                        lastUpdateTime = lastDraw?.date,
                        nextDrawDate = nextDrawStats?.nextDate,
                        nextDrawContest = nextDrawStats?.nextContest,
                        isTodayDrawDay = checkIsTodayDrawDay(nextDrawStats?.nextDate)
                    )
                }
                // Only sync in background if data is not fresh
                if (historyRepository.syncStatus.value !is SyncStatus.Success) {
                    // Check if it's the very first sync (gap from asset)
                    val isPotentiallyInitial = lastDraw?.contestNumber != null && lastDraw.contestNumber <= 3455
                    if (isPotentiallyInitial) {
                        updateState { it.copy(isInitialSync = true) }
                    }
                    syncInBackground()
                }
            } else {
                updateState {
                    it.copy(
                        isScreenLoading = false,
                        errorMessageResId = R.string.error_load_data_failed,
                        historySource = DataLoadSource.CACHE,
                        isShowingStaleData = true
                    )
                }
                // Try to load cached data as fallback
                loadCachedDataAsFallback()
            }
        }
    }

    private fun loadCachedDataAsFallback() {
        viewModelScope.launch {
            try {
                // Try to load any cached data as fallback
                val fallbackHistory = withTimeoutOrNull(2000L) {
                    historyRepository.getHistory().first()
                }

                if (!fallbackHistory.isNullOrEmpty()) {
                    val lastDraw = fallbackHistory.firstOrNull()
                    updateState {
                        it.copy(
                            lastDrawStats = null, // Will be computed later
                            statistics = null, // Will be computed later
                            lastUpdateTime = lastDraw?.date,
                            isShowingStaleData = true
                        )
                    }
                    cachedHistory = fallbackHistory
                    // Compute basic stats in background
                    computeBasicStats()
                } else {
                    sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_load_data_failed))
                }
            } catch (e: Exception) {
                sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_load_data_failed))
            }
        }
    }
    
    private fun computeBasicStats() {
        statsJob = viewModelScope.launch(dispatchersProvider.io) {
            try {
                if (cachedHistory.isNotEmpty()) {
                    val basicStats = statisticsEngine.analyze(cachedHistory.take(50)) // Last 50 draws
                    updateState {
                        it.copy(
                            statistics = basicStats,
                            statisticsSource = DataLoadSource.COMPUTED
                        )
                    }
                }
            } catch (e: Exception) {
                // Silently fail - stats will be computed later
            }
        }
    }

    private fun checkIsTodayDrawDay(nextDate: String?): Boolean {
        if (nextDate.isNullOrBlank()) return false
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val today = LocalDate.now().format(formatter)
            today == nextDate
        } catch (_: Exception) {
            false
        }
    }

    fun refreshData() {
        viewModelScope.launch(dispatchersProvider.default) {
            try {
                updateState { it.copy(isRefreshing = true, errorMessageResId = null) }
                val result = historyRepository.syncHistory()
                updateState { it.copy(isRefreshing = false) }
                when (result) {
                    is AppResult.Success -> {
                        statisticsRepository.clearCache(CacheInvalidationTarget.All)
                        val selectedWindow = currentState.selectedTimeWindow
                        if (selectedWindow > 0) {
                            onTimeWindowSelected(selectedWindow, forceRefresh = true)
                        }
                        updateState {
                            it.copy(
                                historySource = DataLoadSource.NETWORK,
                                isShowingStaleData = false
                            )
                        }
                        sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.refresh_success))
                    }
                    is AppResult.Failure -> {
                        updateState {
                            it.copy(
                                historySource = DataLoadSource.CACHE,
                                isShowingStaleData = true
                            )
                        }
                        sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_sync_failed))
                    }
                }
            } catch (_: Exception) {
                updateState {
                    it.copy(
                        isRefreshing = false,
                        historySource = DataLoadSource.CACHE,
                        isShowingStaleData = true
                    )
                }
                sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_sync_failed))
            }
        }
    }

    fun onTimeWindowSelected(window: Int, forceRefresh: Boolean = false) {
        val current = currentState
        if (!forceRefresh && current.selectedTimeWindow == window) return

        statsJob?.cancel()
        statsJob = viewModelScope.launch(dispatchersProvider.io) {
            updateState { it.copy(isStatsLoading = true, selectedTimeWindow = window) }

            try {
                val cachedStats = if (forceRefresh) {
                    null
                } else {
                    statisticsRepository.getCachedStatistics(
                        windowSize = window,
                        policy = CachePolicy.OnlyValid
                    )
                }
                if (cachedStats != null) {
                    updateState {
                        it.copy(
                            statistics = cachedStats,
                            isStatsLoading = false,
                            statisticsSource = DataLoadSource.CACHE
                        )
                    }
                    return@launch
                }

                val allHistory = cachedHistory.ifEmpty {
                    withTimeoutOrNull(5000L) {
                        historyRepository.getHistory().first()
                    } ?: emptyList()
                }
                val draws = if (window > 0) allHistory.take(window) else allHistory
                val newStats = statisticsEngine.analyze(draws)

                statisticsRepository.cacheStatistics(window, newStats)
                updateState {
                    it.copy(
                        statistics = newStats,
                        isStatsLoading = false,
                        statisticsSource = DataLoadSource.COMPUTED
                    )
                }
            } catch (_: Exception) {
                val staleStats = statisticsRepository.getCachedStatistics(
                    windowSize = window,
                    policy = CachePolicy.AllowStale
                )
                if (staleStats != null) {
                    updateState {
                        it.copy(
                            statistics = staleStats,
                            isStatsLoading = false,
                            statisticsSource = DataLoadSource.CACHE,
                            isShowingStaleData = true
                        )
                    }
                    sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.sync_failed_message))
                } else {
                    updateState { it.copy(isStatsLoading = false) }
                    sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_load_data_failed))
                }
            } finally {
                statisticsRepository.clearExpiredCache()
            }
        }
        jobTracker.trackNonBlocking(statsJob!!)
    }

    fun onPatternSelected(pattern: StatisticPattern) {
        val current = currentState
        if (current.selectedPattern == pattern) return
        updateState { it.copy(selectedPattern = pattern) }
    }

    private fun syncInBackground() {
        viewModelScope.launch(dispatchersProvider.io) {
            when (historyRepository.syncHistory()) {
                is AppResult.Success -> {
                    updateState {
                        it.copy(
                            historySource = DataLoadSource.NETWORK,
                            isShowingStaleData = false
                        )
                    }
                }
                is AppResult.Failure -> {
                    updateState {
                        it.copy(
                            historySource = DataLoadSource.CACHE,
                            isShowingStaleData = true
                        )
                    }
                }
            }
        }
    }
}
