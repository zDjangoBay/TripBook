package com.android.tripbook.data.impl

import com.android.tripbook.data.PostRepository
import com.android.tripbook.data.local.dao.PostDao
import com.android.tripbook.data.local.dao.UserPostLikeDao
import com.android.tripbook.data.local.entity.PostEntity
import com.android.tripbook.data.local.entity.UserPostLikeEntity
import com.android.tripbook.data.local.mapper.PostMapper
import com.android.tripbook.data.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

/**
 * Implementation of PostRepository that uses Room database for storage
 */
class RoomPostRepository(
    private val postDao: PostDao,
    private val userPostLikeDao: UserPostLikeDao,
    private val currentUserId: String = "user1" // In a real app, this would come from auth
) : PostRepository {

    override suspend fun createPost(
        title: String,
        description: String,
        location: String,
        images: List<String>,
        tags: List<String>,
        agencyId: String?
    ): String {
        val now = Date()
        val postId = UUID.randomUUID().toString()
        
        val post = PostEntity(
            id = postId,
            userId = currentUserId,
            title = title,
            description = description,
            location = location,
            images = images,
            latitude = null, // Would come from location picker in real app
            longitude = null,
            tags = tags,
            agencyId = agencyId,
            likes = 0,
            comments = 0,
            createdAt = now,
            updatedAt = now
        )
        
        postDao.insertPost(post)
        return postId
    }

    override suspend fun getPost(postId: String): Post {
        val post = postDao.getPostById(postId) ?: throw NoSuchElementException("Post not found with id: $postId")
        return PostMapper.fromEntity(post)
    }    override suspend fun getUserPosts(): List<Post> {
        val posts = postDao.getPostsByUser(currentUserId).first()
        return PostMapper.fromEntities(posts)
    }
    
    override fun getFeedPosts(limit: Int, offset: Int): Flow<List<Post>> {
        return postDao.getFeedPosts(limit, offset).map { entities ->
            PostMapper.fromEntities(entities)
        }
    }
    
    override fun searchPosts(query: String): Flow<List<Post>> {
        return postDao.searchPostsByText(query).map { entities ->
            PostMapper.fromEntities(entities)
        }
    }

    override suspend fun updatePost(
        postId: String,
        title: String,
        description: String,
        location: String,
        images: List<String>,
        tags: List<String>,
        agencyId: String?
    ): Boolean {
        val existingPost = postDao.getPostById(postId) ?: return false
        
        val updatedPost = existingPost.copy(
            title = title,
            description = description,
            location = location,
            images = images,
            tags = tags,
            agencyId = agencyId,
            updatedAt = Date()
        )
        
        val rowsUpdated = postDao.updatePost(updatedPost)
        return rowsUpdated > 0
    }

    override suspend fun deletePost(postId: String): Boolean {
        val rowsDeleted = postDao.deletePostById(postId)
        return rowsDeleted > 0
    }

    override suspend fun toggleLikePost(postId: String): Boolean {
        val isLiked = userPostLikeDao.isPostLikedByUser(currentUserId, postId)
        
        if (isLiked) {
            // Unlike the post
            userPostLikeDao.deleteUserPostLike(currentUserId, postId)
            postDao.updateLikesCount(postId, -1)
        } else {
            // Like the post
            val userPostLike = UserPostLikeEntity(
                userId = currentUserId,
                postId = postId,
                createdAt = Date()
            )
            userPostLikeDao.insertUserPostLike(userPostLike)
            postDao.updateLikesCount(postId, 1)
        }
        
        return true
    }

    override suspend fun addComment(postId: String, comment: String): Boolean {
        // In a real implementation, we would create a comment entity here
        // For now, we'll just increment the comment count
        val rowsUpdated = postDao.updateCommentsCount(postId, 1)
        return rowsUpdated > 0
    }
}
