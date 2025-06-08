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
    onPlaceSelected: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    searchPlaces: suspend (String) -> List<Pair<String, String>> = { emptyList() }
) {
    var searchText by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var showSuggestions by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
                showSuggestions = newText.isNotEmpty()

                if (newText.isNotEmpty()) {
                    // Replace with real Google Places API logic in production
                    suggestions = listOf(
                        "Search result 1" to "id1",
                        "Search result 2" to "id2",
                        "Search result 3" to "id3"
                    ).filter { it.first.contains(newText, ignoreCase = true) }
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
                    suggestions.forEach { (name, placeId) ->
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
                    }
                }
            }
        }
    }
}
