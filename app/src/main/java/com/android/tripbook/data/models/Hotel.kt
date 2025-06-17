package com.android.tripbook.data.models

data class Hotel(
    val id: String,
    val name: String,
    val location: String,
    val priceRange: String,
    val imageUrl: Int,
    val rating: Double,
    val availableDate: String,
    val description: String,
    // val galleryImages: List<Int> = emptyList()
)