package com.android.tripbook.model

data class Trip(
    val id: String,
    val title: String,
    val location: String,
    val imageUrl: String,
    val rating: Double,
    val price: Double
)
