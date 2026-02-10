package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.domain.repository.GameRepository
import javax.inject.Inject

class ClearUnpinnedGamesUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke() {
        repository.clearUnpinnedGames()
    }
}
