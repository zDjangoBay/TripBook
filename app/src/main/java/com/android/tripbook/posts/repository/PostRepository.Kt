package com.android.tripbook.posts.repository

import com.android.tripbook.posts.model.Comment
import com.android.tripbook.posts.model.PostModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PostRepository {
    private val _posts = MutableStateFlow<List<PostModel>>(emptyList())
    
    fun getAllPosts(): Flow<List<PostModel>> = _posts.asStateFlow()
    
    suspend fun createPost(post: PostModel) {
        delay(1000) // Simulate network delay
        val currentPosts = _posts.value.toMutableList()
        currentPosts.add(0, post)
        _posts.value = currentPosts
    }
    
    suspend fun deletePost(postId: String) {
        delay(500)
        val currentPosts = _posts.value.toMutableList()
        currentPosts.removeAll { it.id == postId }
        _posts.value = currentPosts
    }
    
    suspend fun toggleLike(postId: String, userId: String) {
        delay(300)
        val currentPosts = _posts.value.toMutableList()
        val postIndex = currentPosts.indexOfFirst { it.id == postId }
        if (postIndex != -1) {
            val post = currentPosts[postIndex]
            val likes = post.likes.toMutableList()
            if (likes.contains(userId)) {
                likes.remove(userId)
            } else {
                likes.add(userId)
            }
            currentPosts[postIndex] = post.copy(likes = likes)
            _posts.value = currentPosts
        }
    }
    
    suspend fun addComment(postId: String, comment: Comment) {
        delay(500)
        val currentPosts = _posts.value.toMutableList()
        val postIndex = currentPosts.indexOfFirst { it.id == postId }
        if (postIndex != -1) {
            val post = currentPosts[postIndex]
            val comments = post.comments.toMutableList()
            comments.add(comment)
            currentPosts[postIndex] = post.copy(comments = comments)
            _posts.value = currentPosts
        }
    }
    
    suspend fun addReply(postId: String, commentId: String, reply: Comment) {
        delay(500)
        val currentPosts = _posts.value.toMutableList()
        val postIndex = currentPosts.indexOfFirst { it.id == postId }
        if (postIndex != -1) {
            val post = currentPosts[postIndex]
            val comments = post.comments.toMutableList()
            val commentIndex = comments.indexOfFirst { it.id == commentId }
            if (commentIndex != -1) {
                val comment = comments[commentIndex]
                val replies = comment.replies.toMutableList()
                replies.add(reply)
                comments[commentIndex] = comment.copy(replies = replies)
                currentPosts[postIndex] = post.copy(comments = comments)
                _posts.value = currentPosts
            }
        }
    }
}