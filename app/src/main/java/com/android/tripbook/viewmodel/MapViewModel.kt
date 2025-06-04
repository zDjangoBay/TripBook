package com.android.tripbook.viewmodel

import android.app.Application // <-- NEW IMPORT: Required for AndroidViewModel
import android.location.Geocoder // <-- NEW IMPORT: Required for Geocoder
import android.location.Location // <-- NEW IMPORT: Required for Location object
import androidx.lifecycle.AndroidViewModel // <-- CHANGE: Extend AndroidViewModel instead of ViewModel
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope // <-- NEW IMPORT: For viewModelScope
import com.android.tripbook.model.Trip // Assuming core:model
import com.android.tripbook.model.MapRegion // Assuming core:model
import com.android.tripbook.data.SampleTrips // Assuming data module
import com.android.tripbook.utils.LocationUtils // Assuming core:utils or your current utils package
import kotlinx.coroutines.Dispatchers // <-- NEW IMPORT: For Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext // <-- NEW IMPORT: For withContext
import java.util.Locale // <-- NEW IMPORT: For Locale for Geocoder

// CHANGE: MapViewModel now extends AndroidViewModel
class MapViewModel(application: Application) : AndroidViewModel(application) {

    // NEW: Instance of LocationUtils, initialized with application context
    private val locationUtils = LocationUtils(application.applicationContext)
    // NEW: Geocoder instance for reverse geocoding (Lat/Lon to address)
    private val geocoder = Geocoder(application.applicationContext, Locale.getDefault())

    // NEW: State for the user's current raw location
    private val _userLocation = mutableStateOf<Location?>(null)
    val userLocation: State<Location?> = _userLocation

    // NEW: State for the user's current user-friendly address
    private val _userLocationAddress = mutableStateOf<String?>(null)
    val userLocationAddress: State<String?> = _userLocationAddress

    // _allTrips is now a mutableStateListOf so Compose observes additions/removals
    private val _allTrips = mutableStateListOf<Trip>().apply { addAll(SampleTrips.get()) }
    val allTrips: List<Trip> get() = _allTrips // Exposed as immutable List

    // Corrected typo here: _filteredTrips
    private val _filteredTrips = mutableStateOf(_allTrips.toList()) // .toList() for initial state
    val filteredTrips: State<List<Trip>> = _filteredTrips

    private val _mapRegion = mutableStateOf(MapRegion.defaultRegion())
    val mapRegion: State<MapRegion> = _mapRegion

    private val _selectedTrip = mutableStateOf<Trip?>(null)
    val selectedTrip: State<Trip?> = _selectedTrip

    private val _isMapView = mutableStateOf(true)
    val isMapView: State<Boolean> = _isMapView

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    // NEW: Function to update the user's current location and resolve its address
    fun updateUserLocation(location: Location?) {
        _userLocation.value = location
        // Resolve address in a background coroutine
        viewModelScope.launch {
            if (location != null) {
                _userLocationAddress.value = withContext(Dispatchers.IO) { // Perform geocoding on IO dispatcher
                    try {
                        // Use getFromLocation (deprecated in API 33+, but still common)
                        // For API 33+, consider Geocoder.getFromLocation(latitude, longitude, maxResults, Geocoder.GeocodeListener)
                        // Or reverse geocoding via Places API if more robust results are needed.
                        @Suppress("DEPRECATION") // Suppress deprecation warning for getFromLocation
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0]
                            val city = address.locality ?: address.subAdminArea // Prefer city, fallback to subAdminArea
                            val country = address.countryName
                            if (city != null && country != null) "$city, $country" else address.getAddressLine(0) // Combine city, country or full address
                        } else {
                            "Unknown Location" // No address found
                        }
                    } catch (e: Exception) {
                        // Log the exception for debugging
                        // Timber.e(e, "Error resolving address for location: $location")
                        "Location unavailable" // Handle network/service issues or no geocoding service
                    }
                }
            } else {
                _userLocationAddress.value = null // Clear address if location is null
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterTrips(query)
    }

    private fun filterTrips(query: String) {
        _filteredTrips.value = if (query.isEmpty()) {
            _allTrips.toList() // Use .toList() to ensure a new list instance for state updates
        } else {
            if (query.startsWith("region:")) {
                val actualRegion = query.substringAfter("region:")
                _allTrips.filter { trip ->
                    trip.region?.equals(actualRegion, ignoreCase = true) == true
                }
            } else if (query == "near_me" && _userLocation.value != null) { // "NEAR ME" FILTER LOGIC
                val userLat = _userLocation.value!!.latitude
                val userLon = _userLocation.value!!.longitude
                // Filter trips within a 50 km radius (adjust this radius as needed)
                _allTrips.filter { trip ->
                    locationUtils.calculateDistance(userLat, userLon, trip.latitude, trip.longitude) <= 50 // Distance in kilometers
                }
            } else { // GENERAL TEXT SEARCH LOGIC
                _allTrips.filter { trip ->
                    trip.title.contains(query, ignoreCase = true) ||
                            trip.description.contains(query, ignoreCase = true) ||
                            trip.city.contains(query, ignoreCase = true) ||
                            trip.country.contains(query, ignoreCase = true)
                }
            }
        }
        updateMapRegionForTrips(_filteredTrips.value)
    }

    // Function to add a new trip (used by AddTrip feature)
    fun addTrip(newTrip: Trip) {
        if (!_allTrips.contains(newTrip)) { // Avoid adding duplicates if trip ID is not unique
            _allTrips.add(newTrip)
            filterTrips(_searchQuery.value) // Re-filter to include the new trip if it matches current query
        }
    }

    fun selectTrip(trip: Trip?) {
        _selectedTrip.value = trip
        trip?.let {
            // Zoom to selected trip
            _mapRegion.value = MapRegion(
                centerLatitude = it.latitude,
                centerLongitude = it.longitude,
                latitudeDelta = 0.5, // Smaller delta for closer zoom
                longitudeDelta = 0.5,
                zoomLevel = 12f // Closer zoom
            )
        }
    }

    fun toggleMapView() {
        _isMapView.value = !_isMapView.value
    }

    fun resetFilters() {
        _searchQuery.value = ""
        _filteredTrips.value = _allTrips.toList() // Reset to all original trips
        _selectedTrip.value = null
        updateMapRegionForTrips(_allTrips.toList()) // Reset map region to all trips
    }

    private fun updateMapRegionForTrips(trips: List<Trip>) {
        _mapRegion.value = MapRegion.fromTrips(trips)
    }

    fun moveMapToRegion(latitude: Double, longitude: Double, zoom: Float = 10f) {
        _mapRegion.value = MapRegion(
            centerLatitude = latitude,
            centerLongitude = longitude,
            latitudeDelta = 1.0, // Default delta for general region moves
            longitudeDelta = 1.0,
            zoomLevel = zoom
        )
    }

    fun getTripsByRegion(): Map<String, List<Trip>> {
        return _allTrips.groupBy { it.region ?: "Other" }
    }

    fun getPopularDestinations(): List<Pair<String, Int>> {
        return _allTrips
            .groupBy { "${it.city}, ${it.country}" }
            .map { (destination, trips) -> destination to trips.size }
            .sortedByDescending { it.second }
    }

    fun updateMapRegion(newMapRegion: MapRegion) {
        _mapRegion.value = newMapRegion
        // No additional filtering based on map bounds here for now, as planned.
    }
}