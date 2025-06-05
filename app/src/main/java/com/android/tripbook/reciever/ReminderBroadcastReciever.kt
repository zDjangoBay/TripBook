package com.android.tripbook.receiver

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.tripbook.R // Assuming R class is accessible
import com.android.tripbook.TripCatalogActivity // Example activity to open on tap

class ReminderBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_REMINDER_ID = "com.android.tripbook.REMINDER_ID"
        const val EXTRA_REMINDER_MESSAGE = "com.android.tripbook.REMINDER_MESSAGE"
        private const val CHANNEL_ID = "tripbook_reminders"
        private const val CHANNEL_NAME = "TripBook Reminders"
        private const val TAG = "ReminderReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Broadcast received for reminder")
        val reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, -1)
        val message = intent.getStringExtra(EXTRA_REMINDER_MESSAGE) ?: "Your TripBook reminder!"

        if (reminderId == -1L) {
            Log.e(TAG, "Received invalid reminder ID.")
            return
        }

        createNotificationChannel(context)
        showNotification(context, reminderId, message)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH // High importance for reminders
            ).apply {
                description = "Channel for TripBook travel reminders"
                // Configure other channel properties if needed (lights, vibration, etc.)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created or already exists.")
        }
    }

    private fun showNotification(context: Context, reminderId: Long, message: String) {
        // Intent to launch the app when notification is tapped
        val contentIntent = Intent(context, TripCatalogActivity::class.java).apply {
            // Add flags or extras if you want to navigate to a specific part of the app
            // e.g., putExtra("NAVIGATE_TO_REMINDER_ID", reminderId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingContentIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(), // Use reminder ID for request code uniqueness
            contentIntent,
            pendingIntentFlag
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with a proper notification icon
            .setContentTitle("TripBook Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingContentIntent) // Set the intent to launch on tap
            .setAutoCancel(true) // Dismiss notification when tapped
            // Add actions if needed (e.g., "Snooze", "Dismiss")

        val notificationManager = NotificationManagerCompat.from(context)

        // Check for POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "POST_NOTIFICATIONS permission not granted. Cannot show notification.")
                // TODO: Handle the case where permission is missing.
                // The app should request this permission before scheduling notifications.
                return
            }
        }

        // Use reminderId as the notification ID to allow updating/cancelling specific notifications
        notificationManager.notify(reminderId.toInt(), builder.build())
        Log.i(TAG, "Notification shown for reminder ID: $reminderId")
    }
}
