package com.android.tripbook.data

import com.android.tripbook.data.model.Comment
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for comment-related operations
 */
interface CommentRepository {
    /**
     * Add a new comment to a post
     */
    suspend fun addComment(postId: String, content: String): Comment
    
    /**
     * Get all comments for a post
     */
    fun getCommentsForPost(postId: String): Flow<List<Comment>>
    
    /**
     * Delete a comment
     */
    suspend fun deleteComment(commentId: String): Boolean
    
    /**
     * Update a comment's content
     */
    suspend fun updateComment(commentId: String, newContent: String): Boolean
    
    /**
     * Get a comment by ID
     */
    suspend fun getComment(commentId: String): Comment
    
    /**
     * Get all comments by the current user
     */
    fun getUserComments(): Flow<List<Comment>>
}
