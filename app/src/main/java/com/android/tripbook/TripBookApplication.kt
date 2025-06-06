package com.android.tripbook

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.android.tripbook.notifications.NotificationHelper
import com.android.tripbook.notifications.NotificationScheduler

/**
 * Application class for TripBook app
 * Initializes notification system and WorkManager
 */
class TripBookApplication : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize notification channels
        NotificationHelper.createNotificationChannel(this)
        
        // Initialize notification scheduler
        NotificationScheduler.initialize(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
