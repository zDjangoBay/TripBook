package com.android.tripbook.ui.listing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.data.models.TravelService
import com.android.tripbook.data.repositories.MockServiceRepository
import kotlinx.coroutines.launch

class ServiceListingViewModel(
    private val savedStateHandle: SavedStateHandle // Used to get navigation arguments
) : ViewModel() {

    private val serviceRepository = MockServiceRepository() 

    private val _services = MutableLiveData<List<TravelService>>()
    val services: LiveData<List<TravelService>> = _services

    private val _currentSearchQuery = MutableLiveData<String?>()
    val currentSearchQuery: LiveData<String?> = _currentSearchQuery

    init {
        val searchQuery: String? = savedStateHandle["searchQuery"] 
        _currentSearchQuery.value = searchQuery

        loadServices(searchQuery)
    }

    private fun loadServices(query: String? = null) {
        viewModelScope.launch {
            val allServices = serviceRepository.getMockServices()
            if (query.isNullOrBlank()) {
                _services.value = allServices
            } else {
                val filtered = allServices.filter {
                    it.name.contains(query, ignoreCase = true) ||
                    it.agency.name.contains(query, ignoreCase = true)
                }
                _services.value = filtered
            }
        }
    }

    /**
     * Applies filters to the service list.
     * @param minPrice Minimum price for filtering.
     * @param maxPrice Maximum price for filtering.
     * @param minRating Minimum rating for filtering.
     */
    fun applyFilters(minPrice: Double?, maxPrice: Double?, minRating: Double?) {
        viewModelScope.launch {
            // Get the current list (could be filtered by search already)
            val currentList = _services.value ?: emptyList()
            var filteredList = currentList

            minPrice?.let { price ->
                filteredList = filteredList.filter { it.price >= price }
            }
            maxPrice?.let { price ->
                filteredList = filteredList.filter { it.price <= price }
            }
            minRating?.let { rating ->
                filteredList = filteredList.filter { it.rating >= rating }
            }

            _services.value = filteredList
        }
    }

   
    fun sortServices(sortBy: String) {
        viewModelScope.launch {
            val currentList = _services.value ?: emptyList()
            val sortedList = when (sortBy) {
                "price_asc" -> currentList.sortedBy { it.price }
                "price_desc" -> currentList.sortedByDescending { it.price }
                "rating_desc" -> currentList.sortedByDescending { it.rating }
                else -> currentList 
            }
            _services.value = sortedList
        }
    }

    fun refreshServices() {
        loadServices(_currentSearchQuery.value) 
    }
}