package com.android.tripbook.notifications

import android.content.Context
import androidx.work.CoroutineWorker // <--- ADD THIS IMPORT
import androidx.work.WorkerParameters // <--- ADD THIS IMPORT
import androidx.work.ListenableWorker.Result // <--- ADD THIS IMPORT (explicitly for Result)


/**
 * A WorkManager Worker responsible for displaying trip-related notifications.
 * It receives notification details via input data.
 */
class TripNotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    // Keys for input data
    companion object {
        const val KEY_NOTIFICATION_ID = "notification_id"
        const val KEY_NOTIFICATION_TITLE = "notification_title"
        const val KEY_NOTIFICATION_MESSAGE = "notification_message"
    }

    override suspend fun doWork(): Result { // 'Result' here refers to androidx.work.ListenableWorker.Result
        // Retrieve notification details from input data
        val notificationId = inputData.getInt(KEY_NOTIFICATION_ID, 0)
        val title = inputData.getString(KEY_NOTIFICATION_TITLE) ?: "Trip Reminder"
        val message = inputData.getString(KEY_NOTIFICATION_MESSAGE) ?: "You have an upcoming trip event!"

        // Show the notification using the NotificationHelper
        NotificationHelper.showNotification(applicationContext, notificationId, title, message)

        return Result.success() // Reference the success method from ListenableWorker.Result
    }
}


