package com.example.gamehaven.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.gamehaven.AppDb
import kotlinx.coroutines.launch
import com.example.gamehaven.entity.User

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDb.getDb(application).userDao()

    val allUsers: LiveData<List<User>> = userDao.getAllUsers().asLiveData()

    private val _searchResults = MutableLiveData<List<User>>()
    val searchResults: LiveData<List<User>> = _searchResults

    fun searchUsers(search: String) = viewModelScope.launch {
        userDao.searchUsers(search).collect { users ->
            _searchResults.postValue(users)
        }
    }

    fun getUsersByRole(isAdmin: Boolean): LiveData<List<User>> {
        return userDao.getUsersByRole(isAdmin).asLiveData()
    }

    suspend fun getTotalUsers(): Int {
        return userDao.getTotalUsers()
    }

    suspend fun getTotalRegularUsers(): Int {
        return userDao.getTotalRegularUsers()
    }

    suspend fun getTotalAdminUsers(): Int {
        return userDao.getTotalAdminUsers()
    }

    fun insert(user: User) = viewModelScope.launch {
        userDao.insert(user)
    }

    fun update(user: User) = viewModelScope.launch {
        userDao.update(user)
    }

    fun delete(user: User) = viewModelScope.launch {
        userDao.delete(user)
    }

    suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)
    }

    suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)
    }
}
