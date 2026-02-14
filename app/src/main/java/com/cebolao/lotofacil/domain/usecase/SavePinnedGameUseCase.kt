package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.domain.repository.GameRepository
import javax.inject.Inject

class SavePinnedGameUseCase @Inject constructor(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(numbers: Set<Int>): AppResult<LotofacilGame> {
        val gameToSave = LotofacilGame(numbers = numbers, isPinned = true)
        return gameRepository.upsertSavedGame(gameToSave)
    }
}
