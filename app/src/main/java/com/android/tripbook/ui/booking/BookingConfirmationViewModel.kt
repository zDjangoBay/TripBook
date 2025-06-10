package com.android.tripbook.ui.booking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.data.models.TravelService 
import com.android.tripbook.data.repositories.MockServiceRepository 
import kotlinx.coroutines.delay 
import kotlinx.coroutines.launch
import java.util.UUID

class BookingConfirmationViewModel(
    private val savedStateHandle: SavedStateHandle // Used to get navigation arguments
) : ViewModel() {

    private val serviceRepository = MockServiceRepository() 

    private val _bookingId = MutableLiveData<String>()
    val bookingId: LiveData<String> = _bookingId

    private val _bookedService = MutableLiveData<TravelService>()
    val bookedService: LiveData<TravelService> = _bookedService

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        // Retrieve the service ID from navigation arguments
        // The key "serviceId" must match the argument name in your nav_graph.xml
        val serviceId: String? = savedStateHandle["serviceId"]

        if (serviceId != null) {
            _isLoading.value = true
            viewModelScope.launch {
                try {
                    // Simulate booking process (e.g., API call)
                    delay(1000) // Simulate network delay

                    // Generate a unique booking ID (in a real app, this comes from backend)
                    val generatedId = "TBK-${UUID.randomUUID().toString().substring(0, 8).uppercase()}"
                    _bookingId.value = generatedId

                    // Fetch the details of the booked service for display
                    val service = serviceRepository.getServiceById(serviceId)
                    if (service != null) {
                        _bookedService.value = service
                    } else {
                        _errorMessage.value = "Booked service details not found."
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to confirm booking: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            _errorMessage.value = "No service ID provided for booking confirmation."
            _isLoading.value = false
        }
    }

    // You might add functions here like "viewBookingDetails()" for more complex flows
}