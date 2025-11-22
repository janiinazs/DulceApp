package com.example.dulceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dulceapp.databinding.ItemUserBinding
import com.example.dulceapp.entities.User

class UserAdapter(
    private val onDeleteClicked: (User) -> Unit,
    private val onLocationClicked: (User) -> Unit // MANEJADOR PARA UBICACIÓN
) : ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user, onDeleteClicked, onLocationClicked)
    }

    class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User, onDeleteClicked: (User) -> Unit, onLocationClicked: (User) -> Unit) {
            binding.textViewUserNameItem.text = user.username
            binding.textViewUserEmailItem.text = user.email

            binding.buttonDeleteUser.setOnClickListener { 
                onDeleteClicked(user)
            }

            // CONECTAR EL BOTÓN DE UBICACIÓN
            binding.buttonLocation.setOnClickListener { 
                onLocationClicked(user)
            }
        }
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.email == newItem.email
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}
