package com.android.tripbook.notifications

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.android.tripbook.repository.SupabaseTripRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate

/**
 * WorkManager Worker that runs daily to check for upcoming trips and activities
 * This ensures notifications are sent even if the app hasn't been opened recently
 */
class DailyNotificationCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "DailyNotificationCheckWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Running daily notification check")
            
            val repository = SupabaseTripRepository.getInstance()
            
            // Load all trips
            repository.loadTrips()
            val trips = repository.trips.first()
            
            val today = LocalDate.now()
            
            trips.forEach { trip ->
                // Check if trip starts today
                if (trip.startDate == today) {
                    NotificationHelper.showTripStartingNotification(
                        context = applicationContext,
                        tripName = trip.name,
                        daysUntil = 0
                    )
                }
                
                // Check if trip ends today
                if (trip.endDate == today) {
                    NotificationHelper.showTripEndingNotification(
                        context = applicationContext,
                        tripName = trip.name,
                        daysLeft = 0
                    )
                }
                
                // Check for activities today
                trip.itinerary.filter { it.date == today }.forEach { activity ->
                    // Show reminder for activities happening today
                    NotificationHelper.showItineraryReminder(
                        context = applicationContext,
                        tripName = trip.name,
                        activityTitle = activity.title,
                        timeUntil = "today at ${activity.time}"
                    )
                }
            }
            
            Log.d(TAG, "Daily notification check completed successfully")
            Result.success()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during daily notification check", e)
            Result.retry()
        }
    }
}
