package com.example.gamehaven.dao

import androidx.room.*
import com.example.gamehaven.entity.DownloadHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: DownloadHistory)

    @Query("SELECT * FROM DownloadHistory WHERE userId = :userId ORDER BY downloadDate DESC")
    fun getHistoryByUser(userId: Int): Flow<List<DownloadHistory>>

    @Query("SELECT * FROM DownloadHistory WHERE gameId = :gameId ORDER BY downloadDate DESC")
    fun getHistoryByGame(gameId: Int): Flow<List<DownloadHistory>>

    @Query("SELECT * FROM DownloadHistory WHERE userId = :userId AND gameId = :gameId")
    suspend fun getHistoryByUserAndGame(userId: Int, gameId: Int): DownloadHistory?

    @Query("DELETE FROM DownloadHistory WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(*) FROM DownloadHistory WHERE userId = :userId")
    suspend fun getDownloadCount(userId: Int): Int
}