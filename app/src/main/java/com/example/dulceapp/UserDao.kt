package com.example.dulceapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.ABORT
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = ABORT)
    suspend fun signUp(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): User?

    @Query("SELECT COUNT(*) FROM users WHERE email = :email OR username = :username")
    suspend fun userExists(username: String, email: String): Int
}