package com.android.tripbook.notifications

import android.content.Context
import android.util.Log
import androidx.work.*
import com.android.tripbook.model.Trip
import com.android.tripbook.model.ItineraryItem
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * Service responsible for scheduling trip-related notifications using WorkManager
 */
object NotificationScheduler {
    
    private const val TAG = "NotificationScheduler"
    
    // Work tags for different notification types
    private const val TRIP_START_WORK_TAG = "trip_start_notification"
    private const val TRIP_END_WORK_TAG = "trip_end_notification"
    private const val ITINERARY_WORK_TAG = "itinerary_notification"
    private const val DAILY_CHECK_WORK_TAG = "daily_check_notification"
    
    private lateinit var workManager: WorkManager
    
    fun initialize(context: Context) {
        workManager = WorkManager.getInstance(context)
        scheduleDailyNotificationCheck()
        Log.d(TAG, "NotificationScheduler initialized")
    }
    
    /**
     * Schedule all notifications for a trip
     */
    fun scheduleNotificationsForTrip(trip: Trip) {
        Log.d(TAG, "Scheduling notifications for trip: ${trip.name}")
        
        // Schedule trip start notifications
        scheduleTripStartNotifications(trip)
        
        // Schedule trip end notifications
        scheduleTripEndNotifications(trip)
        
        // Schedule itinerary notifications
        scheduleItineraryNotifications(trip)
    }
    
    /**
     * Cancel all notifications for a trip
     */
    fun cancelNotificationsForTrip(tripId: String) {
        Log.d(TAG, "Cancelling notifications for trip: $tripId")
        
        workManager.cancelAllWorkByTag("trip_$tripId")
    }
    
    /**
     * Schedule notifications for trip start (3 days, 1 day, and day of)
     */
    private fun scheduleTripStartNotifications(trip: Trip) {
        val startDate = trip.startDate
        val today = LocalDate.now()
        
        // Schedule notifications for 3 days before, 1 day before, and day of
        val notificationDays = listOf(3, 1, 0)
        
        notificationDays.forEach { daysBefore ->
            val notificationDate = startDate.minusDays(daysBefore.toLong())
            
            // Only schedule if the notification date is today or in the future
            if (!notificationDate.isBefore(today)) {
                val delay = calculateDelayUntil(notificationDate, LocalTime.of(9, 0))
                
                if (delay >= 0) {
                    val workRequest = OneTimeWorkRequestBuilder<TripStartNotificationWorker>()
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .setInputData(
                            Data.Builder()
                                .putString("trip_id", trip.id)
                                .putString("trip_name", trip.name)
                                .putInt("days_until", daysBefore)
                                .build()
                        )
                        .addTag("trip_${trip.id}")
                        .addTag(TRIP_START_WORK_TAG)
                        .build()
                    
                    workManager.enqueue(workRequest)
                    Log.d(TAG, "Scheduled trip start notification for ${trip.name} - $daysBefore days before")
                }
            }
        }
    }
    
    /**
     * Schedule notifications for trip end (day of and 1 day before)
     */
    private fun scheduleTripEndNotifications(trip: Trip) {
        val endDate = trip.endDate
        val today = LocalDate.now()
        
        // Schedule notifications for 1 day before and day of trip end
        val notificationDays = listOf(1, 0)
        
        notificationDays.forEach { daysBefore ->
            val notificationDate = endDate.minusDays(daysBefore.toLong())
            
            if (!notificationDate.isBefore(today)) {
                val delay = calculateDelayUntil(notificationDate, LocalTime.of(18, 0))
                
                if (delay >= 0) {
                    val workRequest = OneTimeWorkRequestBuilder<TripEndNotificationWorker>()
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .setInputData(
                            Data.Builder()
                                .putString("trip_id", trip.id)
                                .putString("trip_name", trip.name)
                                .putInt("days_left", daysBefore)
                                .build()
                        )
                        .addTag("trip_${trip.id}")
                        .addTag(TRIP_END_WORK_TAG)
                        .build()
                    
                    workManager.enqueue(workRequest)
                    Log.d(TAG, "Scheduled trip end notification for ${trip.name} - $daysBefore days before end")
                }
            }
        }
    }
    
    /**
     * Schedule notifications for itinerary items (2 hours before each activity)
     */
    private fun scheduleItineraryNotifications(trip: Trip) {
        trip.itinerary.forEach { item ->
            scheduleItineraryItemNotification(trip, item)
        }
    }
    
    private fun scheduleItineraryItemNotification(trip: Trip, item: ItineraryItem) {
        val today = LocalDate.now()
        
        // Only schedule for future activities
        if (!item.date.isBefore(today)) {
            try {
                val activityTime = LocalTime.parse(item.time, DateTimeFormatter.ofPattern("HH:mm"))
                val notificationTime = activityTime.minusHours(2) // 2 hours before
                val delay = calculateDelayUntil(item.date, notificationTime)
                
                if (delay >= 0) {
                    val workRequest = OneTimeWorkRequestBuilder<ItineraryNotificationWorker>()
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .setInputData(
                            Data.Builder()
                                .putString("trip_id", trip.id)
                                .putString("trip_name", trip.name)
                                .putString("activity_title", item.title)
                                .putString("activity_time", item.time)
                                .putString("activity_location", item.location)
                                .build()
                        )
                        .addTag("trip_${trip.id}")
                        .addTag(ITINERARY_WORK_TAG)
                        .build()
                    
                    workManager.enqueue(workRequest)
                    Log.d(TAG, "Scheduled itinerary notification for ${item.title} at ${item.time}")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to parse time for itinerary item: ${item.time}", e)
            }
        }
    }
    
    /**
     * Schedule a daily check for upcoming trips and activities
     */
    private fun scheduleDailyNotificationCheck() {
        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyNotificationCheckWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(calculateDelayUntil(LocalDate.now().plusDays(1), LocalTime.of(8, 0)), TimeUnit.MILLISECONDS)
            .addTag(DAILY_CHECK_WORK_TAG)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            "daily_notification_check",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
        
        Log.d(TAG, "Scheduled daily notification check")
    }
    
    /**
     * Calculate delay in milliseconds until a specific date and time
     */
    private fun calculateDelayUntil(date: LocalDate, time: LocalTime): Long {
        val targetDateTime = LocalDateTime.of(date, time)
        val now = LocalDateTime.now()
        
        return if (targetDateTime.isAfter(now)) {
            java.time.Duration.between(now, targetDateTime).toMillis()
        } else {
            -1 // Past time
        }
    }
    
    /**
     * Get status of scheduled notifications for debugging
     */
    fun getScheduledNotificationsInfo(): String {
        // This would be used for debugging/testing
        return "NotificationScheduler is active with WorkManager"
    }
}
