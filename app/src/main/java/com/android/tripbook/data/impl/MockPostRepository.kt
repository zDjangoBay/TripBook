package com.android.tripbook.data.impl

import com.android.tripbook.data.PostRepository
import com.android.tripbook.data.model.Post
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import kotlin.random.Random

/**
 * Implementation of PostRepository that uses mock data for development
 */
class MockPostRepository : PostRepository {
    
    // Mock data store
    private val posts = mutableMapOf<String, Post>()
    
    init {
        // Add some sample posts
        for (i in 1..5) {
            val postId = UUID.randomUUID().toString()
            posts[postId] = Post(
                id = postId,
                userId = "user1", // Current user ID
                title = "Sample Post $i",
                description = "This is a sample travel post description for testing purposes.",
                images = listOf("https://example.com/image$i.jpg"),
                location = "Location $i",
                latitude = Random.nextDouble(-90.0, 90.0),
                longitude = Random.nextDouble(-180.0, 180.0),
                tags = listOf("travel", "sample", "tag$i"),
                agencyId = if (i % 2 == 0) "agency1" else null,
                likes = Random.nextInt(0, 500),
                comments = Random.nextInt(0, 100),
                createdAt = Date(),
                updatedAt = Date()
            )
        }
    }
    
    override suspend fun createPost(
        title: String,
        description: String,
        location: String,
        images: List<String>,
        tags: List<String>,
        agencyId: String?
    ): String {
        // Simulate network delay
        delay(1000)
        
        val postId = UUID.randomUUID().toString()
        val now = Date()
        
        posts[postId] = Post(
            id = postId,
            userId = "user1", // Current user ID (would come from auth in real app)
            title = title,
            description = description,
            images = images,
            location = location,
            latitude = null, // Would come from location picker in real app
            longitude = null,
            tags = tags,
            agencyId = agencyId,
            likes = 0,
            comments = 0,
            createdAt = now,
            updatedAt = now
        )
        
        return postId
    }
    
    override suspend fun getPost(postId: String): Post {
        // Simulate network delay
        delay(500)
        
        return posts[postId] ?: throw NoSuchElementException("Post not found with id: $postId")
    }
      override suspend fun getUserPosts(): List<Post> {
        // Simulate network delay
        delay(800)
        
        // In a real app, we would filter by the current user ID
        return posts.values.filter { it.userId == "user1" }.toList()
    }
    
    override fun getFeedPosts(limit: Int, offset: Int): Flow<List<Post>> {
        return flow {
            // Simulate network delay
            delay(500)
            
            val feedPosts = posts.values
                .sortedByDescending { it.createdAt }
                .drop(offset)
                .take(limit)
                .toList()
            
            emit(feedPosts)
        }
    }
    
    override fun searchPosts(query: String): Flow<List<Post>> {
        return flow {
            // Simulate network delay
            delay(700)
            
            val searchResults = posts.values
                .filter { post -> 
                    post.title.contains(query, ignoreCase = true) ||
                    post.description.contains(query, ignoreCase = true) ||
                    post.location.contains(query, ignoreCase = true) ||
                    post.tags.any { it.contains(query, ignoreCase = true) }
                }
                .sortedByDescending { it.createdAt }
                .toList()
            
            emit(searchResults)
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
        // Simulate network delay
        delay(1000)
        
        val post = posts[postId] ?: return false
        
        posts[postId] = post.copy(
            title = title,
            description = description,
            location = location,
            images = images,
            tags = tags,
            agencyId = agencyId,
            updatedAt = Date()
        )
        
        return true
    }
    
    override suspend fun deletePost(postId: String): Boolean {
        // Simulate network delay
        delay(500)
        
        return posts.remove(postId) != null
    }
    
    override suspend fun toggleLikePost(postId: String): Boolean {
        // Simulate network delay
        delay(300)
        
        val post = posts[postId] ?: return false
        
        // In a real app, we would check if the user has already liked the post
        // For now, just toggle between +1 and -1 for demo purposes
        posts[postId] = post.copy(
            likes = post.likes + 1
        )
        
        return true
    }
    
    override suspend fun addComment(postId: String, comment: String): Boolean {
        // Simulate network delay
        delay(500)
        
        val post = posts[postId] ?: return false
        
        posts[postId] = post.copy(
            comments = post.comments + 1
        )
        
        return true
    }
}
