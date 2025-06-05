package com.android.tripbook.data.model

data class Notification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val content: String,
    val relatedEntityId: String?,
    val isRead: Boolean,
    val createdAt: Long
)

enum class NotificationType {
    NEW_POST, NEW_COMMENT, NEW_FOLLOWER, LIKE, BOOKING_UPDATE, MENTION, GROUP_INVITE
}