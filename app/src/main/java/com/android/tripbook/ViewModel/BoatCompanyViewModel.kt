package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.model.BoatCompany
import com.android.tripbook.model.Destination
import com.android.tripbook.data.BoatMockData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BoatCompanyViewModel : ViewModel() {

    private val _boatCompanies = MutableStateFlow<List<BoatCompany>>(emptyList())
    val boatCompanies: StateFlow<List<BoatCompany>> = _boatCompanies.asStateFlow()

    private val _popularDestinations = MutableStateFlow<List<Destination>>(emptyList())
    val popularDestinations: StateFlow<List<Destination>> = _popularDestinations.asStateFlow()

    private val _isLoadingCompanies = MutableStateFlow(false)
    val isLoadingCompanies: StateFlow<Boolean> = _isLoadingCompanies.asStateFlow()

    private val _isLoadingDestinations = MutableStateFlow(false)
    val isLoadingDestinations: StateFlow<Boolean> = _isLoadingDestinations.asStateFlow()

    init {
        loadBoatCompanies()
        loadPopularDestinations()
    }

    private fun loadBoatCompanies() {
        viewModelScope.launch {
            _isLoadingCompanies.value = true
            // Simulate network delay
            delay(1000)
            _boatCompanies.value = BoatMockData.boatCompanies
            _isLoadingCompanies.value = false
        }
    }

    private fun loadPopularDestinations() {
        viewModelScope.launch {
            _isLoadingDestinations.value = true
            // Simulate network delay
            delay(800)
            _popularDestinations.value = BoatMockData.destinations
            _isLoadingDestinations.value = false
        }
    }

    fun onCompanyClick(company: BoatCompany) {

        // You can navigate to a detailed view or booking screen
    }

    fun onDestinationClick(destination: Destination) {

        // You can filter companies by destination or navigate to destination details
    }

    fun refreshData() {
        loadBoatCompanies()
        loadPopularDestinations()
    }
}