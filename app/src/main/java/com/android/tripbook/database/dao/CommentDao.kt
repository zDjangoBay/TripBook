package com.android.tripbook.database.dao

import androidx.room.*
import com.android.tripbook.database.entity.CommentEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Comment operations
 * Provides database operations for comments
 */
@Dao
interface CommentDao {
    
    /**
     * Get all comments as Flow
     */
    @Query("SELECT * FROM comments ORDER BY timestamp DESC")
    fun getAllComments(): Flow<List<CommentEntity>>
    
    /**
     * Get all comments as list (for one-time queries)
     */
    @Query("SELECT * FROM comments ORDER BY timestamp DESC")
    suspend fun getAllCommentsOnce(): List<CommentEntity>
    
    /**
     * Get comments for specific review
     */
    @Query("SELECT * FROM comments WHERE reviewId = :reviewId ORDER BY timestamp DESC")
    fun getCommentsForReview(reviewId: Int): Flow<List<CommentEntity>>
    
    /**
     * Get comments for specific review as list
     */
    @Query("SELECT * FROM comments WHERE reviewId = :reviewId ORDER BY timestamp DESC")
    suspend fun getCommentsForReviewOnce(reviewId: Int): List<CommentEntity>
    
    /**
     * Get comment by ID
     */
    @Query("SELECT * FROM comments WHERE id = :commentId")
    suspend fun getCommentById(commentId: String): CommentEntity?
    
    /**
     * Get comment by ID as Flow
     */
    @Query("SELECT * FROM comments WHERE id = :commentId")
    fun getCommentByIdFlow(commentId: String): Flow<CommentEntity?>
    
    /**
     * Get replies for a comment
     */
    @Query("SELECT * FROM comments WHERE parentId = :parentId ORDER BY timestamp ASC")
    fun getRepliesForComment(parentId: String): Flow<List<CommentEntity>>
    
    /**
     * Insert single comment
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)
    
    /**
     * Insert multiple comments
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)
    
    /**
     * Update comment
     */
    @Update
    suspend fun updateComment(comment: CommentEntity)
    
    /**
     * Delete comment
     */
    @Delete
    suspend fun deleteComment(comment: CommentEntity)
    
    /**
     * Delete comment by ID
     */
    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteCommentById(commentId: String)
    
    /**
     * Delete all comments for a review
     */
    @Query("DELETE FROM comments WHERE reviewId = :reviewId")
    suspend fun deleteCommentsForReview(reviewId: Int)
    
    /**
     * Delete all comments
     */
    @Query("DELETE FROM comments")
    suspend fun deleteAllComments()
    
    /**
     * Get comments count for review
     */
    @Query("SELECT COUNT(*) FROM comments WHERE reviewId = :reviewId")
    suspend fun getCommentsCountForReview(reviewId: Int): Int
}
