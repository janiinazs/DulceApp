package com.example.dulceapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.dulceapp.R
import com.example.dulceapp.repo.UserRepository
import com.example.dulceapp.database.AppDatabase
import com.example.dulceapp.databinding.ActivityLoginBinding
import com.example.dulceapp.services.FirebaseService
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 2. Infla el layout usando View Binding y asigna el resultado a la variable 'binding'.
        binding = ActivityLoginBinding.inflate(layoutInflater)
        // 3. Pasa la vista raíz del binding a setContentView.
        setContentView(binding.root)

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
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordInputText.text.toString().trim()

            // ==== LÓGICA DE VALIDACIÓN MEJORADA ====
            var formValido = true

            // Validar correo
            if (email.isEmpty()) {
                binding.emailInputLayout.error = "El correo no puede estar vacío"
                formValido = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailInputLayout.error = "Formato de correo inválido"
                formValido = false
            } else {
                binding.emailInputLayout.error = null // Limpiar error si es válido
            }

            // Validar contraseña
            if (password.isEmpty()) {
                binding.passwordInputLayout.error = "La contraseña no puede estar vacía"
                formValido = false
            } else if (password.length < 6) {
                binding.passwordInputLayout.error = "La contraseña debe tener al menos 6 caracteres"
                formValido = false
            } else {
                binding.passwordInputLayout.error = null // Limpiar error si es válida
            }

            if (!formValido) {
                return@setOnClickListener // Si hay errores, no continúes
            }

            // Intentar iniciar sesión
            lifecycleScope.launch {
                try {
                    // Llama a la función suspend y espera el resultado
                    com.example.dulceapp.services.FirebaseService.login(email, password) { user, errorMessage ->
                        runOnUiThread {
                            // Este bloque de código se ejecutará en el futuro,
                            // cuando Firebase termine la operación de login.

                            if (user != null) {
                                // ÉXITO: El usuario se encontró y la contraseña es correcta
                                Toast.makeText(this@LoginActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish() // Cierra LoginActivity para que el usuario no pueda volver atrás
                            } else {
                                // ERROR: Ocurrió un problema, `errorMessage` tendrá los detalles
                                Toast.makeText(this@LoginActivity, "Error: $errorMessage", Toast.LENGTH_LONG).show()

                                // Opcional: Mostrar error en los campos para dar feedback visual
                                binding.emailInputLayout.error = "Verifica tu correo o contraseña"
                                binding.passwordInputLayout.error = " " // Un espacio para que se muestre el indicador de error
                            }
                        }

                    }

                    // Si la función no lanzó una excepción, el login fue exitoso
//                    if (user != null) {
//                        Toast.makeText(this@LoginActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
//                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//                        finish() // Cierra esta actividad
//                    } else {
//                        // Caso en que la función devuelve null sin lanzar excepción (ej: usuario no existe)
//                        Toast.makeText(this@LoginActivity, "Usuario no encontrado o contraseña incorrecta", Toast.LENGTH_LONG).show()
//                    }
                } catch (e: Exception) {
                    // Si Firebase lanza una excepción (red, contraseña incorrecta, etc.), la capturamos
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()

                    // Feedback visual opcional
                    binding.emailInputLayout.error = "Verifica tu correo o contraseña"
                    binding.passwordInputLayout.error = " " // Espacio para mostrar el indicador de error
                }
            }

            // Aquí iría la verificación real con una base de datos
            // Por ahora solo mostramos un mensaje simulado
            //Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

            // Redirigir a pantalla principal o dashboard si existe
            // startActivity(Intent(this, MainActivity::class.java))
        }

        // Acción al hacer clic en "¿No tienes cuenta? Regístrate aquí."
        binding.registerText.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }
}