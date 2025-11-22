package com.example.dulceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dulceapp.databinding.ItemManageProductBinding
import com.example.dulceapp.entities.Product
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ManageProductAdapter(
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : ListAdapter<Product, ManageProductAdapter.ManageProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageProductViewHolder {
        val binding = ItemManageProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ManageProductViewHolder(binding, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: ManageProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ManageProductViewHolder(
        private val binding: ItemManageProductBinding,
        private val onEditClick: (Product) -> Unit,
        private val onDeleteClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.textViewProductName.text = product.name

            Glide.with(binding.root.context)
                .load(product.imageUrl)
                .into(binding.imageViewProduct)


            binding.buttonEditProduct.setOnClickListener {
                onEditClick(product)
            }

            // --- LÓGICA DEL BOTÓN ELIMINAR ---
            binding.buttonDeleteProduct.setOnClickListener {
                MaterialAlertDialogBuilder(binding.root.context)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Estás seguro de que quieres eliminar el producto \"${product.name}\"?")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Eliminar") { _, _ ->
                        onDeleteClick(product)
                    }
                    .show()
            }
        }
    }
}
