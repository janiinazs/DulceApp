package com.example.dulceapp.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dulceapp.databinding.ItemUsuarioBinding
import com.example.dulceapp.entities.User

class UserAdapter(private var userList: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // El ViewHolder contiene la vista de cada fila (item_usuario.xml)
    inner class UserViewHolder(val binding: ItemUsuarioBinding) : RecyclerView.ViewHolder(binding.root)

    // Crea una nueva fila cuando es necesario
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUsuarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    // Rellena la fila con los datos del usuario correspondiente
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.binding.textViewUsername.text = currentUser.username
        holder.binding.textViewEmail.text = currentUser.email
    }

    // Devuelve el total de elementos
    override fun getItemCount(): Int = userList.size

    // Función para actualizar la lista desde MainActivity
    fun updateData(newList: List<User>) {
        userList = newList
        notifyDataSetChanged() // Refresca el RecyclerView
    }
}