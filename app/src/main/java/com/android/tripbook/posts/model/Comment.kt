package com.android.tripbook.posts.model

import java.util.UUID

data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val username: String,
    val text: String,
    val replies: List<Comment> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)