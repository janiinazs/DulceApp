package com.example.dulceapp

import kotlinx.coroutines.flow.Flow

// #TODO: esto me falto we
class UserRepository(private val userDao: UserDao) {
    suspend fun signUp(user: User) = userDao.signUp(user)
    suspend fun login(email: String, password: String) = userDao.login(email, password)
    suspend fun userExists(username: String, email: String) = userDao.userExists(username, email)
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

}