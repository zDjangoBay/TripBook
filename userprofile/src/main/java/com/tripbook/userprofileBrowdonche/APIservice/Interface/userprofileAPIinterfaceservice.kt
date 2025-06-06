package com.tripbook.userprofileBrowdonche.APInterface

interface ApiService {
    suspend fun getUserProfile(userId: String): UserProfile
    // Other API methods...
}