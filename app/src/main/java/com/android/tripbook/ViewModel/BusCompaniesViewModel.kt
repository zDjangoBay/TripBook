package com.android.tripbook.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.Model.BusCompany
import com.android.tripbook.Model.PopularDestination
import com.android.tripbook.data.BusMockData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BusCompaniesViewModel : ViewModel() {

    private val _busCompanies = MutableStateFlow<List<BusCompany>>(emptyList())
    val busCompanies: StateFlow<List<BusCompany>> = _busCompanies.asStateFlow()

    private val _popularDestinations = MutableStateFlow<List<PopularDestination>>(emptyList())
    val popularDestinations: StateFlow<List<PopularDestination>> = _popularDestinations.asStateFlow()

    private val _isLoadingCompanies = MutableStateFlow(false)
    val isLoadingCompanies: StateFlow<Boolean> = _isLoadingCompanies.asStateFlow()

    private val _isLoadingDestinations = MutableStateFlow(false)
    val isLoadingDestinations: StateFlow<Boolean> = _isLoadingDestinations.asStateFlow()

    init {
        loadBusCompanies()
        loadPopularDestinations()
    }

    private fun loadBusCompanies() {
        viewModelScope.launch {
            _isLoadingCompanies.value = true
            // Simulate network delay
            delay(1000)
            _busCompanies.value = BusMockData.busCompanies
            _isLoadingCompanies.value = false
        }
    }

    private fun loadPopularDestinations() {
        viewModelScope.launch {
            _isLoadingDestinations.value = true
            // Simulate network delay
            delay(800)
            _popularDestinations.value = BusMockData.popularDestinations
            _isLoadingDestinations.value = false
        }
    }

    fun onCompanyClick(company: BusCompany) {
        // Handle company selection
        // You can navigate to a detailed view or booking screen
    }

    fun onDestinationClick(destination: PopularDestination) {
        // Handle destination selection
        // You can filter companies by destination or navigate to destination details
    }

    fun refreshData() {
        loadBusCompanies()
        loadPopularDestinations()
    }
}