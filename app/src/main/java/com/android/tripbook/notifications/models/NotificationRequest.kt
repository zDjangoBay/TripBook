package com.android.tripbook.notifications.models

data class NotificationRequest(
    val userId: String,
    val type: NotificationType,
    val data: Map<String, Any>,
    val timestamp: Long = System.currentTimeMillis()
)