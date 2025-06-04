package com.android.tripbook.data.services

import com.android.tripbook.data.database.TripBookDatabase
import com.android.tripbook.data.services.transport.TransportService
import com.android.tripbook.data.services.transport.TransportServiceImpl
import com.android.tripbook.data.services.trip.TripService
import com.android.tripbook.data.services.trip.TripServiceImpl

/**
 * Service provider that creates and provides access to all services
 * Acts as a simple dependency injection container
 */
object ServiceProvider {
    
    private var tripService: TripService? = null
    private var transportService: TransportService? = null
    
    /**
     * Get the TripService instance
     */
    fun getTripService(database: TripBookDatabase): TripService {
        return tripService ?: synchronized(this) {
            tripService ?: TripServiceImpl(database.tripDao()).also { tripService = it }
        }
    }
    
    /**
     * Get the TransportService instance
     */
    fun getTransportService(database: TripBookDatabase): TransportService {
        return transportService ?: synchronized(this) {
            transportService ?: TransportServiceImpl(database.transportDao()).also { transportService = it }
        }
    }
    
    /**
     * Reset all services (useful for testing)
     */
    fun resetServices() {
        tripService = null
        transportService = null
    }
}