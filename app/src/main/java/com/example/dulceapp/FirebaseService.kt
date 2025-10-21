package com.example.dulceapp

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.security.auth.callback.Callback
import com.example.dulceapp.User

object FirebaseService {
    private val db = FirebaseFirestore.getInstance()

    fun guardarUsuario(usuario: User) {
        val data = hashMapOf(
            "username" to usuario.username,
            "email" to usuario.email,
            "password" to usuario.password
        )

        db.collection("usuarios").add(data)
            .addOnSuccessListener {
                Log.d("FirebaseService", "Usuario guardado correctamente en Firestore.")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseService", "Error al guardar en Firestore.", e)
            }
    }

    fun obtenerUsuarios(callback: (List<User>) -> Unit) {
        db.collection("usuarios").get()
            .addOnSuccessListener { result ->
                val lista = result.map { doc ->
                    // Al leer de Firestore, los documentos no tienen un ID de Room,
                    // por lo que se puede dejar como 0 o manejarlo según tu lógica.
                    User(
                        id = 0, // El id de Room no existe en Firestore
                        username = doc.getString("username") ?: "",
                        email = doc.getString("email") ?: "",
                        password = doc.getString("password") ?: ""
                    )
                }
                callback(lista)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseService", "Error al obtener usuarios de Firestore", e)
                callback(emptyList()) // Devuelve una lista vacía en caso de error
            }
    }


}
