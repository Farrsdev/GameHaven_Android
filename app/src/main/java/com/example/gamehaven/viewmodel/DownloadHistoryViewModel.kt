package com.example.gamehaven.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.gamehaven.AppDb
import com.example.gamehaven.entity.DownloadHistory
import kotlinx.coroutines.launch

class DownloadHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val historyDao =
        AppDb.getDb(application).downloadHistoryDao()

    fun insert(history: DownloadHistory) = viewModelScope.launch {
        historyDao.insert(history)
    }

    fun getByUser(userId: Int): LiveData<List<DownloadHistory>> {
        return historyDao.getHistoryByUser(userId).asLiveData()
    }

    fun getByGame(gameId: Int): LiveData<List<DownloadHistory>> {
        return historyDao.getHistoryByGame(gameId).asLiveData()
    }
}
