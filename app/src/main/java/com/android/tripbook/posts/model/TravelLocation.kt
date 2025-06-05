package com.android.tripbook.posts.model

data class TravelLocation(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String?,
    val imageUrl: String?
)