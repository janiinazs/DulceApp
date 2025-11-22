package com.example.dulceapp.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dulceapp.adapters.ProductAdapter
import com.example.dulceapp.databinding.ActivityCategoryBinding
import com.example.dulceapp.entities.Product
import com.example.dulceapp.services.FirebaseService
import com.example.dulceapp.utils.SessionManager

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val category = intent.getStringExtra("CATEGORY_NAME") ?: ""


        binding.toolbarCategory.title = category
        setSupportActionBar(binding.toolbarCategory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
        loadProducts(category)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product, quantity ->
            onAddToCart(product, quantity)
        }

        binding.recyclerViewCategoryProducts.apply {
            layoutManager = GridLayoutManager(this@CategoryActivity, 2)
            adapter = productAdapter
        }
    }

    private fun onAddToCart(product: Product, quantity: Int) {
        if (SessionManager.isGuest(this)) {
            showLoginPromptDialog()
        } else {
            FirebaseService.addProductToCart(product, quantity) { success, errorMessage ->
                if (success) {
                    Toast.makeText(this, "¡Añadido al carrito!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error: ${errorMessage ?: "No se pudo añadir"}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoginPromptDialog() {
        AlertDialog.Builder(this)
            .setTitle("Modo Invitado")
            .setMessage("Para añadir productos al carrito, por favor, inicia sesión o crea una cuenta.")
            .setPositiveButton("Iniciar Sesión") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("Registrarse") { _, _ ->
                val intent = Intent(this, RegistroActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNeutralButton("Cancelar", null)
            .show()
    }

    private fun loadProducts(category: String) {
        FirebaseService.fetchProductsByCategory(category) { products, errorMessage ->
            if (products != null) {
                productAdapter.submitList(products)
            } else {
                Toast.makeText(this, "Error al cargar productos: ${errorMessage ?: ""}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
