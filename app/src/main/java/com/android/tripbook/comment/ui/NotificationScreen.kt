package com.android.tripbook.comment.ui


import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.android.tripbook.comment.model.Notification
import com.android.tripbook.comment.model.NotificationType

@Composable
fun NotificationScreen() {
    val notifications = remember {
        mutableStateListOf(
            Notification(
                id = "1",
                type = NotificationType.COMMENT,
                senderId = "user123",
                senderUsername = "Alice",
                senderAvatarUrl = "https://example.com/avatar1.png",
                receiverId = "user456",
                commentId = "comment1",
                replyId = null,
                message = "Alice commented on your post",
                timestamp = System.currentTimeMillis(),
                isRead = false
            ),
            Notification(
                id = "2",
                type = NotificationType.REPLY,
                senderId = "user789",
                senderUsername = "Bob",
                senderAvatarUrl = "https://example.com/avatar2.png",
                receiverId = "user456",
                commentId = "comment1",
                replyId = "reply1",
                message = "Bob replied to your comment",
                timestamp = System.currentTimeMillis() - 3600000,
                isRead = true
            )
        )
    }

    NotificationListScreen(notifications = notifications) { notification ->
        println("Notification clicked: ${notification.id}")
    }
}
