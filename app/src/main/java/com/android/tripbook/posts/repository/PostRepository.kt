package com.android.tripbook.posts.repository

import com.android.tripbook.posts.model.PostModel
import com.android.tripbook.posts.model.Comment
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getAllPosts(): Flow<List<PostModel>>
    fun getPostById(id: String): Flow<PostModel?>
    suspend fun createPost(post: PostModel)
    suspend fun updatePost(post: PostModel)
    suspend fun deletePost(id: String)
    suspend fun toggleLike(postId: String, userId: String)
    suspend fun addComment(postId: String, comment: Comment)
    suspend fun addReply(postId: String, commentId: String, reply: Comment)
}
