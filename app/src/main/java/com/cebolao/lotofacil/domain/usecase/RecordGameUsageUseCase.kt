package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.repository.GameRepository
import javax.inject.Inject

class RecordGameUsageUseCase @Inject constructor(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(gameId: String): AppResult<Unit> {
        return gameRepository.recordGameUsage(gameId)
    }
}
