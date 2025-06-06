package com.android.tripbook.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.tripbook.MainActivity
import com.android.tripbook.R

object NotificationHelper {

    // Notification Channels
    const val CHANNEL_ID = "trip_milestone_channel"
    const val CHANNEL_NAME = "Trip Milestones"
    const val CHANNEL_DESCRIPTION = "Notifications for upcoming trips and itinerary items"

    const val REMINDER_CHANNEL_ID = "trip_reminder_channel"
    const val REMINDER_CHANNEL_NAME = "Trip Reminders"
    const val REMINDER_CHANNEL_DESCRIPTION = "Daily reminders for upcoming trips"

    // Notification Types
    enum class NotificationType(val id: Int) {
        TRIP_STARTING_SOON(1000),
        TRIP_STARTING_TODAY(2000),
        ITINERARY_REMINDER(3000),
        TRIP_ENDING_SOON(4000)
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Trip Milestones Channel
            val milestoneChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }

            // Trip Reminders Channel
            val reminderChannel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                REMINDER_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = REMINDER_CHANNEL_DESCRIPTION
                enableVibration(false)
                enableLights(false)
            }

            notificationManager.createNotificationChannels(listOf(milestoneChannel, reminderChannel))
        }
    }

    fun showNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        type: NotificationType = NotificationType.TRIP_STARTING_SOON
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = when (type) {
            NotificationType.ITINERARY_REMINDER -> REMINDER_CHANNEL_ID
            else -> CHANNEL_ID
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: Replace with app icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (e: SecurityException) {
            // Handle case where notification permission is not granted
            android.util.Log.w("NotificationHelper", "Notification permission not granted", e)
        }
    }

    fun showTripStartingNotification(context: Context, tripName: String, daysUntil: Int) {
        val title = when (daysUntil) {
            0 -> "🎒 Your trip starts today!"
            1 -> "🎒 Your trip starts tomorrow!"
            else -> "🎒 Trip starting in $daysUntil days"
        }

        val message = "Get ready for your $tripName adventure!"
        val type = if (daysUntil == 0) NotificationType.TRIP_STARTING_TODAY else NotificationType.TRIP_STARTING_SOON

        showNotification(
            context = context,
            notificationId = type.id + tripName.hashCode(),
            title = title,
            message = message,
            type = type
        )
    }

    fun showItineraryReminder(context: Context, tripName: String, activityTitle: String, timeUntil: String) {
        val title = "📅 Upcoming Activity"
        val message = "$activityTitle in $timeUntil for your $tripName trip"

        showNotification(
            context = context,
            notificationId = NotificationType.ITINERARY_REMINDER.id + activityTitle.hashCode(),
            title = title,
            message = message,
            type = NotificationType.ITINERARY_REMINDER
        )
    }

    fun showTripEndingNotification(context: Context, tripName: String, daysLeft: Int) {
        val title = when (daysLeft) {
            0 -> "🏁 Last day of your trip!"
            1 -> "🏁 One day left!"
            else -> "🏁 $daysLeft days left"
        }

        val message = "Make the most of your remaining time on your $tripName adventure!"

        showNotification(
            context = context,
            notificationId = NotificationType.TRIP_ENDING_SOON.id + tripName.hashCode(),
            title = title,
            message = message,
            type = NotificationType.TRIP_ENDING_SOON
        )
    }
}


