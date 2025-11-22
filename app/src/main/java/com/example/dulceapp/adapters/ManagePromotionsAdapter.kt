package com.example.dulceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dulceapp.databinding.ItemManageProductBinding
import com.example.dulceapp.entities.Promotion
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ManagePromotionsAdapter(
    private val onEditClick: (Promotion) -> Unit, // Listener para editar
    private val onRemoveClick: (Promotion) -> Unit
) : ListAdapter<Promotion, ManagePromotionsAdapter.ManagePromotionViewHolder>(PromotionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManagePromotionViewHolder {
        val binding = ItemManageProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ManagePromotionViewHolder(binding, onEditClick, onRemoveClick)
    }

    override fun onBindViewHolder(holder: ManagePromotionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ManagePromotionViewHolder(
        private val binding: ItemManageProductBinding,
        private val onEditClick: (Promotion) -> Unit,
        private val onRemoveClick: (Promotion) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(promotion: Promotion) {
            binding.textViewProductName.text = promotion.name

            Glide.with(binding.root.context)
                .load(promotion.imageUrl)
                .into(binding.imageViewProduct)

            // Habilitamos el botón de editar
            binding.buttonEditProduct.text = "Editar"
            binding.buttonEditProduct.isEnabled = true
            binding.buttonEditProduct.setOnClickListener {
                onEditClick(promotion)
            }

            // Mantenemos la lógica de eliminar
            binding.buttonDeleteProduct.text = "Eliminar"
            binding.buttonDeleteProduct.setOnClickListener {
                 MaterialAlertDialogBuilder(binding.root.context)
                    .setTitle("Eliminar Promoción")
                    .setMessage("¿Estás seguro de que quieres eliminar la promoción \"${promotion.name}\"?")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Eliminar") { _, _ ->
                        onRemoveClick(promotion)
                    }
                    .show()
            }
        }
    }
}

class PromotionDiffCallback : DiffUtil.ItemCallback<Promotion>() {
    override fun areItemsTheSame(oldItem: Promotion, newItem: Promotion): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Promotion, newItem: Promotion): Boolean {
        return oldItem == newItem
    }
}
