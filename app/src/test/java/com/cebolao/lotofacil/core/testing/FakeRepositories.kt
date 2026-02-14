package com.cebolao.lotofacil.core.testing

import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.domain.repository.CacheInvalidationTarget
import com.cebolao.lotofacil.domain.repository.CachePolicy
import com.cebolao.lotofacil.domain.repository.CacheStatistics
import com.cebolao.lotofacil.domain.model.ThemeMode
import com.cebolao.lotofacil.domain.repository.GameListSummary
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.StatisticsRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import com.cebolao.lotofacil.domain.repository.UserPreferencesRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeGameRepository : GameRepository {
    private val _games = MutableStateFlow<ImmutableList<LotofacilGame>>(persistentListOf())
    private val _pinnedGames = MutableStateFlow<ImmutableList<LotofacilGame>>(persistentListOf())
    private val _gamesCount = MutableStateFlow(0)

    override val games: StateFlow<ImmutableList<LotofacilGame>> = _games.asStateFlow()
    override val pinnedGames: StateFlow<ImmutableList<LotofacilGame>> = _pinnedGames.asStateFlow()
    override val gamesCount: Flow<Int> = _gamesCount.asStateFlow()

    override suspend fun addGeneratedGames(newGames: List<LotofacilGame>): AppResult<Unit> {
        _games.value = (_games.value + newGames).distinctBy { it.id }.toImmutableList()
        refreshPinnedGames()
        return AppResult.Success(Unit)
    }

    override suspend fun getGamesPage(limit: Int, offset: Int): AppResult<List<LotofacilGame>> {
        if (offset < 0 || limit <= 0) return AppResult.Success(emptyList())
        val sorted = _games.value.sortedWith(
            compareBy<LotofacilGame> { !it.isPinned }.thenByDescending { it.creationTimestamp }
        )
        return AppResult.Success(sorted.drop(offset).take(limit))
    }

    override suspend fun getGameListSummary(): AppResult<GameListSummary> {
        val total = _games.value.size
        val pinned = _games.value.count { it.isPinned }
        return AppResult.Success(GameListSummary(totalGames = total, pinnedGames = pinned))
    }

    override suspend fun clearUnpinnedGames(): AppResult<Unit> {
        _games.value = _games.value.filter { it.isPinned }.toImmutableList()
        refreshPinnedGames()
        return AppResult.Success(Unit)
    }

    override suspend fun togglePinState(gameToToggle: LotofacilGame): AppResult<Unit> {
        _games.value = _games.value.map { game ->
            if (game.id == gameToToggle.id) game.copy(isPinned = !game.isPinned) else game
        }.toImmutableList()
        refreshPinnedGames()
        return AppResult.Success(Unit)
    }

    override suspend fun deleteGame(gameToDelete: LotofacilGame): AppResult<Unit> {
        _games.value = _games.value.filterNot { it.id == gameToDelete.id }.toImmutableList()
        refreshPinnedGames()
        return AppResult.Success(Unit)
    }

    override suspend fun recordGameUsage(gameId: String): AppResult<Unit> {
        _games.value = _games.value.map { game ->
            if (game.id == gameId) {
                game.copy(usageCount = game.usageCount + 1, lastPlayed = System.currentTimeMillis())
            } else {
                game
            }
        }.toImmutableList()
        return AppResult.Success(Unit)
    }

    private fun refreshPinnedGames() {
        _pinnedGames.value = _games.value.filter { it.isPinned }.toImmutableList()
        _gamesCount.value = _games.value.size
    }
}

class FakeHistoryRepository(initialHistory: List<HistoricalDraw> = emptyList()) : HistoryRepository {
    private val historyState = MutableStateFlow(initialHistory)

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    override val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val _isInitialized = MutableStateFlow(true)
    override val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    override fun getHistory(): Flow<List<HistoricalDraw>> = historyState.asStateFlow()

    override suspend fun getLastDraw(): HistoricalDraw? = historyState.value.maxByOrNull { it.contestNumber }

    override suspend fun syncHistory(): AppResult<Unit> {
        _syncStatus.value = SyncStatus.Success
        return AppResult.Success(Unit)
    }

    fun setHistory(history: List<HistoricalDraw>) {
        historyState.value = history
    }
}

class FakeUserPreferencesRepository(initialPinned: Set<String> = emptySet()) : UserPreferencesRepository {
    private val pinnedState = MutableStateFlow(initialPinned)
    private val themeModeState = MutableStateFlow(ThemeMode.SYSTEM)

    override val pinnedGames: Flow<Set<String>> = pinnedState.asStateFlow()
    override val themeMode: Flow<ThemeMode> = themeModeState.asStateFlow()

    override suspend fun savePinnedGames(games: Set<String>) {
        pinnedState.value = games
    }

    override suspend fun saveThemeMode(mode: ThemeMode) {
        themeModeState.value = mode
    }

    private val timestampState = MutableStateFlow(0L)
    override val lastHistorySyncTimestamp: Flow<Long> = timestampState.asStateFlow()

    override suspend fun saveLastHistorySyncTimestamp(timestamp: Long) {
        timestampState.value = timestamp
    }
}

class FakeStatisticsRepository : StatisticsRepository {
    private val cache = mutableMapOf<Int, StatisticsReport>()
    private val cacheStats = MutableStateFlow(CacheStatistics())

    override suspend fun getCachedStatistics(
        windowSize: Int,
        policy: CachePolicy
    ): StatisticsReport? = cache[windowSize]

    override suspend fun cacheStatistics(windowSize: Int, statistics: StatisticsReport, ttlMs: Long) {
        cache[windowSize] = statistics
        cacheStats.value = cacheStats.value.copy(
            totalEntries = cache.size,
            validEntries = cache.size,
            lastUpdated = System.currentTimeMillis()
        )
    }

    override suspend fun clearCache(target: CacheInvalidationTarget) {
        when (target) {
            CacheInvalidationTarget.All -> cache.clear()
            is CacheInvalidationTarget.Window -> cache.remove(target.windowSize)
        }
        cacheStats.value = CacheStatistics(lastUpdated = System.currentTimeMillis())
    }

    override suspend fun clearExpiredCache() {
        cacheStats.value = CacheStatistics(lastUpdated = System.currentTimeMillis())
    }

    override fun getCacheStatistics(): Flow<CacheStatistics> = cacheStats.asStateFlow()

    override suspend fun hasValidCache(windowSize: Int): Boolean = cache.containsKey(windowSize)
}
