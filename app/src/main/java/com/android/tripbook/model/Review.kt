package com.android.tripbook.model

data class Review(
    val id: Int,
    val date: String,
    val tripId: Int,
    val username: String,
    val rating: Float,
    val comment: String,
    val images: List<String>,
    var isLiked: Boolean = false,
    var isFlagged: Boolean = false,
    var likeCount: Int = 0

)
