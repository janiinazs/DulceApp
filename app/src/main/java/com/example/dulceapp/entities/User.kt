package com.example.dulceapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uid: String = "",
    val username: String = "", 
    val email: String = "",
    val password: String = "",
    val role: String = "cliente",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val keyword: String = "",
    val profileImageUrl: String = ""
)
