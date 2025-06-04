package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.model.Agency
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

    fun clearFilteredAgencies() {
        _filteredAgencies.value = emptyList()
    }
}