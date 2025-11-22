package com.example.dulceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dulceapp.databinding.ItemCartBinding
import com.example.dulceapp.services.FirebaseService
import com.google.android.material.dialog.MaterialAlertDialogBuilder

data class CartItem(
    val name: String = "",
    val price: String = "",
    val quantity: Int = 0,
    val imageUrl: String = ""
)

class CartAdapter(private val onDelete: () -> Unit) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem) {
            binding.textViewCartItemName.text = cartItem.name
            binding.textViewCartItemPrice.text = cartItem.price
            binding.textViewCartItemQuantity.text = "Cantidad: ${cartItem.quantity}"
            Glide.with(binding.root.context).load(cartItem.imageUrl).into(binding.imageViewCartItem)


            binding.buttonDeleteFromCart.setOnClickListener {
                MaterialAlertDialogBuilder(binding.root.context)
                    .setTitle("Eliminar del carrito")
                    .setMessage("¿Estás seguro de que quieres eliminar \"${cartItem.name}\" del carrito?")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Eliminar") { _, _ ->
                        FirebaseService.removeProductFromCart(cartItem.name) { _, _ ->
                            onDelete()
                        }
                    }
                    .show()
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}
