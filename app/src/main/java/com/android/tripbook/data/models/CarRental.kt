package com.android.tripbook.data.models

// Data class for a Car Rental item
data class CarRental(
    val id: String,
    val model: String, // Changed from 'name' for clarity for cars
    val price: String,
    val imageUrl: Int // Drawable resource ID for the image
)