package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripCreationState
import com.android.tripbook.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TripViewModel(
    private val repository: TripRepository = TripRepository.getInstance()
) : ViewModel() {

    val trips: StateFlow<List<Trip>> = repository.trips

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun createTrip(tripCreationState: TripCreationState) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val trip = tripCreationState.toTrip()
                repository.addTrip(trip)
                
            } catch (e: Exception) {
                _error.value = "Failed to create trip: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTrip(trip: Trip) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                repository.updateTrip(trip)
                
            } catch (e: Exception) {
                _error.value = "Failed to update trip: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                repository.deleteTrip(tripId)
                
            } catch (e: Exception) {
                _error.value = "Failed to delete trip: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getTripById(tripId: String): Trip? {
        return repository.getTripById(tripId)
    }

    fun clearError() {
        _error.value = null
    }
}
