package com.android.tripbook

data class Post(
    val id: Int,
    val userId: Int,
    val content: String,
    val imageUrl: String?,
    val createdAt: String,
    val likeCount: Int,
    val commentCount: Int,
    val shareCount: Int,
     val privacy: String = "Public",
    val user: User?, // Relation avec l'auteur
    var isLikedByUser: Boolean = false // Ajout pour suivre l'état du like
)