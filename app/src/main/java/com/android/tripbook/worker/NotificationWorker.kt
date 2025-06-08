package com.android.tripbook.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.android.tripbook.model.*
import com.android.tripbook.service.NotificationService
import java.time.LocalDateTime

/**
 * Worker class that handles the actual sending of notifications
 */
class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val notificationService = NotificationService(context)
    
    override suspend fun doWork(): Result {
        return try {
            // Extract notification data from input
            val notificationData = extractNotificationData()
            
            // Create and show the notification
            notificationService.showNotification(notificationData)
            
            // Log the notification (in a real app, you might save this to a database)
            logNotification(notificationData)
            
            Result.success()
        } catch (exception: Exception) {
            // Log the error (in a real app, you might use a proper logging framework)
            android.util.Log.e("NotificationWorker", "Failed to send notification", exception)
            
            // Retry the work if it failed due to a temporary issue
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
    
    /**
     * Extract notification data from WorkManager input data
     */
    private fun extractNotificationData(): TripNotification {
        val inputData = inputData
        
        val notificationId = inputData.getString("notification_id") 
            ?: throw IllegalArgumentException("Missing notification_id")
        val tripId = inputData.getString("trip_id") 
            ?: throw IllegalArgumentException("Missing trip_id")
        val title = inputData.getString("title") 
            ?: throw IllegalArgumentException("Missing title")
        val message = inputData.getString("message") 
            ?: throw IllegalArgumentException("Missing message")
        val typeString = inputData.getString("notification_type") 
            ?: throw IllegalArgumentException("Missing notification_type")
        val priorityString = inputData.getString("priority") ?: "NORMAL"
        val actionUrl = inputData.getString("action_url")
        
        // Parse enums
        val type = try {
            NotificationType.valueOf(typeString)
        } catch (e: IllegalArgumentException) {
            NotificationType.TRIP_STARTING_SOON // Default fallback
        }
        
        val priority = try {
            NotificationPriority.valueOf(priorityString)
        } catch (e: IllegalArgumentException) {
            NotificationPriority.NORMAL // Default fallback
        }
        
        // Create metadata from additional input data
        val metadata = mutableMapOf<String, String>()
        inputData.getString("trip_name")?.let { metadata["trip_name"] = it }
        inputData.getString("destination")?.let { metadata["destination"] = it }
        
        return TripNotification(
            id = notificationId,
            tripId = tripId,
            type = type,
            title = title,
            message = message,
            scheduledTime = LocalDateTime.now(),
            priority = priority,
            isRead = false,
            isSent = true,
            actionUrl = actionUrl,
            metadata = metadata
        )
    }
    
    /**
     * Log the notification for analytics/debugging purposes
     */
    private fun logNotification(notification: TripNotification) {
        android.util.Log.i(
            "NotificationWorker", 
            "Sent notification: ${notification.type} for trip ${notification.tripId}"
        )
        
        // In a real app, you might:
        // 1. Save to local database
        // 2. Send analytics event
        // 3. Update notification status in backend
        // 4. Track user engagement metrics
    }
}
