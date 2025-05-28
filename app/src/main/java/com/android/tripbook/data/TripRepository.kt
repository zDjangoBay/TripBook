package com.android.tripbook.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.android.tripbook.ui.screens.dashboard.Trip
import java.util.UUID

/**
 * Repository for managing trip data across the application.
 */
object TripRepository {
    // Mutable state list to hold all trips
    private val _trips = mutableStateListOf<Trip>(
        // Initial sample data
        Trip(
            id = "1",
            destination = "Paris, France",
            startDate = "May 1, 2024",
            endDate = "May 7, 2024",
            description = "A week-long exploration of the City of Light",
            completionStatus = 0.8f
        ),
        Trip(
            id = "2",
            destination = "Tokyo, Japan",
            startDate = "June 15, 2024",
            endDate = "June 25, 2024",
            description = "Discovering Japanese culture and cuisine",
            completionStatus = 0.3f
        )
    )
    
    // Public read-only access to trips
    val trips: SnapshotStateList<Trip> = _trips
    
    /**
     * Add a new trip to the repository
     */
    fun addTrip(destination: String, startDate: String, endDate: String, name: String, purpose: String): String {
        val id = UUID.randomUUID().toString()
        val newTrip = Trip(
            id = id,
            destination = destination,
            startDate = startDate,
            endDate = endDate,
            description = "$name: $purpose",
            completionStatus = 0.0f
        )
        _trips.add(newTrip)
        return id
    }
    
    /**
     * Get a trip by its ID
     */
    fun getTripById(id: String): Trip? {
        return _trips.find { it.id == id }
    }
    
    /**
     * Remove all sample data
     */
    fun clearSampleData() {
        _trips.removeAll { it.id == "1" || it.id == "2" }
    }
    
    /**
     * Delete a trip by its ID
     */
    fun deleteTrip(id: String) {
        _trips.removeAll { it.id == id }
    }
}