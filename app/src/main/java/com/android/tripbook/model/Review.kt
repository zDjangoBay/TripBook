package com.android.tripbook.model

data class Review(
    val tripId: Int,
    val username: String,
    val comment: String,
    val images: List<String>,
    val votes: Int = 0 // Add votes for upvote/downvote
)
