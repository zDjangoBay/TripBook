package com.android.tripbook.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for User Information
 * 
 * This entity stores user profile information and preferences.
 * It supports the user authentication and profile management
 * features of the application.
 * 
 * Key Features:
 * - User authentication details
 * - Profile information
 * - Travel preferences
 * - Notification settings
 * - Account status tracking
 * 
 * Used by:
 * - ProfileScreen for displaying user information
 * - Authentication flow
 * - Reservation tracking
 * - Notification preferences
 */
@Entity(
    tableName = "users",
    indices = [
        androidx.room.Index(value = ["email"], unique = true),
        androidx.room.Index(value = ["username"], unique = true),
        androidx.room.Index(value = ["is_active"])
    ]
)
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "username")
    val username: String,
    
    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    @ColumnInfo(name = "first_name")
    val firstName: String,
    
    @ColumnInfo(name = "last_name")
    val lastName: String,
    
    @ColumnInfo(name = "phone")
    val phone: String? = null,

    @ColumnInfo(name = "bio")
    val bio: String? = null,

    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: String? = null, // Stored as ISO date string
    
    @ColumnInfo(name = "profile_image_url")
    val profileImageUrl: String? = null,
    
    @ColumnInfo(name = "preferred_currency")
    val preferredCurrency: String = "USD",
    
    @ColumnInfo(name = "preferred_language")
    val preferredLanguage: String = "en",
    
    @ColumnInfo(name = "notification_enabled")
    val notificationEnabled: Boolean = true,
    
    @ColumnInfo(name = "email_notifications")
    val emailNotifications: Boolean = true,
    
    @ColumnInfo(name = "push_notifications")
    val pushNotifications: Boolean = true,
    
    @ColumnInfo(name = "marketing_emails")
    val marketingEmails: Boolean = false,
    
    @ColumnInfo(name = "travel_preferences")
    val travelPreferences: String? = null, // JSON string of preferences
    
    @ColumnInfo(name = "emergency_contact_name")
    val emergencyContactName: String? = null,
    
    @ColumnInfo(name = "emergency_contact_phone")
    val emergencyContactPhone: String? = null,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    
    @ColumnInfo(name = "is_verified")
    val isVerified: Boolean = false,
    
    @ColumnInfo(name = "last_login")
    val lastLogin: Long? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Gets the user's full name
     */
    fun getFullName(): String {
        return "$firstName $lastName".trim()
    }
    
    /**
     * Gets the user's display name (first name or username if no first name)
     */
    fun getDisplayName(): String {
        return firstName.ifEmpty { username }
    }
    
    /**
     * Checks if the user has completed their profile
     */
    fun isProfileComplete(): Boolean {
        return firstName.isNotEmpty() && 
               lastName.isNotEmpty() && 
               email.isNotEmpty() && 
               phone?.isNotEmpty() == true
    }
    
    /**
     * Checks if the user can receive notifications
     */
    fun canReceiveNotifications(): Boolean {
        return isActive && notificationEnabled
    }
    
    companion object {
        /**
         * Creates a basic UserEntity for new user registration
         */
        fun createNewUser(
            id: String,
            username: String,
            email: String,
            firstName: String,
            lastName: String,
            passwordHash: String = ""
        ): UserEntity {
            return UserEntity(
                id = id,
                username = username,
                email = email,
                passwordHash = passwordHash,
                firstName = firstName,
                lastName = lastName
            )
        }
    }
}
