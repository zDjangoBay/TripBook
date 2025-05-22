package com.android.tripbook.userprofile.data.models

data class UserPreferences(
    val preferredCurrency: String,
    val preferredLanguage: String,
    val allowLocationTracking: Boolean
)
