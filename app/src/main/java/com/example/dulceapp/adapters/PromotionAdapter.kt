package com.example.dulceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dulceapp.databinding.ItemPromotionBinding
import com.example.dulceapp.entities.Promotion

class PromotionAdapter(
    private var promotions: List<Promotion>,
    private val onAddToCartClick: (Promotion) -> Unit // Añadir listener para el click
) : RecyclerView.Adapter<PromotionAdapter.PromotionViewHolder>() {

    fun updatePromotions(newPromotions: List<Promotion>) {
        promotions = newPromotions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val binding = ItemPromotionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PromotionViewHolder(binding, onAddToCartClick) // Pasar el listener
    }

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        holder.bind(promotions[position])
    }

    override fun getItemCount(): Int = promotions.size

    class PromotionViewHolder(
        private val binding: ItemPromotionBinding,
        private val onAddToCartClick: (Promotion) -> Unit // Añadir listener aquí también
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(promotion: Promotion) {
            binding.textViewPromotionName.text = promotion.name
            binding.textViewPromotionPrice.text = promotion.price
            Glide.with(binding.root.context)
                .load(promotion.imageUrl)
                .into(binding.imageViewPromotion)

            // Usar el listener que pasamos
            binding.buttonAddToCart.setOnClickListener {
                onAddToCartClick(promotion)
            }
        }
    }
}
