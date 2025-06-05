package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripCreationState
import com.android.tripbook.repository.SupabaseTripRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TripViewModel(
    private val repository: SupabaseTripRepository = SupabaseTripRepository.getInstance()
) : ViewModel() {

    val trips: StateFlow<List<Trip>> = repository.trips
    val isLoading: StateFlow<Boolean> = repository.isLoading
    val error: StateFlow<String?> = repository.error

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
            val trip = tripCreationState.toTrip()
            val result = repository.addTrip(trip)

            if (result.isFailure) {
                // Error is already handled in repository
                result.exceptionOrNull()?.let { exception ->
                    // Additional error handling if needed
                }
            }
        }
    }

    fun updateTrip(trip: Trip) {
        viewModelScope.launch {
            val result = repository.updateTrip(trip)

            if (result.isFailure) {
                // Error is already handled in repository
                result.exceptionOrNull()?.let { exception ->
                    // Additional error handling if needed
                }
            }
        }
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            val result = repository.deleteTrip(tripId)

            if (result.isFailure) {
                // Error is already handled in repository
                result.exceptionOrNull()?.let { exception ->
                    // Additional error handling if needed
                }
            }
        }
    }

    fun getTripById(tripId: String): Trip? {
        return repository.getTripById(tripId)
    }

    fun clearError() {
        repository.clearError()
    }
}
