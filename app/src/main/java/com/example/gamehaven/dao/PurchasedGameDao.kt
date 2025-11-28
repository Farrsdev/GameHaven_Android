package com.example.gamehaven.dao

import androidx.room.*
import com.example.gamehaven.entity.PurchasedGame
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchasedGameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchasedGame: PurchasedGame)

    @Update
    suspend fun update(purchasedGame: PurchasedGame)

    @Delete
    suspend fun delete(purchasedGame: PurchasedGame)

    @Query("SELECT * FROM PurchasedGame WHERE userId = :userId ORDER BY purchaseDate DESC")
    fun getPurchasedGamesByUser(userId: Int): Flow<List<PurchasedGame>>

    @Query("SELECT * FROM PurchasedGame WHERE id = :id")
    suspend fun getPurchasedGameById(id: Int): PurchasedGame?

    @Query("SELECT * FROM PurchasedGame WHERE userId = :userId AND gameId = :gameId")
    suspend fun getPurchasedGameByUserAndGame(userId: Int, gameId: Int): PurchasedGame?

    @Query("UPDATE PurchasedGame SET downloadStatus = :status WHERE id = :id")
    suspend fun updateDownloadStatus(id: Int, status: com.example.gamehaven.entity.DownloadStatus)

    @Query("SELECT COUNT(*) FROM PurchasedGame WHERE userId = :userId")
    suspend fun getPurchasedGamesCount(userId: Int): Int
}