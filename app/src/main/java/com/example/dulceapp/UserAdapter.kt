package com.example.dulceapp 

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dulceapp.databinding.ItemUsuarioBinding


class UserAdapter : ListAdapter<User, UserAdapter.UserViewHolder>(DiffCallback) {

    // El ViewHolder contiene la vista de cada fila (item_usuario.xml)
    inner class UserViewHolder(val binding: ItemUsuarioBinding) : RecyclerView.ViewHolder(binding.root)

    // Crea una nueva fila cuando es necesario
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUsuarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    // Rellena la fila con los datos del usuario correspondiente
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = getItem(position)
        holder.binding.textViewUsername.text = currentUser.username
        holder.binding.textViewEmail.text = currentUser.email
    }

    companion object DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
