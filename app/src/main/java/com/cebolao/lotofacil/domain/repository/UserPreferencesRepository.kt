package com.cebolao.lotofacil.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val pinnedGames: Flow<Set<String>>

    suspend fun savePinnedGames(games: Set<String>)

}
