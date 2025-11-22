package com.example.dulceapp.activities

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dulceapp.adapters.UserAdapter
import com.example.dulceapp.databinding.ActivityUserListBinding
import com.example.dulceapp.entities.User
import com.example.dulceapp.services.FirebaseService

class UserListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserListBinding
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarUserList)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
        loadUsers()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(
            onDeleteClicked = { user ->
                showDeleteConfirmationDialog(user)
            },
            // --- LÓGICA PARA ABRIR EL MAPA ---
            onLocationClicked = { user ->
                openMapForUser(user)
            }
        )
        binding.recyclerViewUsers.apply {
            layoutManager = LinearLayoutManager(this@UserListActivity)
            adapter = userAdapter
        }
    }

    private fun loadUsers() {
        FirebaseService.fetchAllUsers { users, errorMessage ->
            if (users != null) {
                val currentUser = FirebaseService.auth.currentUser
                val filteredUsers = users.filter { it.uid != currentUser?.uid }
                userAdapter.submitList(filteredUsers)
            } else {
                Toast.makeText(this, "Error al cargar usuarios: ${errorMessage ?: ""}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmationDialog(user: User) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar a ${user.username}?")
            .setPositiveButton("Sí") { _, _ ->
                FirebaseService.deleteUser(user.uid) { success, error ->
                    if (success) {
                        Toast.makeText(this, "Usuario eliminado con éxito", Toast.LENGTH_SHORT).show()
                        loadUsers()
                    } else {
                        Toast.makeText(this, "Error al eliminar: ${error ?: ""}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    // --- FUNCIÓN PARA ABRIR EL MAPA ---
    private fun openMapForUser(user: User) {
        if (user.latitude != 0.0 && user.longitude != 0.0) {
            val gmmIntentUri = Uri.parse("geo:${user.latitude},${user.longitude}?q=${user.latitude},${user.longitude}(${user.username})")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps") // Intenta abrir Google Maps específicamente
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                // Si Google Maps no está instalado, intenta con cualquier app de mapas
                val genericMapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:${user.latitude},${user.longitude}"))
                startActivity(genericMapIntent)
            }
        } else {
            Toast.makeText(this, "Este usuario no tiene una ubicación guardada.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
