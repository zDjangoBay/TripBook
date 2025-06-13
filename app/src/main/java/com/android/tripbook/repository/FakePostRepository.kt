import com.android.tripbook.posts.model.*
import com.android.tripbook.posts.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import com.android.tripbook.repository.PostRepository
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
            username = "Joyce Explorer",
            title = "Waza National park",
            description = "Witnessed the most breathtaking display of nature at its finest. The view of the various wildlife species were amazing. This magical moment reminded me why I fell in love with travel photography and nature.",
            content = "Full story about the national park experience.",
            author = "Joyce Explorer",
            location = Location(
                name = "Far north region",
                city = "Towards Nigeria (Logone and Chari Division)",
                country = "Cameroon",
                coordinates = Coordinates(11.3611, 14.5850)
            ),
            images = listOf(
                ImageModel(
                    id = "img1",
                    uri = "https://unsplash.com/photos/a-dirt-path-with-trees-and-water-Xw1evZiDs6I",
                    isUploaded = true
                ),
                ImageModel(
                    id = "img2",
                    uri = "https://unsplash.com/photos/brown-monkey-on-green-grass-during-daytime-hCfIiuXHmWg",
                    isUploaded = true
                )
            ),
            categories = listOf(Category.NATURE, Category.CULTURE),
            tags = listOf(
                TagModel("tag1", "sunset", Category.NATURE, true),
                TagModel("tag2", "architecture", Category.CULTURE, true)
            ),
            hashtags = listOf("wildlife", "cameroon", "nature", "amazing"),
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
            title = "The street food of Yaoundé",
            description = "Exploring the vibrant and the serene beauty of Yaoundé. From bustling markets to peaceful parks, the city has so much to offer. The street food scene is a must-try, with flavors that will tantalize your taste buds.",
            content = "Full story about the Yaounde adventure.",
            author = "Mike Traveler",
            location = Location(
                name = "Messasi Market",
                city = "Yaoundé",
                country = "Cameroon",
                coordinates = Coordinates(3.8895, 11.4856)
            ),
            images = listOf(
                ImageModel(
                    id = "img3",
                    uri = "https://unsplash.com/photos/grilled-meat-on-charcoal-grill-4jRyugKbQdw",
                    isUploaded = true
                )

            ),
            categories = listOf(Category.FOOD, Category.CULTURE),
            tags = listOf(
                TagModel("tag3", "streetfood", Category.FOOD, true),
                TagModel("tag4", "local", Category.CULTURE, true)
            ),
            hashtags = listOf("streetfood", "cameroon", "market", "spicy"),
            timestamp = Instant.now().minusSeconds(3600 * 8),
            isVerified = false,
            likes = setOf("user1", "user3"),
            comments = listOf()
        )
    )
}