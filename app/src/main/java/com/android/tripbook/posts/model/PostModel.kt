package com.android.tripbook.posts.model


import java.util.UUID

data class PostModel(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val username: String,
    val userAvatar: String,
    val title: String,
    val description: String,
    val location: Location,
    val images: List<ImageModel> = emptyList(),
    val categories: List<Category> = emptyList(),
    val tags: List<TagModel> = emptyList(),
    val hashtags: List<String> = emptyList(),
    val likes: List<String> = emptyList(),
    val comments: List<Comment> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class Category {
    ADVENTURE, CULTURE, FOOD, NATURE, URBAN, BEACH, MOUNTAINS, HISTORICAL
}