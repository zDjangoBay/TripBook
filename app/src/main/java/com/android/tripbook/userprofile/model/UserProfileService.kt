package com.android.tripbook.userprofile.model

// Update the import paths below to match the actual package structure where CreateUserProfileRequest, UpdateUserProfileRequest, and UserProfile are defined.
// For example, if they are in com.android.tripbook.userprofile.model, use:
import com.android.tripbook.userprofile.model.CreateUserProfileRequest
import com.android.tripbook.userprofile.model.UpdateUserProfileRequest
import com.android.tripbook.userprofile.model.UserProfile

interface UserProfileService {
    suspend fun createUserProfile(request: CreateUserProfileRequest): UserProfile?
    suspend fun getUserProfileById(userId: String): UserProfile?
    suspend fun updateUserProfile(userId: String, request: UpdateUserProfileRequest): UserProfile?
    suspend fun deleteUserProfile(userId: String): Boolean
    suspend fun getAllUserProfiles(page: Int = 1, pageSize: Int = 20): List<UserProfile>
}