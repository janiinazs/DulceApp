package com.example.dulceapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dulceapp.databinding.ActivityLoginBinding
import com.example.dulceapp.entities.User
import com.example.dulceapp.services.FirebaseService
import com.example.dulceapp.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener { handleLogin() }
        binding.registerText.setOnClickListener { navigateToRegister() }
        binding.forgotPasswordText.setOnClickListener { navigateToForgotPassword() }
        binding.guestModeText.setOnClickListener { handleGuestMode() }
    }

    private fun handleLogin() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordInputText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseService.login(email, password) { user, errorMessage ->
            if (user != null) {
                SessionManager.setGuestMode(this, false) // Asegurarse de que no es invitado
                navigateToNextScreen(user)
            } else {
                Toast.makeText(this, errorMessage ?: "Error en el login", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleGuestMode() {
        SessionManager.setGuestMode(this, true)
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToNextScreen(user: User) {
        val intent = when (user.role) {
            "admin" -> Intent(this, AdminHomeActivity::class.java)
            else -> Intent(this, HomeActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToForgotPassword() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }
}
