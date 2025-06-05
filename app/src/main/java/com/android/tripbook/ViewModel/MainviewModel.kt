package com.android.tripbook.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.tripbook.Model.Place
import com.android.tripbook.Model.Triphome
import com.android.tripbook.Repository.TripsRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: TripsRepository = TripsRepository()
) : ViewModel() {

    private val _upcomingTrips = MutableLiveData<List<Triphome>>(emptyList())
    val upcomingTrips: LiveData<List<Triphome>> = _upcomingTrips

    private val _recommendedPlaces = MutableLiveData<List<Place>>(emptyList())
    val recommendedPlaces: LiveData<List<Place>> = _recommendedPlaces

    private val _isLoadingUpcoming = MutableLiveData(true)
    val isLoadingUpcoming: LiveData<Boolean> = _isLoadingUpcoming

    private val _isLoadingRecommended = MutableLiveData(true)
    val isLoadingRecommended: LiveData<Boolean> = _isLoadingRecommended

    // Error states
    private val _errorUpcoming = MutableLiveData<String?>(null)
    val errorUpcoming: LiveData<String?> = _errorUpcoming

    private val _errorRecommended = MutableLiveData<String?>(null)
    val errorRecommended: LiveData<String?> = _errorRecommended

    init {
        fetchUpcomingTrips()
        fetchRecommendedPlaces()
    }

    fun refreshAll() {
        fetchUpcomingTrips()
        fetchRecommendedPlaces()
    }

    private fun fetchUpcomingTrips() {
        _isLoadingUpcoming.value = true
        _errorUpcoming.value = null
        viewModelScope.launch {
            try {
                val trips = repository.getUpcomingTrips() // Direct list, no .value needed
                _upcomingTrips.value = trips
            } catch (e: Exception) {
                _errorUpcoming.value = "Failed to load trips: ${e.message}"
                _upcomingTrips.value = emptyList()
            } finally {
                _isLoadingUpcoming.value = false
            }
        }
    }

    private fun fetchRecommendedPlaces() {
        _isLoadingRecommended.value = true
        _errorRecommended.value = null
        viewModelScope.launch {
            try {
                val places = repository.getRecommendedTrips() // Direct list, no .value needed
                _recommendedPlaces.value = places
            } catch (e: Exception) {
                _errorRecommended.value = "Failed to load recommendations: ${e.message}"
                _recommendedPlaces.value = emptyList()
            } finally {
                _isLoadingRecommended.value = false
            }
        }
    }
}