package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.domain.model.ChartDataPoint
import com.cebolao.lotofacil.domain.model.EvolutionMetrics
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import java.util.Locale

/**
 * UseCase to calculate advanced statistics and evolution metrics.
 * Provides data for charting and trend analysis.
 */
class GetEvolutionMetricsUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val dispatchersProvider: DispatchersProvider
) {

    /**
     * Calculate evolution metrics from history.
     *
     * @return Flow of evolution metrics
     */
    operator fun invoke(): Flow<EvolutionMetrics> = flow {
        val history = historyRepository.getHistory().first()

        if (history.isEmpty()) {
            emit(EvolutionMetrics())
            return@flow
        }

        // Calculate number frequency
        val frequencyMap = mutableMapOf<Int, Int>()
        history.forEach { draw ->
            draw.numbers.forEach { number ->
                frequencyMap[number] = (frequencyMap[number] ?: 0) + 1
            }
        }

        val numberFrequency = frequencyMap
            .toList()
            .sortedBy { it.first }
            .map { (number, count) ->
                ChartDataPoint(
                    label = number.toString(),
                    value = count.toFloat()
                )
            }

        // Calculate timeline metrics (recent contests)
        val timelineMetrics = history
            .takeLast(20)
            .map { draw ->
                ChartDataPoint(
                    label = "Concurso ${draw.contestNumber}",
                    value = draw.numbers.size.toFloat()
                )
            }

        // Win distribution (15 hits, 14 hits, etc.)
        val winDistribution = mutableMapOf<Int, Int>()
        repeat(5) { i ->
            val hits = 15 - i
            winDistribution[hits] = 0
        }

        val avgNumber = frequencyMap.values.average()

        // Build trends text
        val mostFrequent = frequencyMap.maxByOrNull { it.value }
        val leastFrequent = frequencyMap.minByOrNull { it.value }
        val trendsAnalysis = buildString {
            appendLine("📊 Análise de Padrões:")
            append("- Número mais frequente: ${mostFrequent?.key} (${mostFrequent?.value}x)")
            append("- Número menos frequente: ${leastFrequent?.key} (${leastFrequent?.value}x)")
            append("- Média de frequência: ${String.format(Locale.getDefault(), "%.1f", avgNumber)}")
        }

        emit(
            EvolutionMetrics(
                numberFrequency = numberFrequency,
                timelineMetrics = timelineMetrics,
                winDistribution = winDistribution,
                avgNumberPerContest = avgNumber,
                trendsAnalysis = trendsAnalysis
            )
        )
    }.flowOn(dispatchersProvider.default)
}
