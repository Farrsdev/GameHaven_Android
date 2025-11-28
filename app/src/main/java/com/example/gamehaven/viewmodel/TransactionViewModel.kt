package com.example.gamehaven.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.gamehaven.AppDb
import com.example.gamehaven.entity.PurchasedGame
import com.example.gamehaven.entity.Transaction
import com.example.gamehaven.entity.TransactionDetail
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val txDao = AppDb.getDb(application).transactionDao()
    private val detailDao = AppDb.getDb(application).transactionDetailDao()
    private val purchasedGameDao = AppDb.getDb(application).purchasedGameDao()


    val allTransactions: LiveData<List<Transaction>> =
        txDao.getAllTransactions().asLiveData()

    fun insertTransaction(
        transaction: Transaction,
        details: List<TransactionDetail>
    ) = viewModelScope.launch {
        val txId = txDao.insert(transaction).toInt()

        val newDetails = details.map {
            it.copy(transactionId = txId)
        }

        detailDao.insertAll(newDetails)

        // Automatically add to purchased games for each game in transaction
        details.forEach { detail ->
            val purchasedGame = PurchasedGame(
                userId = transaction.userId,
                gameId = detail.gameId,
                purchaseDate = transaction.date,
                transactionId = txId
            )
            purchasedGameDao.insert(purchasedGame)
        }
    }

    fun getTransactionsByUser(userId: Int): LiveData<List<Transaction>> {
        return txDao.getTransactionsByUser(userId).asLiveData()
    }

    fun getDetails(transactionId: Int): LiveData<List<TransactionDetail>> {
        return detailDao.getDetailsByTransaction(transactionId).asLiveData()
    }
}
