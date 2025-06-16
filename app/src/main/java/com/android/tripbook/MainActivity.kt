package com.android.tripbook

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.tripbook.comment.model.Comment
import com.android.tripbook.comment.model.Reply
import com.android.tripbook.comment.ui.CommentScreen
import com.android.tripbook.ui.theme.TripBookTheme

class MainActivity : ComponentActivity() {

    private val CHANNEL_ID = "tripbook_channel"
    private val NOTIFICATION_ID = 1001
    private val REQUEST_CODE_POST_NOTIFICATIONS = 2001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
            }
        }

        setContent {
            TripBookTheme {
                AppContent { title, message ->
                    showNotification(title, message)
                }
            }
        }

        createNotificationChannel()
    }

    private fun showNotification(title: String, message: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "TripBook Notifications"
            val descriptionText = "Channel for TripBook alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}

// ✅ Move this OUTSIDE the MainActivity class
@Composable
fun AppContent(onNotify: (String, String) -> Unit) {
    var comments by remember {
        mutableStateOf(
            listOf(
                Comment(
                    id = "1",
                    userId = "u1",
                    username = "Alice",
                    avatarUrl = null,
                    text = "Wow!",
                    timestamp = System.currentTimeMillis() - 3600000,
                    likes = 5,
                    replies = listOf(
                        Reply(
                            id = "r1",
                            userId = "u2",
                            username = "Bob",
                            avatarUrl = null,
                            text = "I agree!",
                            timestamp = System.currentTimeMillis() - 1800000,
                            likes = 2
                        )
                    )
                ),
                Comment(
                    id = "2",
                    userId = "u2",
                    username = "Bob",
                    avatarUrl = null,
                    text = "Love the views!",
                    timestamp = System.currentTimeMillis() - 7200000,
                    likes = 3
                )
            )
        )
    }

    CommentScreen(
        comments = comments,
        onPost = { newComment ->
            comments = comments + newComment
            onNotify("New Comment", "${newComment.username} said: ${newComment.text}")
        },
        onReply = { commentId, userId, reply ->
            comments = comments.map { comment ->
                if (comment.id == commentId) {
                    comment.copy(replies = comment.replies + reply)
                } else comment
            }
            onNotify("New Reply", "${reply.username} replied: ${reply.text}")
        },
        onLike = { commentId ->
            comments = comments.map { comment ->
                if (comment.id == commentId) comment.copy(likes = comment.likes + 1) else comment
            }
        },
        onDelete = { commentId ->
            comments = comments.map {
                if (it.id == commentId) it.copy(isDeleted = true) else it
            }
        },
        onBack = {}
    )
}
