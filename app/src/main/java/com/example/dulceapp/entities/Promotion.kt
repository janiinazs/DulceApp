package com.example.dulceapp.entities

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class Promotion(
    @get:Exclude var id: String = "",
    val name: String = "",
    val price: String = "",
    val imageUrl: String = ""
) : Parcelable
