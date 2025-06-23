// package com.tripbook.posts.data

class PostRepository {
    // Simulate network call
    suspend fun submitPost(content: String): Result<Unit> {
        return if (content.isNotBlank()) {
            kotlinx.coroutines.delay(2000) // Simulate loading
            Result.success(Unit)
        } else {
            Result.failure(Exception("Post content cannot be empty"))
        }
    }
}



