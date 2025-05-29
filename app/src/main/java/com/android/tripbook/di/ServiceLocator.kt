package com.android.tripbook.di

import android.content.Context
import com.android.tripbook.data.CommentRepository
import com.android.tripbook.data.PostRepository
import com.android.tripbook.data.TravelAgencyRepository
import com.android.tripbook.data.UserRepository
import com.android.tripbook.data.impl.MockPostRepository
import com.android.tripbook.data.impl.RoomCommentRepository
import com.android.tripbook.data.impl.RoomPostRepository
import com.android.tripbook.data.impl.RoomTravelAgencyRepository
import com.android.tripbook.data.impl.RoomUserRepository
import com.android.tripbook.data.local.TripBookDatabase
import com.android.tripbook.viewmodel.ViewModelFactory

/**
 * Simple dependency injection provider
 * In a real app, this could be replaced with Hilt or Koin
 */
object ServiceLocator {
    private var database: TripBookDatabase? = null
    private var useRoomDatabase: Boolean = true
    
    // Lazy initialization of repositories
    private var postRepository: PostRepository? = null
    private var userRepository: UserRepository? = null
    private var commentRepository: CommentRepository? = null
    private var travelAgencyRepository: TravelAgencyRepository? = null
    
    /**
     * Initialize the ServiceLocator with application context
     */
    fun initialize(context: Context) {
        database = TripBookDatabase.getInstance(context)
    }
    
    /**
     * Get or create the PostRepository
     */
    fun providePostRepository(context: Context): PostRepository {
        synchronized(this) {
            return postRepository ?: createPostRepository(context)
        }
    }
    
    /**
     * Create a new instance of PostRepository
     */
    private fun createPostRepository(context: Context): PostRepository {
        val newRepo = if (useRoomDatabase) {
            val database = database ?: TripBookDatabase.getInstance(context)
            RoomPostRepository(
                postDao = database.postDao(),
                userPostLikeDao = database.userPostLikeDao()
            )
        } else {
            MockPostRepository()
        }
        
        postRepository = newRepo
        return newRepo
    }
      /**
     * Get or create the UserRepository
     */
    fun provideUserRepository(context: Context): UserRepository {
        synchronized(this) {
            return userRepository ?: createUserRepository(context)
        }
    }
    
    /**
     * Create a new instance of UserRepository
     */
    private fun createUserRepository(context: Context): UserRepository {
        val newRepo = if (useRoomDatabase) {
            val database = database ?: TripBookDatabase.getInstance(context)
            RoomUserRepository(
                userDao = database.userDao()
            )
        } else {
            // Would have a mock implementation in a real app
            throw NotImplementedError("Mock user repository not implemented")
        }
        
        userRepository = newRepo
        return newRepo
    }
    
    /**
     * Get or create the CommentRepository
     */
    fun provideCommentRepository(context: Context): CommentRepository {
        synchronized(this) {
            return commentRepository ?: createCommentRepository(context)
        }
    }
    
    /**
     * Create a new instance of CommentRepository
     */
    private fun createCommentRepository(context: Context): CommentRepository {
        val newRepo = if (useRoomDatabase) {
            val database = database ?: TripBookDatabase.getInstance(context)
            RoomCommentRepository(
                commentDao = database.commentDao(),
                postDao = database.postDao()
            )
        } else {
            // Would have a mock implementation in a real app
            throw NotImplementedError("Mock comment repository not implemented")
        }
        
        commentRepository = newRepo
        return newRepo
    }
    
    /**
     * Get or create the TravelAgencyRepository
     */
    fun provideTravelAgencyRepository(context: Context): TravelAgencyRepository {
        synchronized(this) {
            return travelAgencyRepository ?: createTravelAgencyRepository(context)
        }
    }
    
    /**
     * Create a new instance of TravelAgencyRepository
     */
    private fun createTravelAgencyRepository(context: Context): TravelAgencyRepository {
        val newRepo = if (useRoomDatabase) {
            val database = database ?: TripBookDatabase.getInstance(context)
            RoomTravelAgencyRepository(
                agencyDao = database.travelAgencyDao()
            )
        } else {
            // Would have a mock implementation in a real app
            throw NotImplementedError("Mock travel agency repository not implemented")
        }
        
        travelAgencyRepository = newRepo
        return newRepo
    }
      /**
     * Provide the ViewModelFactory
     */
    fun provideViewModelFactory(context: Context): ViewModelFactory {
        return ViewModelFactory(
            postRepository = providePostRepository(context),
            userRepository = provideUserRepository(context),
            commentRepository = provideCommentRepository(context),
            travelAgencyRepository = provideTravelAgencyRepository(context)
        )
    }
    
    /**
     * For testing purposes: set whether to use Room database or mock data
     */
    fun setUseRoomDatabase(useRoom: Boolean) {
        useRoomDatabase = useRoom
        // Reset all repositories
        postRepository = null
        userRepository = null
        commentRepository = null
        travelAgencyRepository = null
    }
}
