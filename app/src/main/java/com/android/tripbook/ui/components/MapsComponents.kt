package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.android.tripbook.service.GoogleMapsService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.android.tripbook.model.ItineraryType
import androidx.compose.ui.graphics.Color
import com.android.tripbook.service.GeoLocation
import com.android.tripbook.service.NominatimService
import kotlinx.coroutines.launch

// Extension function to convert GeoLocation to LatLng
fun GeoLocation.toLatLng(): LatLng {
    return LatLng(this.lat, this.lng)
}

@Composable
fun TripMapView(
    trip: com.android.tripbook.model.Trip,
    googleMapsService: GoogleMapsService,
    nominatimService: NominatimService,
    modifier: Modifier = Modifier,
    showRoutes: Boolean = true,
    mapType: MapType = MapType.NORMAL
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // State management
    var polylinePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var resolvedMapCenter by remember { mutableStateOf<LatLng?>(null) }
    var isResolving by remember { mutableStateOf(false) }
    var resolutionError by remember { mutableStateOf<String?>(null) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Default location (Yaoundé, Cameroon)
    val fallbackDefaultLatLng = LatLng(3.848, 11.502)

    // Camera position state
    val cameraPositionState = rememberCameraPositionState()

    // Permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        // If permission is granted, try to move camera to resolved center if available
        if (isGranted && resolvedMapCenter != null) {
            scope.launch {
                try {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(resolvedMapCenter!!, 12f)
                    )
                } catch (e: Exception) {
                    println("TripMapView: Camera animation after permission failed: ${e.message}")
                }
            }
        }
    }

    // Effect to resolve the destination coordinates
    LaunchedEffect(trip.destination, trip.destinationCoordinates) {
        isResolving = true
        resolutionError = null

        try {
            // First priority: Use existing coordinates if available
            if (trip.destinationCoordinates != null) {
                resolvedMapCenter = LatLng(
                    trip.destinationCoordinates.latitude,
                    trip.destinationCoordinates.longitude
                )
                println("TripMapView: Using existing coordinates: $resolvedMapCenter")
                return@LaunchedEffect
            }

            // Second priority: Search for destination if name is provided
            if (trip.destination.isNotBlank()) {
                println("TripMapView: Searching for destination: ${trip.destination}")

                // Try Google Maps first
                try {
                    val googleSearchResults = googleMapsService.searchPlaces(trip.destination)
                    val googleResult = googleSearchResults.firstOrNull()

                    if (googleResult?.geometry?.location != null) {
                        val geoLoc = googleResult.geometry.location
                        resolvedMapCenter = LatLng(geoLoc.lat, geoLoc.lng)
                        println("TripMapView: Found location via Google: $resolvedMapCenter")
                        return@LaunchedEffect
                    }
                } catch (e: Exception) {
                    println("TripMapView: Google search failed: ${e.message}")
                    // Don't set resolutionError here, try Nominatim as fallback
                }

                // Try Nominatim as fallback if Google failed or didn't find
                if (resolvedMapCenter == null) {
                    try {
                        val nominatimSearchResults = nominatimService.searchLocation(trip.destination)
                        val nominatimResult = nominatimSearchResults.firstOrNull()

                        if (nominatimResult != null) {
                            resolvedMapCenter = LatLng(
                                nominatimResult.latitude.toDouble(),
                                nominatimResult.longitude.toDouble()
                            )
                            println("TripMapView: Found location via Nominatim: $resolvedMapCenter")
                            return@LaunchedEffect
                        }
                    } catch (e: Exception) {
                        println("TripMapView: Nominatim search failed: ${e.message}")
                        // Set resolutionError only if both failed to find location
                        if (resolvedMapCenter == null) {
                            resolutionError = "Could not find location for '${trip.destination}' via Google Maps or Nominatim."
                        }
                    }
                }
            }

            // Fallback to default location if no coordinates or search results
            if (resolvedMapCenter == null) {
                resolvedMapCenter = fallbackDefaultLatLng
                println("TripMapView: Using fallback location: $fallbackDefaultLatLng")
                // Only set resolutionError if there was an attempt to find a named destination and it failed
                if (trip.destination.isNotBlank() && resolutionError == null) {
                    resolutionError = "Could not find location for '${trip.destination}'. Showing default location."
                }
            }

        } catch (e: Exception) {
            println("TripMapView: Unexpected error during resolution: ${e.message}")
            resolvedMapCenter = fallbackDefaultLatLng
            resolutionError = "Unexpected error resolving location: ${e.message}. Showing default location."
        } finally {
            isResolving = false
        }
    }

    // Effect to animate camera when location is resolved
    LaunchedEffect(resolvedMapCenter) {
        resolvedMapCenter?.let { center ->
            println("TripMapView: Animating camera to: $center")
            try {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(center, 12f)
                )
            } catch (e: Exception) {
                println("TripMapView: Camera animation failed: ${e.message}. Consider checking map lifecycle or initial state.")
            }
        }
    }

    // Effect to load route polylines when showRoutes is true
    // This will now only run if resolvedMapCenter is not null, ensuring null safety for resolvedMapCenter!!
    LaunchedEffect(trip.itinerary, showRoutes, resolvedMapCenter) {
        if (showRoutes && resolvedMapCenter != null) {
            scope.launch {
                try {
                    val points = mutableListOf<LatLng>()

                    // Add destination as first point
                    points.add(resolvedMapCenter!!) // Safe due to enclosing 'if (resolvedMapCenter != null)'

                    // Add itinerary points that have coordinates
                    trip.itinerary.forEach { item ->
                        item.coordinates?.let { location ->
                            points.add(LatLng(location.latitude, location.longitude))
                        }
                    }

                    // TODO: Implement actual route calculation using Google Directions API
                    // For now, just connect points directly
                    if (points.size > 1) {
                        polylinePoints = points
                    } else {
                        // Clear polyline if there's only one point or no points to draw a route
                        polylinePoints = emptyList()
                    }

                } catch (e: Exception) {
                    println("TripMapView: Route calculation failed: ${e.message}")
                    // Optionally, show a Toast or Snackbar to the user about route calculation failure
                }
            }
        } else {
            // Clear polyline if showRoutes is false or resolvedMapCenter is null
            polylinePoints = emptyList()
        }
    }

    // Show loading state
    if (isResolving) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading map location...")
            }
        }
        return
    }

    // Show error state if needed (always display if an error occurred)
    if (resolutionError != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Text(
                text = resolutionError!!, // Display the specific error message
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            compassEnabled = true,
            // Only enable myLocationButton if permission is granted
            myLocationButtonEnabled = hasLocationPermission,
            mapToolbarEnabled = true
        ),
        properties = MapProperties(
            mapType = mapType,
            // Only enable isMyLocationEnabled if permission is granted
            isMyLocationEnabled = hasLocationPermission
        ),
        onMapClick = { latLng ->
            // Handle map clicks if needed
            println("Map clicked at: $latLng")
        }
    ) {
        // Main destination marker
        resolvedMapCenter?.let { center ->
            Marker(
                state = MarkerState(position = center),
                title = if (trip.destination.isNotBlank()) trip.destination else "Destination",
                snippet = "Main Destination",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )
        }

        // Itinerary markers
        trip.itinerary.forEachIndexed { index, item ->
            item.coordinates?.let { location ->
                Marker(
                    state = MarkerState(
                        position = LatLng(location.latitude, location.longitude)
                    ),
                    title = item.title,
                    snippet = "${item.time} - ${item.location}",
                    icon = when (item.type) {
                        ItineraryType.ACTIVITY -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        ItineraryType.ACCOMMODATION -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                        ItineraryType.TRANSPORTATION -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                    }
                )
            }
        }

        // Route polyline
        if (showRoutes && polylinePoints.isNotEmpty()) {
            Polyline(
                points = polylinePoints,
                color = Color(0xFF667EEA),
                width = 8f
            )
        }
    }

    // A button or UI element to trigger permission request, instead of LaunchedEffect(Unit)
    // You would integrate this into your UI, for example, showing a button if hasLocationPermission is false.
    // For demonstration, let's add a simple FloatingActionButton
    if (!hasLocationPermission) {
        // This is a placeholder for how you might integrate it into your UI.
        // You might want to show a more descriptive message or a dedicated screen.
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Enable Location")
            }
        }
    }
}