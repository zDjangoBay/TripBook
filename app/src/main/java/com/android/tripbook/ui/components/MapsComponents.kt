package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.Location
import com.android.tripbook.model.Trip
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun TripMapView(
    trip: Trip,
    modifier: Modifier = Modifier,
    showRoute: Boolean = true
) {
    // Default center point (Yaoundé, Cameroon)
    val defaultCenter = LatLng(3.848, 11.502)
    val mapCenter = trip.destinationCoordinates?.let { coordinates ->
        LatLng(coordinates.latitude, coordinates.longitude)
    } ?: trip.itinerary.firstOrNull()?.coordinates?.let { coordinates ->
        LatLng(coordinates.latitude, coordinates.longitude)
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