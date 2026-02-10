package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.DomainError
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.service.StatisticsEngine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

data class PatternAnalysis(
    val size: Int,
    val patterns: List<Pair<Set<Int>, Int>>,
    val totalDraws: Int
)

class GetPatternAnalysisUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val analyzer: StatisticsEngine
) {
    suspend operator fun invoke(size: Int): AppResult<PatternAnalysis> {
        return try {
            val history = historyRepository.getHistory().first()
            if (history.isEmpty()) {
                AppResult.Failure(DomainError.HistoryUnavailable)
            } else {
                val patterns = analyzer.getCommonPatterns(history, size)
                AppResult.Success(
                    PatternAnalysis(
                        size = size,
                        patterns = patterns,
                        totalDraws = history.size
                    )
                )
            }
        } catch (e: Exception) {
            AppResult.Failure(DomainError.Unknown(e))
        }
    }
}
