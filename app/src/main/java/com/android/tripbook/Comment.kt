package com.android.tripbook

// 3. DATA MODEL - Create new file: Comment.kt
data class Comment(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val author: String = "User"
)