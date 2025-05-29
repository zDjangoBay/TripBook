package com.android.tripbook.Model

data class Review(
    val tripId: Int,
    val username: String,
    val comment: String,
    val images: List<String>
)
