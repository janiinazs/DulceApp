package com.example.dulceapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dulceapp.adapters.ManagePromotionsAdapter
import com.example.dulceapp.databinding.ActivityManagePromotionsBinding
import com.example.dulceapp.entities.Promotion
import com.example.dulceapp.services.FirebaseService

class ManagePromotionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManagePromotionsBinding
    private lateinit var managePromotionsAdapter: ManagePromotionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagePromotionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarManagePromotions)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        loadPromotions()
    }

    private fun setupRecyclerView() {
        managePromotionsAdapter = ManagePromotionsAdapter(
            onEditClick = { promotion ->
                val intent = Intent(this, EditPromotionActivity::class.java)
                intent.putExtra("PROMOTION_EXTRA", promotion)
                startActivity(intent)
            },
            onRemoveClick = { promotion ->
                removePromotion(promotion.id)
            }
        )
        binding.recyclerViewManagePromotions.apply {
            layoutManager = LinearLayoutManager(this@ManagePromotionsActivity)
            adapter = managePromotionsAdapter
        }
    }

    private fun loadPromotions() {
        FirebaseService.fetchAllPromotions { promotions, errorMessage ->
            if (promotions != null) {
                managePromotionsAdapter.submitList(promotions)
            } else {
                Toast.makeText(this, "Error al cargar promociones: ${errorMessage ?: ""}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removePromotion(promotionId: String) {
        FirebaseService.deletePromotion(promotionId) { success, errorMessage ->
            if (success) {
                Toast.makeText(this, "Promoción eliminada con éxito", Toast.LENGTH_SHORT).show()
                loadPromotions() // Recargar la lista
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
