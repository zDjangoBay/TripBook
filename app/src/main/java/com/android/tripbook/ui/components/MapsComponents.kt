package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.Location
import com.android.tripbook.model.Trip
import com.android.tripbook.service.GoogleMapsService
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun TripMapView(
    trip: Trip,
    modifier: Modifier = Modifier,
    showRoute: Boolean = true
) {
    // Default center point
    val defaultCenter = LatLng(0.0, 0.0)
    val mapCenter = trip.destinationCoordinates?.let {
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
                if (index < trip.itinerary.size - 1) {
                    val nextItem = trip.itinerary[index + 1]
                    if (item.coordinates != null && nextItem.coordinates != null) {
                        // Simple straight line between points for now
                        Polyline(
                            points = listOf(
                                LatLng(item.coordinates.latitude, item.coordinates.longitude),
                                LatLng(nextItem.coordinates.latitude, nextItem.coordinates.longitude)
                            ),
                            color = androidx.compose.ui.graphics.Color.Blue,
                            width = 5f
                        )
                    }
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
    googleMapsService: GoogleMapsService? = null,
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
                // Use mock data for now
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