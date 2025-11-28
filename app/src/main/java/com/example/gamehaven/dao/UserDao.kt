package com.example.gamehaven.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gamehaven.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM User ORDER BY id DESC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM User WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?

    @Query("SELECT * FROM User WHERE id = :id")
    suspend fun getUserById(id: Int): User?

    @Query("SELECT COUNT(*) FROM User")
    suspend fun getTotalUsers(): Int

    @Query("SELECT COUNT(*) FROM User WHERE role = 0")
    suspend fun getTotalRegularUsers(): Int

    @Query("SELECT COUNT(*) FROM User WHERE role = 1")
    suspend fun getTotalAdminUsers(): Int

    @Query("SELECT * FROM User WHERE username LIKE '%' || :search || '%' OR email LIKE '%' || :search || '%'")
    fun searchUsers(search: String): Flow<List<User>>

    @Query("SELECT * FROM User WHERE role = :isAdmin")
    fun getUsersByRole(isAdmin: Boolean): Flow<List<User>>
}