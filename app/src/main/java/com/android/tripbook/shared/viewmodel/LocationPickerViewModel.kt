package com.android.tripbook.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.shared.model.Location
import com.android.tripbook.shared.service.LocationSearchException
import com.android.tripbook.shared.service.LocationSearchService
import com.android.tripbook.shared.service.MockLocationSearchService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing location picker state and operations.
 */
class LocationPickerViewModel(
    private val locationSearchService: LocationSearchService = MockLocationSearchService()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LocationPickerUiState())
    val uiState: StateFlow<LocationPickerUiState> = _uiState.asStateFlow()
    
    /**
     * Searches for locations based on the provided query.
     */
    fun searchLocations(query: String) {
        if (query.length < 2) {
            _uiState.value = _uiState.value.copy(
                searchResults = emptyList(),
                searchError = null
            )
            return
        }
        
        _uiState.value = _uiState.value.copy(
            isSearching = true,
            searchError = null
        )
        
        viewModelScope.launch {
            try {
                val results = locationSearchService.searchLocations(query, maxResults = 10)
                _uiState.value = _uiState.value.copy(
                    searchResults = results,
                    isSearching = false,
                    searchError = null
                )
            } catch (e: LocationSearchException) {
                _uiState.value = _uiState.value.copy(
                    searchResults = emptyList(),
                    isSearching = false,
                    searchError = e.message ?: "Failed to search locations"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    searchResults = emptyList(),
                    isSearching = false,
                    searchError = "An unexpected error occurred"
                )
            }
        }
    }
    
    /**
     * Clears the search results and resets the search state.
     */
    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchResults = emptyList(),
            searchError = null,
            isSearching = false
        )
    }
    
    /**
     * Gets nearby locations around the specified coordinates.
     */
    fun getNearbyLocations(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int = 1000
    ) {
        _uiState.value = _uiState.value.copy(
            isSearching = true,
            searchError = null
        )
        
        viewModelScope.launch {
            try {
                val results = locationSearchService.getNearbyLocations(
                    latitude = latitude,
                    longitude = longitude,
                    radiusMeters = radiusMeters,
                    maxResults = 10
                )
                _uiState.value = _uiState.value.copy(
                    searchResults = results,
                    isSearching = false,
                    searchError = null
                )
            } catch (e: LocationSearchException) {
                _uiState.value = _uiState.value.copy(
                    searchResults = emptyList(),
                    isSearching = false,
                    searchError = e.message ?: "Failed to get nearby locations"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    searchResults = emptyList(),
                    isSearching = false,
                    searchError = "An unexpected error occurred"
                )
            }
        }
    }
}

/**
 * UI state for the location picker.
 */
data class LocationPickerUiState(
    val searchResults: List<Location> = emptyList(),
    val isSearching: Boolean = false,
    val searchError: String? = null
)
