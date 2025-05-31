package com.android.tripbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*
import com.android.tripbook.model.Trip
import com.android.tripbook.model.MapRegion
import com.android.tripbook.data.SampleTripsWithLocation

class MapViewModel : ViewModel() {

    private val _allTrips = mutableStateOf(SampleTripsWithLocation.get())
    val allTrips: State<List<Trip>> = _allTrips

    private val _filteredTrips = mutableStateOf(_allTrips.value)
    val filteredTrips: State<List<Trip>> = _filteredTrips

    private val _mapRegion = mutableStateOf(MapRegion.defaultRegion())
    val mapRegion: State<MapRegion> = _mapRegion

    private val _selectedTrip = mutableStateOf<Trip?>(null)
    val selectedTrip: State<Trip?> = _selectedTrip

    private val _isMapView = mutableStateOf(true)
    val isMapView: State<Boolean> = _isMapView

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterTrips(query)
    }

    private fun filterTrips(query: String) {
        _filteredTrips.value = if (query.isEmpty()) {
            _allTrips.value
        } else {
            _allTrips.value.filter { trip ->
                trip.title.contains(query, ignoreCase = true) ||
                        trip.description.contains(query, ignoreCase = true) ||
                        trip.city.contains(query, ignoreCase = true) ||
                        trip.country.contains(query, ignoreCase = true) ||
                        trip.region?.contains(query, ignoreCase = true) == true
            }
        }

        // Update map region to fit filtered trips
        updateMapRegionForTrips(_filteredTrips.value)
    }

    fun filterTripsByRegion(region: String) {
        _filteredTrips.value = _allTrips.value.filter { trip ->
            trip.region?.equals(region, ignoreCase = true) == true
        }
        updateMapRegionForTrips(_filteredTrips.value)
    }

    fun selectTrip(trip: Trip?) {
        _selectedTrip.value = trip
        trip?.let {
            // Zoom to selected trip
            _mapRegion.value = MapRegion(
                centerLatitude = it.latitude,
                centerLongitude = it.longitude,
                latitudeDelta = 0.5,
                longitudeDelta = 0.5,
                zoomLevel = 12f
            )
        }
    }

    fun toggleMapView() {
        _isMapView.value = !_isMapView.value
    }

    fun resetFilters() {
        _searchQuery.value = ""
        _filteredTrips.value = _allTrips.value
        _selectedTrip.value = null
        updateMapRegionForTrips(_allTrips.value)
    }

    private fun updateMapRegionForTrips(trips: List<Trip>) {
        _mapRegion.value = MapRegion.fromTrips(trips)
    }

    fun moveMapToRegion(latitude: Double, longitude: Double, zoom: Float = 10f) {
        _mapRegion.value = MapRegion(
            centerLatitude = latitude,
            centerLongitude = longitude,
            latitudeDelta = 1.0,
            longitudeDelta = 1.0,
            zoomLevel = zoom
        )
    }

    // Get trips by continent/region for filtering
    fun getTripsByRegion(): Map<String, List<Trip>> {
        return _allTrips.value.groupBy { it.region ?: "Other" }
    }

    // Get popular destinations (cities with multiple trips)
    fun getPopularDestinations(): List<Pair<String, Int>> {
        return _allTrips.value
            .groupBy { "${it.city}, ${it.country}" }
            .map { (destination, trips) -> destination to trips.size }
            .sortedByDescending { it.second }
    }
}