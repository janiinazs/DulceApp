package com.example.dulceapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dulceapp.databinding.ActivityAdminHomeBinding
import com.example.dulceapp.services.FirebaseService

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonManageUsers.setOnClickListener {
            val intent = Intent(this, UserListActivity::class.java)
            startActivity(intent)
        }

        binding.buttonViewOrders.setOnClickListener {
            val intent = Intent(this, OrderListActivity::class.java)
            startActivity(intent)
        }

        binding.buttonAddProduct.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }

        binding.buttonManageProducts.setOnClickListener {
            val intent = Intent(this, ManageProductsActivity::class.java)
            startActivity(intent)
        }

        // --- LÓGICA DE BOTONES DE PROMOCIONES ---
        binding.buttonAddPromotion.setOnClickListener {
            val intent = Intent(this, AddPromotionActivity::class.java)
            startActivity(intent)
        }

        binding.buttonManagePromotions.setOnClickListener {
            val intent = Intent(this, ManagePromotionsActivity::class.java)
            startActivity(intent)
        }

        binding.buttonLogout.setOnClickListener {
            FirebaseService.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
