package com.cebolao.lotofacil.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val pinnedGames: Flow<Set<String>>
    val lastHistorySyncTimestamp: Flow<Long>

    suspend fun savePinnedGames(games: Set<String>)
    suspend fun saveLastHistorySyncTimestamp(timestamp: Long)

}
