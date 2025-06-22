package com.android.tripbook.ui.components.comment_libby.model

/**
 * Data model for comments
 * Created by Libby
 */
data class Comment(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val text: String,
    val timestamp: String,
    val likes: Int = 0,
    val replies: List<Comment> = emptyList()
)
