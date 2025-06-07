package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment

import android.app.Application
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.database.TripBookDatabase
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.database.DatabaseInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * TripBook Application Class
 * 
 * This class handles application-level initialization including
 * database setup and dependency injection preparation.
 * 
 * Key Features:
 * - Database initialization on first launch
 * - Application-wide resource management
 * - Dependency injection setup (future)
 * - Global configuration
 * 
 * Used by:
 * - Android system for app initialization
 * - Database access throughout the app
 * - Future dependency injection framework
 */
class TripBookApplication : Application() {
    
    /**
     * Database instance - lazy initialization
     */
    val database by lazy { TripBookDatabase.getDatabase(this) }
    
    /**
     * Application scope for background operations
     */
    private val applicationScope = CoroutineScope(Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize database with sample data on first launch
        initializeDatabase()
    }
    
    /**
     * Initialize database with sample data
     * This runs in the background to avoid blocking the main thread
     */
    private fun initializeDatabase() {
        applicationScope.launch {
            try {
                DatabaseInitializer.initializeDatabase(database)
            } catch (e: Exception) {
                // Log error but don't crash the app
                android.util.Log.e("TripBookApp", "Failed to initialize database", e)
            }
        }
    }
}
