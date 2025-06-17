package com.android.tripbook.ui.listing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.data.models.Service
import com.android.tripbook.data.repositories.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.android.tripbook.ui.listing.SortOrder



@HiltViewModel
class ServiceListingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _services = MutableLiveData<List<Service>>()
    val services: LiveData<List<Service>> = _services

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var currentSearchQuery: String? = null
    private var currentSortOrder: SortOrder = SortOrder.NONE

    init {
        // Retrieve the search query passed from the HomeFragment
        val queryFromArgs: String? = savedStateHandle["query"]
        currentSearchQuery = queryFromArgs
        loadServices(currentSearchQuery, currentSortOrder)
    }


    fun loadServices(query: String?, sortOrder: SortOrder = SortOrder.NONE) {
        currentSearchQuery = query // Update stored query
        currentSortOrder = sortOrder // Update stored sort order

        viewModelScope.launch {
            try {
                // The repository handles the filtering by query
                var fetchedServices = serviceRepository.searchServices(query)

                // Apply sorting in the ViewModel
                fetchedServices = when (sortOrder) {
                    SortOrder.PRICE_ASC -> fetchedServices.sortedBy { parsePriceToDouble(it.price) }
                    SortOrder.PRICE_DESC -> fetchedServices.sortedByDescending { parsePriceToDouble(it.price) }
                    SortOrder.RATING_DESC -> fetchedServices.sortedByDescending { it.rating }
                    SortOrder.NONE -> fetchedServices
                }

                _services.value = fetchedServices
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load services: ${e.message}"
                _services.value = emptyList()
            }
        }
    }


    private fun parsePriceToDouble(priceString: String): Double {
        return priceString.replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0
    }


    fun sortServices(sortOrder: SortOrder) {
        loadServices(currentSearchQuery, sortOrder)
    }

    // for user interactions specific to the listing screen,
    // e.g., "applyFilters()", "refreshList()", etc.
}