package com.example.gamehaven.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.gamehaven.AppDb
import com.example.gamehaven.entity.Game
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val gameDao = AppDb.getDb(application).gameDao()

    val allGames: LiveData<List<Game>> = gameDao.getAllGames().asLiveData()

    private val _searchResults = MutableLiveData<List<Game>>()
    val searchResults: LiveData<List<Game>> = _searchResults

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    fun searchGames(search: String) = viewModelScope.launch {
        gameDao.searchGames(search).collect { games ->
            _searchResults.postValue(games)
        }
    }

    fun loadCategories() = viewModelScope.launch {
        gameDao.getAllCategories().collect { categories ->
            _categories.postValue(categories)
        }
    }

    fun getLowStockGames(threshold: Int = 5): LiveData<List<Game>> {
        return gameDao.getLowStockGames(threshold).asLiveData()
    }

    suspend fun getTotalGames(): Int {
        return gameDao.getTotalGames()
    }

    suspend fun getAvailableGames(): Int {
        return gameDao.getAvailableGames()
    }

    suspend fun getOutOfStockGames(): Int {
        return gameDao.getOutOfStockGames()
    }

    suspend fun getTotalInventoryValue(): Double {
        return gameDao.getTotalInventoryValue() ?: 0.0
    }

    fun insert(game: Game) = viewModelScope.launch {
        gameDao.insert(game)
    }

    fun update(game: Game) = viewModelScope.launch {
        gameDao.update(game)
    }

    fun delete(game: Game) = viewModelScope.launch {
        gameDao.delete(game)
    }

    fun getByCategory(category: String): LiveData<List<Game>> {
        return gameDao.getGamesByCategory(category).asLiveData()
    }

    suspend fun getById(id: Int): Game? {
        return gameDao.getGameById(id)
    }
}
