package com.android.tripbook.comment.model

data class Comment(
    val id: String,
    val userId: String,
    val username: String,
    val avatarUrl: String?,
    val text: String,
    val timestamp: Long,
    val likes: Int = 0,
    val replies: List<Reply> = emptyList(),
    val isDeleted: Boolean = false,
    val commentText: Nothing? = null
)

data class Reply(
    val id: String,
    val userId: String,
    val username: String,
    val avatarUrl: String?,
    val text: String,
    val timestamp: Long,
    val likes: Int = 0,
    val commentText: Nothing? = null
)
