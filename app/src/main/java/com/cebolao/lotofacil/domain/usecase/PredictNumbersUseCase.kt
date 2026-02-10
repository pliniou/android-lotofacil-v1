package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.service.SimpleLotofacilPredictor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * UseCase to predict numbers using ML patterns.
 * Uses historical data to suggest numbers based on statistical analysis.
 *
 * WARNING: Lottery is random. This is for entertainment only.
 */
class PredictNumbersUseCase @Inject constructor(
    private val predictor: SimpleLotofacilPredictor,
    private val historyRepository: HistoryRepository,
    private val dispatchersProvider: DispatchersProvider
) {

    /**
     * Predict likely numbers for next game.
     *
     * @param suggestionCount How many numbers to predict (default 15)
     * @return Flow of suggested numbers
     */
    operator fun invoke(suggestionCount: Int = 15): Flow<Set<Int>> = flow {
        val history = historyRepository.getHistory().first()
        val suggestions = predictor.predictNumbers(history, suggestionCount)
        emit(suggestions)
    }.flowOn(dispatchersProvider.default)
}
