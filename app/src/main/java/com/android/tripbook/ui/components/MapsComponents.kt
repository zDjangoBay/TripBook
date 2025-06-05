package com.android.tripbook.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.android.tripbook.model.ItineraryType
import com.android.tripbook.service.GeoLocation
import com.android.tripbook.service.GoogleMapsService
import com.android.tripbook.service.NominatimService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

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
    // Renamed for clarity: `routePolylinePoints` to avoid confusion with general polylines
    var routePolylinePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    // Default location (Yaoundé, Cameroon)
    val fallbackDefaultLatLng = LatLng(3.848, 11.502)

    // State to track the resolved map center
    var resolvedMapCenter by remember { mutableStateOf<LatLng?>(null) }
    var isResolving by remember { mutableStateOf(false) }
    var resolutionError by remember { mutableStateOf<String?>(null) }

    // Initialize camera position state with a state that updates when resolvedMapCenter changes
    val cameraPositionState = rememberCameraPositionState()

    // Permissions management for My Location button
    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (!hasLocationPermission) {
            Log.w("TripMapView", "Location permission denied.")
        }
    }

    // Effect to request permissions when the composable is first displayed
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
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
                Log.d("TripMapView", "Using existing coordinates: ${resolvedMapCenter}")
                return@LaunchedEffect
            }

            // Second priority: Search for destination if name is provided
            if (trip.destination.isNotBlank()) {
                Log.d("TripMapView", "Searching for destination: ${trip.destination}")

                // Try Google Maps first
                try {
                    val googleSearchResults = googleMapsService.searchPlaces(trip.destination)
                    val googleResult = googleSearchResults.firstOrNull()

                    if (googleResult?.geometry?.location != null) {
                        val geoLoc = googleResult.geometry.location
                        resolvedMapCenter = LatLng(geoLoc.lat, geoLoc.lng)
                        Log.d("TripMapView", "Found location via Google: ${resolvedMapCenter}")
                        return@LaunchedEffect
                    }
                } catch (e: Exception) {
                    Log.e("TripMapView", "Google search failed: ${e.message}", e)
                    resolutionError = "Google search failed: ${e.message}"
                }

                // Try Nominatim as fallback
                try {
                    val nominatimSearchResults = nominatimService.searchLocation(trip.destination)
                    val nominatimResult = nominatimSearchResults.firstOrNull()

                    if (nominatimResult != null) {
                        resolvedMapCenter = LatLng(
                            nominatimResult.latitude.toDouble(),
                            nominatimResult.longitude.toDouble()
                        )
                        Log.d("TripMapView", "Found location via Nominatim: ${resolvedMapCenter}")
                        return@LaunchedEffect
                    }
                } catch (e: Exception) {
                    Log.e("TripMapView", "Nominatim search failed: ${e.message}", e)
                    resolutionError = "${resolutionError ?: ""}\nNominatim search failed: ${e.message}"
                }
            }

            // Fallback to default location
            resolvedMapCenter = fallbackDefaultLatLng
            Log.d("TripMapView", "Using fallback location: ${resolvedMapCenter}")

        } catch (e: Exception) {
            Log.e("TripMapView", "Unexpected error during resolution: ${e.message}", e)
            resolvedMapCenter = fallbackDefaultLatLng
            resolutionError = "Unexpected error: ${e.message}"
        } finally {
            isResolving = false
        }
    }

    // Effect to animate camera when location is resolved
    LaunchedEffect(resolvedMapCenter) {
        resolvedMapCenter?.let { center ->
            Log.d("TripMapView", "Animating camera to: $center")
            try {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(center, 12f)
                )
            } catch (e: Exception) {
                Log.e("TripMapView", "Camera animation failed: ${e.message}", e)
            }
        }
    }

    // Effect to fetch and draw route polylines
    // Only if showRoutes is true and there are at least two points to connect
    LaunchedEffect(showRoutes, resolvedMapCenter, trip.itinerary) {
        if (showRoutes && resolvedMapCenter != null && trip.itinerary.size > 1) {
            val waypoints = mutableListOf<LatLng>()

            // Start from the trip's main destination (resolvedMapCenter)
            resolvedMapCenter?.let { waypoints.add(it) }

            // Add itinerary points
            trip.itinerary.forEach { item ->
                item.coordinates?.let { coords ->
                    waypoints.add(LatLng(coords.latitude, coords.longitude))
                }
            }

            // Fetch route if there are at least two valid points
            if (waypoints.size >= 2) {
                scope.launch {
                    try {
                        // Assume GoogleMapsService has a function to get directions and return polyline
                        // You'll need to implement this in GoogleMapsService
                        val encodedPolyline = googleMapsService.getDirectionsPolyline(waypoints)
                        if (encodedPolyline != null) {
                            // Decode polyline (Google Maps Utils library has a function for this)
                            // This part needs the Google Maps Utils library: `implementation 'com.google.maps.android:maps-utils-ktx:3.0.0'`
                            // Or, implement your own decoding logic
                            routePolylinePoints = decodePoly(encodedPolyline)
                            Log.d("TripMapView", "Polyline fetched and decoded.")
                        } else {
                            Log.w("TripMapView", "No polyline returned from Google Maps Service.")
                        }
                    } catch (e: Exception) {
                        Log.e("TripMapView", "Failed to fetch route polyline: ${e.message}", e)
                        routePolylinePoints = emptyList() // Clear polyline on error
                    }
                }
            } else {
                routePolylinePoints = emptyList() // Not enough points for a route
            }
        } else {
            routePolylinePoints = emptyList() // Clear polyline if not showing routes or insufficient points
        }
    }


    // Show loading or error state if needed
    if (isResolving) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Show error message if resolution failed (optional)
    resolutionError?.let { error ->
        Log.e("TripMapView", "Resolution errors: $error") // Using Log.e for errors
        Text(
            text = "Map Resolution Error: $error",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(8.dp)
        )
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            compassEnabled = true,
            // Enable myLocationButton only if permission is granted
            myLocationButtonEnabled = hasLocationPermission,
            mapToolbarEnabled = true
        ),
        properties = MapProperties(
            mapType = mapType,
            // Enable isMyLocationEnabled only if permission is granted
            isMyLocationEnabled = hasLocationPermission
        )
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
        if (showRoutes && routePolylinePoints.isNotEmpty()) {
            Polyline(
                points = routePolylinePoints,
                color = Color(0xFF667EEA), // A nice blue-purple
                width = 8f
            )
        }
    }
}

