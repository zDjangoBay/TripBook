package com.android.tripbook.model

import java.util.UUID

data class CommentReaction(
    val id: String = UUID.randomUUID().toString(),
    val commentId: String,
    val emoji: String,
    val username: String,
    val timestamp: Long = System.currentTimeMillis()
)
