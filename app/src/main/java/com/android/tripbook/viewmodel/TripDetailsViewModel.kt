package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.model.Trip
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.JournalEntry
import com.android.tripbook.repository.SupabaseTripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class TripDetailsUiState(
    val trip: Trip? = null,
    val selectedDate: LocalDate? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddActivityDialog: Boolean = false,
    val selectedTab: String = "Overview",
    val mapViewMode: MapViewMode = MapViewMode.LIST
)

enum class MapViewMode {
    LIST, MAP
}

class TripDetailsViewModel(
    private val repository: SupabaseTripRepository = SupabaseTripRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripDetailsUiState())
    val uiState: StateFlow<TripDetailsUiState> = _uiState.asStateFlow()

    fun loadTripDetails(tripId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Load trip with full details including itinerary
                val trip = repository.getTripWithDetails(tripId)
                _uiState.value = _uiState.value.copy(
                    trip = trip,
                    isLoading = false,
                    selectedDate = trip?.startDate
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load trip details: ${e.message}"
                )
            }
        }
    }

    fun selectTab(tab: String) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    fun toggleMapViewMode() {
        val newMode = if (_uiState.value.mapViewMode == MapViewMode.LIST) {
            MapViewMode.MAP
        } else {
            MapViewMode.LIST
        }
        _uiState.value = _uiState.value.copy(mapViewMode = newMode)
    }

    fun showAddActivityDialog() {
        _uiState.value = _uiState.value.copy(showAddActivityDialog = true)
    }

    fun hideAddActivityDialog() {
        _uiState.value = _uiState.value.copy(showAddActivityDialog = false)
    }

    fun addItineraryItem(item: ItineraryItem) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val result = repository.addItineraryItem(item)
                if (result.isSuccess) {
                    // Reload trip details to get updated itinerary
                    _uiState.value.trip?.let { trip ->
                        loadTripDetails(trip.id)
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to add activity: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to add activity: ${e.message}"
                )
            }
        }
    }

    fun updateItineraryItem(item: ItineraryItem) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val result = repository.updateItineraryItem(item)
                if (result.isSuccess) {
                    // Update the trip in the current state
                    _uiState.value.trip?.let { trip ->
                        val updatedItinerary = trip.itinerary.map { 
                            if (it.id == item.id) item else it 
                        }
                        val updatedTrip = trip.copy(itinerary = updatedItinerary)
                        _uiState.value = _uiState.value.copy(
                            trip = updatedTrip,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to update activity: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to update activity: ${e.message}"
                )
            }
        }
    }

    fun deleteItineraryItem(itemId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val result = repository.deleteItineraryItem(itemId)
                if (result.isSuccess) {
                    // Remove the item from the current state
                    _uiState.value.trip?.let { trip ->
                        val updatedItinerary = trip.itinerary.filter { it.id != itemId }
                        val updatedTrip = trip.copy(itinerary = updatedItinerary)
                        _uiState.value = _uiState.value.copy(
                            trip = updatedTrip,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to delete activity: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to delete activity: ${e.message}"
                )
            }
        }
    }

    fun addJournalEntry(entry: JournalEntry) {
        viewModelScope.launch {
            val updatedEntries = uiState.value.trip?.journalEntries.orEmpty() + entry
            _uiState.value = _uiState.value.copy(
                trip = uiState.value.trip?.copy(journalEntries = updatedEntries)
            )
        }
    }

    fun editJournalEntry(entry: JournalEntry) {
        viewModelScope.launch {
            val updatedEntries = uiState.value.trip?.journalEntries.orEmpty().map {
                if (it.id == entry.id) entry else it
            }
            _uiState.value = _uiState.value.copy(
                trip = uiState.value.trip?.copy(journalEntries = updatedEntries)
            )
        }
    }

    fun deleteJournalEntry(entryId: String) {
        viewModelScope.launch {
            val updatedEntries = uiState.value.trip?.journalEntries.orEmpty().filterNot {
                it.id == entryId
            }
            _uiState.value = _uiState.value.copy(
                trip = uiState.value.trip?.copy(journalEntries = updatedEntries)
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun getTripStatistics(): TripStatistics {
        val trip = _uiState.value.trip ?: return TripStatistics()
        
        val totalDays = java.time.temporal.ChronoUnit.DAYS.between(trip.startDate, trip.endDate).toInt() + 1
        val activitiesCount = trip.itinerary.size
        val completedActivities = trip.itinerary.count { it.isCompleted }
        val totalCost = trip.itinerary.sumOf { it.cost }
        
        return TripStatistics(
            totalDays = totalDays,
            activitiesCount = activitiesCount,
            completedActivities = completedActivities,
            totalCost = totalCost,
            remainingBudget = trip.budget - totalCost
        )
    }

    fun getItineraryByDate(date: LocalDate): List<ItineraryItem> {
        return _uiState.value.trip?.itinerary?.filter { it.date == date }?.sortedBy { it.time } ?: emptyList()
    }

    fun getTripDates(): List<LocalDate> {
        val trip = _uiState.value.trip ?: return emptyList()
        val dates = mutableListOf<LocalDate>()
        var currentDate = trip.startDate
        
        while (!currentDate.isAfter(trip.endDate)) {
            dates.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }
        
        return dates
    }
}

data class TripStatistics(
    val totalDays: Int = 0,
    val activitiesCount: Int = 0,
    val completedActivities: Int = 0,
    val totalCost: Double = 0.0,
    val remainingBudget: Double = 0.0
)
