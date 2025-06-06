package com.tripbook.userprofileBrowdonche.cache.reository

class UserProfileRepository(
    private val apiService: ApiService,
    private val profileCache: ProfileCache
) {
    suspend fun getUserProfile(userId: String): UserProfile {
        // Check cache first
        profileCache.getProfile(userId)?.let {
            return it
        }
        // Fetch from API and cache the result
        val userProfile = apiService.getUserProfile(userId)
        profileCache.saveProfile(userProfile)
        return userProfile
    }
}