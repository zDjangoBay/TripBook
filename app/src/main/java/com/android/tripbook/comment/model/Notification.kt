package com.android.tripbook.comment.model.Notification


data class Notification(
    val id: String,
    val type: NotificationType,
    val senderId: String,
    val senderUsername: String,
    val senderAvatarUrl: String?,
    val receiverId: String,
    val commentId: String?,
    val replyId: String?,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false
)

enum class NotificationType {
    COMMENT,
    REPLY,
    LIKE_COMMENT,
    LIKE_REPLY
}
