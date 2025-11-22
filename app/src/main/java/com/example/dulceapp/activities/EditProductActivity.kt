package com.example.dulceapp.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.dulceapp.databinding.ActivityEditProductBinding
import com.example.dulceapp.entities.Product
import com.example.dulceapp.services.FirebaseService
import java.io.File

class EditProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProductBinding
    private var imageUri: Uri? = null
    private var currentProduct: Product? = null
    private var productId: String? = null
    private val categories = listOf("Tortas", "Postres", "Bocaditos", "Bebidas")

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
            Toast.makeText(this, "El permiso de la cámara es necesario.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarEditProduct)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupCategorySpinner()

        productId = intent.getStringExtra("PRODUCT_ID")
        if (productId == null) {
            Toast.makeText(this, "Error: No se encontró el producto.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadProductData()
        setupImageButtons()
        setupSaveButton()
    }

    private fun loadProductData() {

        FirebaseService.fetchProductById(productId!!) { product, error ->
            if (product != null) {
                currentProduct = product
                populateForm()
            } else {
                Toast.makeText(this, "Error al cargar el producto: ${error ?: "No encontrado"}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun populateForm() {
        currentProduct?.let {
            binding.editTextProductName.setText(it.name)
            binding.editTextProductPrice.setText(it.price.replace("$", ""))
            val categoryPosition = categories.indexOf(it.category)
            if (categoryPosition >= 0) {
                binding.spinnerProductCategory.setSelection(categoryPosition)
            }
            Glide.with(this).load(it.imageUrl).into(binding.imageViewProductPreview)
        }
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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

            if (name.isEmpty() || price.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (imageUri != null) {
                FirebaseService.uploadProductImage(imageUri!!) { success, downloadUrl ->
                    if (success && downloadUrl != null) {
                        updateProductData(name, price, category, downloadUrl)
                    } else {
                        Toast.makeText(this, "Error al subir la nueva imagen.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                updateProductData(name, price, category, currentProduct!!.imageUrl)
            }
        }
    }

    private fun updateProductData(name: String, price: String, category: String, imageUrl: String) {
        val updatedData = mapOf(
            "name" to name,
            "price" to "$${price}",
            "category" to category,
            "imageUrl" to imageUrl
        )

        FirebaseService.updateProduct(productId!!, updatedData) { success, error ->
            if (success) {
                Toast.makeText(this, "Producto actualizado con éxito", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ManageProductsActivity::class.java)
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
