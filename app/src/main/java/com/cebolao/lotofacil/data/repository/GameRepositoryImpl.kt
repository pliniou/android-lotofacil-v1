package com.cebolao.lotofacil.data.repository

import com.cebolao.lotofacil.core.error.ErrorMapper
import com.cebolao.lotofacil.core.extensions.toDomain
import com.cebolao.lotofacil.core.extensions.toEntity
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.utils.TimeProvider
import com.cebolao.lotofacil.data.datasource.database.GameDao
import com.cebolao.lotofacil.di.ApplicationScope
import com.cebolao.lotofacil.domain.repository.GameListSummary
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.domain.repository.GameRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val gameDao: GameDao,
    @ApplicationScope private val repositoryScope: CoroutineScope,
    private val timeProvider: TimeProvider
) : GameRepository {

    override val gamesCount: Flow<Int> = gameDao.observeGamesCount()

    override val games: StateFlow<ImmutableList<LotofacilGame>> = gameDao.getAllGames()
        .map { entities ->
            entities.mapNotNull { it.toDomain() }.toImmutableList()
        }
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.WhileSubscribed(5000), // More efficient than Eagerly
            initialValue = kotlinx.collections.immutable.persistentListOf()
        )

    override val pinnedGames: StateFlow<ImmutableList<LotofacilGame>> = gameDao.getPinnedGames()
        .map { entities ->
            entities.mapNotNull { it.toDomain() }.toImmutableList()
        }
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = kotlinx.collections.immutable.persistentListOf()
        )

    override suspend fun getGamesPage(limit: Int, offset: Int): AppResult<List<LotofacilGame>> = try {
        val page = gameDao.getGamesPage(limit = limit, offset = offset)
            .mapNotNull { it.toDomain() }
        AppResult.Success(page)
    } catch (e: Exception) {
        AppResult.Failure(ErrorMapper.toAppError(e))
    }

    override suspend fun getGameListSummary(): AppResult<GameListSummary> = try {
        val total = gameDao.getGamesCount()
        val pinned = gameDao.getPinnedGamesCount()
        AppResult.Success(
            GameListSummary(
                totalGames = total,
                pinnedGames = pinned
            )
        )
    } catch (e: Exception) {
        AppResult.Failure(ErrorMapper.toAppError(e))
    }

    override suspend fun addGeneratedGames(newGames: List<LotofacilGame>): AppResult<Unit> = try {
        val entities = newGames.map { it.toEntity() }
        gameDao.insertGames(entities)
        AppResult.Success(Unit)
    } catch (e: Exception) {
        AppResult.Failure(ErrorMapper.toAppError(e))
    }

    override suspend fun clearUnpinnedGames(): AppResult<Unit> = try {
        gameDao.clearUnpinnedGames()
        AppResult.Success(Unit)
    } catch (e: Exception) {
        AppResult.Failure(ErrorMapper.toAppError(e))
    }

    override suspend fun togglePinState(gameToToggle: LotofacilGame): AppResult<Unit> = try {
        val updatedGame = gameToToggle.copy(isPinned = !gameToToggle.isPinned)
        gameDao.updateGame(updatedGame.toEntity())
        AppResult.Success(Unit)
    } catch (e: Exception) {
        AppResult.Failure(ErrorMapper.toAppError(e))
    }

    override suspend fun deleteGame(gameToDelete: LotofacilGame): AppResult<Unit> = try {
        gameDao.deleteGame(gameToDelete.toEntity())
        AppResult.Success(Unit)
    } catch (e: Exception) {
        AppResult.Failure(ErrorMapper.toAppError(e))
    }

    override suspend fun recordGameUsage(gameId: String): AppResult<Unit> = try {
        val existingGame = gameDao.getGameById(gameId)
        if (existingGame != null) {
            val updatedGame = existingGame.copy(
                usageCount = existingGame.usageCount + 1,
                lastPlayed = timeProvider.currentTimeMillis()
            )
            gameDao.updateGame(updatedGame)
        }
        AppResult.Success(Unit)
    } catch (e: Exception) {
        AppResult.Failure(ErrorMapper.toAppError(e))
    }
}
