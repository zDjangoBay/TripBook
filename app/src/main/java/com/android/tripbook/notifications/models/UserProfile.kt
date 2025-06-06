package com.android.tripbook.notifications.models

data class UserProfile(
    val userId: String,
    val email: String,
    val fcmToken: String?,
    val notificationPreferences: NotificationPreferences
)

data class NotificationPreferences(
    val pushEnabled: Boolean = true,
    val emailEnabled: Boolean = true,
    val reminderEnabled: Boolean = true
)