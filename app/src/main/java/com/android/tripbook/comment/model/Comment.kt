

// File: com/android/tripbook/comment/model/Comment.kt
package com.android.tripbook.comment.model

data class Comment(
    val id: String,
    val userId: String,
    val username: String,
    val avatarUrl: String?,
    val text: String,
    val timestamp: Long
)

