package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.model.Trip
import com.android.tripbook.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TripSearchViewModel(private val tripRepository: TripRepository) : ViewModel() {

    private val _filteredTrips = MutableStateFlow<List<Trip>>(emptyList())
    val filteredTrips: StateFlow<List<Trip>> = _filteredTrips

    fun searchTrips(
        destination: String? = null,
        budgetRange: Pair<Double, Double>? = null,
        dateRange: Pair<String, String>? = null,
        status: String? = null,
        travelers: Int? = null
    ) {
        viewModelScope.launch {
            val allTrips = tripRepository.getTrips().first() // Collecting the trips from the repository

            val results = allTrips.filter { trip ->
                (destination == null || trip.destination.contains(destination, ignoreCase = true)) &&
                        (budgetRange == null || (trip.budget in budgetRange.first..budgetRange.second)) &&
                        (dateRange == null || (trip.startDate.toString() >= dateRange.first && trip.endDate.toString() <= dateRange.second)) &&
                        (status == null || trip.status.name.equals(status, ignoreCase = true)) &&
                        (travelers == null || trip.travelers == travelers)
            }

            _filteredTrips.value = results
        }
    }
}