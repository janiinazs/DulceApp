package com.example.dulceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dulceapp.databinding.ItemProductBinding
import com.example.dulceapp.entities.Product

// --- ADAPTADOR MODIFICADO PARA EL MODO INVITADO ---
class ProductAdapter(
    private val onAddToCartClick: (product: Product, quantity: Int) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding, onAddToCartClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(
        private val binding: ItemProductBinding,
        private val onAddToCartClick: (product: Product, quantity: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.textViewProductName.text = product.name
            binding.textViewProductPrice.text = product.price

            Glide.with(binding.root.context)
                .load(product.imageUrl)
                .into(binding.imageViewProduct)

            binding.buttonIncreaseQuantity.setOnClickListener {
                var quantity = binding.textViewQuantity.text.toString().toInt()
                quantity++
                binding.textViewQuantity.text = quantity.toString()
            }

            binding.buttonDecreaseQuantity.setOnClickListener {
                var quantity = binding.textViewQuantity.text.toString().toInt()
                if (quantity > 1) {
                    quantity--
                    binding.textViewQuantity.text = quantity.toString()
                }
            }

            binding.buttonAddToCart.setOnClickListener {
                val quantity = binding.textViewQuantity.text.toString().toInt()
                onAddToCartClick(product, quantity) // Se delega la acción a la actividad
            }
        }
    }
}

class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}
