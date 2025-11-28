package com.example.gamehaven.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gamehaven.entity.Game
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: Game)

    @Update
    suspend fun update(game: Game)

    @Delete
    suspend fun delete(game: Game)

    @Query("SELECT * FROM Game ORDER BY id DESC")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM Game WHERE id = :id")
    suspend fun getGameById(id: Int): Game?

    @Query("SELECT * FROM Game WHERE category = :category")
    fun getGamesByCategory(category: String): Flow<List<Game>>

    @Query("SELECT COUNT(*) FROM Game")
    suspend fun getTotalGames(): Int

    @Query("SELECT COUNT(*) FROM Game WHERE stock > 0")
    suspend fun getAvailableGames(): Int

    @Query("SELECT COUNT(*) FROM Game WHERE stock = 0")
    suspend fun getOutOfStockGames(): Int

    @Query("SELECT SUM(price) FROM Game")
    suspend fun getTotalInventoryValue(): Double?

    @Query("SELECT DISTINCT category FROM Game")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT * FROM Game WHERE title LIKE '%' || :search || '%' OR developer LIKE '%' || :search || '%' OR category LIKE '%' || :search || '%'")
    fun searchGames(search: String): Flow<List<Game>>

    @Query("SELECT * FROM Game WHERE stock <= :lowStockThreshold")
    fun getLowStockGames(lowStockThreshold: Int = 5): Flow<List<Game>>
}