package com.example.dulceapp.activities

import android.Manifest
import android.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.dulceapp.databinding.ActivityAddProductBinding
import com.example.dulceapp.entities.Product
import com.example.dulceapp.services.FirebaseService
import java.io.File

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private var imageUri: Uri? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            binding.imageViewProductPreview.setImageURI(it)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            imageUri?.let {
                binding.imageViewProductPreview.setImageURI(it)
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
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAddProduct)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupCategorySpinner()
        setupImageButtons()
        setupSaveButton()
    }

    private fun setupCategorySpinner() {
        val categories = listOf("Tortas", "Postres", "Bocaditos", "Bebidas")
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerProductCategory.adapter = adapter
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
        binding.buttonSaveProduct.setOnClickListener {
            val name = binding.editTextProductName.text.toString().trim()
            val price = binding.editTextProductPrice.text.toString().trim()
            val category = binding.spinnerProductCategory.selectedItem.toString()

            if (name.isEmpty() || price.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Por favor, completa todos los campos y selecciona una imagen.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseService.uploadProductImage(imageUri!!) { success, downloadUrl ->
                if (success && downloadUrl != null) {
                    val product = Product(name = name, price = "$${price}", category = category, description = "", imageUrl = downloadUrl)

                    FirebaseService.saveProduct(product) { productSaved, error ->
                        if (productSaved) {
                            Toast.makeText(this, "Producto guardado con éxito", Toast.LENGTH_SHORT).show()
                            // --- NAVEGACIÓN A LA PANTALLA DE GESTIÓN ---
                            val intent = Intent(this, ManageProductsActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Error al guardar el producto: ${error ?: ""}", Toast.LENGTH_SHORT).show()
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
