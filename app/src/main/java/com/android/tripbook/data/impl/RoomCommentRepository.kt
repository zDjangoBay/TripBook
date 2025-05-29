package com.android.tripbook.data.impl

import com.android.tripbook.data.CommentRepository
import com.android.tripbook.data.local.dao.CommentDao
import com.android.tripbook.data.local.dao.PostDao
import com.android.tripbook.data.local.entity.CommentEntity
import com.android.tripbook.data.local.mapper.CommentMapper
import com.android.tripbook.data.model.Comment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.NoSuchElementException
import java.util.UUID

/**
 * Room-based implementation of CommentRepository
 */
class RoomCommentRepository(
    private val commentDao: CommentDao,
    private val postDao: PostDao,
    private val currentUserId: String = "user1" // In a real app, this would come from auth
) : CommentRepository {

    override suspend fun addComment(postId: String, content: String): Comment {
        val commentId = UUID.randomUUID().toString()
        val now = Date()
        
        val commentEntity = CommentEntity(
            id = commentId,
            postId = postId,
            userId = currentUserId,
            content = content,
            createdAt = now,
            updatedAt = now
        )
        
        commentDao.insertComment(commentEntity)
        
        // Update comment count on the post
        postDao.updateCommentsCount(postId, 1)
        
        return CommentMapper.fromEntity(commentEntity)
    }

    override fun getCommentsForPost(postId: String): Flow<List<Comment>> {
        return commentDao.getCommentsByPostId(postId).map { entities ->
            CommentMapper.fromEntities(entities)
        }
    }

    override suspend fun deleteComment(commentId: String): Boolean {
        val comment = commentDao.getCommentById(commentId) ?: return false
        
        // Delete the comment
        val result = commentDao.deleteCommentById(commentId)
        
        if (result > 0) {
            // Update comment count on the post
            postDao.updateCommentsCount(comment.postId, -1)
            return true
        }
        
        return false
    }

    override suspend fun updateComment(commentId: String, newContent: String): Boolean {
        val commentEntity = commentDao.getCommentById(commentId) 
            ?: throw NoSuchElementException("Comment not found with id: $commentId")
        
        // Check if user is the author of the comment
        if (commentEntity.userId != currentUserId) {
            return false
        }
        
        val updatedComment = commentEntity.copy(
            content = newContent,
            updatedAt = Date()
        )
        
        val result = commentDao.updateComment(updatedComment)
        return result > 0
    }

    override suspend fun getComment(commentId: String): Comment {
        val commentEntity = commentDao.getCommentById(commentId) 
            ?: throw NoSuchElementException("Comment not found with id: $commentId")
        
        return CommentMapper.fromEntity(commentEntity)
    }

    override fun getUserComments(): Flow<List<Comment>> {
        return commentDao.getCommentsByUserId(currentUserId).map { entities ->
            CommentMapper.fromEntities(entities)
        }
    }
}
