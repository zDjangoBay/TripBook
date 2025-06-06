package com.android.tripbook.Model

data class TrainCompany(
    val id: Int,
    val name: String,
    val logoRes: Int? = null,
    val logoUrl: String? = null,
    val description: String,
    val rating: Float,
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
    val popularTimes: String
)