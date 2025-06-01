package com.android.tripbook

import android.app.Application
import com.android.tripbook.di.ServiceLocator

/**
 * Application class for the TripBook app
 */
class TripBookApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize ServiceLocator
        ServiceLocator.initialize(applicationContext)
    }
}
