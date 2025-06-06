package com.android.tripbook.posts.utils

import com.android.tripbook.BuildConfig

object ConfigManager {
    // API key should be stored in gradle.properties and accessed via BuildConfig
    val geoapifyApiKey: String
        get() = BuildConfig.GEOAPIFY_API_KEY
    
    // Default user info - in real app, get from authentication
    const val DEFAULT_USER_ID = "user_001"
    const val DEFAULT_USERNAME = "TravelExplorer"
    const val DEFAULT_USER_AVATAR = "https://i.pravatar.cc/100?u=001"
}