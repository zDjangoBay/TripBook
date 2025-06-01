package com.android.tripbook.data

import com.android.tripbook.data.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for post-related operations
 */
interface PostRepository {
    /**
     * Create a new post
     */
    suspend fun createPost(
        title: String, 
        description: String, 
        location: String, 
        images: List<String>, 
        tags: List<String>,
        agencyId: String? = null
    ): String
    
    /**
     * Get a post by ID
     */
    suspend fun getPost(postId: String): Post
    
    /**
     * Get all posts for the current user
     */
    suspend fun getUserPosts(): List<Post>
    
    /**
     * Get posts for the feed, with pagination support
     */
    fun getFeedPosts(limit: Int = 20, offset: Int = 0): Flow<List<Post>>
    
    /**
     * Search for posts by title, description, location or tags
     */
    fun searchPosts(query: String): Flow<List<Post>>
    
    /**
     * Update an existing post
     */
    suspend fun updatePost(
        postId: String,
        title: String, 
        description: String, 
        location: String, 
        images: List<String>, 
        tags: List<String>,
        agencyId: String? = null
    ): Boolean
    
    /**
     * Delete a post
     */
    suspend fun deletePost(postId: String): Boolean
    
    /**
     * Like or unlike a post
     */
    suspend fun toggleLikePost(postId: String): Boolean
    
    /**
     * Add a comment to a post
     */
    suspend fun addComment(postId: String, comment: String): Boolean
}
