package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.error.EmptyHistoryError
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.result.toSuccess
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.LastDrawStats
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class HomeScreenData(
    val lastDrawStats: LastDrawStats?,
    val initialStats: StatisticsReport,
    val history: List<HistoricalDraw>,
    val statisticsSource: StatisticsDataSource,
    val isShowingStaleStatistics: Boolean
)

class GetHomeScreenDataUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val getStatisticsDataUseCase: GetStatisticsDataUseCase,
    private val dispatchersProvider: DispatchersProvider
) {
    operator fun invoke(): Flow<AppResult<HomeScreenData>> {
        return historyRepository.getHistory()
            .map { history ->
                if (history.isEmpty()) {
                    AppResult.Failure(EmptyHistoryError)
                } else {
                    val lastDraw = history.first()
                    val lastDrawStats = lastDraw.toLastDrawStats()
                    when (
                        val reportResult = getStatisticsDataUseCase.loadReportForHistory(
                            history = history,
                            timeWindow = 0,
                            forceRefresh = false
                        )
                    ) {
                        is AppResult.Success -> {
                            HomeScreenData(
                                lastDrawStats = lastDrawStats,
                                initialStats = reportResult.value.report,
                                history = history,
                                statisticsSource = reportResult.value.source,
                                isShowingStaleStatistics = reportResult.value.isStale
                            ).toSuccess()
                        }

                        is AppResult.Failure -> reportResult
                    }
                }
            }
            .distinctUntilChanged { old, new ->
                // Optimize: Only re-emit/re-calculate if history size or latest contest changed
                if (old is AppResult.Success && new is AppResult.Success) {
                    val oldData = old.value
                    val newData = new.value
                    oldData.history.size == newData.history.size &&
                            oldData.history.firstOrNull()?.contestNumber == newData.history.firstOrNull()?.contestNumber
                } else {
                    old == new
                }
            }
            .flowOn(dispatchersProvider.io)
    }
}

/**
 * Extension to convert HistoricalDraw to LastDrawStats to reduce boilerplate.
 */
fun HistoricalDraw.toLastDrawStats(): LastDrawStats {
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
