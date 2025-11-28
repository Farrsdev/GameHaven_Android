package com.example.gamehaven.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.gamehaven.entity.TransactionDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDetailDao {

    @Insert
    suspend fun insert(detail: TransactionDetail)

    @Insert
    suspend fun insertAll(details: List<TransactionDetail>)

    @Query("SELECT * FROM TransactionDetail WHERE transactionId = :transactionId")
    fun getDetailsByTransaction(transactionId: Int): Flow<List<TransactionDetail>>

    @Delete
    suspend fun delete(detail: TransactionDetail)

}