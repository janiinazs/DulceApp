package com.example.dulceapp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dulceapp.databinding.ActivityForgotPasswordBinding
import com.example.dulceapp.services.FirebaseService

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.verifyButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val keyword = binding.keywordEditText.text.toString().trim()

            if (email.isEmpty() || keyword.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- LÓGICA DE VERIFICACIÓN Y RECUPERACIÓN ---
            FirebaseService.findUserByEmail(email) { user ->
                if (user == null) {
                    Toast.makeText(this, "No se encontró ninguna cuenta con ese correo electrónico.", Toast.LENGTH_LONG).show()
                    return@findUserByEmail
                }

                if (user.keyword == keyword) {
                    // La palabra clave es correcta, enviar correo de recuperación
                    FirebaseService.sendPasswordResetEmail(email) { success, exception ->
                        if (success) {
                            Toast.makeText(this, "¡Verificación exitosa! Se ha enviado un correo para restablecer tu contraseña. Revisa tu bandeja de entrada.", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Error al enviar el correo de recuperación.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // La palabra clave es incorrecta
                    Toast.makeText(this, "La palabra clave es incorrecta.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
