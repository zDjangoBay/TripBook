package com.android.tripbook.data.models

data class CarRental(
    val id: String,
    val model: String, 
    val price: String,
    val imageUrl: Int // Drawable resource ID for the image
)