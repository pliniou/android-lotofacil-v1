package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.domain.repository.GameRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSavedGamesUseCase @Inject constructor(
    private val repository: GameRepository
) {
    operator fun invoke(): Flow<ImmutableList<LotofacilGame>> {
        return repository.games.map { games ->
            games.sortedWith(
                compareBy<LotofacilGame> { !it.isPinned }
                    .thenByDescending { it.creationTimestamp }
            ).toImmutableList()
        }
    }
}
