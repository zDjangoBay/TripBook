package com.android.tripbook.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.data.models.TravelService
import com.android.tripbook.data.repositories.MockServiceRepository
import kotlinx.coroutines.launch

// ViewModel for the ServiceDetailFragment
class ServiceDetailViewModel(
    private val savedStateHandle: SavedStateHandle // Used to get navigation arguments
) : ViewModel() {

    private val serviceRepository = MockServiceRepository() // In a real app, inject this

    // LiveData to hold the details of the selected service
    private val _service = MutableLiveData<TravelService>()
    val service: LiveData<TravelService> = _service

    // LiveData for error messages (e.g., service not found)
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        // Retrieve the service ID from navigation arguments
        // The key "serviceId" must match the argument name in your nav_graph.xml
        val serviceId: String? = savedStateHandle["serviceId"]

        if (serviceId != null) {
            loadServiceDetails(serviceId)
        } else {
            _errorMessage.value = "Service ID is missing."
        }
    }

    private fun loadServiceDetails(id: String) {
        viewModelScope.launch {
            // In a real app, this would be an asynchronous call to the repository
            val foundService = serviceRepository.getServiceById(id)
            if (foundService != null) {
                _service.value = foundService
            } else {
                _errorMessage.value = "Service with ID $id not found."
            }
        }
    }

    // You might add functions here for user interactions specific to the detail screen,
    // e.g., "addToFavorites()", "checkAvailability()", etc.
}   