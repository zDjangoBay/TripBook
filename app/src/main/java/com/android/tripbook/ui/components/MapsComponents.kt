package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.Location
import com.android.tripbook.model.Trip
// import com.google.android.gms.maps.model.CameraPosition
// import com.google.android.gms.maps.model.LatLng
// import com.google.maps.android.compose.GoogleMap
// import com.google.maps.android.compose.MapProperties
// import com.google.maps.android.compose.MapType
// import com.google.maps.android.compose.MapUiSettings
// import com.google.maps.android.compose.Marker
// import com.google.maps.android.compose.MarkerState
// import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun TripMapView(
    trip: Trip,
    modifier: Modifier = Modifier,
    showRoute: Boolean = true
) {
    // Default center point (you can customize this)
    val defaultCenter = LatLng(0.0, 0.0)
    val mapCenter = trip.mapCenter?.let {
        LatLng(it.latitude, it.longitude)
    } ?: trip.destinationCoordinates?.let {
        LatLng(it.latitude, it.longitude)
    } ?: defaultCenter

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapCenter, 12f)
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
        // Add markers for itinerary items
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

        // Add destination marker if available
        trip.destinationCoordinates?.let { destination ->
            Marker(
                state = MarkerState(
                    position = LatLng(destination.latitude, destination.longitude)
                ),
                title = trip.destination,
                snippet = "Trip Destination"
            )
        }

        // Add route polylines if requested
        if (showRoute) {
            trip.itinerary.forEachIndexed { index, item ->
                item.routeToNext?.let { route ->
                    // You would decode the polyline here and add it as a Polyline
                    // This requires the GoogleMapsService.decodePolyline function
                }
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
            }
        ) {
            selectedLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Selected Location"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        selectedLocation?.let { location ->
            Button(
                onClick = {
                    onLocationSelected(
                        Location(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            address = "Selected Location"
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm Location")
            }
        }
    }
}

@Composable
fun PlaceSearchField(
    onPlaceSelected: (String, String) -> Unit, // placeName, placeId
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var showSuggestions by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
                showSuggestions = newText.isNotEmpty()
                // Here you would call your GoogleMapsService to get suggestions
                // For now, we'll use mock data
                if (newText.isNotEmpty()) {
                    suggestions = listOf(
                        "Search result 1",
                        "Search result 2",
                        "Search result 3"
                    ).filter { it.contains(newText, ignoreCase = true) }
                }
            },
            label = { Text("Search for places") },
            modifier = Modifier.fillMaxWidth()
        )

        if (showSuggestions && suggestions.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    suggestions.forEach { suggestion ->
                        TextButton(
                            onClick = {
                                searchText = suggestion
                                showSuggestions = false
                                onPlaceSelected(suggestion, "mock_place_id")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = suggestion,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

/*
@Composable
fun SimpleMapView(
    modifier: Modifier = Modifier,
    cameraPosition: LatLng = LatLng(0.0, 0.0), // Default to (0,0) or a sensible default
    zoomLevel: Float = 10f,
    mapProperties: MapProperties = MapProperties(),
    mapUiSettings: MapUiSettings = MapUiSettings()
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cameraPosition, zoomLevel)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = mapUiSettings
    )
}
*/

/*
@Composable
fun MapWithMarkers(
    modifier: Modifier = Modifier,
    markers: List<LatLng>,
    initialLocation: LatLng = markers.firstOrNull() ?: LatLng(0.0, 0.0),
    zoomLevel: Float = 10f
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, zoomLevel)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        markers.forEach { location ->
            Marker(
                state = MarkerState(position = location),
                title = "Marker at $location"
            )
        }
    }
}
*/

/**
 * A more advanced map view that can display a list of locations (markers) and
 * potentially routes between them (though route drawing isn't implemented here yet).
 *
 * @param modifier Modifier for styling the map.
 * @param locations List of `com.android.tripbook.model.Location` objects to display as markers.
 * @param onLocationClick Lambda to be invoked when a marker is clicked.
 * @param mapProperties Properties to configure the map (e.g., map type).
 * @param mapUiSettings UI settings for the map (e.g., zoom controls).
 */
/*
@Composable
fun TripMapView(
    modifier: Modifier = Modifier,
    locations: List<com.android.tripbook.model.Location>,
    // onLocationClick: (com.android.tripbook.model.Location) -> Unit = {},
    // mapProperties: MapProperties = MapProperties(mapType = MapType.NORMAL),
    // mapUiSettings: MapUiSettings = MapUiSettings(zoomControlsEnabled = true)
) {
    val validLocations = locations.filter { it.latitude != 0.0 || it.longitude != 0.0 }
    val initialCameraPosition = validLocations.firstOrNull()
        ?.let { LatLng(it.latitude, it.longitude) }
        ?: LatLng(0.0, 0.0) // Default if no valid locations

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialCameraPosition, 10f) // Default zoom
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        // properties = mapProperties,
        // uiSettings = mapUiSettings
    ) {
        validLocations.forEach { location ->
            Marker(
                state = MarkerState(position = LatLng(location.latitude, location.longitude)),
                title = location.address.ifEmpty { "Unnamed Location" },
                snippet = "Lat: ${location.latitude}, Lng: ${location.longitude}",
                // onClick = { onLocationClick(location); true } // Returning true consumes the click
            )
        }
        // TODO: Add Polyline drawing for routes if needed
    }
}
*/