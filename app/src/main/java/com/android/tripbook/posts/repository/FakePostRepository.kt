package com.android.tripbook.posts.repository

import com.android.tripbook.posts.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import java.time.Instant

class FakePostRepository : PostRepository {
    private val _posts = MutableStateFlow(generateSamplePosts())
    private val posts: Flow<List<PostModel>> = _posts.asStateFlow()

    override fun getAllPosts(): Flow<List<PostModel>> = posts

    override fun getPostById(id: String): Flow<PostModel?> =
        MutableStateFlow(_posts.value.find { it.id == id }).asStateFlow()

    override suspend fun createPost(post: PostModel) {
        delay(500) // Simulate network delay
        val currentPosts = _posts.value.toMutableList()
        currentPosts.add(0, post)
        _posts.value = currentPosts
    }

    override suspend fun updatePost(post: PostModel) {
        delay(300)
        val currentPosts = _posts.value.toMutableList()
        val index = currentPosts.indexOfFirst { it.id == post.id }
        if (index != -1) {
            currentPosts[index] = post
            _posts.value = currentPosts
        }
    }

    override suspend fun deletePost(id: String) {
        delay(300)
        val currentPosts = _posts.value.toMutableList()
        currentPosts.removeAll { it.id == id }
        _posts.value = currentPosts
    }

    override suspend fun toggleLike(postId: String, userId: String) {
        delay(200)
        val currentPosts = _posts.value.toMutableList()
        val postIndex = currentPosts.indexOfFirst { it.id == postId }
        if (postIndex != -1) {
            val post = currentPosts[postIndex]
            val newLikes = if (userId in post.likes) {
                post.likes - userId
            } else {
                post.likes + userId
            }
            currentPosts[postIndex] = post.copy(likes = newLikes)
            _posts.value = currentPosts
        }
    }

    override suspend fun addComment(postId: String, comment: Comment) {
        delay(300)
        val currentPosts = _posts.value.toMutableList()
        val postIndex = currentPosts.indexOfFirst { it.id == postId }
        if (postIndex != -1) {
            val post = currentPosts[postIndex]
            val newComments = post.comments + comment
            currentPosts[postIndex] = post.copy(comments = newComments)
            _posts.value = currentPosts
        }
    }

    override suspend fun addReply(postId: String, commentId: String, reply: Comment) {
        delay(300)
        val currentPosts = _posts.value.toMutableList()
        val postIndex = currentPosts.indexOfFirst { it.id == postId }
        if (postIndex != -1) {
            val post = currentPosts[postIndex]
            val updatedComments = post.comments.map { comment ->
                if (comment.id == commentId) {
                    comment.copy(replies = comment.replies + reply)
                } else comment
            }
            currentPosts[postIndex] = post.copy(comments = updatedComments)
            _posts.value = currentPosts
        }
    }

    private fun generateSamplePosts(): List<PostModel> = listOf(
        PostModel(
            id = "1",
            userId = "user1",
            username = "Sarah Explorer",
            title = "Sunset at Santorini",
            description = "Witnessed the most breathtaking sunset from Oia village. The blue domes and white buildings create a perfect contrast against the golden sky. This magical moment reminded me why I fell in love with travel photography.",
            location = Location(
                name = "Oia Village",
                city = "Santorini",
                country = "Greece",
                coordinates = Coordinates(36.4618, 25.3753)
            ),
            images = listOf(
                ImageModel(uri = "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff", isUploaded = true),
                ImageModel(uri = "https://images.unsplash.com/photo-1613395877344-13d4a8e0d49e", isUploaded = true)
            ),
            categories = listOf(Category.NATURE, Category.CULTURE),
            tags = listOf(
                TagModel("tag1", "sunset", Category.NATURE, true),
                TagModel("tag2", "architecture", Category.CULTURE, true)
            ),
            hashtags = listOf("sunset", "greece", "architecture", "romantic"),
            timestamp = Instant.now().minusSeconds(3600 * 2),
            isVerified = true,
            likes = setOf("user2", "user3", "user4"),
            comments = listOf(
                Comment(
                    id = "c1",
                    userId = "user2",
                    username = "Mike Traveler",
                    text = "Absolutely stunning! How long did you wait for this shot?",
                    timestamp = Instant.now().minusSeconds(3600),
                    replies = listOf(
                        Comment(
                            id = "r1",
                            userId = "user1",
                            username = "Sarah Explorer",
                            text = "About 2 hours, but totally worth it!",
                            timestamp = Instant.now().minusSeconds(3000)
                        )
                    )
                )
            )
        ),
        PostModel(
            id = "2",
            userId = "user2",
            username = "Mike Traveler",
            title = "Street Food Adventure in Bangkok",
            description = "Exploring the vibrant street food scene in Chatuchak Market. From pad thai to mango sticky rice, every bite was incredible! The local vendors were so friendly and passionate about their craft.",
            location = Location(
                name = "Chatuchak Weekend Market",
                city = "Bangkok",
                country = "Thailand"
            ),
            images = listOf(ImageModel(uri = "https://images.unsplash.com/photo-1578662996442-48f60103fc96", isUploaded = true)),
            categories = listOf(Category.FOOD, Category.CULTURE),
            tags = listOf(
                TagModel("tag3", "streetfood", Category.FOOD, true),
                TagModel("tag4", "local", Category.CULTURE, true)
            ),
            hashtags = listOf("streetfood", "thailand", "market", "spicy"),
            timestamp = Instant.now().minusSeconds(3600 * 8),
            likes = setOf("user1", "user3"),
            comments = listOf()
        )
    )
}

