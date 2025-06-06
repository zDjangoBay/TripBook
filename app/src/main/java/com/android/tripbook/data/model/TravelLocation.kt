package com.android.tripbook.data.model

data class TravelLocation(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String?,
    val imageUrl: String?
)