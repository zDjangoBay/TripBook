package com.android.tripbook.posts.model

import java.time.Instant

data class PostModel(
    val id: String,
    val userId: String,
    val username: String,
    val userAvatar: String?,
    val isVerified: Boolean,
    val title: String,
    val description: String,
    val images: List<ImageModel>,
    val location: Any,
    val tags: List<TagModel>,
    val createdAt: Instant,
    val lastEditedAt: Instant?,
    val visibility: PostVisibility,
    val collaborators: List<UserMinimal>?,
    val isEphemeral: Boolean,
    val ephemeralDurationMillis: Long?,
    val likes: List<String>,
    val comments: List<Comment>
) {
    val hashtags: Any
        get() {
            TODO()
        }
}

enum class PostVisibility {
    PUBLIC, FRIENDS, PRIVATE
}

data class UserMinimal(
    val id: String,
    val username: String
)