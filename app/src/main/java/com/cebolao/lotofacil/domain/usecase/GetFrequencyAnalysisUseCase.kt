package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.DomainError
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.service.StatisticsEngine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

data class FrequencyAnalysis(
    val frequencies: Map<Int, Int>,
    val topNumbers: List<Int>,
    val overdueNumbers: List<Pair<Int, Int>>,
    val totalDraws: Int
)

class GetFrequencyAnalysisUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val analyzer: StatisticsEngine
) {
    suspend operator fun invoke(): AppResult<FrequencyAnalysis> {
        return try {
            val history = historyRepository.getHistory().first()
            if (history.isEmpty()) {
                AppResult.Failure(DomainError.HistoryUnavailable)
            } else {
                val frequencies = analyzer.getNumberFrequencies(history)
                val topNumbers = analyzer.getTopNumbers(frequencies)
                val overdueNumbers = analyzer.getOverdueNumbers(history)
                AppResult.Success(
                    FrequencyAnalysis(
                        frequencies = frequencies,
                        topNumbers = topNumbers,
                        overdueNumbers = overdueNumbers,
                        totalDraws = history.size
                    )
                )
            }
        } catch (e: Exception) {
            AppResult.Failure(DomainError.Unknown(e))
        }
    }
}
