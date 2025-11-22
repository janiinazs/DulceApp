package com.example.dulceapp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.dulceapp.databinding.ActivityRegistroBinding
import com.example.dulceapp.entities.User
import com.example.dulceapp.services.FirebaseService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation: Location? = null

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                getLastKnownLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                getLastKnownLocation()
            }
            else -> {
                Toast.makeText(this, "El permiso de ubicación es necesario para el registro.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.buttonRegistrar.setOnClickListener {
            checkLocationPermissionAndRegister()
        }

        binding.textViewLogin.setOnClickListener {
            finish()
        }
    }

    private fun checkLocationPermissionAndRegister() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                getLastKnownLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            }
            else -> {
                locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            }
        }
    }

    private fun getLastKnownLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        userLocation = location
                        registerUser()
                    } else {
                        Toast.makeText(this, "No se pudo obtener la ubicación. Activa el GPS y vuelve a intentarlo.", Toast.LENGTH_LONG).show()
                    }
                }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Error de seguridad al obtener la ubicación.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerUser() {
        val username = binding.editTextNombre.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()
        val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()
        val keyword = binding.editTextKeyword.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || keyword.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
            return
        }

        if (userLocation == null) {
            Toast.makeText(this, "No se ha podido obtener la ubicación para el registro.", Toast.LENGTH_SHORT).show()
            return
        }

        val user = User(
            username = username,
            email = email,
            password = password,
            role = "cliente",
            latitude = userLocation!!.latitude,
            longitude = userLocation!!.longitude,
            keyword = keyword
        )

        FirebaseService.saveUser(user) { success, exception ->
            if (success) {
                Toast.makeText(this, "¡Registro exitoso! Por favor, inicia sesión.", Toast.LENGTH_LONG).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // --- TRADUCTOR DE ERRORES DE FIREBASE ---
                val errorMessage = when (exception) {
                    is FirebaseAuthWeakPasswordException -> "La contraseña es demasiado débil. Debe tener al menos 6 caracteres."
                    is FirebaseAuthInvalidCredentialsException -> "El formato del correo electrónico no es válido."
                    is FirebaseAuthUserCollisionException -> "Este correo electrónico ya está en uso por otra cuenta."
                    else -> "Error en el registro. Inténtalo de nuevo."
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}
