package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.error.AppError
import com.cebolao.lotofacil.core.error.EmptyHistoryError
import com.cebolao.lotofacil.core.error.UnknownError
import com.cebolao.lotofacil.domain.model.CheckResult
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.service.StatisticsEngine
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

import kotlinx.coroutines.flow.first

class CheckGameUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val statisticsEngine: StatisticsEngine,
    private val dispatchersProvider: DispatchersProvider
) {
    private val recentContestsCount = 15

    operator fun invoke(gameNumbers: Set<Int>): Flow<GameCheckState> = flow {
        emit(GameCheckState.InProgress(GameCheckPhase.HISTORICAL, 0.1f))
        val history = historyRepository.getHistory().first()

        if (history.isEmpty()) {
            emit(GameCheckState.Failure(EmptyHistoryError))
            return@flow
        }

        emit(GameCheckState.InProgress(GameCheckPhase.CALCULATION, 0.5f))
        val result = calculateResult(gameNumbers, history)

        emit(GameCheckState.InProgress(GameCheckPhase.STATISTICS, 0.8f))
        val gameForAnalysis = LotofacilGame(numbers = gameNumbers)
        val simpleStats = statisticsEngine.analyzeGame(gameForAnalysis)

        emit(GameCheckState.Success(result = result, stats = simpleStats.toImmutableList()))
    }.catch { throwable ->
        emit(GameCheckState.Failure(mapErrorToAppError(throwable)))
    }.flowOn(dispatchersProvider.io)

    private fun mapErrorToAppError(throwable: Throwable): AppError = when (throwable) {
        is AppError -> throwable
        else -> UnknownError(throwable)
    }

    private fun calculateResult(
        gameNumbers: Set<Int>,
        history: List<HistoricalDraw>
    ): CheckResult {
        val scoreCounts = mutableMapOf<Int, Int>()
        var lastHitContest: Int? = null
        var lastHitScore: Int? = null

        val recentHits = history.take(recentContestsCount)
            .map { draw -> draw.contestNumber to draw.numbers.intersect(gameNumbers).size }
            .reversed()

        history.forEach { draw ->
            val hits = draw.numbers.intersect(gameNumbers).size
            if (hits >= 11) {
                scoreCounts[hits] = (scoreCounts[hits] ?: 0) + 1
                if (lastHitContest == null) {
                    lastHitContest = draw.contestNumber
                    lastHitScore = hits
                }
            }
        }

        return CheckResult(
            scoreCounts = scoreCounts.toImmutableMap(),
            lastHitContest = lastHitContest,
            lastHitScore = lastHitScore,
            lastCheckedContest = history.firstOrNull()?.contestNumber ?: 0,
            recentHits = recentHits.toImmutableList()
        )
    }
}
