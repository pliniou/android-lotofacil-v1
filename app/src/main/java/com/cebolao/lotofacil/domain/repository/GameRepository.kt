package com.cebolao.lotofacil.domain.repository

import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.LotofacilGame
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

data class GameListSummary(
    val totalGames: Int,
    val pinnedGames: Int
)

interface GameRepository {
    val games: StateFlow<ImmutableList<LotofacilGame>>
    val pinnedGames: StateFlow<ImmutableList<LotofacilGame>>
    val gamesCount: Flow<Int>

    suspend fun addGeneratedGames(newGames: List<LotofacilGame>): AppResult<Unit>
    suspend fun getGamesPage(limit: Int, offset: Int): AppResult<List<LotofacilGame>>
    suspend fun getGameListSummary(): AppResult<GameListSummary>
    suspend fun clearUnpinnedGames(): AppResult<Unit>
    suspend fun togglePinState(gameToToggle: LotofacilGame): AppResult<Unit>
    suspend fun deleteGame(gameToDelete: LotofacilGame): AppResult<Unit>
    suspend fun recordGameUsage(gameId: String): AppResult<Unit>
    suspend fun upsertSavedGame(game: LotofacilGame): AppResult<LotofacilGame>
}
