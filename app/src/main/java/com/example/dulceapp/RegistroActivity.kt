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

class RegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

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

            Toast.makeText(this, "Registro exitoso: $nombre", Toast.LENGTH_SHORT).show()

            // Ejemplo: ir a la pantalla de login después del registro
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Ir a LoginActivity desde el texto
        loginTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
