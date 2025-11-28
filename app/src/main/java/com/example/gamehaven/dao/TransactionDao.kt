package com.example.gamehaven.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gamehaven.entity.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Query("SELECT * FROM `Transaction` ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM `Transaction` WHERE userId = :userId ORDER BY date DESC")
    fun getTransactionsByUser(userId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM `Transaction` WHERE id = :id")
    suspend fun getTransactionById(id: Int): Transaction?
}