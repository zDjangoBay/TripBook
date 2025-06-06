package com.android.tripbook.data.model


import java.time.Instant

data class Comment(
    val id: String,
    val postId: String,
    val userId: String,
    val username: String,
    val userAvatar: String?,
    val text: String,
    val timestamp: Instant,
    val replies: List<Comment> = emptyList() // Réponses imbriquées de type Comment canonique
)
