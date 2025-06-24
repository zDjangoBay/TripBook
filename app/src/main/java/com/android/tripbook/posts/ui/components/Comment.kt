package com.android.tripbook.posts.model

import java.time.Instant

data class Comment(
    val id: String,
    val userId: String,
    val username: String,
    val text: String,
    val timestamp: Instant,
    val replies: List<Comment> = emptyList()
)