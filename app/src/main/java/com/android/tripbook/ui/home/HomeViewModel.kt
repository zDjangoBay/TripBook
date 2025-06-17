package com.android.tripbook.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.data.models.Service
import com.android.tripbook.data.repositories.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _flights = MutableLiveData<List<Service>>()
    val flights: LiveData<List<Service>> = _flights

    private val _tours = MutableLiveData<List<Service>>()
    val tours: LiveData<List<Service>> = _tours

    private val _carRentals = MutableLiveData<List<Service>>()
    val carRentals: LiveData<List<Service>> = _carRentals

    private val _hotels = MutableLiveData<List<Service>>()
    val hotels: LiveData<List<Service>> = _hotels

    init {
        loadAllServices()
    }

    private fun loadAllServices() {
        viewModelScope.launch {

            val allServices = serviceRepository.searchServices(null)

            // Filter them into categories
            _flights.value = allServices.filter { it.type == "Flight" }
            _tours.value = allServices.filter { it.type == "Tour" }
            _carRentals.value = allServices.filter { it.type == "Car Rental" }

            _hotels.value = allServices.filter { it.type == "Hotel" }
        }
    }

    // for user interactions specific to the home screen,
    // e.g., "refreshData()", "prepareForSearch()", etc.
}