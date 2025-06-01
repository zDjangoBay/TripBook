package com.android.tripbook.model

data class ReviewComment(
    val id: Int,
    val tripId: Int,
    val username: String,
    val content: String,
    val timestamp: String,
    val imageUri: String? = null
)