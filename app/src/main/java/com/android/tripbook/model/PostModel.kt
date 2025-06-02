package com.android.tripbook.posts.model

import java.time.Instant
import java.util.UUID

data class PostModel(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val username: String,
    val userAvatar: String? = null,
    val title: String,
    val description: String,
    val location: Location,
    val images: List<ImageModel> = emptyList(),
    val categories: List<Category> = emptyList(),
    val tags: List<TagModel> = emptyList(),
    val hashtags: List<String> = emptyList(),
    val timestamp: Instant = Instant.now(),
    val isVerified: Boolean = false,
    val likes: Set<String> = emptySet(),
    val comments: List<Comment> = emptyList()
)
