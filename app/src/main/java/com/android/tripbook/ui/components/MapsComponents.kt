package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.android.tripbook.service.GoogleMapsService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.android.tripbook.model.ItineraryType
import androidx.compose.ui.graphics.Color
import com.android.tripbook.service.GeoLocation
import com.android.tripbook.service.NominatimService
import kotlinx.coroutines.CancellationException // Important for coroutine cancellations
import kotlinx.coroutines.launch
import java.io.IOException // For network-related exceptions

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
    mapType: MapType = MapType.NORMAL,
    // New parameters for permission handling
    isMyLocationEnabled: Boolean,
    onPermissionRequest: () -> Unit // Lambda to request permission
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // State management
    var polylinePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var resolvedMapCenter by remember { mutableStateOf<LatLng?>(null) }
    var isResolving by remember { mutableStateOf(false) }
    var resolutionError by remember { mutableStateOf<String?>(null) }
    var routeError by remember { mutableStateOf<String?>(null) } // New state for route specific errors

    // Default location (Yaoundé, Cameroon)
    val fallbackDefaultLatLng = LatLng(3.848, 11.502) // Your specified fallback location

    // Camera position state
    val cameraPositionState = rememberCameraPositionState()

    // Effect to resolve the destination coordinates
    LaunchedEffect(trip.destination, trip.destinationCoordinates) {
        isResolving = true
        resolutionError = null // Clear previous resolution errors

        try {
            // First priority: Use existing coordinates if available
            if (trip.destinationCoordinates != null) {
                resolvedMapCenter = LatLng(
                    trip.destinationCoordinates.latitude,
                    trip.destinationCoordinates.longitude
                )
                println("TripMapView: Using existing coordinates: $resolvedMapCenter")
                return@LaunchedEffect // Exit LaunchedEffect if coordinates are found
            }

            // Second priority: Search for destination if name is provided
            if (trip.destination.isNotBlank()) {
                println("TripMapView: Attempting to resolve destination: '${trip.destination}'")
                var foundViaGoogle = false

                // Try Google Maps first with a specific error catch
                try {
                    val googleSearchResults = googleMapsService.searchPlaces(trip.destination)
                    val googleResult = googleSearchResults.firstOrNull()

                    if (googleResult?.geometry?.location != null) {
                        val geoLoc = googleResult.geometry.location
                        resolvedMapCenter = LatLng(geoLoc.lat, geoLoc.lng)
                        println("TripMapView: Found location via Google: $resolvedMapCenter")
                        foundViaGoogle = true // Indicate success via Google
                    } else {
                        println("TripMapView: Google search found no results for '${trip.destination}'.")
                    }
                } catch (e: IOException) {
                    println("TripMapView: Google Maps network error: ${e.message}")
                    // Don't set resolutionError here yet, try Nominatim as fallback for network issues too
                } catch (e: Exception) {
                    println("TripMapView: Google Maps API error or unexpected issue: ${e.message}")
                    // Don't set resolutionError here, try Nominatim as fallback
                }

                // Try Nominatim as fallback if Google failed or didn't find
                if (resolvedMapCenter == null) { // Only try Nominatim if Google didn't yield a result
                    try {
                        val nominatimSearchResults = nominatimService.searchLocation(trip.destination)
                        val nominatimResult = nominatimSearchResults.firstOrNull()

                        if (nominatimResult != null) {
                            resolvedMapCenter = LatLng(
                                nominatimResult.latitude.toDouble(),
                                nominatimResult.longitude.toDouble()
                            )
                            println("TripMapView: Found location via Nominatim: $resolvedMapCenter")
                        } else {
                            println("TripMapView: Nominatim search found no results for '${trip.destination}'.")
                        }
                    } catch (e: IOException) {
                        // Catch network issues specifically for Nominatim
                        println("TripMapView: Nominatim network error: ${e.message}")
                        resolutionError = "Network error: Could not connect to location services. Check your internet connection."
                    } catch (e: Exception) {
                        // Catch other general exceptions for Nominatim
                        println("TripMapView: Nominatim API error or unexpected issue: ${e.message}")
                        resolutionError = "An error occurred while searching for '${trip.destination}'. Please try again."
                    }
                }

                // If after both attempts, no location is found for a named destination
                if (resolvedMapCenter == null && trip.destination.isNotBlank()) {
                    // Set a specific error message if neither service found the location,
                    // but only if a more specific network error wasn't already set.
                    if (resolutionError == null) {
                        resolutionError = "Could not pinpoint location for '${trip.destination}'. Displaying a default area."
                    }
                }
            }

            // Final fallback to default location if no coordinates or search results at all
            if (resolvedMapCenter == null) {
                resolvedMapCenter = fallbackDefaultLatLng
                println("TripMapView: Using fallback location: $fallbackDefaultLatLng")
                // Only set resolutionError if there was an attempt to find a named destination and it completely failed,
                // and no other error was more specific.
                if (trip.destination.isNotBlank() && resolutionError == null) {
                    resolutionError = "Could not find location for '${trip.destination}'. Showing default location (Yaoundé)."
                } else if (trip.destination.isBlank() && resolutionError == null) {
                    // If destination is blank, just inform that a default location is shown
                    resolutionError = "No specific destination provided. Showing default location (Yaoundé)."
                }
            }

        } catch (e: CancellationException) {
            // Propagate CancellationException if the coroutine is cancelled
            throw e
        } catch (e: Exception) {
            // General catch for any unexpected error during the entire resolution process
            println("TripMapView: Unexpected top-level error during resolution: ${e.message}")
            resolvedMapCenter = fallbackDefaultLatLng
            resolutionError = "An unexpected error occurred: ${e.message}. Showing default map location."
        } finally {
            isResolving = false
        }
    }

    // Effect to animate camera when location is resolved
    LaunchedEffect(resolvedMapCenter) {
        resolvedMapCenter?.let { center ->
            println("TripMapView: Animating camera to: $center")
            try {
                // Ensure the map is ready for animation
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(center, 12f)
                )
            } catch (e: IllegalStateException) {
                // Catch specific exceptions if the map or camera state isn't ready
                println("TripMapView: Camera animation failed (IllegalState): ${e.message}. Map might not be fully initialized.")
                // No need to set a user-facing error here as it's an internal state issue
            } catch (e: Exception) {
                println("TripMapView: Generic Camera animation failed: ${e.message}. Consider checking map lifecycle or initial state.")
            }
        }
    }

    // Effect to load route polylines when showRoutes is true
    LaunchedEffect(trip.itinerary, showRoutes, resolvedMapCenter) {
        routeError = null // Clear previous route errors
        if (showRoutes && resolvedMapCenter != null) {
            scope.launch {
                try {
                    val points = mutableListOf<LatLng>()

                    // Add destination as first point
                    points.add(resolvedMapCenter!!) // Safe due to enclosing 'if (resolvedMapCenter != null)'

                    // Filter out itinerary items without valid coordinates to prevent errors
                    val validItineraryItems = trip.itinerary.filter { it.coordinates != null }

                    if (validItineraryItems.isEmpty() && points.size == 1) {
                        // If only the destination marker exists, no route can be drawn.
                        println("TripMapView: No valid itinerary points with coordinates to draw a route.")
                        polylinePoints = emptyList() // Clear any old polyline
                        routeError = "No valid itinerary points found to draw a route."
                        return@launch
                    }

                    // Add itinerary points that have coordinates
                    validItineraryItems.forEach { item ->
                        item.coordinates?.let { location ->
                            points.add(LatLng(location.latitude, location.longitude))
                        }
                    }

                    // TODO: Implement actual route calculation using Google Directions API
                    // Placeholder for actual route calculation:
                    if (points.size > 1) {
                        // In a real app, you'd call googleMapsService.getDirections(points[0], points[1], ...) here
                        // For now, just connect points directly as per your original logic
                        polylinePoints = points
                        println("TripMapView: Polyline points set: ${points.size} points.")
                    } else {
                        // Clear polyline if there's only one point (the destination) or no points to draw a route
                        println("TripMapView: Not enough points to draw a polyline (only ${points.size} point(s)).")
                        polylinePoints = emptyList()
                        routeError = "Not enough locations to draw a complete route."
                    }

                } catch (e: CancellationException) {
                    throw e // Propagate cancellation
                } catch (e: IOException) {
                    println("TripMapView: Route calculation network error: ${e.message}")
                    routeError = "Network error during route calculation. Check your connection."
                } catch (e: Exception) {
                    println("TripMapView: Unexpected error during route calculation: ${e.message}")
                    routeError = "Failed to calculate route: ${e.message}. Please try again later."
                }
            }
        } else {
            // Clear polyline if showRoutes is false or resolvedMapCenter is null
            polylinePoints = emptyList()
            routeError = null // Clear route error if routes are hidden or map center is null
        }
    }

    // Main layout with loading, error, and map
    Box(modifier = modifier.fillMaxSize()) {
        // Show loading state
        if (isResolving) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading map location...")
            }
        } else {
            // The map itself
            GoogleMap(
                modifier = Modifier.fillMaxSize(), // Make map fill the Box
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    compassEnabled = true,
                    myLocationButtonEnabled = isMyLocationEnabled, // Controlled by parent
                    mapToolbarEnabled = true
                ),
                properties = MapProperties(
                    mapType = mapType,
                    isMyLocationEnabled = isMyLocationEnabled // Controlled by parent
                ),
                onMapClick = { latLng ->
                    // Handle map clicks if needed
                    println("Map clicked at: $latLng")
                },
                // Handle clicks on the "My Location" button when permission is not granted
                onMyLocationButtonClick = {
                    if (!isMyLocationEnabled) {
                        onPermissionRequest() // Trigger the permission request in the parent
                        true // Consume the event so the default behavior doesn't run
                    } else {
                        false // Allow default behavior (move to current location)
                    }
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
                    item.coordinates?.let { location -> // Ensure coordinates exist
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
                    } ?: run {
                        // Optionally log or handle itinerary items without coordinates
                        println("TripMapView: Itinerary item '${item.title}' has no coordinates and will not be marked.")
                        // You could also add a toast or a small error message here if this is a common issue
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
        }

        // Overlay error messages on top of the map or loading indicator
        // Always display resolutionError if it exists
        resolutionError?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter) // Position at the top center
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        // Overlay routeError message if it exists (e.g., if resolutionError is null)
        // Ensure this doesn't overlap excessively with resolutionError if both are present
        // Or consider combining them into one error display if multiple errors can occur.
        // For now, let's display it separately, perhaps slightly below or with a different background.
        routeError?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter) // Position at the bottom center
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer) // A different color for route specific errors
            ) {
                Text(
                    text = "Route Error: $message",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}