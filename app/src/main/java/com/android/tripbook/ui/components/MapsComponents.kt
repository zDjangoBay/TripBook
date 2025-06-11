package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.android.tripbook.model.Location
import com.android.tripbook.model.Trip
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.*

@Composable
fun TripMapView(
    trip: Trip,
    modifier: Modifier = Modifier,
    showRoute: Boolean = true
) {
    val defaultCenter = LatLng(0.0, 0.0)
    val mapCenter = trip.mapCenter?.let {
        LatLng(it.latitude, it.longitude)
    } ?: trip.destinationCoordinates?.let {
        LatLng(it.latitude, it.longitude)
    } ?: defaultCenter

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapCenter, 12f)
    }

    // Safely decode all polylines outside the composable rendering
    val allRoutes = remember(trip.itinerary) {
        trip.itinerary.mapNotNull { item ->
            try {
                item.routeToNext?.polyline?.let { encoded ->
                    PolyUtil.decode(encoded)
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            compassEnabled = true,
            myLocationButtonEnabled = true
        )
    ) {
        trip.itinerary.forEach { item ->
            item.coordinates?.let { location ->
                Marker(
                    state = MarkerState(
                        position = LatLng(location.latitude, location.longitude)
                    ),
                    title = item.title,
                    snippet = "${item.time} - ${item.location}"
                )
            }
        }

        trip.destinationCoordinates?.let { destination ->
            Marker(
                state = MarkerState(
                    position = LatLng(destination.latitude, destination.longitude)
                ),
                title = trip.destination,
                snippet = "Trip Destination"
            )
        }

        if (showRoute) {
            allRoutes.forEach { path ->
                Polyline(
                    points = path,
                    color = Color.Blue,
                    width = 5f
                )
            }
        }
    }
}

@Composable
fun LocationPicker(
    onLocationSelected: (Location) -> Unit,
    modifier: Modifier = Modifier,
    initialLocation: LatLng = LatLng(0.0, 0.0)
) {
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var isReverseGeocoding by remember { mutableStateOf(false) }
    var resolvedAddress by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 12f)
    }

    Column(modifier = modifier) {
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                selectedLocation = latLng
                resolvedAddress = null

                // Perform reverse geocoding
                coroutineScope.launch {
                    isReverseGeocoding = true
                    try {
                        val address = performReverseGeocoding(latLng)
                        resolvedAddress = address
                    } catch (e: Exception) {
                        resolvedAddress = "Unable to resolve address"
                    } finally {
                        isReverseGeocoding = false
                    }
                }
            }
        ) {
            selectedLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Selected Location",
                    snippet = resolvedAddress ?: "Resolving address..."
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Display selected location info
        selectedLocation?.let { location ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Selected Coordinates:",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "${String.format("%.6f", location.latitude)}, ${String.format("%.6f", location.longitude)}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    when {
                        isReverseGeocoding -> {
                            Row {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Resolving address...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        resolvedAddress != null -> {
                            Text(
                                text = "Address:",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = resolvedAddress!!,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onLocationSelected(
                        Location(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            address = resolvedAddress ?: "Selected Location"
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isReverseGeocoding
            ) {
                if (isReverseGeocoding) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Confirm Location")
            }
        }
    }
}

@Composable
fun PlaceSearchField(
    onPlaceSelected: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    searchPlaces: suspend (String) -> List<Pair<String, String>> = { emptyList() }
) {
    var searchText by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var showSuggestions by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
                showSuggestions = newText.isNotEmpty()

                if (newText.isNotEmpty() && newText.length >= 3) {
                    // Debounced search with real API call capability
                    coroutineScope.launch {
                        isSearching = true
                        try {
                            // Try to use the provided search function first
                            val apiResults = searchPlaces(newText)
                            suggestions = if (apiResults.isNotEmpty()) {
                                apiResults
                            } else {
                                // Fallback to mock data for development
                                getMockSearchResults(newText)
                            }
                        } catch (e: Exception) {
                            // Fallback to mock data on error
                            suggestions = getMockSearchResults(newText)
                        } finally {
                            isSearching = false
                        }
                    }
                } else {
                    suggestions = emptyList()
                }
            },
            label = { Text("Search for places") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                }
            },
            supportingText = {
                if (searchText.isNotEmpty() && searchText.length < 3) {
                    Text(
                        text = "Type at least 3 characters to search",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )

        if (showSuggestions && suggestions.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    suggestions.take(5).forEach { (name, placeId) ->
                        TextButton(
                            onClick = {
                                searchText = name
                                showSuggestions = false
                                onPlaceSelected(name, placeId)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = name,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        if (suggestions.indexOf(name to placeId) < suggestions.size - 1) {
                            Divider()
                        }
                    }

                    if (suggestions.size > 5) {
                        Text(
                            text = "... and ${suggestions.size - 5} more results",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        // Show helpful message when no results found
        if (showSuggestions && suggestions.isEmpty() && !isSearching && searchText.length >= 3) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No places found for '$searchText'",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

// Enhanced mock search results that are more location-aware
private fun getMockSearchResults(query: String): List<Pair<String, String>> {
    val mockPlaces = listOf(
        // Major cities
        "Paris, France" to "paris_france",
        "London, United Kingdom" to "london_uk",
        "New York, NY, USA" to "new_york_usa",
        "Tokyo, Japan" to "tokyo_japan",
        "Rome, Italy" to "rome_italy",
        "Barcelona, Spain" to "barcelona_spain",
        "Sydney, Australia" to "sydney_australia",
        "Dubai, UAE" to "dubai_uae",
        "Bangkok, Thailand" to "bangkok_thailand",
        "Cairo, Egypt" to "cairo_egypt",

        // Landmarks and attractions
        "Eiffel Tower, Paris" to "eiffel_tower",
        "Big Ben, London" to "big_ben",
        "Times Square, New York" to "times_square",
        "Colosseum, Rome" to "colosseum",
        "Sagrada Familia, Barcelona" to "sagrada_familia",
        "Opera House, Sydney" to "opera_house",
        "Burj Khalifa, Dubai" to "burj_khalifa",
        "Pyramids of Giza, Egypt" to "pyramids_giza",

        // Hotels and accommodations
        "Hilton Hotel" to "hilton_generic",
        "Marriott Hotel" to "marriott_generic",
        "Holiday Inn" to "holiday_inn_generic",
        "Ritz Carlton" to "ritz_carlton_generic",

        // Transportation hubs
        "Charles de Gaulle Airport" to "cdg_airport",
        "Heathrow Airport" to "heathrow_airport",
        "JFK Airport" to "jfk_airport",
        "Central Station" to "central_station_generic"
    )

    return mockPlaces.filter { (name, _) ->
        name.contains(query, ignoreCase = true)
    }.sortedBy { (name, _) ->
        // Sort by relevance - exact matches first, then starts with, then contains
        when {
            name.equals(query, ignoreCase = true) -> 0
            name.startsWith(query, ignoreCase = true) -> 1
            else -> 2
        }
    }
}

// Mock reverse geocoding function
// In production, this should call Google's Geocoding API
private suspend fun performReverseGeocoding(latLng: LatLng): String {
    // Simulate network delay
    kotlinx.coroutines.delay(1000)

    // Mock geocoding based on rough coordinates
    return when {
        // Paris area
        latLng.latitude in 48.8..48.9 && latLng.longitude in 2.3..2.4 ->
            "Paris, France"
        // London area
        latLng.latitude in 51.5..51.6 && latLng.longitude in -0.2..0.0 ->
            "London, United Kingdom"
        // New York area
        latLng.latitude in 40.7..40.8 && latLng.longitude in -74.1..-73.9 ->
            "New York, NY, USA"
        // Cairo area
        latLng.latitude in 30.0..30.1 && latLng.longitude in 31.2..31.3 ->
            "Cairo, Egypt"
        // Generic location for other coordinates
        else -> {
            val lat = String.format("%.4f", latLng.latitude)
            val lng = String.format("%.4f", latLng.longitude)
            "Location at $lat, $lng"
        }
    }
}