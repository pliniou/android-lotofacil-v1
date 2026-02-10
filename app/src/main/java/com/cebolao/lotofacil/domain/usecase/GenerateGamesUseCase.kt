package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.error.ErrorMapper
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.result.toSuccess
import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.service.GameGenerator
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Generates a list of random Lotofácil games based on the provided filters.
 * Errors are propagated as [AppResult.Failure] with a mapped [AppError].
 */
class GenerateGamesUseCase @Inject constructor(
    private val gameGenerator: GameGenerator,
    private val historyRepository: HistoryRepository,
    private val dispatchersProvider: DispatchersProvider
) {
    suspend operator fun invoke(
        quantity: Int,
        activeFilters: List<FilterState>
    ): AppResult<List<LotofacilGame>> = withContext(dispatchersProvider.io) {
        try {
            val lastDrawNumbers = if (activeFilters.any { it.type == FilterType.REPETIDAS_CONCURSO_ANTERIOR && it.isEnabled }) {
                historyRepository.getLastDraw()?.numbers
            } else null
            if (activeFilters.any { it.type == FilterType.REPETIDAS_CONCURSO_ANTERIOR && it.isEnabled } && lastDrawNumbers == null) {
                return@withContext AppResult.Failure(com.cebolao.lotofacil.core.error.EmptyHistoryError)
            }
            val games = gameGenerator.generateGames(
                activeFilters = activeFilters,
                count = quantity,
                lastDraw = lastDrawNumbers
            )
            games.toSuccess()
        } catch (e: Exception) {
            val error = ErrorMapper.toAppError(e)
            AppResult.Failure(error)
        }
    }
}
