package com.android.posts.model

import kotlinx.seriaization.*

@Serializable
data class CreatePostRequest(
    val PostAuthor_id:String,
    val PostAuthor_username:String?,
    val value: String?,
    val Post_mediasUrl: List<String>? = emptyList(),
    val Localisation: String?,
    val Posttype: PostType = PostType.ORIGINAL,// We assume by default that
    val parentPostId: String? = null, // Requis si Posttype est REPLY
    val repostedPostId: String? = null, // Requis si Posttype est REPOST
    val Hashtags: List<String>? = emptyList(),
    val mention: List<String>? = emptyList()
    val visibility: PostVisibility = PostVisibility.PUBLIC
)
