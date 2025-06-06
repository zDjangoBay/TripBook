package com.android.tripbook.notifications.models

import java.util.*

data class NotificationLog(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val type: NotificationType,
    val status: NotificationStatus,
    val sentAt: Long = System.currentTimeMillis(),
    val error: String? = null
)

enum class NotificationStatus {
    PENDING,
    SENT,
    FAILED,
    DELIVERED
}