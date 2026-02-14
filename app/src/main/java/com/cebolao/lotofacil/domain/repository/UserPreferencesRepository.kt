package com.cebolao.lotofacil.domain.repository

import com.cebolao.lotofacil.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val pinnedGames: Flow<Set<String>>
    val lastHistorySyncTimestamp: Flow<Long>
    val themeMode: Flow<ThemeMode>

    suspend fun savePinnedGames(games: Set<String>)
    suspend fun saveLastHistorySyncTimestamp(timestamp: Long)
    suspend fun saveThemeMode(mode: ThemeMode)
}
