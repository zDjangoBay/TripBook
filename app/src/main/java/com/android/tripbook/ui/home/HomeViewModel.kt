package com.android.tripbook.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.tripbook.data.models.TravelService 
import com.android.tripbook.data.repositories.MockServiceRepository 

class HomeViewModel : ViewModel() {

    private val serviceRepository = MockServiceRepository()

   
    private val _popularCategories = MutableLiveData<List<String>>().apply {
        value = listOf("Flights", "Hotels", "Tours", "Car Rentals")
    }
    val popularCategories: LiveData<List<String>> = _popularCategories

    private val _featuredServices = MutableLiveData<List<TravelService>>()
    val featuredServices: LiveData<List<TravelService>> = _featuredServices

    init {
        loadFeaturedServices()
    }

    private fun loadFeaturedServices() {
       
        _featuredServices.value = serviceRepository.getMockServices().take(3) 
    }

    
    fun performSearch(query: String) {
        
        println("Search performed with query: $query")
       
    }

    // add more LiveData or functions as needed for your Home UI.
    // For example, LiveData for user messages, loading states, etc.
}