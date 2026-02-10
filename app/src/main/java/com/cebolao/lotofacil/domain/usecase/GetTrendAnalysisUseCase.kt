package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.DomainError
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.service.StatisticsEngine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

enum class TrendType {
    SUM, EVENS, PRIMES, FRAME, PORTRAIT, FIBONACCI, MULTIPLES_OF_3
}

data class TrendAnalysis(
    val type: TrendType,
    val timeline: List<Pair<Int, Float>>,
    val averageValue: Float
)

class GetTrendAnalysisUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val analyzer: StatisticsEngine
) {
    suspend operator fun invoke(type: TrendType, windowSize: Int = 50): AppResult<TrendAnalysis> {
        return try {
            val history = historyRepository.getHistory().first()
            if (history.isEmpty()) {
                AppResult.Failure(DomainError.HistoryUnavailable)
            } else {
                val timeline = when (type) {
                    TrendType.SUM -> analyzer.getAverageSumTimeline(history, windowSize)
                    TrendType.EVENS -> analyzer.getDistributionTimeline(history, windowSize) { it.evens }
                    TrendType.PRIMES -> analyzer.getDistributionTimeline(history, windowSize) { it.primes }
                    TrendType.FRAME -> analyzer.getDistributionTimeline(history, windowSize) { it.frame }
                    TrendType.PORTRAIT -> analyzer.getDistributionTimeline(history, windowSize) { it.portrait }
                    TrendType.FIBONACCI -> analyzer.getDistributionTimeline(history, windowSize) { it.fibonacci }
                    TrendType.MULTIPLES_OF_3 -> analyzer.getDistributionTimeline(history, windowSize) { it.multiplesOf3 }
                }
                
                val averageValue = timeline.map { it.second }.average().toFloat()
                
                AppResult.Success(
                    TrendAnalysis(
                        type = type,
                        timeline = timeline,
                        averageValue = averageValue
                    )
                )
            }
        } catch (e: Exception) {
            AppResult.Failure(DomainError.Unknown(e))
        }
    }
}
