package com.example.gamehaven.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.gamehaven.AppDb
import com.example.gamehaven.entity.DownloadHistory
import com.example.gamehaven.entity.DownloadStatus
import com.example.gamehaven.entity.PurchasedGame
import kotlinx.coroutines.launch
import java.util.Date

class PurchasedGameViewModel(application: Application) : AndroidViewModel(application) {

    private val purchasedGameDao = AppDb.getDb(application).purchasedGameDao()
    private val downloadHistoryDao = AppDb.getDb(application).downloadHistoryDao()

    fun getPurchasedGamesByUser(userId: Int): LiveData<List<PurchasedGame>> {
        return purchasedGameDao.getPurchasedGamesByUser(userId).asLiveData()
    }

    fun insertPurchasedGame(purchasedGame: PurchasedGame) = viewModelScope.launch {
        purchasedGameDao.insert(purchasedGame)
    }

    fun updateDownloadStatus(id: Int, status: DownloadStatus) = viewModelScope.launch {
        purchasedGameDao.updateDownloadStatus(id, status)

        // If download is completed, add to download history
        if (status == DownloadStatus.DOWNLOADED || status == DownloadStatus.INSTALLED) {
            val purchasedGame = purchasedGameDao.getPurchasedGameById(id)
            purchasedGame?.let {
                val downloadHistory = DownloadHistory(
                    id = 0,
                    userId = it.userId,
                    gameId = it.gameId,
                    downloadDate = Date()
                )
                downloadHistoryDao.insert(downloadHistory)
            }
        }
    }

    suspend fun isGamePurchased(userId: Int, gameId: Int): Boolean {
        return purchasedGameDao.getPurchasedGameByUserAndGame(userId, gameId) != null
    }

    suspend fun getPurchasedGamesCount(userId: Int): Int {
        return purchasedGameDao.getPurchasedGamesCount(userId)
    }
}