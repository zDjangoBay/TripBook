package com.android.tripbook.notifications

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * WorkManager Worker for trip end notifications
 */
class TripEndNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "TripEndNotificationWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val tripId = inputData.getString("trip_id") ?: return Result.failure()
            val tripName = inputData.getString("trip_name") ?: "Your Trip"
            val daysLeft = inputData.getInt("days_left", 0)

            Log.d(TAG, "Showing trip end notification for $tripName - $daysLeft days left")

            NotificationHelper.showTripEndingNotification(
                context = applicationContext,
                tripName = tripName,
                daysLeft = daysLeft
            )

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing trip end notification", e)
            Result.failure()
        }
    }
}
