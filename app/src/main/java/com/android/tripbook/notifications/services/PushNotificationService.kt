package com.android.tripbook.notifications.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.android.tripbook.MainActivity
import com.android.tripbook.R
import com.android.tripbook.notifications.models.NotificationRequest
import com.android.tripbook.notifications.models.NotificationTemplate
import com.android.tripbook.notifications.models.UserProfile

class PushNotificationService(private val context: Context) {
    private val TAG = "PushNotificationService"
    private val CHANNEL_ID = "tripbook_notifications"
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    fun sendLocalNotification(
        userProfile: UserProfile,
        template: NotificationTemplate,
        request: NotificationRequest
    ): Boolean {
        return try {
             //ouvrir l'app quand on clique sur la notification
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("notification_type", request.type.name)
                putExtra("user_id", request.userId)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                request.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Construire la notification
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(template.title)
                .setContentText(template.body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()


            notificationManager.notify(request.hashCode(), notification)

            Log.d(TAG, "Local notification sent to user: ${userProfile.userId}")
            true

        } catch (e: Exception) {
            Log.e(TAG, "Failed to send local notification", e)
            false
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "TripBook Notifications"
            val descriptionText = "Notifications pour les réservations TripBook"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}