package com.android.tripbook.notifications

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.Trip
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

/**
 * Utility object for scheduling various trip-related notifications using WorkManager.
 */
object NotificationScheduler {

    private const val TAG = "NotificationScheduler"

    /**
     * Schedules a notification for a trip starting soon.
     *
     * @param context The application context.
     * @param trip The Trip object for which to schedule the notification.
     * @param daysBefore The number of days before the trip start date to trigger the notification.
     */
    fun scheduleTripStartNotification(context: Context, trip: Trip, daysBefore: Long) {
        // Calculate the notification time: daysBefore days before the trip's start date, at 9:00 AM
        val notificationDateTime = trip.startDate.atTime(9, 0) // 9 AM on the notification day
            .minusDays(daysBefore)

        // Ensure the notification time is in the future
        val now = LocalDateTime.now()
        if (notificationDateTime.isBefore(now)) {
            Log.d(TAG, "Notification time for trip '${trip.name}' is in the past. Not scheduling.")
            return
        }

        // Calculate the delay in milliseconds
        val delayMillis = Duration.between(now, notificationDateTime).toMillis()

        // Create input data for the worker
        val inputData = Data.Builder()
            .putInt(TripNotificationWorker.KEY_NOTIFICATION_ID, trip.id.hashCode() + daysBefore.toInt()) // Unique ID for this notification
            .putString(TripNotificationWorker.KEY_NOTIFICATION_TITLE, "Upcoming Trip!")
            .putString(TripNotificationWorker.KEY_NOTIFICATION_MESSAGE, "Your trip to ${trip.destination} starts in $daysBefore day(s)! Get ready for your adventure.")
            .build()

        // Create a unique work request name to avoid duplicate notifications for the same trip milestone
        val workName = "trip_start_notification_${trip.id}_${daysBefore}days"

        val workRequest = OneTimeWorkRequestBuilder<TripNotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("trip_notification") // Tag for easy cancellation/querying
            .build()

        // Enqueue the work request, keeping existing work if it's already scheduled
        WorkManager.getInstance(context).enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.REPLACE, // REPLACE ensures that if the trip details change, the old notification is cancelled and a new one is scheduled.
            workRequest
        )
        Log.d(TAG, "Scheduled trip start notification for '${trip.name}' in $daysBefore days. Delay: ${delayMillis / (1000 * 60 * 60 * 24)} days.")
    }

    /**
     * Schedules a notification for a specific itinerary item.
     *
     * @param context The application context.
     * @param tripName The name of the trip this itinerary item belongs to.
     * @param item The ItineraryItem object for which to schedule the notification.
     */
    fun scheduleItineraryItemNotification(context: Context, tripName: String, item: ItineraryItem) {
        // Parse time string to LocalTime
        val itemTime = try {
            // Assuming time is in "HH:MM AM/PM" format (e.g., "10:00 AM")
            // This is a simplified parsing. For robust parsing, consider a dedicated formatter.
            val (hourStr, minuteStr) = item.time.split(" ")[0].split(":")
            val hour = hourStr.toInt()
            val minute = minuteStr.toInt()
            val isPm = item.time.contains("PM", ignoreCase = true)

            // Adjust hour for PM if not 12 PM
            LocalTime.of(if (isPm && hour != 12) hour + 12 else if (!isPm && hour == 12) 0 else hour, minute)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing itinerary item time: ${item.time}", e)
            // Fallback to a default time if parsing fails
            LocalTime.of(9, 0)
        }

        // Combine date and time
        val notificationDateTime = item.date.atTime(itemTime)

        // Ensure the notification time is in the future
        val now = LocalDateTime.now()
        if (notificationDateTime.isBefore(now)) {
            Log.d(TAG, "Notification time for itinerary item '${item.title}' is in the past. Not scheduling.")
            return
        }

        // Calculate the delay in milliseconds
        val delayMillis = Duration.between(now, notificationDateTime).toMillis()

        // Create input data for the worker
        val inputData = Data.Builder()
            .putInt(TripNotificationWorker.KEY_NOTIFICATION_ID, item.id.hashCode()) // Unique ID for this notification
            .putString(TripNotificationWorker.KEY_NOTIFICATION_TITLE, "Itinerary Reminder for $tripName")
            .putString(TripNotificationWorker.KEY_NOTIFICATION_MESSAGE, "Your activity '${item.title}' at ${item.location} is scheduled for today at ${item.time}!")
            .build()

        // Create a unique work request name for this itinerary item
        val workName = "itinerary_item_notification_${item.id}"

        val workRequest = OneTimeWorkRequestBuilder<TripNotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("itinerary_notification")
            .build()

        // Enqueue the work request
        WorkManager.getInstance(context).enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.REPLACE, // REPLACE ensures that if the item's details change, the old notification is cancelled and a new one is scheduled.
            workRequest
        )
        Log.d(TAG, "Scheduled itinerary item notification for '${item.title}'. Delay: ${delayMillis / 1000} seconds.")
    }

    /**
     * Schedules a reminder to add activities if the trip itinerary is empty.
     * This is designed as a one-time check for a new trip.
     * For a more robust solution, consider a periodic worker that checks all active trips.
     *
     * @param context The application context.
     * @param trip The Trip object to check.
     * @param daysAfterCreation The number of days after trip creation to check if itinerary is empty.
     */
    fun scheduleItineraryReminder(context: Context, trip: Trip, daysAfterCreation: Long) {
        // For simplicity, let's assume trip creation date is 'now' for this reminder.
        // In a real app, you might store the trip creation timestamp.
        val reminderDateTime = LocalDateTime.now().plusDays(daysAfterCreation).withHour(10).withMinute(0) // 10 AM, X days after creation

        val now = LocalDateTime.now()
        if (reminderDateTime.isBefore(now)) {
            Log.d(TAG, "Itinerary reminder time for trip '${trip.name}' is in the past. Not scheduling.")
            return
        }

        val delayMillis = Duration.between(now, reminderDateTime).toMillis()

        val inputData = Data.Builder()
            .putInt(TripNotificationWorker.KEY_NOTIFICATION_ID, trip.id.hashCode() + 999) // Unique ID
            .putString(TripNotificationWorker.KEY_NOTIFICATION_TITLE, "Don't Forget Your Trip!")
            .putString(TripNotificationWorker.KEY_NOTIFICATION_MESSAGE, "Your trip to ${trip.destination} is coming up, but your itinerary is empty! Add some activities now.")
            .build()

        val workName = "itinerary_empty_reminder_${trip.id}"

        val workRequest = OneTimeWorkRequestBuilder<TripNotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("itinerary_reminder")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        Log.d(TAG, "Scheduled itinerary empty reminder for '${trip.name}'. Delay: ${delayMillis / (1000 * 60 * 60 * 24)} days.")
    }
}


