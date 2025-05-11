package com.android.tripbook

import android.app.Application
import com.android.tripbook.datamining.data.database.TripBookDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Application class for TripBook
 */
class TripBookApplication : Application() {
    // Database instance
    val database by lazy {
        TripBookDatabase.getDatabase(this, CoroutineScope(Dispatchers.IO))
    }
}
