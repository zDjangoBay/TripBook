package com.yourpackage.ui.screens

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}


package com.yourpackage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourpackage.data.SampleTrips
import com.yourpackage.model.Trip
import com.yourpackage.ui.screens.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MockTripViewModel : ViewModel() {

    private val _tripState = MutableStateFlow<UiState<List<Trip>>>(UiState.Loading)
    val tripState: StateFlow<UiState<List<Trip>>> = _tripState

    init {
        loadTrips()
    }

    private fun loadTrips() {
        viewModelScope.launch {
            _tripState.value = UiState.Loading
            delay(1000) // Simulate network delay
            val trips = SampleTrips.trips
            if (trips.isEmpty()) {
                _tripState.value = UiState.Empty
            } else {
                _tripState.value = UiState.Success(trips)
            }
        }
    }

    // Add error simulation or actual API handling as needed
    fun simulateError() {
        _tripState.value = UiState.Error("Failed to load trips.")
    }
}