// --- LocationPicker and PlaceSearchField components ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPicker(
    selectedLocation: com.android.tripbook.model.Location?, // Changed type to your Location model
    onLocationSelected: (com.android.tripbook.model.Location) -> Unit, // Changed type
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val nominatimService = remember { NominatimService() } // Or inject it

    Box(modifier = modifier
        .fillMaxWidth()
        .clickable { showDialog = true }
        .padding(16.dp)) {
        Text(
            text = selectedLocation?.address ?: "Select Location", // Use actual address
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Select Location",
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Search for Location") },
            text = {
                // Pass onPlaceSelected a lambda that creates your Location model
                PlaceSearchField(
                    onPlaceSelected = { displayName, lat, lon ->
                        val address = getAddressFromLatLng(context, lat, lon) ?: displayName // Try to get a more readable address
                        onLocationSelected(
                            com.android.tripbook.model.Location(
                                address = address,
                                latitude = lat,
                                longitude = lon
                            )
                        )
                        showDialog = false
                    },
                    nominatimService = nominatimService
                )
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Helper function to get address from LatLng (can be slow, run on background thread in real app)
private fun getAddressFromLatLng(context: Context, latitude: Double, longitude: Double): String? {
    if (latitude == 0.0 && longitude == 0.0) return null // Avoid geocoding 0,0

    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                if (addresses.isNotEmpty()) {
                    // This is a callback, so it's asynchronous. For a direct return,
                    // we might need a different approach or run this on a dispatcher.
                    // For now, assuming it's called and value processed when available.
                }
            }.firstOrNull()?.getAddressLine(0)
        } else {
            @Suppress("DEPRECATION")
            geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()?.getAddressLine(0)
        }
    } catch (e: IOException) {
        Log.e("LocationPicker", "Geocoder service not available or network error: ${e.message}")
        null
    } catch (e: IllegalArgumentException) {
        Log.e("LocationPicker", "Invalid latitude or longitude: ${e.message}")
        null
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceSearchField(
    onPlaceSelected: (displayName: String, latitude: Double, longitude: Double) -> Unit,
    nominatimService: NominatimService, // Pass the service as a dependency
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<com.android.tripbook.service.NominatimPlace>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { newValue ->
                searchText = newValue
                if (newValue.length > 2) { // Search after 2 characters
                    scope.launch {
                        searchResults = nominatimService.searchLocation(newValue)
                    }
                } else {
                    searchResults = emptyList()
                }
            },
            label = { Text("Search for a place") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    scope.launch {
                        searchResults = nominatimService.searchLocation(searchText)
                    }
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (searchResults.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    searchResults.forEach { place ->
                        ListItem(
                            headlineContent = { Text(place.displayName) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onPlaceSelected(
                                        place.displayName,
                                        place.latitude.toDouble(),
                                        place.longitude.toDouble()
                                    )
                                    searchText = place.displayName // Update text field
                                    searchResults = emptyList() // Clear results
                                    keyboardController?.hide()
                                }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}


// --- Google Maps Utils (Requires maps-utils-ktx dependency) ---
// This function needs to be available. It's typically from com.google.maps.android.PolyUtil
// For simplicity, I'm including a common implementation.
// Make sure to add `implementation 'com.google.maps.android:maps-utils-ktx:3.0.0'` in your build.gradle
fun decodePoly(encoded: String): List<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
        lng += dlng

        val p = LatLng(
            lat.toDouble() / 1E5,
            lng.toDouble() / 1E5
        )
        poly.add(p)
    }
    return poly
}