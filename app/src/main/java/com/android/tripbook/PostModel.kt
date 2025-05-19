package com.android.tripbook.model

data class PostModel(
    val title: String,
    val description: String,
    val imageUrl: String,
    val hashtags: List<String>
)
