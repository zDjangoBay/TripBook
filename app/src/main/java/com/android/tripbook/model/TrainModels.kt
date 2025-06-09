package com.android.tripbook.model

data class TrainCompany(
    val id: Int,
    val name: String,
    val logoRes: Int? = null,
    val logoUrl: String? = null,
    val description: String,
    val rating: Float = 0f,           // Added rating field
    val totalRatings: Int = 0,         // Added total ratings field
    val priceRange: String
)

data class TrainDestination(
    val id: Int,
    val name: String,
    val imageRes: Int? = null,
    val imageUrl: String? = null,
    val description: String,
    val duration: String,
    val price: String,
    val distance: String,
    val popularTimes: String,
    val rating: Float = 0f,           // Added rating field
    val totalRatings: Int = 0        // Added total ratings field
)