package com.cebolao.lotofacil.viewmodels

import androidx.lifecycle.viewModelScope
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.LastDrawStats
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import com.cebolao.lotofacil.domain.usecase.GetHomeScreenDataUseCase
import com.cebolao.lotofacil.domain.usecase.GetStatisticsDataUseCase
import com.cebolao.lotofacil.domain.usecase.StatisticsDataSource
import com.cebolao.lotofacil.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeScreenDataUseCase: GetHomeScreenDataUseCase,
    private val getStatisticsDataUseCase: GetStatisticsDataUseCase,
    private val historyRepository: HistoryRepository,
    private val dispatchersProvider: DispatchersProvider
) : StateViewModel<HomeUiState>(HomeUiState()) {

    private var cachedHistory: List<HistoricalDraw> = emptyList()
    private var statsJob: Job? = null
    private var syncStatusJob: Job? = null

    init {
        observeSyncStatus()
        loadInitialData()
    }

    private fun observeSyncStatus() {
        syncStatusJob?.cancel()
        syncStatusJob = viewModelScope.launch {
            historyRepository.syncStatus.collect { status ->
                when (status) {
                    SyncStatus.Idle -> {
                        updateState { it.copy(syncState = HomeSyncState.Idle) }
                    }

                    SyncStatus.Syncing -> {
                        updateState { it.copy(syncState = HomeSyncState.InProgress(current = null, total = null)) }
                    }

                    is SyncStatus.Progress -> {
                        updateState {
                            it.copy(
                                syncState = HomeSyncState.InProgress(
                                    current = status.current,
                                    total = status.total
                                )
                            )
                        }
                    }

                    SyncStatus.Success -> {
                        updateState {
                            it.copy(
                                syncState = HomeSyncState.Success,
                                historySource = DataLoadSource.NETWORK,
                                isShowingStaleData = false
                            )
                        }
                    }

                    is SyncStatus.Failed -> {
                        updateState {
                            it.copy(
                                syncState = HomeSyncState.Failed(status.message),
                                historySource = DataLoadSource.CACHE,
                                isShowingStaleData = true
                            )
                        }
                    }
                }
            }
        }
        jobTracker.trackNonBlocking(syncStatusJob!!)
    }

    private fun loadInitialData() {
        viewModelScope.launch(dispatchersProvider.io) {
            updateState { it.copy(isScreenLoading = true, errorMessageResId = null) }

            val result = withTimeoutOrNull(3000L) {
                getHomeScreenDataUseCase().first {
                    it is AppResult.Success || it is AppResult.Failure
                }
            }

            when (result) {
                is AppResult.Success -> {
                    val data = result.value
                    val lastDraw = data.history.firstOrNull()
                    cachedHistory = data.history

                    updateState {
                        it.copy(
                            isScreenLoading = false,
                            errorMessageResId = null,
                            lastDrawStats = data.lastDrawStats,
                            statistics = data.initialStats,
                            historySource = DataLoadSource.CACHE,
                            statisticsSource = mapStatisticsSource(data.statisticsSource),
                            isShowingStaleData = data.isShowingStaleStatistics,
                            lastUpdateTime = lastDraw?.date,
                            nextDraw = data.lastDrawStats?.toNextDrawUiModel(),
                            isTodayDrawDay = checkIsTodayDrawDay(data.lastDrawStats?.nextDate)
                        )
                    }
                }

                else -> {
                    updateState {
                        it.copy(
                            isScreenLoading = false,
                            errorMessageResId = R.string.error_load_data_failed,
                            historySource = DataLoadSource.CACHE,
                            isShowingStaleData = true
                        )
                    }
                    loadCachedDataAsFallback()
                }
            }
        }
    }

    private fun loadCachedDataAsFallback() {
        viewModelScope.launch(dispatchersProvider.io) {
            try {
                val fallbackHistory = withTimeoutOrNull(2000L) {
                    historyRepository.getHistory().first()
                }

                if (fallbackHistory.isNullOrEmpty()) {
                    sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_load_data_failed))
                    return@launch
                }

                cachedHistory = fallbackHistory
                val latestDraw = fallbackHistory.firstOrNull()
                val latestStats = latestDraw?.toLastDrawStats()

                val statisticsResult = getStatisticsDataUseCase.loadReportForHistory(
                    history = fallbackHistory,
                    timeWindow = currentState.selectedTimeWindow,
                    forceRefresh = false
                )

                when (statisticsResult) {
                    is AppResult.Success -> {
                        updateState {
                            it.copy(
                                lastDrawStats = latestStats,
                                statistics = statisticsResult.value.report,
                                statisticsSource = mapStatisticsSource(statisticsResult.value.source),
                                isShowingStaleData = true,
                                lastUpdateTime = latestDraw?.date,
                                nextDraw = latestStats?.toNextDrawUiModel(),
                                isTodayDrawDay = checkIsTodayDrawDay(latestStats?.nextDate)
                            )
                        }
                    }

                    is AppResult.Failure -> {
                        updateState {
                            it.copy(
                                lastDrawStats = latestStats,
                                statistics = null,
                                isShowingStaleData = true,
                                lastUpdateTime = latestDraw?.date,
                                nextDraw = latestStats?.toNextDrawUiModel(),
                                isTodayDrawDay = checkIsTodayDrawDay(latestStats?.nextDate)
                            )
                        }
                    }
                }
            } catch (_: Exception) {
                sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_load_data_failed))
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch(dispatchersProvider.default) {
            when (val result = historyRepository.syncHistory()) {
                is AppResult.Success -> {
                    getStatisticsDataUseCase.clearCache()
                    refreshScreenDataAfterSync()
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
        }
    }

    private suspend fun refreshScreenDataAfterSync() {
        when (val dataResult = getHomeScreenDataUseCase().first()) {
            is AppResult.Success -> {
                val data = dataResult.value
                cachedHistory = data.history
                updateState {
                    it.copy(
                        lastDrawStats = data.lastDrawStats,
                        statistics = data.initialStats,
                        historySource = DataLoadSource.NETWORK,
                        statisticsSource = mapStatisticsSource(data.statisticsSource),
                        isShowingStaleData = data.isShowingStaleStatistics,
                        lastUpdateTime = data.history.firstOrNull()?.date,
                        nextDraw = data.lastDrawStats?.toNextDrawUiModel(),
                        isTodayDrawDay = checkIsTodayDrawDay(data.lastDrawStats?.nextDate)
                    )
                }
                val selectedWindow = currentState.selectedTimeWindow
                if (selectedWindow > 0) {
                    onTimeWindowSelected(selectedWindow, forceRefresh = true)
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

    fun onTimeWindowSelected(window: Int, forceRefresh: Boolean = false) {
        val current = currentState
        if (!forceRefresh && current.selectedTimeWindow == window) return

        statsJob?.cancel()
        statsJob = viewModelScope.launch(dispatchersProvider.io) {
            updateState { it.copy(isStatsLoading = true, selectedTimeWindow = window) }

            val history = cachedHistory.ifEmpty {
                withTimeoutOrNull(5000L) {
                    historyRepository.getHistory().first()
                } ?: emptyList()
            }

            when (
                val result = getStatisticsDataUseCase.loadReportForHistory(
                    history = history,
                    timeWindow = window,
                    forceRefresh = forceRefresh
                )
            ) {
                is AppResult.Success -> {
                    updateState {
                        it.copy(
                            statistics = result.value.report,
                            isStatsLoading = false,
                            statisticsSource = mapStatisticsSource(result.value.source),
                            isShowingStaleData = result.value.isStale
                        )
                    }
                    if (result.value.isStale) {
                        sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.sync_failed_message))
                    }
                }

                is AppResult.Failure -> {
                    updateState { it.copy(isStatsLoading = false) }
                    sendUiEvent(UiEvent.ShowSnackbar(messageResId = R.string.error_load_data_failed))
                }
            }
        }
        jobTracker.trackNonBlocking(statsJob!!)
    }

    fun onPatternSelected(pattern: StatisticPattern) {
        val current = currentState
        if (current.selectedPattern == pattern) return
        updateState { it.copy(selectedPattern = pattern) }
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

    private fun mapStatisticsSource(source: StatisticsDataSource): DataLoadSource {
        return when (source) {
            StatisticsDataSource.CACHE -> DataLoadSource.CACHE
            StatisticsDataSource.COMPUTED -> DataLoadSource.COMPUTED
        }
    }

    private fun LastDrawStats.toNextDrawUiModel(): NextDrawUiModel? {
        val contest = nextContest ?: return null
        return NextDrawUiModel(
            contestNumber = contest,
            date = nextDate,
            prizeEstimate = nextEstimate ?: 0.0,
            isAccumulated = accumulated
        )
    }

    private fun HistoricalDraw.toLastDrawStats(): LastDrawStats {
        return LastDrawStats(
            contest = contestNumber,
            date = date,
            numbers = numbers.toImmutableSet(),
            sum = sum,
            evens = evens,
            odds = odds,
            primes = primes,
            frame = frame,
            portrait = portrait,
            fibonacci = fibonacci,
            multiplesOf3 = multiplesOf3,
            prizes = prizes,
            winners = winners,
            nextContest = nextContest,
            nextDate = nextDate,
            nextEstimate = nextEstimate,
            accumulated = accumulated
        )
    }
}
