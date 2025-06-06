package com.android.tripbook.viewmodel // Or your actual package

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Ensure this import is present
import com.android.tripbook.data.SampleTrips // Assuming this is still used for initial data
import com.android.tripbook.model.Review // Assuming you have this model
import com.android.tripbook.model.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MockTripViewModel : ViewModel() {

    // Initialize with SampleTrips or an empty list if SampleTrips isn't used for initial state here
    private val _trips = MutableStateFlow<List<Trip>>(SampleTrips.get())
    val trips: StateFlow<List<Trip>> = _trips

    fun getTripById(id: Int): Trip? =
        _trips.value.find { it.id == id }

    fun addReview(review: Review) {
        // Placeholder: Implement review adding logic
        // For example, find the trip and add the review to it, then update _trips
        // This is complex and depends on how reviews are associated with trips
        Log.d("MockTripViewModel", "addReview called with: $review (Not yet implemented)")
    }

    /**
     * Adds a new trip to the list.
     * This function now requires all necessary fields to construct a valid Trip object.
     */
    fun addNewTrip(
        title: String,
        description: String,
        city: String,
        country: String,
        latitude: Double,
        longitude: Double,
        imageUrl: String,
        caption: String = "",
        region: String? = null
    ) {
        viewModelScope.launch {
            _trips.update { currentTrips ->
                val newId = currentTrips.maxOfOrNull { it.id }?.plus(1) ?: 0
                currentTrips + Trip(
                    id = newId,
                    title = title,
                    description = description,
                    city = city,
                    country = country,
                    latitude = latitude,
                    longitude = longitude,
                    imageUrl = listOf(imageUrl),
                    caption = caption,
                    region = region
                     )
            }
        }
    }
}