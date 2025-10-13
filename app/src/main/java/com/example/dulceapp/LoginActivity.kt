package com.example.dulceapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Inicializar repositorio (ajustar según cómo se inicialice AppDatabase en tu proyecto)
        val userDao = AppDatabase.getDatabase(this).userDao() // Asegúrate de tener AppDatabase configurado
        userRepository = UserRepository(userDao)

        // Aplicar padding para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Conectar vistas
        val emailEditText = findViewById<TextInputEditText>(R.id.email_edit_text)
        val passwordEditText = findViewById<TextInputEditText>(R.id.password_input_text)
        val loginButton = findViewById<Button>(R.id.login_button)
        val registerText = findViewById<TextView>(R.id.register_text)

        // Lógica del botón de "Entrar"
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty()) {
                emailEditText.error = "Ingresa tu correo"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordEditText.error = "Ingresa tu contraseña"
                return@setOnClickListener
            }

            // Intentar iniciar sesión
            lifecycleScope.launch {
                try {
                    val user = userRepository.login(email, password)
                    if (user != null) {
                        Toast.makeText(this@LoginActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        // Redirigir a pantalla principal o dashboard
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            // Aquí iría la verificación real con una base de datos
            // Por ahora solo mostramos un mensaje simulado
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

            // Redirigir a pantalla principal o dashboard si existe
            // startActivity(Intent(this, MainActivity::class.java))
        }

        // Acción al hacer clic en "¿No tienes cuenta? Regístrate aquí."
        registerText.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }
}