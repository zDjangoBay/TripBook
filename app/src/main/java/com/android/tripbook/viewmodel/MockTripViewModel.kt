
package com.android.tripbook.ViewModel
package com.android.tripbook.viewmodel // Or your actual package
import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.tripbook.data.SampleTrips
import com.android.tripbook.Model.Review
import com.android.tripbook.Model.Trip

import androidx.lifecycle.viewModelScope // Ensure this import is present
import com.android.tripbook.data.SampleTrips // Assuming this is still used for initial data
import com.android.tripbook.model.Review // Assuming you have this model
import com.android.tripbook.model.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MockTripViewModel : ViewModel() {

    private val _trips = MutableStateFlow<List<Trip>>(SampleTrips.get())
    val trips: StateFlow<List<Trip>> = _trips

    fun getTripById(id: Int): Trip? =
        _trips.value.find { it.id == id }

    fun addReview(review: Review) {

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