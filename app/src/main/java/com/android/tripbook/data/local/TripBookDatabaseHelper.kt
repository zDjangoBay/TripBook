package com.android.tripbook.data.local

import android.content.Context
import com.android.tripbook.data.CommentRepository
import com.android.tripbook.data.PostRepository
import com.android.tripbook.data.TravelAgencyRepository
import com.android.tripbook.data.UserRepository
import com.android.tripbook.data.impl.RoomCommentRepository
import com.android.tripbook.data.impl.RoomPostRepository
import com.android.tripbook.data.impl.RoomTravelAgencyRepository
import com.android.tripbook.data.impl.RoomUserRepository
import com.android.tripbook.data.model.Comment
import com.android.tripbook.data.model.Post
import com.android.tripbook.data.model.TravelAgency
import com.android.tripbook.data.model.User
import kotlinx.coroutines.flow.Flow

/**
 * A utility class that provides simplified access to the database functionality
 * This can be used directly in UI components that don't need the additional ViewModel layer
 */
class TripBookDatabaseHelper(private val context: Context) {
    
    private val db by lazy { TripBookDatabase.getInstance(context) }
    
    private val postRepository: PostRepository by lazy {
        RoomPostRepository(
            postDao = db.postDao(),
            userPostLikeDao = db.userPostLikeDao()
        )
    }
    
    private val userRepository: UserRepository by lazy {
        RoomUserRepository(
            userDao = db.userDao()
        )
    }
    
    private val commentRepository: CommentRepository by lazy {
        RoomCommentRepository(
            commentDao = db.commentDao(),
            postDao = db.postDao()
        )
    }
    
    private val agencyRepository: TravelAgencyRepository by lazy {
        RoomTravelAgencyRepository(
            agencyDao = db.travelAgencyDao()
        )
    }
    
    // Post operations
    suspend fun createPost(
        title: String,
        description: String,
        location: String,
        images: List<String>,
        tags: List<String>,
        agencyId: String? = null
    ): String = postRepository.createPost(title, description, location, images, tags, agencyId)
    
    suspend fun getPost(postId: String): Post = postRepository.getPost(postId)
    
    fun getFeedPosts(limit: Int = 20, offset: Int = 0): Flow<List<Post>> = 
        postRepository.getFeedPosts(limit, offset)
    
    suspend fun getUserPosts(): List<Post> = postRepository.getUserPosts()
    
    fun searchPosts(query: String): Flow<List<Post>> = postRepository.searchPosts(query)
    
    suspend fun updatePost(
        postId: String,
        title: String,
        description: String,
        location: String,
        images: List<String>,
        tags: List<String>,
        agencyId: String? = null
    ): Boolean = postRepository.updatePost(postId, title, description, location, images, tags, agencyId)
    
    suspend fun deletePost(postId: String): Boolean = postRepository.deletePost(postId)
    
    suspend fun toggleLikePost(postId: String): Boolean = postRepository.toggleLikePost(postId)
    
    // User operations
    suspend fun getCurrentUser(): User = userRepository.getCurrentUser()
    
    suspend fun getUserById(userId: String): User = userRepository.getUserById(userId)
    
    suspend fun updateUserProfile(
        displayName: String,
        bio: String?,
        profileImage: String?
    ): Boolean = userRepository.updateUserProfile(displayName, bio, profileImage)
    
    // Comment operations
    suspend fun addComment(postId: String, content: String): Comment = 
        commentRepository.addComment(postId, content)
    
    fun getCommentsForPost(postId: String): Flow<List<Comment>> = 
        commentRepository.getCommentsForPost(postId)
    
    suspend fun deleteComment(commentId: String): Boolean = commentRepository.deleteComment(commentId)
    
    // Agency operations
    suspend fun getAgencyById(agencyId: String): TravelAgency = 
        agencyRepository.getAgencyById(agencyId)
    
    fun getAllAgencies(): Flow<List<TravelAgency>> = agencyRepository.getAllAgencies()
    
    fun searchAgencies(query: String): Flow<List<TravelAgency>> = 
        agencyRepository.searchAgencies(query)
}
