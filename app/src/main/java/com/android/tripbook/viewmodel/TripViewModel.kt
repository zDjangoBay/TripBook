package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripCategory
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

    // --- Budget/Statistics Logic ---

    fun getTotalPlannedBudget(): Double {
        return trips.value.sumOf { it.plannedBudget ?: (it.budget?.toDouble() ?: 0.0) }
    }

    fun getTotalActualCost(): Double {
        return trips.value.sumOf { it.actualCost ?: 0.0 }
    }

    fun getBudgetByCategory(category: TripCategory): Double {
        return trips.value
            .filter { it.category == category || (it.categories?.contains(category) == true) }
            .sumOf { it.plannedBudget ?: (it.budget?.toDouble() ?: 0.0) }
    }

    fun getTripsByCategory(category: TripCategory): List<Trip> {
        return trips.value.filter { it.category == category || (it.categories?.contains(category) == true) }
    }

    // --- Navigation Logic (stubs, expand as needed) ---

    fun navigateToBudgetScreen(onNavigate: () -> Unit) {
        onNavigate()
    }

    fun navigateToTripDetails(tripId: String, onNavigate: (String) -> Unit) {
        onNavigate(tripId)
    }
}