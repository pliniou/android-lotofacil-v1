package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.error.EmptyHistoryError
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.result.toSuccess
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.LastDrawStats
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.service.StatisticsEngine
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class HomeScreenData(
    val lastDrawStats: LastDrawStats?,
    val initialStats: StatisticsReport,
    val history: List<HistoricalDraw>
)

class GetHomeScreenDataUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val statisticsEngine: StatisticsEngine,
    private val dispatchersProvider: DispatchersProvider
) {
    operator fun invoke(): Flow<AppResult<HomeScreenData>> {
        return historyRepository.getHistory()
            .map { history ->
                if (history.isEmpty()) {
                    AppResult.Failure(EmptyHistoryError)
                } else {
                    val lastDraw = history.first()
                    val lastDrawStats = calculateLastDrawStats(lastDraw)
                    val initialStats = statisticsEngine.analyze(history)

                    HomeScreenData(
                        lastDrawStats = lastDrawStats,
                        initialStats = initialStats,
                        history = history
                    ).toSuccess()
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

    private fun calculateLastDrawStats(lastDraw: HistoricalDraw): LastDrawStats {
        return LastDrawStats(
            contest = lastDraw.contestNumber,
            date = lastDraw.date,
            numbers = lastDraw.numbers.toImmutableSet(),
            sum = lastDraw.sum,
            evens = lastDraw.evens,
            odds = lastDraw.odds,
            primes = lastDraw.primes,
            frame = lastDraw.frame,
            portrait = lastDraw.portrait,
            fibonacci = lastDraw.fibonacci,
            multiplesOf3 = lastDraw.multiplesOf3,
            prizes = lastDraw.prizes,
            winners = lastDraw.winners,
            nextContest = lastDraw.nextContest,
            nextDate = lastDraw.nextDate,
            nextEstimate = lastDraw.nextEstimate,
            accumulated = lastDraw.accumulated
        )
    }
}
