package com.android.tripbook.posts.model

data class Post(
    val id: String,
    val title: String,
    val content: String,
    val images: List<ImageModel> = emptyList()
)
