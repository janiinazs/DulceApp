package com.example.dulceapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.dulceapp.databinding.ActivityRegistroBinding

class RegistroActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository
    private lateinit var binding: ActivityRegistroBinding // <-- USA VIEW BINDING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 2. Infla el layout con View Binding.
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        // 3. Establece la vista con el root del binding.
        setContentView(binding.root)

        // Inicializar repositorio (ajustar según cómo se inicialice AppDatabase en tu proyecto)
        val userDao = AppDatabase.getDatabase(this).userDao() // Asegúrate de tener AppDatabase configurado
        userRepository = UserRepository(userDao)

        // Ajustar insets de sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonRegistrar.setOnClickListener {
            val nombre = binding.editTextNombre.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            // ==== LÓGICA DE VALIDACIÓN MEJORADA ====
            var formValido = true

            // Validar nombre
            if (nombre.isEmpty()) {
                binding.textInputLayoutNombre.error = "El nombre no puede estar vacío"
                formValido = false
            } else {
                binding.textInputLayoutNombre.error = null
            }

            // Validar correo
            if (email.isEmpty()) {
                binding.textInputLayoutEmail.error = "El correo no puede estar vacío"
                formValido = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.textInputLayoutEmail.error = "Formato de correo inválido"
                formValido = false
            } else {
                binding.textInputLayoutEmail.error = null
            }
            // Validar contraseña
            if (password.isEmpty()) {
                binding.textInputLayoutPassword.error = "La contraseña no puede estar vacía"
                formValido = false
            } else if (password.length < 6) {
                binding.textInputLayoutPassword.error = "La contraseña debe tener al menos 6 caracteres"
                formValido = false
            } else {
                binding.textInputLayoutPassword.error = null
            }

            if (!formValido) {
                return@setOnClickListener // Si hay errores, no continúes
            }

            // Intentar registrarse
            lifecycleScope.launch {
                try {
                    val user = User(username = nombre, email = email, password = password)
                    userRepository.signUp(user)
                    Toast.makeText(this@RegistroActivity, "Registro exitoso: $nombre", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegistroActivity, LoginActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    // Posible usuario duplicado
                    binding.textInputLayoutEmail.error = "El correo o usuario ya existe"
                }
            }
        }

        // Ir a LoginActivity desde el texto
        binding.textViewLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
