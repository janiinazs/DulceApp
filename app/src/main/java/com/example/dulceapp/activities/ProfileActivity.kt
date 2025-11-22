package com.example.dulceapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.dulceapp.adapters.CartAdapter
import com.example.dulceapp.adapters.CartItem
import com.example.dulceapp.databinding.ActivityProfileBinding
import com.example.dulceapp.services.FirebaseService

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarProfile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
        loadCartData()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter { 
            loadCartData() 
        }
        binding.recyclerViewCartItems.apply {
            layoutManager = LinearLayoutManager(this@ProfileActivity)
            adapter = cartAdapter
        }
    }

    private fun setupButtons() {
        binding.buttonCheckout.setOnClickListener {
            if (cartAdapter.itemCount == 0) {
                Toast.makeText(this, "Debes agregar productos al carrito para confirmar un pedido", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, CheckoutActivity::class.java)
                intent.putExtra("TOTAL_PRICE", binding.textViewTotalPrice.text.toString())
                startActivity(intent)
            }
        }

        binding.buttonLogout.setOnClickListener {
            FirebaseService.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadUserData() {
        FirebaseService.getCurrentUser { user ->
            if (user != null) {
                binding.textViewUserName.text = user.username
                binding.textViewUserEmail.text = user.email
                

                if (user.profileImageUrl.isNotEmpty()) {
                    Glide.with(this)
                        .load(user.profileImageUrl)
                        .into(binding.imageViewProfileImage)
                }
            } else {
                binding.textViewUserName.text = "Usuario no encontrado"
                binding.textViewUserEmail.text = ""
            }
        }
    }

    private fun loadCartData() {
        FirebaseService.getCartItems { cartItems, _ ->
            if (cartItems != null) {
                cartAdapter.submitList(cartItems)
                calculateAndShowTotal(cartItems)
            } else {
                cartAdapter.submitList(emptyList())
                calculateAndShowTotal(emptyList())
            }
        }
    }

    private fun calculateAndShowTotal(cartItems: List<CartItem>) {
        val total = cartItems.sumOf { item ->
            (item.price.replace("$", "").toDoubleOrNull() ?: 0.0) * item.quantity
        }
        binding.textViewTotalPrice.text = "Total: $${String.format("%.2f", total)}"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
