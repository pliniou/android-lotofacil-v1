package com.cebolao.lotofacil.domain.service

import com.cebolao.lotofacil.core.constants.AppConstants
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.domain.model.LotofacilGame
import kotlinx.coroutines.withContext
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton

class GameGenerationException(message: String) : Exception(message)

@Singleton
class GameGenerator @Inject constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val random: Random = Random()
) {
    private val allNumbers = AppConstants.LOTOFACIL_NUMBER_RANGE.toList()

    suspend fun generateGames(
        activeFilters: List<FilterState>,
        count: Int,
        lastDraw: Set<Int>? = null,
        maxAttempts: Int = AppConstants.MAX_GAME_GENERATION_ATTEMPTS
    ): List<LotofacilGame> = withContext(dispatchersProvider.io) {
        val uniqueGames = mutableSetOf<Set<Int>>()
        val resultList = mutableListOf<LotofacilGame>()
        var attempts = 0

        val enabledFilters = activeFilters.filter { it.isEnabled }

        while (resultList.size < count && attempts < maxAttempts) {
            val numbers = generateRandomNumbers()
            if (uniqueGames.add(numbers)) {
                val game = LotofacilGame(numbers)
                if (isGameValid(game, enabledFilters, lastDraw)) {
                    resultList.add(game)
                }
            }
            attempts++
        }

        if (resultList.size < count) {
            throw GameGenerationException("Unable to generate the desired number of games with current filters. Try less restrictive settings.")
        }

        return@withContext resultList
    }

    private fun generateRandomNumbers(): Set<Int> {
        val pool = allNumbers.toMutableList()
        val selection = mutableSetOf<Int>()
        repeat(AppConstants.GAME_SIZE) {
            val index = random.nextInt(pool.size)
            selection.add(pool.removeAt(index))
        }
        return selection
    }

    private fun isGameValid(
        game: LotofacilGame,
        enabledFilters: List<FilterState>,
        lastDraw: Set<Int>?
    ): Boolean {
        return enabledFilters.all { filter ->
            val value = when (filter.type) {
                FilterType.SOMA_DEZENAS -> game.sum
                FilterType.PARES -> game.evens
                FilterType.PRIMOS -> game.primes
                FilterType.MOLDURA -> game.frame
                FilterType.RETRATO -> game.portrait
                FilterType.FIBONACCI -> game.fibonacci
                FilterType.MULTIPLOS_DE_3 -> game.multiplesOf3
                FilterType.REPETIDAS_CONCURSO_ANTERIOR -> game.repeatedFrom(lastDraw)
            }
            filter.containsValue(value)
        }
    }
}
