package com.android.tripbook.notifications

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * WorkManager Worker for itinerary item notifications
 */
class ItineraryNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "ItineraryNotificationWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val tripId = inputData.getString("trip_id") ?: return Result.failure()
            val tripName = inputData.getString("trip_name") ?: "Your Trip"
            val activityTitle = inputData.getString("activity_title") ?: "Activity"
            val activityTime = inputData.getString("activity_time") ?: ""
            val activityLocation = inputData.getString("activity_location") ?: ""

            Log.d(TAG, "Showing itinerary notification for $activityTitle at $activityTime")

            val timeUntil = "2 hours" // Since we schedule 2 hours before
            
            NotificationHelper.showItineraryReminder(
                context = applicationContext,
                tripName = tripName,
                activityTitle = activityTitle,
                timeUntil = timeUntil
            )

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing itinerary notification", e)
            Result.failure()
        }
    }
}
