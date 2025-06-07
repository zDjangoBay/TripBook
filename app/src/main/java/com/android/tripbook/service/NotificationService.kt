package com.android.tripbook.service

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
import com.android.tripbook.model.NotificationPriority
import com.android.tripbook.model.NotificationType
import com.android.tripbook.model.TripNotification

/**
 * Service responsible for displaying local notifications
 */
class NotificationService(private val context: Context) {
    
    companion object {
        const val CHANNEL_ID_TRIP_REMINDERS = "trip_reminders"
        const val CHANNEL_ID_TRIP_UPDATES = "trip_updates"
        const val CHANNEL_ID_GENERAL = "general"
        
        const val NOTIFICATION_ID_TRIP_START = 1001
        const val NOTIFICATION_ID_ACTIVITIES = 1002
        const val NOTIFICATION_ID_BUDGET = 1003
        const val NOTIFICATION_ID_PACKING = 1004
        const val NOTIFICATION_ID_DOCUMENTS = 1005
    }
    
    init {
        createNotificationChannels()
    }
    
    /**
     * Create notification channels for different types of notifications
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_ID_TRIP_REMINDERS,
                    "Trip Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications about upcoming trips and important reminders"
                },
                NotificationChannel(
                    CHANNEL_ID_TRIP_UPDATES,
                    "Trip Updates",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Updates about your current trips"
                },
                NotificationChannel(
                    CHANNEL_ID_GENERAL,
                    "General",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "General app notifications"
                }
            )
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            channels.forEach { notificationManager.createNotificationChannel(it) }
        }
    }
    
    /**
     * Display a trip milestone notification
     */
    fun showNotification(notification: TripNotification) {
        val channelId = getChannelIdForType(notification.type)
        val notificationId = getNotificationIdForType(notification.type)
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Add extras for deep linking
            putExtra("trip_id", notification.tripId)
            putExtra("notification_type", notification.type.name)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(getIconForType(notification.type))
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setPriority(getPriorityForNotification(notification.priority))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notification.message))
        
        // Add action buttons based on notification type
        addActionButtons(builder, notification)
        
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
    
    /**
     * Get the appropriate channel ID for notification type
     */
    private fun getChannelIdForType(type: NotificationType): String {
        return when (type) {
            NotificationType.TRIP_STARTING_SOON,
            NotificationType.TRIP_STARTING_TODAY,
            NotificationType.PACKING_REMINDER,
            NotificationType.DOCUMENT_REMINDER -> CHANNEL_ID_TRIP_REMINDERS
            
            NotificationType.ADD_ACTIVITIES,
            NotificationType.BUDGET_REMINDER -> CHANNEL_ID_TRIP_UPDATES
            
            else -> CHANNEL_ID_GENERAL
        }
    }
    
    /**
     * Get unique notification ID for each type
     */
    private fun getNotificationIdForType(type: NotificationType): Int {
        return when (type) {
            NotificationType.TRIP_STARTING_SOON,
            NotificationType.TRIP_STARTING_TODAY -> NOTIFICATION_ID_TRIP_START
            NotificationType.ADD_ACTIVITIES -> NOTIFICATION_ID_ACTIVITIES
            NotificationType.BUDGET_REMINDER -> NOTIFICATION_ID_BUDGET
            NotificationType.PACKING_REMINDER -> NOTIFICATION_ID_PACKING
            NotificationType.DOCUMENT_REMINDER -> NOTIFICATION_ID_DOCUMENTS
            else -> System.currentTimeMillis().toInt()
        }
    }
    
    /**
     * Get appropriate icon for notification type
     */
    private fun getIconForType(type: NotificationType): Int {
        return when (type) {
            NotificationType.TRIP_STARTING_SOON,
            NotificationType.TRIP_STARTING_TODAY -> android.R.drawable.ic_dialog_info
            NotificationType.ADD_ACTIVITIES -> android.R.drawable.ic_menu_add
            NotificationType.BUDGET_REMINDER -> android.R.drawable.ic_dialog_alert
            else -> android.R.drawable.ic_dialog_info
        }
    }
    
    /**
     * Convert notification priority to Android priority
     */
    private fun getPriorityForNotification(priority: NotificationPriority): Int {
        return when (priority) {
            NotificationPriority.LOW -> NotificationCompat.PRIORITY_LOW
            NotificationPriority.NORMAL -> NotificationCompat.PRIORITY_DEFAULT
            NotificationPriority.HIGH -> NotificationCompat.PRIORITY_HIGH
            NotificationPriority.URGENT -> NotificationCompat.PRIORITY_MAX
        }
    }
    
    /**
     * Add action buttons to notifications
     */
    private fun addActionButtons(builder: NotificationCompat.Builder, notification: TripNotification) {
        when (notification.type) {
            NotificationType.ADD_ACTIVITIES -> {
                val addActivityIntent = Intent(context, MainActivity::class.java).apply {
                    putExtra("action", "add_activity")
                    putExtra("trip_id", notification.tripId)
                }
                val addActivityPendingIntent = PendingIntent.getActivity(
                    context,
                    2001,
                    addActivityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                builder.addAction(
                    android.R.drawable.ic_menu_add,
                    "Add Activity",
                    addActivityPendingIntent
                )
            }
            
            NotificationType.TRIP_STARTING_SOON -> {
                val viewTripIntent = Intent(context, MainActivity::class.java).apply {
                    putExtra("action", "view_trip")
                    putExtra("trip_id", notification.tripId)
                }
                val viewTripPendingIntent = PendingIntent.getActivity(
                    context,
                    2002,
                    viewTripIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                builder.addAction(
                    android.R.drawable.ic_menu_view,
                    "View Trip",
                    viewTripPendingIntent
                )
            }
            
            else -> {
                // Default action - open app
            }
        }
    }
    
    /**
     * Cancel a specific notification
     */
    fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
    
    /**
     * Cancel all notifications
     */
    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
}
