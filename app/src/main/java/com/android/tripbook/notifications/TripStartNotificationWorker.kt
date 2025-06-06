package com.android.tripbook.notifications

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * WorkManager Worker for trip start notifications
 */
class TripStartNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "TripStartNotificationWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val tripId = inputData.getString("trip_id") ?: return Result.failure()
            val tripName = inputData.getString("trip_name") ?: "Your Trip"
            val daysUntil = inputData.getInt("days_until", 0)

            Log.d(TAG, "Showing trip start notification for $tripName - $daysUntil days until")

            NotificationHelper.showTripStartingNotification(
                context = applicationContext,
                tripName = tripName,
                daysUntil = daysUntil
            )

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing trip start notification", e)
            Result.failure()
        }
    }
}
