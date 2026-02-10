package com.cebolao.lotofacil.domain.repository

import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.error.AppError
import com.cebolao.lotofacil.domain.model.LotofacilGame
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow

interface GameRepository {
    val games: StateFlow<ImmutableList<LotofacilGame>>
    val pinnedGames: StateFlow<ImmutableList<LotofacilGame>>

    suspend fun addGeneratedGames(newGames: List<LotofacilGame>): AppResult<Unit>
    suspend fun clearUnpinnedGames(): AppResult<Unit>
    suspend fun togglePinState(gameToToggle: LotofacilGame): AppResult<Unit>
    suspend fun deleteGame(gameToDelete: LotofacilGame): AppResult<Unit>
    suspend fun recordGameUsage(gameId: String): AppResult<Unit>
}
