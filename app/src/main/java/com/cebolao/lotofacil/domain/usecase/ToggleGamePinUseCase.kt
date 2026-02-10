package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.domain.repository.GameRepository
import javax.inject.Inject

class ToggleGamePinUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(game: LotofacilGame) {
        repository.togglePinState(game)
    }
}
