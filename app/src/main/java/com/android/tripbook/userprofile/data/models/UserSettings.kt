package com.android.tripbook.userprofile.data.models

data class UserSettings(
    val notificationsEnabled: Boolean = true,
    val darkMode: Boolean = false,
    val language: String = "en",
    val autoSync: Boolean = true
)
