package com.tripbook.userprofileBrowdonche.profiledata.repository

class UserProfileRepository(private val apiService: ApiService) {
    suspend fun getUserProfile(userId: String): UserProfile {
        return apiService.getUserProfile(userId)
    }
}