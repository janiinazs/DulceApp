package com.example.dulceapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.dulceapp.databinding.ActivityLoginBinding
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
        val userDao = AppDatabase.getInstance(this).userDao() // Asegúrate de tener AppDatabase configurado
        userRepository = UserRepository(userDao)

        // Aplicar padding para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
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
                    val user = userRepository.login(email, password)
                    if (user != null) {
                        Toast.makeText(this@LoginActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        // Muestra el error en ambos campos para no dar pistas al atacante
                        binding.emailInputLayout.error = "Correo o contraseña incorrectos"
                        binding.passwordInputLayout.error = "Correo o contraseña incorrectos"
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Acción al hacer clic en "¿No tienes cuenta? Regístrate aquí."
        binding.registerText.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }
}
