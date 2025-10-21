package com.example.dulceapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ListaUsuariosActivity : AppCompatActivity() {
    private lateinit var rv: RecyclerView
    private lateinit var adapter: UserAdapter // Asegúrate de que tienes esta clase adaptadora
    private lateinit var btnFirebase: Button
    private lateinit var btnRoom: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Asegúrate de que el nombre del layout coincida con el de tu proyecto
        setContentView(R.layout.activity_lista_usuarios)

        // Inicialización de vistas
        rv = findViewById(R.id.rvUsuarios)
        btnFirebase = findViewById(R.id.btnFirebase)
        btnRoom = findViewById(R.id.btnRoom)

        // Configuración del RecyclerView
        adapter = UserAdapter()
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        // Instancia del DAO de Room
        val dao = AppDatabase.getInstance(this).userDao()

        // Listener para el botón de Room
        btnRoom.setOnClickListener {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    dao.getAllUsers().collectLatest { lista ->
                        adapter.submitList(lista)
                    }
                }
            }
        }

        // Listener para el botón de Firebase
        btnFirebase.setOnClickListener {
            FirebaseService.obtenerUsuarios {
                    listaFirebase ->
                adapter.submitList(listaFirebase)
                }
            }
        }
    }

