package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import com.android.tripbook.data.SampleTrips
import com.android.tripbook.model.Review
import com.android.tripbook.model.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockTripViewModel : ViewModel() {

    private val _trips = MutableStateFlow<List<Trip>>(SampleTrips.get())
    val trips: StateFlow<List<Trip>> = _trips

    fun getTripById(id: Int): Trip? =
        _trips.value.find { it.id == id }

    fun addReview(review: Review) {
        // Add to a mutable list or API call
    }
}

