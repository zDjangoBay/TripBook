package com.android.tripbook.comment.model

data class Comment(
    val id: String,
    val userId: String,
    val username: String,
    val avatarUrl: String?,
    val text: String,
    val timestamp: Long,
    val likes: Int = 0,
    val isDeleted: Boolean = false,
    val replies: List<Reply> = emptyList(),
    val imageUri: String? = null  // <-- Added image URI here
)

data class Reply(
    val id: String,
    val userId: String,
    val username: String,
    val avatarUrl: String?,
    val text: String,
    val timestamp: Long,
    val likes: Int = 0
)
