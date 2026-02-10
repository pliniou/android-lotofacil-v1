package com.cebolao.lotofacil.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: AppLogger
) : UserPreferencesRepository {

    companion object {
        private const val TAG = "UserPreferencesRepo"
        private val PINNED_GAMES_KEY = stringSetPreferencesKey("pinned_games")

    }

    override val pinnedGames: Flow<Set<String>> = context.dataStore.data
        .catch { exception ->
            handleError(exception, "reading pinned games")
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[PINNED_GAMES_KEY] ?: emptySet()
        }

    override suspend fun savePinnedGames(games: Set<String>) {
        withContext(dispatchersProvider.io) {
            try {
                context.dataStore.edit { preferences ->
                    preferences[PINNED_GAMES_KEY] = games
                }
                logger.d(TAG, "Saved ${games.size} pinned games")
            } catch (e: IOException) {
                handleError(e, "saving pinned games")
            }
        }
    }




    private fun handleError(exception: Throwable, contextMessage: String) {
        logger.e(TAG, "Error $contextMessage", exception)
    }
}
