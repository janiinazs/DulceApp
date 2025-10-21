package com.example.dulceapp

import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    suspend fun signUp(user: User) {
        //1. Guardar en la base de datos local (Room)
        userDao.signUp(user)
        // 2. Guardar en la base de datos en la nube (Firebase)
        FirebaseService.guardarUsuario(user)
    }

    suspend fun login(email: String, password: String) = userDao.login(email, password)
    suspend fun userExists(username: String, email: String) = userDao.userExists(username, email)
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
}
