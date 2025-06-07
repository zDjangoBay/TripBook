package com.android.tripbook.service

import android.content.Context
import androidx.work.*
import com.android.tripbook.model.*
import com.android.tripbook.worker.NotificationWorker
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

/**
 * Service responsible for scheduling trip milestone notifications using WorkManager
 */
class NotificationScheduler(private val context: Context) {
    
    private val workManager = WorkManager.getInstance(context)
    private val milestoneCalculator = MilestoneCalculatorService()
    
    /**
     * Schedule all notifications for a trip
     */
    fun scheduleNotificationsForTrip(trip: Trip, preferences: NotificationPreferences) {
        // Cancel existing notifications for this trip
        cancelNotificationsForTrip(trip.id)
        
        // Calculate milestones
        val milestones = milestoneCalculator.calculateMilestonesForTrip(trip, preferences)
        
        // Schedule each milestone
        milestones.forEach { milestone ->
            scheduleNotificationForMilestone(milestone, trip, preferences)
        }
    }
    
    /**
     * Schedule a single notification for a milestone
     */
    private fun scheduleNotificationForMilestone(
        milestone: TripMilestone,
        trip: Trip,
        preferences: NotificationPreferences
    ) {
        // Check if notification should be scheduled (not in quiet hours, etc.)
        if (!shouldScheduleNotification(milestone.triggerDate, preferences)) {
            return
        }
        
        // Calculate delay until notification should be sent
        val now = LocalDateTime.now()
        val delay = calculateDelayUntilTrigger(now, milestone.triggerDate)
        
        if (delay <= 0) {
            // If the time has already passed, don't schedule
            return
        }
        
        // Create work request
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(createInputDataForMilestone(milestone, trip))
            .addTag("trip_notification")
            .addTag("trip_${trip.id}")
            .addTag("milestone_${milestone.id}")
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()
        
        // Schedule the work
        workManager.enqueueUniqueWork(
            "notification_${milestone.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    /**
     * Create input data for the notification worker
     */
    private fun createInputDataForMilestone(milestone: TripMilestone, trip: Trip): Data {
        val notification = milestoneCalculator.generateNotificationForMilestone(milestone, trip)
        
        return Data.Builder()
            .putString("notification_id", notification.id)
            .putString("trip_id", trip.id)
            .putString("trip_name", trip.name)
            .putString("destination", trip.destination)
            .putString("notification_type", milestone.type.name)
            .putString("title", notification.title)
            .putString("message", notification.message)
            .putString("priority", notification.priority.name)
            .putString("action_url", notification.actionUrl)
            .build()
    }
    
    /**
     * Calculate delay in milliseconds until the trigger time
     */
    private fun calculateDelayUntilTrigger(now: LocalDateTime, triggerTime: LocalDateTime): Long {
        val nowMillis = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val triggerMillis = triggerTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return triggerMillis - nowMillis
    }
    
    /**
     * Check if notification should be scheduled based on preferences
     */
    private fun shouldScheduleNotification(
        triggerTime: LocalDateTime,
        preferences: NotificationPreferences
    ): Boolean {
        // Check if push notifications are enabled
        if (!preferences.enablePushNotifications) {
            return false
        }
        
        // Check quiet hours
        val hour = triggerTime.hour
        if (isInQuietHours(hour, preferences.quietHoursStart, preferences.quietHoursEnd)) {
            return false
        }
        
        return true
    }
    
    /**
     * Check if the given hour is within quiet hours
     */
    private fun isInQuietHours(hour: Int, quietStart: Int, quietEnd: Int): Boolean {
        return if (quietStart <= quietEnd) {
            // Same day quiet hours (e.g., 22:00 to 08:00 next day)
            hour >= quietStart || hour < quietEnd
        } else {
            // Quiet hours span midnight (e.g., 10 PM to 8 AM)
            hour >= quietStart && hour < quietEnd
        }
    }
    
    /**
     * Cancel all notifications for a specific trip
     */
    fun cancelNotificationsForTrip(tripId: String) {
        workManager.cancelAllWorkByTag("trip_$tripId")
    }
    
    /**
     * Cancel a specific milestone notification
     */
    fun cancelNotificationForMilestone(milestoneId: String) {
        workManager.cancelUniqueWork("notification_$milestoneId")
    }
    
    /**
     * Cancel all trip notifications
     */
    fun cancelAllTripNotifications() {
        workManager.cancelAllWorkByTag("trip_notification")
    }
    
    /**
     * Reschedule notifications for a trip (useful when trip details change)
     */
    fun rescheduleNotificationsForTrip(trip: Trip, preferences: NotificationPreferences) {
        scheduleNotificationsForTrip(trip, preferences)
    }
    
    /**
     * Get scheduled notifications for a trip
     */
    fun getScheduledNotificationsForTrip(tripId: String): List<WorkInfo> {
        return try {
            workManager.getWorkInfosByTag("trip_$tripId").get()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Schedule immediate notification (for testing or manual triggers)
     */
    fun scheduleImmediateNotification(trip: Trip, type: NotificationType) {
        val milestone = TripMilestone(
            id = "${trip.id}_immediate_${System.currentTimeMillis()}",
            tripId = trip.id,
            type = type,
            triggerDate = LocalDateTime.now()
        )
        
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(createInputDataForMilestone(milestone, trip))
            .addTag("immediate_notification")
            .build()
        
        workManager.enqueue(workRequest)
    }
    
    /**
     * Update notification preferences and reschedule all notifications
     */
    fun updateNotificationPreferences(
        preferences: NotificationPreferences,
        trips: List<Trip>
    ) {
        // Cancel all existing notifications
        cancelAllTripNotifications()
        
        // Reschedule notifications for all trips with new preferences
        trips.forEach { trip ->
            if (trip.status == TripStatus.PLANNED || trip.status == TripStatus.ACTIVE) {
                scheduleNotificationsForTrip(trip, preferences)
            }
        }
    }
}
