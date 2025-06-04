package com.android.tripbook.model

data class Review(
    val id: Int,
    val tripId: Int,
    val username: String,
    val userAvatar: String,
    val rating: Float, // 1-5 stars
    val comment: String,
    val date: String,
    val images: List<String> = emptyList()
)
    val username: String,
    val rating: Int,
    val comment: String,
    val images: List<String>,
    var isLiked: Boolean = false,
    var isFlagged: Boolean = false,
    var likeCount: Int = 0

)
>>>>>>> d90952d8704d10f2091d5b680619905aba83bd29
