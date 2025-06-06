package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripCreationState
import com.android.tripbook.repository.SupabaseTripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TripViewModel(
    private val repository: SupabaseTripRepository = SupabaseTripRepository.getInstance()
) : ViewModel() {

    val trips: StateFlow<List<Trip>> = repository.trips
    val isLoading: StateFlow<Boolean> = repository.isLoading
    val error: StateFlow<String?> = repository.error

    // New: StateFlow to indicate trip creation success
    private val _tripCreationSuccess = MutableStateFlow(false)
    val tripCreationSuccess: StateFlow<Boolean> = _tripCreationSuccess.asStateFlow()

    init {
        // Load trips when ViewModel is created
        loadTrips()
    }

    private fun loadTrips() {
        viewModelScope.launch {
            repository.loadTrips()
        }
    }

    fun refreshTrips() {
        loadTrips()
    }

    fun createTrip(tripCreationState: TripCreationState) {
        viewModelScope.launch {
            _tripCreationSuccess.value = false // Reset before new attempt
            val trip = tripCreationState.toTrip()
            val result = repository.addTrip(trip)

            if (result.isSuccess) { // Check for success
                _tripCreationSuccess.value = true // Set to true on success
            } else {
                // Error is already handled in repository, but you can add more here if needed
                result.exceptionOrNull()?.let { exception ->
                    // Additional error handling if needed, e.g., logging
                }
            }
        }
    }

    // New: Function to clear the trip creation success flag
    fun clearTripCreationSuccess() {
        _tripCreationSuccess.value = false
    }

    fun getTripById(tripId: String): Trip? {
        return repository.getTripById(tripId)
    }

    fun clearError() {
        repository.clearError()
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            repository.deleteTrip(tripId)
            refreshTrips()
        }
    }

    fun updateTrip(trip: Trip) {
        viewModelScope.launch {
            repository.updateTrip(trip)
            refreshTrips()
        }
    }
}