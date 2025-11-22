package com.example.dulceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dulceapp.databinding.ItemAddPromotionBinding
import com.example.dulceapp.entities.Product

class AddPromotionAdapter(
    private val onAddClick: (Product) -> Unit
) : ListAdapter<Product, AddPromotionAdapter.AddPromotionViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddPromotionViewHolder {
        val binding = ItemAddPromotionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddPromotionViewHolder(binding, onAddClick)
    }

    override fun onBindViewHolder(holder: AddPromotionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AddPromotionViewHolder(
        private val binding: ItemAddPromotionBinding,
        private val onAddClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.textViewProductName.text = product.name
            binding.textViewProductPrice.text = product.price
            Glide.with(binding.root.context)
                .load(product.imageUrl)
                .into(binding.imageViewProduct)

            binding.buttonAddPromotion.setOnClickListener {
                onAddClick(product)
            }
        }
    }
}
