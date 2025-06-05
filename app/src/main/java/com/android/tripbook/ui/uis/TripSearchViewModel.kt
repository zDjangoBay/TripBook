package com.android.tripbook.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripStatus
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Data class representing the current state of all trip filters.
 * Consolidates all filter parameters into a single, immutable object.
 */
data class TripFilterState(
    val searchText: String = "",
    val destination: String? = null,
    val minBudget: Double? = null,
    val maxBudget: Double? = null,
    val status: TripStatus? = null,
    val minTravelers: Int? = null,
    val maxTravelers: Int? = null,
    val startDateFrom: LocalDate? = null,
    val startDateTo: LocalDate? = null

)

/**
 * ViewModel for managing trip search and filtering logic.
 * This ViewModel holds the UI state related to search queries, filters,
 * and the resulting filtered list of trips. This version uses a single
 * TripFilterState data class to manage all filter parameters.
 */
class TripSearchViewModel(
    private val tripRepository: TripSearchViewModel // Dependency on TripRepository
) : ViewModel() {

    // All trips fetched from the repository
    private val _allTrips = MutableStateFlow<List<Trip>>(emptyList())

    // Single MutableStateFlow holding the current state of all filters
    private val _filterState = MutableStateFlow(TripFilterState())
    val filterState: StateFlow<TripFilterState> = _filterState

    // The final list of trips after applying all search and filter criteria
    @OptIn(FlowPreview::class)
    val filteredTrips: Flow<List<Trip>> = combine(
        _allTrips,
        _filterState.debounce(300L).distinctUntilChanged() // Debounce changes to filter state
    ) { trips, currentFilterState ->
        trips.filter { trip ->
            // Apply all filter conditions based on the currentFilterState
            val matchesSearchText = currentFilterState.searchText.isBlank() ||
                    trip.name.contains(currentFilterState.searchText, ignoreCase = true) ||
                    trip.destination.contains(currentFilterState.searchText, ignoreCase = true) ||
                    trip.description.contains(currentFilterState.searchText, ignoreCase = true)

            val matchesDestination = currentFilterState.destination == null ||
                    trip.destination.contains(currentFilterState.destination, ignoreCase = true)

            val matchesBudget = (currentFilterState.minBudget == null || trip.budget >= (currentFilterState.minBudget)) &&
                    (currentFilterState.maxBudget == null || trip.budget <= (currentFilterState.maxBudget))

            val matchesStatus = currentFilterState.status == null || trip.status == currentFilterState.status

            val matchesTravelers = (currentFilterState.minTravelers == null || trip.travelers >= (currentFilterState.minTravelers)) &&
                    (currentFilterState.maxTravelers == null || trip.travelers <= (currentFilterState.maxTravelers))

            val matchesDateRange = (currentFilterState.startDateFrom == null || !trip.startDate.isBefore(currentFilterState.startDateFrom)) &&
                    (currentFilterState.startDateTo == null || !trip.startDate.isAfter(currentFilterState.startDateTo))

            matchesSearchText && matchesDestination && matchesBudget &&
                    matchesStatus && matchesTravelers && matchesDateRange
        }
    }.distinctUntilChanged() as StateFlow<List<Trip>> // Only emit new list if content changes

    init {
        // Fetch all trips when the ViewModel is initialized
        viewModelScope.launch {
            this@TripSearchViewModel.filteredTrips {
                _allTrips.value
                                                                                                                                           }
        }
    }

    // --- Event Handlers (Functions to update state from UI) ---

    fun onSearchTextChanged(text: String) {
        _filterState.value = _filterState.value.copy(searchText = text)
    }

    fun onFilterDestinationChanged(destination: String?) {
        _filterState.value = _filterState.value.copy(destination = destination)
    }

    fun onFilterMinBudgetChanged(minBudget: Double?) {
        _filterState.value = _filterState.value.copy(minBudget = minBudget)
    }

    fun onFilterMaxBudgetChanged(maxBudget: Double?) {
        _filterState.value = _filterState.value.copy(maxBudget = maxBudget)
    }

    fun onFilterStatusChanged(status: TripStatus?) {
        _filterState.value = _filterState.value.copy(status = status)
    }

    fun onFilterMinTravelersChanged(minTravelers: Int?) {
        _filterState.value = _filterState.value.copy(minTravelers = minTravelers)
    }

    fun onFilterMaxTravelersChanged(maxTravelers: Int?) {
        _filterState.value = _filterState.value.copy(maxTravelers = maxTravelers)
    }

    fun onFilterStartDateRangeChanged(from: LocalDate?, to: LocalDate?) {
        _filterState.value = _filterState.value.copy(startDateFrom = from, startDateTo = to)
    }

    fun clearAllFilters() {
        _filterState.value = TripFilterState() // Reset to default filter state
    }

    // TODO: Implement saved search queries (requires persistence, e.g., Room/Firestore)
    // TODO: Implement voice search capabilities (requires SpeechRecognizer integration)
    // TODO: Implement intelligent search results ranking (requires more complex logic/data)
}

private fun TripSearchViewModel.filteredTrips(value: Any) {

}
