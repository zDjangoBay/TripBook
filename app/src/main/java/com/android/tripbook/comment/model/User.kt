package com.android.tripbook.comment.model

data class User(
    val id: String,
    val username: String,
    val profileImageUrl: String? = null // Nullable for fallback
)
