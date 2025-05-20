package com.android.tripbook.datamining.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.tripbook.datamining.data.model.Airport
import com.android.tripbook.datamining.data.model.AirportSummary
import com.android.tripbook.datamining.data.repository.AirportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the airports feature
 */
class AirportViewModel(
    private val repository: AirportRepository
) : ViewModel() {
    
    // UI state for loading indicator
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // UI state for error messages
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // UI state for selected airport
    private val _selectedAirport = MutableStateFlow<Airport?>(null)
    val selectedAirport: StateFlow<Airport?> = _selectedAirport.asStateFlow()
    
    // UI state for search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // UI state for selected region filter
    private val _selectedRegion = MutableStateFlow("All")
    val selectedRegion: StateFlow<String> = _selectedRegion.asStateFlow()
    
    // UI state for selected country filter
    private val _selectedCountry = MutableStateFlow("All")
    val selectedCountry: StateFlow<String> = _selectedCountry.asStateFlow()
    
    // Available regions for filtering
    val availableRegions = listOf(
        "All", "North Africa", "West Africa", "East Africa", 
        "Central Africa", "Southern Africa"
    )
    
    // Available countries for filtering (will be populated from data)
    private val _availableCountries = MutableStateFlow<List<String>>(emptyList())
    val availableCountries: StateFlow<List<String>> = _availableCountries.asStateFlow()
    
    // Filtered airport summaries
    val filteredAirports: StateFlow<List<AirportSummary>> = combine(
        repository.airportSummaries,
        searchQuery,
        selectedRegion,
        selectedCountry
    ) { airports, query, region, country ->
        var filtered = airports
        
        // Apply search filter
        if (query.isNotEmpty()) {
            filtered = filtered.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.city.contains(query, ignoreCase = true) ||
                it.country.contains(query, ignoreCase = true) ||
                it.iataCode.contains(query, ignoreCase = true)
            }
        }
        
        // Apply region filter
        if (region != "All") {
            filtered = filtered.filter { it.region == region }
        }
        
        // Apply country filter
        if (country != "All") {
            filtered = filtered.filter { it.country == country }
        }
        
        filtered
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    init {
        // Populate available countries
        viewModelScope.launch {
            repository.airports.collect { airports ->
                val countries = airports.map { it.country }.distinct().sorted()
                _availableCountries.value = listOf("All") + countries
            }
        }
    }
    
    /**
     * Set the search query
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * Set the selected region filter
     */
    fun setSelectedRegion(region: String) {
        _selectedRegion.value = region
    }
    
    /**
     * Set the selected country filter
     */
    fun setSelectedCountry(country: String) {
        _selectedCountry.value = country
    }
    
    /**
     * Select an airport by ID
     */
    fun selectAirport(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAirportById(id).collect { airport ->
                    _selectedAirport.value = airport
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Failed to load airport details: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Clear the selected airport
     */
    fun clearSelectedAirport() {
        _selectedAirport.value = null
    }
    
    /**
     * Factory for creating AirportViewModel with dependencies
     */
    class Factory(private val repository: AirportRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AirportViewModel::class.java)) {
                return AirportViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
