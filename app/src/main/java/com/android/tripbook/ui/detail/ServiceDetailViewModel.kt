package com.android.tripbook.ui.detail

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
class ServiceDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _service = MutableLiveData<Service?>()
    val service: LiveData<Service?> = _service

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        val serviceId: String? = savedStateHandle["serviceId"]
        if (serviceId != null) {
            loadServiceDetails(serviceId)
        } else {
            _errorMessage.value = "Service ID is missing."
        }
    }

    fun loadServiceDetails(id: String) {
        viewModelScope.launch {
            try {
                val foundService = serviceRepository.getServiceById(id)
                _service.value = foundService
                if (foundService == null) {
                    _errorMessage.value = "Service with ID $id not found."
                } else {
                    _errorMessage.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading service: ${e.message}"
                _service.value = null
            }
        }
    }
}