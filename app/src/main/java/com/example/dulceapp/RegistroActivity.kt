package com.example.dulceapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RegistroActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicializar repositorio (ajustar según cómo se inicialice AppDatabase en tu proyecto)
        val userDao = AppDatabase.getDatabase(this).userDao() // Asegúrate de tener AppDatabase configurado
        userRepository = UserRepository(userDao)

        // Ajustar insets de sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencias a los campos
        val nombreEditText = findViewById<TextInputEditText>(R.id.editTextNombre)
        val emailEditText = findViewById<TextInputEditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<TextInputEditText>(R.id.editTextPassword)
        val registrarButton = findViewById<Button>(R.id.buttonRegistrar)
        val loginTextView = findViewById<TextView>(R.id.textViewLogin)

        // Acción del botón "Registrarse"
        registrarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validaciones simples
            if (nombre.isEmpty()) {
                nombreEditText.error = "Ingresa tu nombre"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                emailEditText.error = "Ingresa tu correo"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordEditText.error = "Ingresa una contraseña"
                return@setOnClickListener
            }

            // Intentar registrarse
            lifecycleScope.launch {
                try {
                    val user = User(username = nombre, email = email, password = password)
                    userRepository.signUp(user)
                    Toast.makeText(this@RegistroActivity, "Registro exitoso: $nombre", Toast.LENGTH_SHORT).show()

                    // Ir a la pantalla de login después del registro
                    startActivity(Intent(this@RegistroActivity, LoginActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@RegistroActivity, "Error al registrarse: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Ir a LoginActivity desde el texto
        loginTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
