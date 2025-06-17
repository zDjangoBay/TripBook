package com.android.tripbook.ui.booking

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

@HiltViewModel
class BookingConfirmationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _service = MutableLiveData<Service?>()
    val service: LiveData<Service?> = _service

    private val _bookingConfirmed = MutableLiveData<Boolean>()
    val bookingConfirmed: LiveData<Boolean> = _bookingConfirmed

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        val serviceId: String? = savedStateHandle["serviceId"]

        if (serviceId != null) {
            loadServiceDetails(serviceId)
        } else {
            _errorMessage.value = "Service ID for booking is missing."
        }
    }

    private fun loadServiceDetails(id: String) {
        viewModelScope.launch {
            try {
                val foundService = serviceRepository.getServiceById(id)
                _service.value = foundService
                if (foundService == null) {
                    _errorMessage.value = "Service with ID $id not found for booking."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading service details for booking: ${e.message}"
                _service.value = null
            }
        }
    }

    fun confirmBooking() {
        viewModelScope.launch {
            _bookingConfirmed.value = true
        }
    }
}