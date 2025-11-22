package com.example.dulceapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dulceapp.adapters.ManageProductAdapter
import com.example.dulceapp.databinding.ActivityManageProductsBinding
import com.example.dulceapp.entities.Product
import com.example.dulceapp.services.FirebaseService

class ManageProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageProductsBinding
    private lateinit var manageProductAdapter: ManageProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarManageProducts)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    private fun setupRecyclerView() {
        manageProductAdapter = ManageProductAdapter(
            onEditClick = { product ->
                // --- LÓGICA RESTAURADA PARA ABRIR LA PANTALLA DE EDICIÓN ---
                val intent = Intent(this, EditProductActivity::class.java)
                // Es crucial pasar el ID o el objeto completo para que la otra pantalla sepa qué editar
                intent.putExtra("PRODUCT_ID", product.id)
                startActivity(intent)
            },
            onDeleteClick = { product ->
                deleteProduct(product)
            }
        )
        binding.recyclerViewManageProducts.apply {
            layoutManager = LinearLayoutManager(this@ManageProductsActivity)
            adapter = manageProductAdapter
        }
    }

    private fun loadProducts() {
        FirebaseService.fetchAllProducts { products, errorMessage ->
            if (products != null) {
                manageProductAdapter.submitList(products)
            } else {
                Toast.makeText(this, "Error al cargar productos: ${errorMessage ?: ""}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteProduct(product: Product) {
        FirebaseService.deleteProduct(product.id) { success, errorMessage ->
            if (success) {
                Toast.makeText(this, "Producto \"${product.name}\" eliminado", Toast.LENGTH_SHORT).show()
                loadProducts() // Recargar la lista
            } else {
                Toast.makeText(this, "Error al eliminar: ${errorMessage ?: ""}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
