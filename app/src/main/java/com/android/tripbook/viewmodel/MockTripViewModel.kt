package com.android.tripbook.ViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.android.tripbook.data.SampleTrips
import com.android.tripbook.Model.Review
import com.android.tripbook.Model.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MockTripViewModel : ViewModel() {

    private val _trips = MutableStateFlow<List<Trip>>(SampleTrips.get())
    val trips: StateFlow<List<Trip>> = _trips

    // Default ratings for each trip - these persist until app is completely closed
    private val defaultRatings = mutableMapOf<Int, SnapshotStateList<Int>>().apply {
        // Initialize with default ratings for different trips
        put(1, mutableStateListOf(5, 4, 5, 3, 4, 5, 2, 4, 5, 3, 4, 5, 4, 3, 5))
        put(2, mutableStateListOf(4, 5, 3, 4, 4, 5, 3, 4, 5, 4, 3, 5, 4, 5, 4))
        put(3, mutableStateListOf(5, 5, 4, 5, 3, 4, 5, 4, 5, 4, 5, 3, 4, 5, 5))
        put(4, mutableStateListOf(3, 4, 4, 5, 4, 3, 4, 5, 4, 3, 5, 4, 4, 3, 4))
        put(5, mutableStateListOf(5, 4, 5, 5, 4, 5, 3, 4, 5, 4, 5, 4, 5, 4, 5))
        put(6, mutableStateListOf(4, 3, 4, 5, 4, 4, 5, 3, 4, 5, 4, 3, 4, 5, 4))
        put(7, mutableStateListOf(5, 5, 4, 5, 5, 4, 5, 4, 5, 4, 5, 5, 4, 5, 4))
        put(8, mutableStateListOf(3, 4, 5, 4, 3, 4, 5, 4, 3, 5, 4, 4, 3, 4, 5))
    }

    fun getTripById(id: Int): Trip? =
        _trips.value.find { it.id == id }

    /**
     * Get ratings for a specific trip
     * Returns existing ratings or creates default ones if trip doesn't have ratings yet
     */
    fun getRatingsForTrip(tripId: Int): SnapshotStateList<Int> {
        return defaultRatings.getOrPut(tripId) {
            // Create default ratings for new trips
            mutableStateListOf(4, 5, 3, 4, 5, 4, 3, 5, 4, 4)
        }
    }

    /**
     * Add a new rating for a specific trip
     * This will immediately update the rating summary
     */
    fun addRating(tripId: Int, rating: Int) {
        val tripRatings = getRatingsForTrip(tripId)
        tripRatings.add(rating)
    }

    /**
     * Get average rating for a specific trip
     */
    fun getAverageRating(tripId: Int): Float {
        val ratings = getRatingsForTrip(tripId)
        return if (ratings.isNotEmpty()) {
            ratings.average().toFloat()
        } else {
            0f
        }
    }

    /**
     * Get total number of ratings for a specific trip
     */
    fun getTotalRatings(tripId: Int): Int {
        return getRatingsForTrip(tripId).size
    }

    fun addReview(review: Review) {
        // Add to a mutable list or API call
    }

    fun addNewTrip(title: String, caption: String, description: String, imageUrl: String) {
        viewModelScope.launch {
            _trips.update { currentTrips ->
                val newTripId = currentTrips.maxOfOrNull { it.id }?.plus(1) ?: 0

                // Initialize default ratings for the new trip
                defaultRatings[newTripId] = mutableStateListOf(4, 5, 4, 3, 5, 4)

                currentTrips + Trip(
                    id = newTripId,
                    title = title,
                    caption = caption,
                    description = description,
                    imageUrl = listOf(imageUrl) // Single image URL
                )
            }
        }
    }
}