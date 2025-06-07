package com.android.posts.model

import java.time.LocalDateTime
import java.util.UUID
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

// @Serializable
data class Post(
    val Post_id: String = UUID.randomUUID().toString(),
    val PostAuthor_id: String,
    val PostAuthor_username: String,
    val PostAuthorProfileUrl: String?,
    val value: String?, // Texte du post (peut être null si media-only)
    val Post_mediasUrl: List<String> = emptyList(),
    val Localisation: String?,
    val Posttype: PostType = PostType.ORIGINAL,
    val parentPostId: String? = null, // ID du post parent si c'est une réponse
    val repostedPostId: String? = null, // ID du post original si c'est un repost
    var likesCount: Int = 0, // Mutable pour être incrémenté/décrémenté
    var repostCount: Int = 0, // Mutable
    var repliesCount: Int = 0, // Mutable
    val Hashtags: List<String> = emptyList(), // Corrigé de Hastags
    val mention: List<String> = emptyList(),
    var reportCount: Int = 0, // Mutable
    val CreatedAt: LocalDateTime,
    val visibility: PostVisibility = PostVisibility.PUBLIC,
    var isDeleted: Boolean = false,

)
