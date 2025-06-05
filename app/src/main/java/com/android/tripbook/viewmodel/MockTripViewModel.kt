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
        city: String,          // Added
        country: String,       // Added
        latitude: Double,      // Added
        longitude: Double,     // Added
        imageUrl: String,      // Assuming a single main image URL string is passed here
        caption: String = "",  // Uses default from Trip model if not overridden by caller
        region: String? = null // Uses default from Trip model if not overridden by caller
        // Add other fields like rating, reviewCount, duration if they are not
        // meant to always use the defaults from the Trip model when calling this function.
        // For now, they will use defaults from Trip model (e.g., 0.0f for rating if defined there)
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
                    imageUrl = listOf(imageUrl), // Convert single URL string to a list
                    caption = caption,           // Pass along caption
                    region = region              // Pass along region
                    // rating, reviewCount, duration etc., will use defaults from Trip model
                    // if your Trip model defines them and they are not passed here.
                    // Based on your provided Trip model, rating, reviewCount, duration were commented out,
                    // so they are not parameters here. If you uncomment them in Trip model
                    // and they don't have defaults, they'd need to be added here too.
                )
            }
        }
    }
}