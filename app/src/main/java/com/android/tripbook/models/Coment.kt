package com.android.tripbook

data class Comment(
    val id: Int,
    val userId: Int,
    val postId: Int,
    val parentCommentId: Int?,
    val content: String,
    val createdAt: String,
    val user: User?, // Relation avec l'auteur
    val likeCount: Int
)