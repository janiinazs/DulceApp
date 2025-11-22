package com.example.dulceapp.entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class OrderItem(
    val name: String = "",
    val price: String = "",
    val quantity: Int = 0,
    val imageUrl: String = ""
)

data class Order(
    @DocumentId val id: String = "",
    val customerName: String = "",
    val address: String = "",
    val phone: String = "",
    val total: String = "",
    val paymentMethod: String = "",
    val createdAt: Timestamp? = null,
    val items: List<OrderItem> = emptyList(),
    val status: String = "pendiente",
    val deliveryDate: Timestamp? = null
)
