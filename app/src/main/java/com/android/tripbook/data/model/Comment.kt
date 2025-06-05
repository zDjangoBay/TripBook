package com.android.tripbook.data.model


data class Comment(
    val id: String,
    val postId: String,
    val userId: String,
    val content: String,
    val createdAt: Long
)