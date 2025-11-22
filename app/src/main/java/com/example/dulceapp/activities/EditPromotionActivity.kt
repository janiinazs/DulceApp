package com.example.dulceapp.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.dulceapp.databinding.ActivityEditPromotionBinding
import com.example.dulceapp.entities.Promotion
import com.example.dulceapp.services.FirebaseService
import java.io.File

class EditPromotionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPromotionBinding
    private var imageUri: Uri? = null
    private var currentPromotion: Promotion? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            binding.imageViewPromotionPreview.setImageURI(it)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            imageUri?.let {
                binding.imageViewPromotionPreview.setImageURI(it)
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "El permiso de la cámara es necesario.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPromotionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarEditPromotion)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        currentPromotion = intent.getParcelableExtra("PROMOTION_EXTRA")
        if (currentPromotion == null) {
            Toast.makeText(this, "Error: No se pudo cargar la promoción.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        populateForm()
        setupImageButtons()
        setupSaveButton()
    }

    private fun populateForm() {
        currentPromotion?.let {
            binding.editTextPromotionName.setText(it.name)
            binding.editTextPromotionPrice.setText(it.price.replace("$", ""))
            Glide.with(this).load(it.imageUrl).into(binding.imageViewPromotionPreview)
        }
    }

    private fun setupImageButtons() {
        binding.buttonGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }
        binding.buttonCamera.setOnClickListener {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val file = File(filesDir, "pic.jpg")
        imageUri = FileProvider.getUriForFile(this, "com.example.dulceapp.fileprovider", file)
        cameraLauncher.launch(imageUri!!)
    }

    private fun setupSaveButton() {
        binding.buttonSavePromotion.setOnClickListener {
            val name = binding.editTextPromotionName.text.toString().trim()
            val price = binding.editTextPromotionPrice.text.toString().trim()

            if (name.isEmpty() || price.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (imageUri != null) {
                // Si se seleccionó una nueva imagen, subirla primero
                FirebaseService.uploadPromotionImage(imageUri!!) { success, downloadUrl ->
                    if (success && downloadUrl != null) {
                        updatePromotionData(name, price, downloadUrl)
                    } else {
                        Toast.makeText(this, "Error al subir la nueva imagen.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Si no hay nueva imagen, usar la URL existente
                updatePromotionData(name, price, currentPromotion!!.imageUrl)
            }
        }
    }

    private fun updatePromotionData(name: String, price: String, imageUrl: String) {
        val updatedData = mapOf(
            "name" to name,
            "price" to "$${price}",
            "imageUrl" to imageUrl
        )

        FirebaseService.updatePromotion(currentPromotion!!.id, updatedData) { success, error ->
            if (success) {
                Toast.makeText(this, "Promoción actualizada con éxito", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ManagePromotionsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al actualizar: ${error ?: ""}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
