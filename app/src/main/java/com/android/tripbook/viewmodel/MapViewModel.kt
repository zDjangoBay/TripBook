
package com.android.tripbook.viewmodel

import android.app.Application
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import com.android.tripbook.model.Trip
import com.android.tripbook.model.MapRegion
import com.android.tripbook.data.SampleTrips
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

    private val _allTrips = mutableStateListOf<Trip>()
    val allTrips: List<Trip> get() = _allTrips

    private val _filteredTrips = mutableStateOf<List<Trip>>(emptyList())
    val filteredTrips: State<List<Trip>> = _filteredTrips

    private val _mapRegion = mutableStateOf(MapRegion.defaultRegion())
    val mapRegion: State<MapRegion> = _mapRegion

    private val _selectedTrip = mutableStateOf<Trip?>(null)
    val selectedTrip: State<Trip?> = _selectedTrip

    private val _isMapView = mutableStateOf(true)
    val isMapView: State<Boolean> = _isMapView

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery


    init {
        loadInitialTrips()
    }

    private fun loadInitialTrips() {
        viewModelScope.launch {
            //could be fetched from a real db
            val tripsFromSource = SampleTrips.get() // Get trips from my sample data
            _allTrips.clear()
            _allTrips.addAll(tripsFromSource)

            filterTrips(_searchQuery.value)
        }
    }


    fun generateNewTripId(): Int {
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
        filterTrips(query)
    }

    private fun filterTrips(query: String) {
        val sourceTrips = _allTrips.toList()

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
        if (_allTrips.none { it.id == newTrip.id }) {
            _allTrips.add(newTrip)

            filterTrips(_searchQuery.value)
        } else {

            println("Warning: Trip with ID ${newTrip.id} already exists.")
        }
    }


    fun selectTrip(trip: Trip?) {
        _selectedTrip.value = trip
        trip?.let {
            _mapRegion.value = MapRegion(
                centerLatitude = it.latitude,
                centerLongitude = it.longitude,
                latitudeDelta = 0.05,
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

    }

    // This function determines the map region based on a list of trips
    private fun updateMapRegionForTrips(tripsForRegion: List<Trip>) {
        _mapRegion.value = MapRegion.fromTrips(tripsForRegion)
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

    fun getTripsByRegion(): Map<String, List<Trip>> {
        return _allTrips.groupBy { it.region ?: "Other" }
    }

    fun getPopularDestinations(): List<Pair<String, Int>> {
        return _allTrips
            .groupBy { "${it.city}, ${it.country}" }
            .map { (destination, tripsInDestination) -> destination to tripsInDestination.size }
            .sortedByDescending { it.second }
    }


    fun updateMapRegion(newMapRegionFromView: MapRegion) {

        _mapRegion.value = newMapRegionFromView

    }
}