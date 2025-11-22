package com.example.dulceapp.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.dulceapp.databinding.ActivityAddPromotionBinding
import com.example.dulceapp.entities.Promotion
import com.example.dulceapp.services.FirebaseService
import java.io.File

class AddPromotionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPromotionBinding
    private var imageUri: Uri? = null

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
            Toast.makeText(this, "El permiso de la cámara es necesario para tomar fotos.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPromotionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAddPromotion)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupImageButtons()
        setupSaveButton()
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

            if (name.isEmpty() || price.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Por favor, completa todos los campos y selecciona una imagen.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseService.uploadPromotionImage(imageUri!!) { success, downloadUrl ->
                if (success && downloadUrl != null) {
                    val promotion = Promotion(name = name, price = "$${price}", imageUrl = downloadUrl)

                    FirebaseService.savePromotion(promotion) { promotionSaved, error ->
                        if (promotionSaved) {
                            Toast.makeText(this, "Promoción guardada con éxito", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, ManagePromotionsActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Error al guardar la promoción: ${error ?: ""}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Error al subir la imagen: ${downloadUrl ?: ""}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
