package com.android.tripbook.posts.model

import com.android.tripbook.data.model.TravelLocation 
import com.android.tripbook.data.model.Comment 
import java.time.Instant
import java.util.UUID 

data class PostModel(
    val id: String = UUID.randomUUID().toString(), 
    val userId: String,
    val username: String,
    val userAvatar: String? = null, 
    val isVerified: Boolean = false, 
    val title: String,
    val description: String,
    val images: List<ImageModel> = emptyList(), 
    val location: TravelLocation, 
    val tags: List<TagModel> = emptyList(), 
    val hashtags: List<String> = emptyList(), 
    val createdAt: Instant = Instant.now(), 
    val lastEditedAt: Instant? = null,
    val visibility: PostVisibility,
    val collaborators: List<UserMinimal>? = emptyList(), 
    val isEphemeral: Boolean = false, 
    val ephemeralDurationMillis: Long? = null, 
    val likes: List<String> = emptyList(), 
    val comments: List<Comment> = emptyList() 
)

enum class PostVisibility {
    PUBLIC, FRIENDS, PRIVATE
}

data class UserMinimal(
    val id: String,
    val username: String
)
