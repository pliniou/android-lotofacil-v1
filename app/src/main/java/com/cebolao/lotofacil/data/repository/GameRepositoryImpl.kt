package com.cebolao.lotofacil.data.repository

import com.cebolao.lotofacil.core.error.ErrorMapper
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.utils.TimeProvider
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.di.ApplicationScope
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.repository.UserPreferencesRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    @ApplicationScope private val repositoryScope: CoroutineScope,
    private val timeProvider: TimeProvider
) : GameRepository {

    private val gamesMutex = Mutex()
    private val _games = MutableStateFlow<ImmutableList<LotofacilGame>>(persistentListOf())
    override val games: StateFlow<ImmutableList<LotofacilGame>> = _games.asStateFlow()

    override val pinnedGames: StateFlow<ImmutableList<LotofacilGame>> = games
        .map { gamesList -> gamesList.filter { it.isPinned }.toImmutableList() }
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.Eagerly,
            initialValue = persistentListOf()
        )

    init {
        repositoryScope.launch {
            userPreferencesRepository.pinnedGames.collectLatest { pinnedGameStrings ->
                val persistedPinnedGames = pinnedGameStrings
                    .mapNotNull { LotofacilGame.fromCompactString(it) }
                    .associateBy { it.id }

                _games.update { currentGames ->
                    val currentById = currentGames.associateBy { it.id }
                    val updatedCurrentGames = currentGames.map { game ->
                        game.copy(isPinned = persistedPinnedGames.containsKey(game.id))
                    }
                    val missingPersistedGames = persistedPinnedGames.values
                        .filter { persisted -> persisted.id !in currentById }

                    (updatedCurrentGames + missingPersistedGames)
                        .distinctBy { it.id }
                        .toImmutableList()
                }
            }
        }
    }

    override suspend fun addGeneratedGames(newGames: List<LotofacilGame>): AppResult<Unit> = gamesMutex.withLock {
        return try {
            _games.update { currentGames ->
                val currentPinned = currentGames.filter { it.isPinned }
                (currentPinned + newGames)
                    .distinctBy { it.numbers }
                    .toImmutableList()
            }
            AppResult.Success(Unit)
        } catch (e: Exception) {
            val error = ErrorMapper.toAppError(e)
            AppResult.Failure(error)
        }
    }

    override suspend fun clearUnpinnedGames(): AppResult<Unit> = try {
        _games.update { currentGames ->
            currentGames.filter { it.isPinned }.toImmutableList()
        }
        AppResult.Success(Unit)
    } catch (e: Exception) {
        val error = ErrorMapper.toAppError(e)
        AppResult.Failure(error)
    }

    override suspend fun togglePinState(gameToToggle: LotofacilGame): AppResult<Unit> = gamesMutex.withLock {
        return try {
            val updatedGame = gameToToggle.copy(isPinned = !gameToToggle.isPinned)
            _games.update { currentGames ->
                currentGames
                    .map { if (it.id == updatedGame.id) updatedGame else it }
                    .toImmutableList()
            }
            persistPinnedGames()
            AppResult.Success(Unit)
        } catch (e: Exception) {
            val error = ErrorMapper.toAppError(e)
            AppResult.Failure(error)
        }
    }

    override suspend fun deleteGame(gameToDelete: LotofacilGame): AppResult<Unit> = gamesMutex.withLock {
        return try {
            _games.update { currentGames ->
                currentGames.filterNot { it.id == gameToDelete.id }.toImmutableList()
            }
            if (gameToDelete.isPinned) {
                persistPinnedGames()
            }
            AppResult.Success(Unit)
        } catch (e: Exception) {
            val error = ErrorMapper.toAppError(e)
            AppResult.Failure(error)
        }
    }

    override suspend fun recordGameUsage(gameId: String): AppResult<Unit> = gamesMutex.withLock {
        return try {
            _games.update { currentGames ->
                currentGames.map { game ->
                    if (game.id == gameId) {
                        game.copy(
                            usageCount = game.usageCount + 1,
                            lastPlayed = timeProvider.currentTimeMillis()
                        )
                    } else game
                }.toImmutableList()
            }
            val game = _games.value.find { it.id == gameId }
            if (game?.isPinned == true) {
                persistPinnedGames()
            }
            AppResult.Success(Unit)
        } catch (e: Exception) {
            val error = ErrorMapper.toAppError(e)
            AppResult.Failure(error)
        }
    }

    private suspend fun persistPinnedGames() {
        val pinned = _games.value.filter { it.isPinned }
        userPreferencesRepository.savePinnedGames(pinned.map { it.toCompactString() }.toSet())
    }
}
