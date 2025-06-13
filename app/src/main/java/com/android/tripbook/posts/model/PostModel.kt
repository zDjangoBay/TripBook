package com.android.tripbook.posts.model

import java.time.Instant

data class PostModel(
    val id: String,
    val userId: String,
    val username: String,
    val title: String,
    val description: String,
    val content: String,
    val author: String,
    val location: Location,
    val images: List<ImageModel>,
    val categories: List<Category>,
    val tags: List<TagModel>,
    val hashtags: List<String>,
    val timestamp: Instant,
    val isVerified: Boolean,
    val likes: Set<String>,
    val comments: List<Comment>,
    val userReaction: String? = null
) {
    companion object {
        fun mock(id: String): PostModel {
            return PostModel(
                id = id,
                userId = "user_$id",
                username = "User $id",
                title = "Sample Title $id",
                description = "Sample description for post $id",
                content = "Sample content for post $id",
                author = "Author $id",
                location = Location(
                    name = "Sample Place",
                    city = "Sample City",
                    country = "Sample Country",
                    coordinates = Coordinates(0.0, 0.0)
                ),
                images = listOf(ImageModel(uri = "https://example.com/image.jpg", isUploaded = true)),
                categories = listOf(Category.NATURE),
                tags = listOf(TagModel(id = "tag1", name = "SampleTag", category = Category.NATURE, isTrending = false)),
                hashtags = listOf("#sample"),
                timestamp = Instant.now(),
                isVerified = false,
                likes = emptySet(),
                comments = emptyList(),
                userReaction = null
            )
        }
    }
}