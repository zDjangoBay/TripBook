package com.android.tripbook.ui.servicelisting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.data.models.Service
import com.android.tripbook.data.repositories.MockServiceRepository // Import your mock repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceListingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val serviceRepository: MockServiceRepository // Inject the repository
) : ViewModel() {

    private val _services = MutableLiveData<List<Service>>()
    val services: LiveData<List<Service>> = _services

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        // Load initial services or based on argument if available
        val query = savedStateHandle.get<String>("query")
        loadServices(query)
    }

    fun loadServices(query: String?) {
        viewModelScope.launch {
            try {
                val results = serviceRepository.searchServices(query)
                _services.value = results
                _errorMessage.value = null 
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load services: ${e.message}"
                _services.value = emptyList()
            }
        }
    }
}