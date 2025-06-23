
package com.android.tripbook.data.database.dao

import androidx.room.*
import com.android.tripbook.data.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User operations
 * 
 * This DAO handles all database operations for users including
 * authentication, profile management, and user preferences.
 * It supports the ProfileScreen and user authentication flow.
 * 
 * Key Features:
 * - User authentication queries
 * - Profile management
 * - Preference tracking
 * - Account status management
 * - Security and verification
 * 
 * Used by:
 * - UserManager for business logic
 * - ProfileScreen for displaying user information
 * - Authentication flow
 * - User preference management
 */
@Dao
interface UserDao {
    
    /**
     * Get a user by ID
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?
    
    /**
     * Get a user by email (for login)
     */
    @Query("SELECT * FROM users WHERE email = :email AND is_active = 1")
    suspend fun getUserByEmail(email: String): UserEntity?
    
    /**
     * Get a user by username (for login)
     */
    @Query("SELECT * FROM users WHERE username = :username AND is_active = 1")
    suspend fun getUserByUsername(username: String): UserEntity?
    
    /**
     * Get user as Flow for reactive UI updates
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByIdFlow(userId: String): Flow<UserEntity?>
    
    /**
     * Check if email exists (for registration validation)
     */
    @Query("SELECT COUNT(*) > 0 FROM users WHERE email = :email")
    suspend fun emailExists(email: String): Boolean
    
    /**
     * Check if username exists (for registration validation)
     */
    @Query("SELECT COUNT(*) > 0 FROM users WHERE username = :username")
    suspend fun usernameExists(username: String): Boolean
    
    /**
     * Get all active users (admin function)
     */
    @Query("SELECT * FROM users WHERE is_active = 1 ORDER BY created_at DESC")
    fun getAllActiveUsers(): Flow<List<UserEntity>>
    
    /**
     * Get users by verification status
     */
    @Query("SELECT * FROM users WHERE is_verified = :isVerified AND is_active = 1 ORDER BY created_at DESC")
    fun getUsersByVerificationStatus(isVerified: Boolean): Flow<List<UserEntity>>
    
    /**
     * Search users by name or email (admin function)
     */
    @Query("""
        SELECT * FROM users 
        WHERE is_active = 1 
        AND (
            first_name LIKE '%' || :query || '%' 
            OR last_name LIKE '%' || :query || '%' 
            OR email LIKE '%' || :query || '%'
            OR username LIKE '%' || :query || '%'
        )
        ORDER BY first_name, last_name
    """)
    fun searchUsers(query: String): Flow<List<UserEntity>>
    
    /**
     * Get users with incomplete profiles
     */
    @Query("""
        SELECT * FROM users 
        WHERE is_active = 1 
        AND (
            first_name = '' 
            OR last_name = '' 
            OR phone IS NULL 
            OR phone = ''
        )
        ORDER BY created_at DESC
    """)
    fun getUsersWithIncompleteProfiles(): Flow<List<UserEntity>>
    
    /**
     * Insert a new user
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long
    
    /**
     * Update an existing user
     */
    @Update
    suspend fun updateUser(user: UserEntity)
    
    /**
     * Update user profile information
     */
    @Query("""
        UPDATE users 
        SET first_name = :firstName, 
            last_name = :lastName, 
            phone = :phone, 
            updated_at = :timestamp 
        WHERE id = :userId
    """)
    suspend fun updateProfile(
        userId: String,
        firstName: String,
        lastName: String,
        phone: String?,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Update user preferences
     */
    @Query("""
        UPDATE users 
        SET preferred_currency = :currency,
            preferred_language = :language,
            travel_preferences = :travelPreferences,
            updated_at = :timestamp 
        WHERE id = :userId
    """)
    suspend fun updatePreferences(
        userId: String,
        currency: String,
        language: String,
        travelPreferences: String?,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Update notification settings
     */
    @Query("""
        UPDATE users 
        SET notification_enabled = :notificationEnabled,
            email_notifications = :emailNotifications,
            push_notifications = :pushNotifications,
            marketing_emails = :marketingEmails,
            updated_at = :timestamp 
        WHERE id = :userId
    """)
    suspend fun updateNotificationSettings(
        userId: String,
        notificationEnabled: Boolean,
        emailNotifications: Boolean,
        pushNotifications: Boolean,
        marketingEmails: Boolean,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Update profile image URL
     */
    @Query("""
        UPDATE users 
        SET profile_image_url = :imageUrl, updated_at = :timestamp 
        WHERE id = :userId
    """)
    suspend fun updateProfileImage(
        userId: String,
        imageUrl: String?,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Update emergency contact information
     */
    @Query("""
        UPDATE users 
        SET emergency_contact_name = :contactName,
            emergency_contact_phone = :contactPhone,
            updated_at = :timestamp 
        WHERE id = :userId
    """)
    suspend fun updateEmergencyContact(
        userId: String,
        contactName: String?,
        contactPhone: String?,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Update last login timestamp
     */
    @Query("""
        UPDATE users 
        SET last_login = :loginTime, updated_at = :timestamp 
        WHERE id = :userId
    """)
    suspend fun updateLastLogin(
        userId: String,
        loginTime: Long,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Verify user account
     */
    @Query("""
        UPDATE users 
        SET is_verified = 1, updated_at = :timestamp 
        WHERE id = :userId
    """)
    suspend fun verifyUser(
        userId: String,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Deactivate user account (soft delete)
     */
    @Query("""
        UPDATE users 
        SET is_active = 0, updated_at = :timestamp 
        WHERE id = :userId
    """)
    suspend fun deactivateUser(
        userId: String,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Reactivate user account
     */
    @Query("""
        UPDATE users 
        SET is_active = 1, updated_at = :timestamp 
        WHERE id = :userId
    """)
    suspend fun reactivateUser(
        userId: String,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Delete user (hard delete)
     */
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    /**
     * Get total user count
     */
    @Query("SELECT COUNT(*) FROM users WHERE is_active = 1")
    suspend fun getActiveUserCount(): Int
    
    /**
     * Get verified user count
     */
    @Query("SELECT COUNT(*) FROM users WHERE is_active = 1 AND is_verified = 1")
    suspend fun getVerifiedUserCount(): Int
    
    /**
     * Get user statistics
     */
    @Query("""
        SELECT 
            COUNT(*) as total_users,
            SUM(CASE WHEN is_verified = 1 THEN 1 ELSE 0 END) as verified_users,
            SUM(CASE WHEN last_login IS NOT NULL THEN 1 ELSE 0 END) as users_with_login
        FROM users 
        WHERE is_active = 1
    """)
    suspend fun getUserStats(): UserStats?
}

/**
 * Data class for user statistics
 */
data class UserStats(
    @ColumnInfo(name = "total_users") val totalUsers: Int,
    @ColumnInfo(name = "verified_users") val verifiedUsers: Int,
    @ColumnInfo(name = "users_with_login") val usersWithLogin: Int
)
