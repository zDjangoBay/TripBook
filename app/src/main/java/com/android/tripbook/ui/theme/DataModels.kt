package com.android.tripbook

data class ImageModel(val uri: String)

data class TagModel(val name: String)

data class PostModel(
    val title: String,
    val description: String,
    val location: String,
    val createdAt: Long,
    val images: List<ImageModel>,
    val tags: List<TagModel>,
    val hashtags: List<String>
)