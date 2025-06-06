package com.tripbook.userprofileBrowdonche.cache

class ProfileCache {
    private val cache = mutableMapOf<String, UserProfile>()

    fun getProfile(userId: String): UserProfile? {
        return cache[userId]
    }

    fun saveProfile(userProfile: UserProfile) {
        cache[userProfile.id] = userProfile
    }
}