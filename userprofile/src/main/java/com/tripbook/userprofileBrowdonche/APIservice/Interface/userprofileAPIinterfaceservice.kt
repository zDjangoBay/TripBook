package com.tripbook.userprofileBrowdonche.APInterface

import com.tripbook.userprofileBrowdonche.Data.UserProfile

interface ApiService {
    suspend fun getUserProfile(userId: String): UserProfile
    // Other API methods...
}