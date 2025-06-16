package com.android.tripbook.notification // Or your desired package

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.tripbook.comment.model.Comment // Import your Comment class
import com.android.tripbook.comment.model.Reply   // Import your Reply class
// Import your MainActivity or the activity you want to open on tap
import com.android.tripbook.MainActivity // Replace with your actual MainActivity

object NotificationHelper {

    private const val CHANNEL_ID_COMMENT = "comment_channel"
    private const val CHANNEL_NAME_COMMENT = "Comments"
    private const val CHANNEL_ID_REPLY = "reply_channel"
    private const val CHANNEL_NAME_REPLY = "Replies"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val commentChannel = NotificationChannel(
                CHANNEL_ID_COMMENT,
                CHANNEL_NAME_COMMENT,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for new comments"
            }

            val replyChannel = NotificationChannel(
                CHANNEL_ID_REPLY,
                CHANNEL_NAME_REPLY,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for new replies"
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(commentChannel)
            notificationManager.createNotificationChannel(replyChannel)
        }
    }

    fun showCommentNotification(context: Context, comment: Comment) {
        // Intent to open when the notification is tapped
        // You'll likely want to navigate to the specific comment or post
        val intent = Intent(context, MainActivity::class.java).apply { // Replace MainActivity
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // You can add extras to the intent to identify the comment
            putExtra("comment_id", comment.id)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            comment.id.hashCode(), // Unique request code for each comment notification
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_COMMENT)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app's icon
            .setContentTitle("New Comment from ${comment.username}")
            .setContentText(comment.text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Dismiss notification when tapped

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            // Using comment.id.hashCode() can be a simple way to generate a unique ID
            notify(comment.id.hashCode(), builder.build())
        }
    }

    fun showReplyNotification(context: Context, reply: Reply, originalCommentId: String) {
        // Intent to open when the notification is tapped
        // You'll likely want to navigate to the specific reply or comment
        val intent = Intent(context, MainActivity::class.java).apply { // Replace MainActivity
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Add extras to identify the reply and its parent comment
            putExtra("comment_id", originalCommentId)
            putExtra("reply_id", reply.id)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            reply.id.hashCode(), // Unique request code for each reply notification
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_REPLY)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app's icon
            .setContentTitle("New Reply from ${reply.username}")
            .setContentText(reply.text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(reply.id.hashCode(), builder.build())
        }
    }
}