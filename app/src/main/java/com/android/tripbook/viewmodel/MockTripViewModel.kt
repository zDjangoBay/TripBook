package com.android.tripbook.ViewModel

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

    fun getTripById(id: Int): Trip? =
        _trips.value.find { it.id == id }

    fun addReview(review: Review) {
        // Add to a mutable list or API call
    }

    fun addNewTrip(title: String, caption: String, description: String, imageUrl: String) {
        viewModelScope.launch {
            _trips.update { currentTrips ->
                currentTrips + Trip(
                    id = currentTrips.maxOfOrNull { it.id }?.plus(1) ?: 0, // Auto-increment ID
                    title = title,
                    caption = caption,
                    description = description,
                    imageUrl = listOf(imageUrl) // Single image URL
                )
            }
        }
    }
}

