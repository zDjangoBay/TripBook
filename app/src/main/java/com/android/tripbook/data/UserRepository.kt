package com.android.tripbook.data

import com.android.tripbook.data.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user-related operations
 */
interface UserRepository {
    /**
     * Get the current signed-in user
     */
    suspend fun getCurrentUser(): User
    
    /**
     * Get a user by their ID
     */
    suspend fun getUserById(userId: String): User
    
    /**
     * Update the current user's profile
     */
    suspend fun updateUserProfile(
        displayName: String,
        bio: String?,
        profileImage: String?
    ): Boolean
    
    /**
     * Get a list of all users
     */
    fun getAllUsers(): Flow<List<User>>
    
    /**
     * Search for users by username or display name
     */
    fun searchUsers(query: String): Flow<List<User>>
}
