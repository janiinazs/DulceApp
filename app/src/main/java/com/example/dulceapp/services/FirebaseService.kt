package com.example.dulceapp.services

import com.example.dulceapp.entities.User
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseService {
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    /**
     * Save a user document to Firestore in a collection "users".
     * Uses the user's email as document id to avoid duplicates in Firestore.
     * Calls the callback with (success, errorMessage).
     */
    fun saveUser(user: User, callback: (Boolean, String?) -> Unit) {
        try {
            val docId = user.email // simple approach for uniqueness in testing
            val data = hashMapOf(
                "username" to user.username,
                "email" to user.email,
                // Do NOT store plaintext passwords in production. This is only for testing.
                "password" to user.password
            )
            firestore.collection("users")
                .document(docId)
                .set(data)
                .addOnSuccessListener {
                    callback(true, null)
                }
                .addOnFailureListener { e ->
                    callback(false, e.message)
                }
        } catch (e: Exception) {
            callback(false, e.message)
        }
    }

    fun login(email: String, password: String, callback: (User?, String?) -> Unit) {
        try {
            firestore.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        callback(null, "Usuario o contraseña incorrectos")
                    } else {
                        val user = querySnapshot.documents.first().toObject(User::class.java)
                        callback(user, null)
                    }
                }
                .addOnFailureListener { e ->
                    callback(null, e.message)
                }
        } catch (e: Exception) {
            callback(null, e.message)
        }
    }

    /**
     * Fetch all users from Firestore "users" collection.
     * Returns a list of User via callback: (users, errorMessage)
     */
    fun fetchAllUsers(callback: (List<User>?, String?) -> Unit) {
        try {
            firestore.collection("users")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val list = querySnapshot.documents.mapNotNull { doc ->
                        val username = doc.getString("username")
                        val email = doc.getString("email")
                        val password = doc.getString("password") // only for testing
                        if (username != null && email != null && password != null) {
                            User(username = username, email = email, password = password)
                        } else null
                    }
                    callback(list, null)
                }
                .addOnFailureListener { e ->
                    callback(null, e.message)
                }
        } catch (e: Exception) {
            callback(null, e.message)
        }
    }
}