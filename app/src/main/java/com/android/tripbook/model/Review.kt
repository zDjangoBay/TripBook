package com.android.tripbook.Model

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
    var likeCount: Int = 0,
    val votes: Int = 0 // Add votes for upvote/downvote
)
