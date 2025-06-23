package com.android.tripbook.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.Model.Review
import com.android.tripbook.model.Trip
import com.android.tripbook.database.TripBookDatabase
import com.android.tripbook.database.repository.LocalTripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * NEW Room-based ViewModel for Trip data
 * This is an ALTERNATIVE to MockTripViewModel
 * Team members can choose which one to use:
 * - MockTripViewModel: Uses static data (existing, unchanged)
 * - RoomTripViewModel: Uses Room database (new, optional)
 */
class RoomTripViewModel(application: Application) : AndroidViewModel(application) {

    private val database = TripBookDatabase.getDatabase(application)
    private val repository = LocalTripRepository(database.tripDao())

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()

    init {
        // Load trips from database
        viewModelScope.launch {
            repository.getAllTrips().collect { tripList ->
                _trips.value = tripList
            }
        }
        
        // Seed data if database is empty
        viewModelScope.launch {
            repository.seedDataIfEmpty()
        }
    }

    /**
     * Same interface as MockTripViewModel for compatibility
     */
    fun getTripById(id: Int): Trip? =
        _trips.value.find { it.id == id }

    fun addReview(review: Review) {
        // This will be handled by RoomReviewViewModel
        // Keeping for compatibility with MockTripViewModel interface
    }
    
    /**
     * NEW Room-specific methods (not available in MockTripViewModel)
     */
    fun addTrip(trip: Trip) {
        viewModelScope.launch {
            repository.insertTrip(trip)
        }
    }
    
    fun updateTrip(trip: Trip) {
        viewModelScope.launch {
            repository.updateTrip(trip)
        }
    }
    
    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            repository.deleteTrip(trip)
        }
    }
}
