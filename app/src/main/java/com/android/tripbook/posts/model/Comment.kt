package com.android.tripbook.posts.model

import java.time.Instant

data class Comment(
    val id: String,
    val userId: String,
    val username: String,
    val userAvatar: String? = null,
    val content: String,
    val timestamp: Instant = Instant.now(),
    val likes: Set<String> = emptySet(),
    val replies: List<Comment> = emptyList(),
    val parentCommentId: String? = null,
    val isEdited: Boolean = false,
    val editedAt: Instant? = null
)
