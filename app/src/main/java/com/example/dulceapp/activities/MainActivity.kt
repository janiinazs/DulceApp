package com.example.dulceapp.activities // Paquete principal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dulceapp.utils.UserAdapter
import com.example.dulceapp.repo.UserRepository
import com.example.dulceapp.database.AppDatabase
import com.example.dulceapp.databinding.ActivityMainBinding
import com.example.dulceapp.services.FirebaseService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userRepository: UserRepository
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 1. Inicializa el repositorio
        val userDao = AppDatabase.getDatabase(applicationContext).userDao()
        userRepository = UserRepository(userDao)
        // 2. Configura el RecyclerView
        setupRecyclerView()
        // 3. Observa los cambios en la base de datos
//        observeUsers()
        // 3. Configura botones: load from Room, load from Firestore, clear
        binding.btnLoadRoom.setOnClickListener { loadUsersFromRoomOnce() }
        binding.btnLoadFirebase.setOnClickListener { loadUsersFromFirestore() }
        binding.btnClear.setOnClickListener { clearUsers() }

        binding.fabLogout.setOnClickListener {
            // Crea un intent para ir a LoginActivity
            val intent = Intent(this, LoginActivity::class.java)

            // Limpia el stack de actividades para que el usuario no pueda volver atrás
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            // Inicia la actividad
            startActivity(intent)

            // Opcional: Finaliza la MainActivity actual
            finish()
        }
    }

    private fun setupRecyclerView() {
        // Inicializa el adaptador con una lista vacía
        userAdapter = UserAdapter(emptyList())

        // Asigna el adaptador y el layout manager al RecyclerView del layout
        binding.recyclerViewUsuarios.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = userAdapter
        }
    }

    /*private fun observeUsers() {
        // Usa una corrutina atada al ciclo de vida de la Activity
        lifecycleScope.launch {
            // "collect" escuchará continuamente los cambios emitidos por el Flow
            userRepository.getAllUsers().collect { listOfUsers ->
                // Cuando llega una nueva lista de usuarios, actualiza el adaptador
                userAdapter.updateData(listOfUsers)
            }
        }
    }*/

    private fun loadUsersFromRoomOnce() {
        lifecycleScope.launch {
            try {
                // Collect the current list once
                val users = userRepository.getAllUsers().first()
                userAdapter.updateData(users)
            } catch (e: Exception) {
                // Optionally show a toast or log
                }
            }
        }

    private fun loadUsersFromFirestore() {
        // Use FirebaseService to fetch users, update adapter on UI thread
        FirebaseService.fetchAllUsers { users, error ->
            runOnUiThread {
                if (users != null) {
                    userAdapter.updateData(users)
                } else {
                    // Optionally show an error (toast/log). Keep UI responsive.
                }
            }
        }
    }

    private fun clearUsers() {
        userAdapter.updateData(emptyList())
    }
}
