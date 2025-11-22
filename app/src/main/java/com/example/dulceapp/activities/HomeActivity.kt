package com.example.dulceapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dulceapp.R
import com.example.dulceapp.adapters.ProductAdapter
import com.example.dulceapp.adapters.PromotionAdapter
import com.example.dulceapp.databinding.ActivityHomeBinding
import com.example.dulceapp.entities.Product
import com.example.dulceapp.entities.Promotion
import com.example.dulceapp.services.FirebaseService
import com.example.dulceapp.utils.SessionManager

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var promotionAdapter: PromotionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        loadWelcomeMessage() // Cargar el mensaje de bienvenida
        setupRecyclerViews()
        setupCategoryButtons()
        setupBottomNavigation()

        loadPromotions()
        loadFeaturedProducts()
    }

    private fun loadWelcomeMessage() {
        if (SessionManager.isGuest(this)) {
            binding.textViewWelcome.text = "¡Bienvenido/a!"
            binding.textViewWelcomeSubtitle.text = "Descubre nuestros sabores"
        } else {
            FirebaseService.getCurrentUser { user ->
                if (user != null) {
                    binding.textViewWelcome.text = "Hola, ${user.username}"
                    binding.textViewWelcomeSubtitle.text = "¡Bienvenido/a!"
                } else {
                    binding.textViewWelcome.text = "¡Bienvenido/a!"
                    binding.textViewWelcomeSubtitle.text = "Descubre nuestros sabores"
                }
            }
        }
    }

    private fun setupRecyclerViews() {
        // Adaptador para Productos Destacados
        productAdapter = ProductAdapter { product, quantity ->
            onProductAddToCart(product, quantity)
        }
        binding.recyclerViewProducts.apply {
            layoutManager = GridLayoutManager(this@HomeActivity, 2)
            adapter = productAdapter
            isNestedScrollingEnabled = false
        }

        // Adaptador para Promociones
        promotionAdapter = PromotionAdapter(emptyList()) { promotion ->
            onPromotionAddToCart(promotion)
        }
        binding.recyclerViewPromotions.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = promotionAdapter
        }
    }

    private fun setupCategoryButtons() {
        binding.buttonTortas.setOnClickListener { openCategory("Tortas") }
        binding.buttonPostres.setOnClickListener { openCategory("Postres") }
        binding.buttonBocaditos.setOnClickListener { openCategory("Bocaditos") }
        binding.buttonBebidas.setOnClickListener { openCategory("Bebidas") }
    }

    private fun setupBottomNavigation() {
        // Marcar "Inicio" como seleccionado por defecto
        binding.bottomNavigation.selectedItemId = R.id.nav_home

        // Ocultar perfil para invitados
        if (SessionManager.isGuest(this)) {
            binding.bottomNavigation.menu.findItem(R.id.nav_profile).isVisible = false
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Ya estamos en Home, no hacer nada
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun openCategory(categoryName: String) {
        val intent = Intent(this, CategoryActivity::class.java)
        intent.putExtra("CATEGORY_NAME", categoryName)
        startActivity(intent)
    }

    private fun loadPromotions() {
        FirebaseService.fetchAllPromotions { promotions, _ ->
            if (promotions != null) {
                promotionAdapter.updatePromotions(promotions)
            }
        }
    }

    private fun loadFeaturedProducts() {
        FirebaseService.fetchAllProducts { products, _ ->
            if (products != null) {
                productAdapter.submitList(products.shuffled().take(6))
            }
        }
    }

    private fun onProductAddToCart(product: Product, quantity: Int) {
        if (SessionManager.isGuest(this)) {
            showLoginPromptDialog()
        } else {
            FirebaseService.addProductToCart(product, quantity) { success, errorMessage ->
                if (success) {
                    Toast.makeText(this, "¡Añadido al carrito!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error: ${errorMessage ?: ""}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onPromotionAddToCart(promotion: Promotion) {
        if (SessionManager.isGuest(this)) {
            showLoginPromptDialog()
        } else {
            FirebaseService.addProductToCart(promotion, 1) { success, errorMessage ->
                if (success) {
                    Toast.makeText(this, "¡Promoción añadida al carrito!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error: ${errorMessage ?: ""}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoginPromptDialog() {
        AlertDialog.Builder(this)
            .setTitle("Modo Invitado")
            .setMessage("Para realizar esta acción, por favor, inicia sesión o crea una cuenta.")
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
}
