// file: com/android/tripbook/viewmodel/MapViewModel.kt
package com.android.tripbook.viewmodel

import android.app.Application
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import com.android.tripbook.model.Trip
import com.android.tripbook.model.MapRegion
import com.android.tripbook.data.SampleTrips // Ensure this is your UPDATED SampleTrips
import com.android.tripbook.utils.LocationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val locationUtils = LocationUtils(application.applicationContext)
    private val geocoder = Geocoder(application.applicationContext, Locale.getDefault())

    private val _userLocation = mutableStateOf<Location?>(null)
    val userLocation: State<Location?> = _userLocation

    private val _userLocationAddress = mutableStateOf<String?>(null)
    val userLocationAddress: State<String?> = _userLocationAddress

    private val _allTrips = mutableStateListOf<Trip>() // Initialize empty
    val allTrips: List<Trip> get() = _allTrips // Expose as immutable but observable List

    private val _filteredTrips = mutableStateOf<List<Trip>>(emptyList()) // Start empty
    val filteredTrips: State<List<Trip>> = _filteredTrips

    private val _mapRegion = mutableStateOf(MapRegion.defaultRegion())
    val mapRegion: State<MapRegion> = _mapRegion

    private val _selectedTrip = mutableStateOf<Trip?>(null)
    val selectedTrip: State<Trip?> = _selectedTrip

    private val _isMapView = mutableStateOf(true)
    val isMapView: State<Boolean> = _isMapView

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    // --- ADD INIT BLOCK TO LOAD TRIPS ---
    init {
        loadInitialTrips()
    }

    private fun loadInitialTrips() {
        viewModelScope.launch {
            // In a real app, this would be from a repository (network/database)
            val tripsFromSource = SampleTrips.get() // Get trips from your sample data
            _allTrips.clear()
            _allTrips.addAll(tripsFromSource)
            // After loading all trips, apply the initial (empty) filter
            // which will populate _filteredTrips with all trips.
            filterTrips(_searchQuery.value) // Or simply filterTrips("")
        }
    }
    // --- END INIT BLOCK ---

    fun generateNewTripId(): Int {
        // Find the highest existing ID from the current list and add 1
        val maxId = allTrips.maxOfOrNull { it.id } ?: 0
        return maxId + 1
    }


    fun updateUserLocation(location: Location?) {
        _userLocation.value = location
        viewModelScope.launch {
            if (location != null) {
                _userLocationAddress.value = withContext(Dispatchers.IO) {
                    try {
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0]
                            val city = address.locality ?: address.subAdminArea
                            val country = address.countryName
                            if (city != null && country != null) "$city, $country" else address.getAddressLine(0)
                        } else { "Unknown Location" }
                    } catch (e: Exception) { "Location unavailable" }
                }
            } else {
                _userLocationAddress.value = null
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterTrips(query) // This will update _filteredTrips
    }

    private fun filterTrips(query: String) {
        val sourceTrips = _allTrips.toList() // Work with a stable copy for filtering

        val result = if (query.isEmpty()) {
            sourceTrips
        } else {
            if (query.startsWith("region:")) {
                val actualRegion = query.substringAfter("region:")
                sourceTrips.filter { trip ->
                    trip.region?.equals(actualRegion, ignoreCase = true) == true
                }
            } else if (query.equals("near_me", ignoreCase = true) && _userLocation.value != null) {
                val userLat = _userLocation.value!!.latitude
                val userLon = _userLocation.value!!.longitude
                sourceTrips.filter { trip ->
                    locationUtils.calculateDistance(userLat, userLon, trip.latitude, trip.longitude) <= 50 // 50km radius
                }
            } else {
                sourceTrips.filter { trip ->
                    trip.title.contains(query, ignoreCase = true) ||
                            trip.description.contains(query, ignoreCase = true) ||
                            trip.city.contains(query, ignoreCase = true) ||
                            trip.country.contains(query, ignoreCase = true) ||
                            (trip.region?.contains(query, ignoreCase = true) == true) // Also search in region
                }
            }
        }
        _filteredTrips.value = result // Update the state
        updateMapRegionForTrips(result) // Update map region based on filtered results
    }

    fun addTrip(newTrip: Trip) {
        // Ensure unique ID or handle updates if ID exists
        if (_allTrips.none { it.id == newTrip.id }) {
            _allTrips.add(newTrip) // Add to the observable list
            // Re-apply current filter to see if the new trip should be in filteredTrips
            filterTrips(_searchQuery.value)
        } else {
            // Handle case where trip ID already exists (e.g., update it or log warning)
            println("Warning: Trip with ID ${newTrip.id} already exists.")
        }
    }


    fun selectTrip(trip: Trip?) {
        _selectedTrip.value = trip
        trip?.let {
            _mapRegion.value = MapRegion( // Using your MapRegion constructor
                centerLatitude = it.latitude,
                centerLongitude = it.longitude,
                latitudeDelta = 0.05, // Smaller delta for closer zoom on selected trip
                longitudeDelta = 0.05,
                zoomLevel = 14f // Closer zoom
            )
        }
    }

    fun toggleMapView() {
        _isMapView.value = !_isMapView.value
    }

    fun resetFilters() {
        _searchQuery.value = ""
        // filterTrips("") will repopulate _filteredTrips with all trips
        // and updateMapRegionForTrips will be called internally.
        filterTrips("")
        _selectedTrip.value = null
        // No need to call updateMapRegionForTrips explicitly here if filterTrips does it.
    }

    // This function determines the map region based on a list of trips
    private fun updateMapRegionForTrips(tripsForRegion: List<Trip>) {
        _mapRegion.value = MapRegion.fromTrips(tripsForRegion) // Uses your static method
    }


    fun moveMapToRegion(latitude: Double, longitude: Double, zoom: Float = 10f) {
        _mapRegion.value = MapRegion( // Using your MapRegion constructor
            centerLatitude = latitude,
            centerLongitude = longitude,
            // Provide some default deltas if your MapRegion needs them,
            // otherwise zoomLevel might be sufficient for CameraPosition.
            latitudeDelta = 1.0, // Example default delta
            longitudeDelta = 1.0, // Example default delta
            zoomLevel = zoom
        )
    }

    // These now operate on the ViewModel's _allTrips
    fun getTripsByRegion(): Map<String, List<Trip>> {
        return _allTrips.groupBy { it.region ?: "Other" }
    }

    fun getPopularDestinations(): List<Pair<String, Int>> {
        return _allTrips
            .groupBy { "${it.city}, ${it.country}" }
            .map { (destination, tripsInDestination) -> destination to tripsInDestination.size }
            .sortedByDescending { it.second }
    }

    // This is called by MapView when the user manually pans/zooms
    fun updateMapRegion(newMapRegionFromView: MapRegion) {
        // Here, newMapRegionFromView is the one constructed by MapView
        // based on its current camera state (center, zoom, deltas).
        _mapRegion.value = newMapRegionFromView
        // Optional: If you want manual map navigation to also re-filter trips
        // shown in the list below the map (not just what's visible on map),
        // you could trigger a filter based on the new bounds.
        // For now, it just updates the map's state.
    }
}