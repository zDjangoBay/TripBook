package com.android.tripbook.model

data class Review(
    val id: Int,
    val tripId: Int,
    val userName: String,
    val userAvatar: String,
    val rating: Float, // 1-5 stars
    val comment: String,
    val date: String,
    val images: List<String> = emptyList()
)