package com.android.tripbook.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location as AndroidLocation // Alias to avoid conflict with model.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.Location
import com.android.tripbook.model.Trip
import com.android.tripbook.service.GoogleMapsService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.android.tripbook.model.ItineraryType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.android.tripbook.service.PlaceResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.android.tripbook.service.GeoLocation
import com.android.tripbook.service.NominatimPlace
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import com.android.tripbook.service.NominatimService


// Extension function to convert GeoLocation to LatLng
fun GeoLocation.toLatLng(): LatLng {
    return LatLng(this.lat, this.lng)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    apiKey: String,
    modifier: Modifier = Modifier,
    initialDestinationLatLng: LatLng? = null,
    onDestinationSelected: ((PlaceResult) -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var hasLocationPermission by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<PlaceResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var showSearchResults by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) } // New state for search errors


    val mapsService = remember { GoogleMapsService(context, apiKey) }
    val fusedLocationClient: FusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var currentMapTargetLatLng by remember { mutableStateOf<LatLng?>(initialDestinationLatLng) }
    var currentMapTargetName by remember { mutableStateOf<String?>(null) }

    val defaultMapLatLng = LatLng(6.8787, 10.2291)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialDestinationLatLng ?: defaultMapLatLng, initialDestinationLatLng?.let { 10f } ?: 5f)
    }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    val newLocation = Location(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        name = "Current Location",
                        address = "",
                        rating = 0.0,
                        types = emptyList(),
                        placeId = null
                    )
                    currentLocation = newLocation
                    if (currentMapTargetLatLng == null) {
                        currentMapTargetLatLng = LatLng(it.latitude, it.longitude)
                        currentMapTargetName = "Your Location"
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(newLocation.latitude, newLocation.longitude),
                                    15f
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

        if (hasLocationPermission) {
            scope.launch {
                try {
                    val locationRequest = LocationRequest.create().apply {
                        interval = 10000
                        fastestInterval = 5000
                        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    }
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
                } catch (e: SecurityException) {
                    // Handle permission not actually granted or other security issues
                    searchError = "Location permission not granted. Cannot get current location."
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    DisposableEffect(fusedLocationClient) {
        onDispose {
            if (hasLocationPermission) {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    LaunchedEffect(initialDestinationLatLng) {
        if (initialDestinationLatLng != null) {
            currentMapTargetLatLng = initialDestinationLatLng
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(initialDestinationLatLng, 10f)
            )
        } else if (currentLocation != null) {
            currentMapTargetLatLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            currentMapTargetName = "Your Location"
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(currentMapTargetLatLng!!, 15f)
            )
        }
    }


    Column(modifier = modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column( // Use Column to stack text field and error message
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search places...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (searchQuery.isNotBlank()) {
                                scope.launch {
                                    isLoading = true
                                    searchError = null // Clear previous errors
                                    try {
                                        searchResults = mapsService.searchPlaces(searchQuery)
                                        showSearchResults = true
                                    } catch (e: Exception) {
                                        searchResults = emptyList()
                                        val userFriendlyMessage = when {
                                            e.message?.contains("REQUEST_DENIED", ignoreCase = true) == true ->
                                                "Failed to search places. Please check your internet connection and API key configuration."
                                            e.message?.contains("INVALID_REQUEST", ignoreCase = true) == true ->
                                                "Invalid search request. Please refine your query."
                                            e.message?.contains("ZERO_RESULTS", ignoreCase = true) == true ->
                                                "No results found for your search. Try a different query."
                                            else ->
                                                "An unexpected error occurred during search. Please try again."
                                        }
                                        searchError = userFriendlyMessage
                                        println("Error searching places on map: ${e.message}")
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        enabled = !isLoading && searchQuery.isNotBlank()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Search")
                        }
                    }
                }
                if (searchError != null) {
                    Text(
                        text = searchError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = hasLocationPermission
                ),
                properties = MapProperties(
                    isMyLocationEnabled = hasLocationPermission
                ),
                onMapClick = { latLng ->
                    scope.launch {
                        val clickedPlaceResult = PlaceResult(
                            name = "Selected Point",
                            address = "Lat: ${"%.4f".format(latLng.latitude)}, Lng: ${"%.4f".format(latLng.longitude)}",
                            geometry = com.android.tripbook.service.Geometry(GeoLocation(latLng.latitude, latLng.longitude))
                        )
                        currentMapTargetLatLng = latLng
                        currentMapTargetName = clickedPlaceResult.name
                        onDestinationSelected?.invoke(clickedPlaceResult)
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                        )
                    }
                }
            ) {
                currentMapTargetLatLng?.let { latLng ->
                    Marker(
                        state = MarkerState(position = latLng),
                        title = currentMapTargetName ?: "Location",
                        snippet = "Selected Location"
                    )
                }
            }

            if (showSearchResults && searchResults.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.4f)
                        .align(Alignment.BottomCenter),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Search Results (${searchResults.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            TextButton(
                                onClick = { showSearchResults = false }
                            ) {
                                Text("Close")
                            }
                        }

                        Divider()

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(searchResults) { placeResult ->
                                LocationResultCard(
                                    location = Location(
                                        latitude = placeResult.geometry?.location?.lat ?: 0.0,
                                        longitude = placeResult.geometry?.location?.lng ?: 0.0,
                                        name = placeResult.name,
                                        address = placeResult.address,
                                        rating = placeResult.rating ?: 0.0,
                                        types = placeResult.types,
                                        placeId = placeResult.placeId
                                    ),
                                    onLocationClick = { selectedLocation ->
                                        val selectedLatLng = LatLng(selectedLocation.latitude, selectedLocation.longitude)
                                        currentMapTargetLatLng = selectedLatLng
                                        currentMapTargetName = selectedLocation.name
                                        onDestinationSelected?.invoke(
                                            PlaceResult(
                                                placeId = selectedLocation.placeId ?: "",
                                                name = selectedLocation.name,
                                                address = selectedLocation.address,
                                                geometry = com.android.tripbook.service.Geometry(
                                                    GeoLocation(selectedLocation.latitude, selectedLocation.longitude)
                                                )
                                            )
                                        )
                                        scope.launch {
                                            cameraPositionState.animate(
                                                CameraUpdateFactory.newLatLngZoom(
                                                    selectedLatLng,
                                                    15f
                                                )
                                            )
                                        }
                                        showSearchResults = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (hasLocationPermission) {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                searchError = null // Clear previous errors
                                val location = mapsService.getCurrentLocation()
                                location?.let {
                                    val newLatLng = LatLng(it.latitude, it.longitude)
                                    currentMapTargetLatLng = newLatLng
                                    currentMapTargetName = "Your Location"
                                    onDestinationSelected?.invoke(
                                        PlaceResult(
                                            name = "Your Location",
                                            address = it.address ?: "",
                                            geometry = com.android.tripbook.service.Geometry(GeoLocation(it.latitude, it.longitude))
                                        )
                                    )
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(newLatLng, 15f)
                                    )
                                } ?: run {
                                    searchError = "Could not get current location."
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "My Location")
                    }
                }

                FloatingActionButton(
                    onClick = {
                        currentLocation?.let { location ->
                            scope.launch {
                                isLoading = true // Assuming isLoading can be reused for attractions too
                                searchError = null // Clear previous errors
                                try {
                                    searchResults = mapsService.searchNearbyAttractions(
                                        location.latitude,
                                        location.longitude
                                    )
                                    showSearchResults = true
                                } catch (e: Exception) {
                                    val userFriendlyMessage = when {
                                        e.message?.contains("REQUEST_DENIED", ignoreCase = true) == true ->
                                            "Failed to find nearby attractions. Check API key and network connection."
                                        e.message?.contains("ZERO_RESULTS", ignoreCase = true) == true ->
                                            "No attractions found around your current location."
                                        else ->
                                            "An unexpected error occurred while searching for attractions. Try again."
                                    }
                                    searchError = userFriendlyMessage
                                    println("Error searching attractions: ${e.message}")
                                } finally {
                                    isLoading = false
                                }
                            }
                        } ?: run {
                            searchError = "Cannot find attractions without current location. Please enable location services."
                        }
                    }
                ) {
                    Text("📍")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationResultCard(
    location: Location,
    onLocationClick: (Location) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = { onLocationClick(location) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = location.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (location.address.isNotBlank()) {
                Text(
                    text = location.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (location.rating > 0.0) {
                    Text(
                        text = "⭐ ${location.rating}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (location.types.isNotEmpty()) {
                    Text(
                        text = location.types.first().replace("_", " ").uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
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
    var polylinePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    // Default location (Yaoundé, Cameroon)
    val fallbackDefaultLatLng = LatLng(3.848, 11.502)

    // State to track the resolved map center
    var resolvedMapCenter by remember { mutableStateOf<LatLng?>(null) }
    var isResolving by remember { mutableStateOf(false) }
    var resolutionError by remember { mutableStateOf<String?>(null) }

    // Initialize camera position state with a state that updates when resolvedMapCenter changes
    val cameraPositionState = rememberCameraPositionState()

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
                println("TripMapView: Using existing coordinates: ${resolvedMapCenter}")
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
                        println("TripMapView: Found location via Google: ${resolvedMapCenter}")
                        return@LaunchedEffect
                    }
                } catch (e: Exception) {
                    println("TripMapView: Google search failed: ${e.message}")
                    // Don't set resolutionError for Google failure if Nominatim is fallback
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
                        println("TripMapView: Found location via Nominatim: ${resolvedMapCenter}")
                        return@LaunchedEffect
                    }
                } catch (e: Exception) {
                    println("TripMapView: Nominatim search failed: ${e.message}")
                    resolutionError = "Failed to resolve destination. Check internet or try a different destination." // Generic error message
                }
            }

            // Fallback to default location
            resolvedMapCenter = fallbackDefaultLatLng
            if (resolutionError == null) { // Only set this if no specific API error occurred
                resolutionError = "Could not find a specific location for '${trip.destination}'. Displaying default area."
            }
            println("TripMapView: Using fallback location: ${resolvedMapCenter}")

        } catch (e: Exception) {
            println("TripMapView: Unexpected error during resolution: ${e.message}")
            resolvedMapCenter = fallbackDefaultLatLng
            resolutionError = "An unexpected error occurred while resolving destination: ${e.message}"
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
                println("TripMapView: Camera animation failed: ${e.message}")
            }
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            compassEnabled = true,
            myLocationButtonEnabled = true,
            mapToolbarEnabled = true
        ),
        properties = MapProperties(
            mapType = mapType,
            isMyLocationEnabled = false
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
        if (showRoutes && polylinePoints.isNotEmpty()) {
            Polyline(
                points = polylinePoints,
                color = Color(0xFF667EEA),
                width = 8f
            )
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
                            name = "Selected Location",
                            address = "Selected Location",
                            rating = 0.0,
                            types = emptyList(),
                            placeId = null
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceSearchField(
    onPlaceSelected: (PlaceResult) -> Unit,
    modifier: Modifier = Modifier,
    apiKey: String,
    initialQuery: String = ""
) {
    var searchText by remember { mutableStateOf(initialQuery) }
    var suggestions by remember { mutableStateOf<List<com.google.android.libraries.places.api.model.AutocompletePrediction>>(emptyList()) }
    var showSuggestions by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val googleMapsService = remember { GoogleMapsService(context, apiKey) }
    val scope = rememberCoroutineScope()


    Column(modifier = modifier) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
                showSuggestions = newText.isNotEmpty()
                if (newText.length >= 2) {
                    scope.launch {
                        try {
                            suggestions = googleMapsService.getPlaceSuggestions(newText)
                        } catch (e: Exception) {
                            suggestions = emptyList()
                            // For simplicity, not displaying error for suggestions directly in UI
                            // A Toast message or log could be added here for debugging.
                            println("Error fetching place suggestions: ${e.message}")
                        }
                    }
                } else {
                    suggestions = emptyList()
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
                                scope.launch {
                                    val placeDetails = googleMapsService.getPlaceDetails(suggestion.placeId)
                                    placeDetails?.let {
                                        onPlaceSelected(
                                            PlaceResult(
                                                placeId = it.placeId ?: "",
                                                name = it.name,
                                                address = it.address,
                                                geometry = com.android.tripbook.service.Geometry(GeoLocation(it.latitude, it.longitude))
                                            )
                                        )
                                    }
                                    searchText = suggestion.getFullText(null).toString()
                                    showSuggestions = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = suggestion.getFullText(null).toString(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}