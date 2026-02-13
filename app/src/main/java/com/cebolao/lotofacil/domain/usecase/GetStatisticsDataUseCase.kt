package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.error.EmptyHistoryError
import com.cebolao.lotofacil.core.error.ErrorMapper
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.FrequencyAnalysis
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.PatternAnalysis
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.domain.model.TrendAnalysis
import com.cebolao.lotofacil.domain.model.TrendType
import com.cebolao.lotofacil.domain.repository.CacheInvalidationTarget
import com.cebolao.lotofacil.domain.repository.CachePolicy
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.StatisticsRepository
import com.cebolao.lotofacil.domain.service.StatisticsEngine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

enum class StatisticsDataSource {
    CACHE,
    COMPUTED
}

data class StatisticsReportSnapshot(
    val report: StatisticsReport,
    val source: StatisticsDataSource,
    val isStale: Boolean,
    val totalHistorySize: Int,
    val draws: List<HistoricalDraw>
)

data class StatisticsScreenData(
    val reportSnapshot: StatisticsReportSnapshot,
    val frequencyAnalysis: FrequencyAnalysis,
    val patternAnalysis: PatternAnalysis,
    val trendAnalysis: TrendAnalysis
)

class GetStatisticsDataUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val statisticsRepository: StatisticsRepository,
    private val statisticsEngine: StatisticsEngine
) {

    suspend fun loadReport(
        timeWindow: Int,
        forceRefresh: Boolean = false
    ): AppResult<StatisticsReportSnapshot> {
        val history = historyRepository.getHistory().first()
        return loadReportForHistory(history, timeWindow, forceRefresh)
    }

    suspend fun loadReportForHistory(
        history: List<HistoricalDraw>,
        timeWindow: Int,
        forceRefresh: Boolean = false
    ): AppResult<StatisticsReportSnapshot> {
        if (history.isEmpty()) {
            return AppResult.Failure(EmptyHistoryError)
        }

        val draws = selectDraws(history = history, timeWindow = timeWindow)

        return try {
            val cachedReport = if (forceRefresh) {
                null
            } else {
                statisticsRepository.getCachedStatistics(
                    windowSize = timeWindow,
                    policy = CachePolicy.OnlyValid
                )
            }

            if (cachedReport != null && cachedReport.totalDrawsAnalyzed == draws.size) {
                AppResult.Success(
                    StatisticsReportSnapshot(
                        report = cachedReport,
                        source = StatisticsDataSource.CACHE,
                        isStale = false,
                        totalHistorySize = history.size,
                        draws = draws
                    )
                )
            } else {
                val computedReport = statisticsEngine.analyze(draws)
                statisticsRepository.cacheStatistics(windowSize = timeWindow, statistics = computedReport)
                AppResult.Success(
                    StatisticsReportSnapshot(
                        report = computedReport,
                        source = StatisticsDataSource.COMPUTED,
                        isStale = false,
                        totalHistorySize = history.size,
                        draws = draws
                    )
                )
            }
        } catch (throwable: Throwable) {
            val staleReport = statisticsRepository.getCachedStatistics(
                windowSize = timeWindow,
                policy = CachePolicy.AllowStale
            )
            if (staleReport != null) {
                AppResult.Success(
                    StatisticsReportSnapshot(
                        report = staleReport,
                        source = StatisticsDataSource.CACHE,
                        isStale = true,
                        totalHistorySize = history.size,
                        draws = draws
                    )
                )
            } else {
                AppResult.Failure(ErrorMapper.toAppError(throwable))
            }
        } finally {
            statisticsRepository.clearExpiredCache()
        }
    }

    suspend fun loadScreenData(
        timeWindow: Int,
        patternSize: Int,
        trendType: TrendType,
        trendWindow: Int,
        forceRefresh: Boolean = false
    ): AppResult<StatisticsScreenData> {
        return when (
            val reportResult = loadReport(
                timeWindow = timeWindow,
                forceRefresh = forceRefresh
            )
        ) {
            is AppResult.Success -> {
                val snapshot = reportResult.value
                val draws = snapshot.draws
                val frequencies = statisticsEngine.getNumberFrequencies(draws)
                val topNumbers = statisticsEngine.getTopNumbers(frequencies)
                val overdueNumbers = statisticsEngine.getOverdueNumbers(draws)

                val frequencyAnalysis = FrequencyAnalysis(
                    frequencies = frequencies,
                    topNumbers = topNumbers,
                    overdueNumbers = overdueNumbers,
                    totalDraws = draws.size
                )

                val patternAnalysis = PatternAnalysis(
                    size = patternSize,
                    patterns = statisticsEngine.getCommonPatterns(draws, patternSize),
                    totalDraws = draws.size
                )

                val timeline = buildTimeline(
                    draws = draws,
                    trendType = trendType,
                    trendWindow = trendWindow
                )
                val trendAverage = if (timeline.isNotEmpty()) {
                    timeline.map { it.second }.average().toFloat()
                } else {
                    0f
                }

                val trendAnalysis = TrendAnalysis(
                    type = trendType,
                    timeline = timeline,
                    averageValue = trendAverage
                )

                AppResult.Success(
                    StatisticsScreenData(
                        reportSnapshot = snapshot,
                        frequencyAnalysis = frequencyAnalysis,
                        patternAnalysis = patternAnalysis,
                        trendAnalysis = trendAnalysis
                    )
                )
            }

            is AppResult.Failure -> reportResult
        }
    }

    suspend fun loadPatternAnalysis(
        timeWindow: Int,
        patternSize: Int
    ): AppResult<PatternAnalysis> {
        return when (val reportResult = loadReport(timeWindow = timeWindow, forceRefresh = false)) {
            is AppResult.Success -> {
                val draws = reportResult.value.draws
                AppResult.Success(
                    PatternAnalysis(
                        size = patternSize,
                        patterns = statisticsEngine.getCommonPatterns(draws, patternSize),
                        totalDraws = draws.size
                    )
                )
            }

            is AppResult.Failure -> reportResult
        }
    }

    suspend fun loadTrendAnalysis(
        timeWindow: Int,
        trendType: TrendType,
        trendWindow: Int
    ): AppResult<TrendAnalysis> {
        return when (val reportResult = loadReport(timeWindow = timeWindow, forceRefresh = false)) {
            is AppResult.Success -> {
                val draws = reportResult.value.draws
                val timeline = buildTimeline(
                    draws = draws,
                    trendType = trendType,
                    trendWindow = trendWindow
                )
                val trendAverage = if (timeline.isNotEmpty()) {
                    timeline.map { it.second }.average().toFloat()
                } else {
                    0f
                }
                AppResult.Success(
                    TrendAnalysis(
                        type = trendType,
                        timeline = timeline,
                        averageValue = trendAverage
                    )
                )
            }

            is AppResult.Failure -> reportResult
        }
    }

    suspend fun clearCache(target: CacheInvalidationTarget = CacheInvalidationTarget.All) {
        statisticsRepository.clearCache(target)
    }

    private fun selectDraws(history: List<HistoricalDraw>, timeWindow: Int): List<HistoricalDraw> {
        if (timeWindow <= 0) return history
        return history.take(timeWindow)
    }

    private fun buildTimeline(
        draws: List<HistoricalDraw>,
        trendType: TrendType,
        trendWindow: Int
    ): List<Pair<Int, Float>> = when (trendType) {
        TrendType.SUM -> statisticsEngine.getAverageSumTimeline(draws, trendWindow)
        TrendType.EVENS -> statisticsEngine.getDistributionTimeline(draws, trendWindow) { it.evens }
        TrendType.PRIMES -> statisticsEngine.getDistributionTimeline(draws, trendWindow) { it.primes }
        TrendType.FRAME -> statisticsEngine.getDistributionTimeline(draws, trendWindow) { it.frame }
        TrendType.PORTRAIT -> statisticsEngine.getDistributionTimeline(draws, trendWindow) { it.portrait }
        TrendType.FIBONACCI -> statisticsEngine.getDistributionTimeline(draws, trendWindow) { it.fibonacci }
        TrendType.MULTIPLES_OF_3 -> statisticsEngine.getDistributionTimeline(draws, trendWindow) { it.multiplesOf3 }
    }
}
