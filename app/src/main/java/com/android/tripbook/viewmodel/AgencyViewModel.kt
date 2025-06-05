package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.model.Agency
import com.android.tripbook.model.Bus
import com.android.tripbook.model.Destination
import com.android.tripbook.repository.SupabaseAgencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AgencyViewModel(private val agencyRepository: SupabaseAgencyRepository) : ViewModel() {

    private val _agencies = MutableStateFlow<List<Agency>>(emptyList())
    val agencies: StateFlow<List<Agency>> = _agencies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // State for destinations of a specific agency
    private val _destinations = MutableStateFlow<List<Destination>>(emptyList())
    val destinations: StateFlow<List<Destination>> = _destinations.asStateFlow()

    // State for agencies filtered by destination
    private val _filteredAgencies = MutableStateFlow<List<Agency>>(emptyList())
    val filteredAgencies: StateFlow<List<Agency>> = _filteredAgencies.asStateFlow()

    // State for buses
    private val _buses = MutableStateFlow<List<Bus>>(emptyList())
    val buses: StateFlow<List<Bus>> = _buses.asStateFlow()

    // State for buses grouped by agency
    private val _busesByAgency = MutableStateFlow<Map<Int, List<Bus>>>(emptyMap())
    val busesByAgency: StateFlow<Map<Int, List<Bus>>> = _busesByAgency.asStateFlow()

    // State for reviews of a specific agency
    private val _reviews = MutableStateFlow<List<com.android.tripbook.model.Review>>(emptyList())
    val reviews: StateFlow<List<com.android.tripbook.model.Review>> = _reviews.asStateFlow()

    init {
        loadAgencies()
    }

    fun loadAgencies() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val loadedAgencies = agencyRepository.loadAgencies()
                _agencies.value = loadedAgencies
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadReviewsForAgency(agencyId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val loadedReviews = agencyRepository.loadReviewsForAgency(agencyId)
                _reviews.value = loadedReviews
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitReview(review: com.android.tripbook.model.Review, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val success = agencyRepository.addReview(review)
                onResult(success)
            } catch (e: Exception) {
                _error.value = e.message
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadDestinationsForAgency(agencyId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val loadedDestinations = agencyRepository.loadDestinationsForAgency(agencyId)
                _destinations.value = loadedDestinations
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAgenciesForDestination(destinationQuery: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val loadedAgencies = agencyRepository.loadAgenciesForDestination(destinationQuery)
                _filteredAgencies.value = loadedAgencies
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadBusesForAgency(agencyId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val loadedBuses = agencyRepository.loadBusesForAgency(agencyId)
                _buses.value = loadedBuses
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadBusesForDestination(destinationQuery: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val loadedBuses = agencyRepository.loadBusesForDestination(destinationQuery)
                _buses.value = loadedBuses

                // Group buses by agency for easier display
                val groupedBuses = loadedBuses.groupBy { it.agencyId }
                _busesByAgency.value = groupedBuses
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearFilteredAgencies() {
        _filteredAgencies.value = emptyList()
    }

    fun clearBuses() {
        _buses.value = emptyList()
        _busesByAgency.value = emptyMap()
    }
}