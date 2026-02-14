package com.cebolao.lotofacil.data.datasource.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cebolao.lotofacil.data.datasource.database.entity.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY creationTimestamp DESC")
    fun getAllGames(): Flow<List<GameEntity>>

    @Query(
        """
        SELECT * FROM games
        ORDER BY isPinned DESC, creationTimestamp DESC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun getGamesPage(limit: Int, offset: Int): List<GameEntity>

    @Query("SELECT COUNT(*) FROM games")
    fun observeGamesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM games")
    suspend fun getGamesCount(): Int

    @Query("SELECT COUNT(*) FROM games WHERE isPinned = 1")
    suspend fun getPinnedGamesCount(): Int

    @Query("SELECT * FROM games WHERE isPinned = 1 ORDER BY creationTimestamp DESC")
    fun getPinnedGames(): Flow<List<GameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameEntity>)

    @Update
    suspend fun updateGame(game: GameEntity)

    @Delete
    suspend fun deleteGame(game: GameEntity)

    @Query("DELETE FROM games WHERE isPinned = 0")
    suspend fun clearUnpinnedGames()
    
    @Query("SELECT * FROM games WHERE id = :gameId LIMIT 1")
    suspend fun getGameById(gameId: String): GameEntity?
}
