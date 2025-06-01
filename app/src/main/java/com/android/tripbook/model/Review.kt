package com.android.tripbook.model

data class Review(
    val tripId: Int,
    val username: String,
    val comment: String,
    val images: List<String>,
    var isLiked: Boolean = false,
    var isFlagged: Boolean = false
)
