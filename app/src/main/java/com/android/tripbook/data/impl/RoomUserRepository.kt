package com.android.tripbook.data.impl

import com.android.tripbook.data.UserRepository
import com.android.tripbook.data.local.dao.UserDao
import com.android.tripbook.data.local.mapper.UserMapper
import com.android.tripbook.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.NoSuchElementException

/**
 * Room-based implementation of UserRepository
 */
class RoomUserRepository(
    private val userDao: UserDao,
    private val currentUserId: String = "user1" // In a real app, this would come from auth
) : UserRepository {

    override suspend fun getCurrentUser(): User {
        val userEntity = userDao.getUserById(currentUserId) 
            ?: throw IllegalStateException("Current user not found in database")
        
        return UserMapper.fromEntity(userEntity)
    }

    override suspend fun getUserById(userId: String): User {
        val userEntity = userDao.getUserById(userId) 
            ?: throw NoSuchElementException("User not found with id: $userId")
        
        return UserMapper.fromEntity(userEntity)
    }

    override suspend fun updateUserProfile(
        displayName: String,
        bio: String?,
        profileImage: String?
    ): Boolean {
        val userEntity = userDao.getUserById(currentUserId) 
            ?: throw IllegalStateException("Current user not found in database")
        
        val updatedUser = userEntity.copy(
            displayName = displayName,
            bio = bio,
            profileImage = profileImage,
            updatedAt = Date()
        )
        
        val result = userDao.updateUser(updatedUser)
        return result > 0
    }

    override fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers().map { userEntities ->
            UserMapper.fromEntities(userEntities)
        }
    }

    override fun searchUsers(query: String): Flow<List<User>> {
        // In a real implementation, you'd use Full Text Search or a more sophisticated query
        // For now, we'll just use the getAllUsers flow and filter in memory
        return userDao.getAllUsers().map { userEntities ->
            userEntities
                .filter { 
                    it.username.contains(query, ignoreCase = true) || 
                    it.displayName.contains(query, ignoreCase = true) 
                }
                .let { UserMapper.fromEntities(it) }
        }
    }
}
