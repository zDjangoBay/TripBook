package com.android.tripbook.ui.uis

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.android.tripbook.model.Trip
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.ItineraryType
import com.android.tripbook.model.Location
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.android.tripbook.ui.components.TripMapView
import com.android.tripbook.service.GoogleMapsService
import com.android.tripbook.service.NominatimService
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import com.google.android.gms.maps.CameraUpdateFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import com.android.tripbook.service.PlaceResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailsScreen(
    trip: Trip,
    onBackClick: () -> Unit,
    onEditItineraryClick: () -> Unit,
    apiKey: String
) {
    var selectedTab by remember { mutableStateOf("Overview") }
    val context = LocalContext.current
    val googleMapsService = remember(apiKey) { GoogleMapsService(context, apiKey) }
    val nominatimService = remember { NominatimService() }

    // Coroutine scope for launching suspend functions within this Composable
    val scope = rememberCoroutineScope()

    // States to hold search results for restaurants
    var nearbyRestaurants by remember { mutableStateOf<List<PlaceResult>>(emptyList()) }
    var isLoadingRestaurants by remember { mutableStateOf(false) }
    var restaurantError by remember { mutableStateOf<String?>(null) }

    // States to hold search results for gas stations
    var nearbyGasStations by remember { mutableStateOf<List<PlaceResult>>(emptyList()) }
    var isLoadingGasStations by remember { mutableStateOf(false) }
    var gasStationError by remember { mutableStateOf<String?>(null) }

    // State to hold location permission status for the map
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Snackbar Host State to show messages (e.g., permission denied)
    val snackbarHostState = remember { SnackbarHostState() }


    // Permission launcher for location
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (!isGranted) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Location permission denied. Map's 'My Location' feature will be unavailable.",
                    actionLabel = "Grant",
                    withDismissAction = true,
                    duration = SnackbarDuration.Long
                )
            }
        } else {
            // Optionally, show a success message if permission was just granted
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Location permission granted!",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667EEA),
                        Color(0xFF764BA2)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = trip.name,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "${trip.startDate.format(DateTimeFormatter.ofPattern("MMM d"))} - ${
                            trip.endDate.format(DateTimeFormatter.ofPattern("MMM d,yyyy"))
                        }",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Overview", "Itinerary", "Map", "Expenses").forEach { tab ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTab = tab },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = tab,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selectedTab == tab) Color(0xFF667EEA) else Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (selectedTab == tab) {
                                    Box(
                                        modifier = Modifier
                                            .width(40.dp)
                                            .height(3.dp)
                                            .background(
                                                Color(0xFF667EEA),
                                                RoundedCornerShape(2.dp)
                                            )
                                    )
                                }
                            }
                        }
                    }

                    Divider(
                        color = Color(0xFFE2E8F0),
                        thickness = 1.dp,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        when (selectedTab) {
                            "Overview" -> OverviewTab(
                                trip = trip,
                                googleMapsService = googleMapsService,
                                scope = scope,
                                nearbyRestaurants = nearbyRestaurants,
                                setNearbyRestaurants = { nearbyRestaurants = it },
                                isLoadingRestaurants = isLoadingRestaurants,
                                setIsLoadingRestaurants = { isLoadingRestaurants = it },
                                restaurantError = restaurantError,
                                setRestaurantError = { restaurantError = it },
                                nearbyGasStations = nearbyGasStations,
                                setNearbyGasStations = { nearbyGasStations = it },
                                isLoadingGasStations = isLoadingGasStations,
                                setIsLoadingGasStations = { isLoadingGasStations = it },
                                gasStationError = gasStationError,
                                setGasStationError = { gasStationError = it }
                            )
                            "Itinerary" -> ItineraryTab(trip, onEditItineraryClick)
                            "Map" -> MapTab(
                                trip = trip,
                                googleMapsService = googleMapsService,
                                nominatimService = nominatimService,
                                hasLocationPermission = hasLocationPermission,
                                onPermissionRequest = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                                snackbarHostState = snackbarHostState
                            )
                            "Expenses" -> ExpensesTab(trip)
                        }
                    }
                }
            }
        }
        // This is the line where the error was reported.
        // It's placed directly inside the root Box's content lambda,
        // so BoxScope is indeed available here.
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapTab(
    trip: Trip,
    googleMapsService: GoogleMapsService,
    nominatimService: NominatimService,
    hasLocationPermission: Boolean,
    onPermissionRequest: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var showRoutes by remember { mutableStateOf(true) }
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = showRoutes,
                    onCheckedChange = { showRoutes = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF667EEA),
                        checkedTrackColor = Color(0xFF667EEA).copy(alpha = 0.5f)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Show Routes",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }

            OutlinedButton(
                onClick = {
                    mapType = when (mapType) {
                        MapType.NORMAL -> MapType.SATELLITE
                        MapType.SATELLITE -> MapType.HYBRID
                        MapType.HYBRID -> MapType.TERRAIN
                        MapType.TERRAIN -> MapType.NORMAL
                        else -> MapType.NORMAL
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF667EEA)
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF667EEA), Color(0xFF667EEA))
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = when (mapType) {
                        MapType.NORMAL -> "Normal"
                        MapType.SATELLITE -> "Satellite"
                        MapType.HYBRID -> "Hybrid"
                        MapType.TERRAIN -> "Terrain"
                        else -> "Normal"
                    },
                    fontSize = 12.sp
                )
            }
        }

        TripMapView(
            trip = trip,
            googleMapsService = googleMapsService,
            nominatimService = nominatimService,
            showRoutes = showRoutes,
            mapType = mapType,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp)),
            isMyLocationEnabled = hasLocationPermission,
            onPermissionRequest = {
                onPermissionRequest()
            }
        )

        if (!hasLocationPermission) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Enable location to see your current position on the map.",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onPermissionRequest() }) {
                        Text("Grant Access")
                    }
                }
            }
        }
        if (trip.itinerary.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            MapLegend()
        }
    }
}

@Composable
private fun OverviewTab(
    trip: Trip,
    googleMapsService: GoogleMapsService,
    scope: CoroutineScope,
    nearbyRestaurants: List<PlaceResult>,
    setNearbyRestaurants: (List<PlaceResult>) -> Unit,
    isLoadingRestaurants: Boolean,
    setIsLoadingRestaurants: (Boolean) -> Unit,
    restaurantError: String?,
    setRestaurantError: (String?) -> Unit,
    nearbyGasStations: List<PlaceResult>,
    setNearbyGasStations: (List<PlaceResult>) -> Unit,
    isLoadingGasStations: Boolean,
    setIsLoadingGasStations: (Boolean) -> Unit,
    gasStationError: String?,
    setGasStationError: (String?) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Trip Summary",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A202C),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    DetailItem(icon = "📅", text = "8 days, 7 nights")
                    DetailItem(icon = "🏨", text = "Safari Lodge, Luxury Tents")
                    DetailItem(icon = "🚌", text = "4x4 Safari Vehicle")
                    DetailItem(icon = "🍽️", text = "All meals included")
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Travelers",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A202C),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    TravelerItem(
                        initials = "JD",
                        name = "Fontcha Steve (Trip Leader)",
                        color = Color(0xFF667EEA)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TravelerItem(
                        initials = "JS",
                        name = "Chi Rosita",
                        color = Color(0xFF764BA2)
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Nearby Amenities",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A202C),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Button(
                        onClick = {
                            trip.destinationCoordinates?.let { coords ->
                                setIsLoadingRestaurants(true)
                                setRestaurantError(null)
                                scope.launch {
                                    try {
                                        val results = googleMapsService.searchNearbyRestaurants(
                                            latitude = coords.latitude,
                                            longitude = coords.longitude
                                        )
                                        setNearbyRestaurants(results)
                                        println("Found ${results.size} nearby restaurants for ${trip.destination}")
                                        results.forEach { println(" - ${it.name} (${it.address})") }
                                    } catch (e: Exception) {
                                        val userFriendlyMessage = when {
                                            e.message?.contains("REQUEST_DENIED", ignoreCase = true) == true ->
                                                "Failed to load restaurants. Please check your internet connection and API key configuration."
                                            e.message?.contains("INVALID_REQUEST", ignoreCase = true) == true ->
                                                "Failed to load restaurants due to an invalid request. Please contact support."
                                            e.message?.contains("ZERO_RESULTS", ignoreCase = true) == true ->
                                                "No restaurants found nearby. Try a different location or search area."
                                            else ->
                                                "Failed to load restaurants: An unexpected error occurred. Please try again."
                                        }
                                        setRestaurantError(userFriendlyMessage)
                                        println("Error searching restaurants: ${e.message}")
                                    } finally {
                                        setIsLoadingRestaurants(false)
                                    }
                                }
                            } ?: run {
                                setRestaurantError("Destination coordinates not available for search.")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoadingRestaurants
                    ) {
                        if (isLoadingRestaurants) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("Find Nearby Restaurants")
                        }
                    }

                    if (isLoadingRestaurants) {
                        Text("Searching for restaurants...", modifier = Modifier.padding(top = 8.dp))
                    } else if (restaurantError != null) {
                        Text("Error: $restaurantError", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                    } else if (nearbyRestaurants.isNotEmpty()) {
                        Text("Restaurants Found:", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                        nearbyRestaurants.take(5).forEach {
                            Text("- ${it.name} (${it.address})", style = MaterialTheme.typography.bodySmall)
                        }
                        if (nearbyRestaurants.size > 5) {
                            Text("... and ${nearbyRestaurants.size - 5} more.", style = MaterialTheme.typography.bodySmall)
                        }
                    } else {
                        Text("No restaurants found yet. Click 'Find' to search.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            trip.destinationCoordinates?.let { coords ->
                                setIsLoadingGasStations(true)
                                setGasStationError(null)
                                scope.launch {
                                    try {
                                        val results = googleMapsService.searchNearbyGasStations(
                                            latitude = coords.latitude,
                                            longitude = coords.longitude
                                        )
                                        setNearbyGasStations(results)
                                        println("Found ${results.size} nearby gas stations for ${trip.destination}")
                                        results.forEach { println(" - ${it.name} (${it.address})") }
                                    } catch (e: Exception) {
                                        val userFriendlyMessage = when {
                                            e.message?.contains("REQUEST_DENIED", ignoreCase = true) == true ->
                                                "Failed to load gas stations. Please check your internet connection and API key configuration."
                                            e.message?.contains("INVALID_REQUEST", ignoreCase = true) == true ->
                                                "Failed to load gas stations due to an invalid request. Please contact support."
                                            e.message?.contains("ZERO_RESULTS", ignoreCase = true) == true ->
                                                "No gas stations found nearby. Try a different location or search area."
                                            else ->
                                                "Failed to load gas stations: An unexpected error occurred. Please try again."
                                        }
                                        setGasStationError(userFriendlyMessage)
                                        println("Error searching gas stations: ${e.message}")
                                    } finally {
                                        setIsLoadingGasStations(false)
                                    }
                                }
                            } ?: run {
                                setGasStationError("Destination coordinates not available for search.")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoadingGasStations
                    ) {
                        if (isLoadingGasStations) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("Find Nearby Gas Stations")
                        }
                    }

                    if (isLoadingGasStations) {
                        Text("Searching for gas stations...", modifier = Modifier.padding(top = 8.dp))
                    } else if (gasStationError != null) {
                        Text("Error: $gasStationError", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                    } else if (nearbyGasStations.isNotEmpty()) {
                        Text("Gas Stations Found:", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                        nearbyGasStations.take(5).forEach {
                            Text("- ${it.name} (${it.address})", style = MaterialTheme.typography.bodySmall)
                        }
                        if (nearbyGasStations.size > 5) {
                            Text("... and ${nearbyGasStations.size - 5} more.", style = MaterialTheme.typography.bodySmall)
                        }
                    } else {
                        Text("No gas stations found yet. Click 'Find' to search.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MapLegend() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Map Legend",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A202C),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(
                    color = Color(0xFF667EEA),
                    label = "Activities",
                    modifier = Modifier.weight(1f)
                )
                LegendItem(
                    color = Color(0xFFE91E63),
                    label = "Hotels",
                    modifier = Modifier.weight(1f)
                )
                LegendItem(
                    color = Color(0xFF00CC66),
                    label = "Transport",
                    modifier = Modifier.weight(1f)
                )
                LegendItem(
                    color = Color(0xFFDC2626),
                    label = "Destination",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
private fun ItineraryTab(trip: Trip, onEditItineraryClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onEditItineraryClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF667EEA)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Edit Itinerary",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        if (trip.itinerary.isEmpty()) {
            Text(
                text = "No itinerary items added yet.",
                fontSize = 16.sp,
                color = Color(0xFF64748B),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 30.dp)
            ) {
                trip.itinerary.sortedBy { it.date }.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(30.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(
                                        when (item.type) {
                                            ItineraryType.ACTIVITY -> Color(0xFF667EEA)
                                            ItineraryType.ACCOMMODATION -> Color(0xFFE91E63)
                                            ItineraryType.TRANSPORTATION -> Color(0xFF00CC66)
                                        },
                                        CircleShape
                                    )
                                    .border(
                                        width = 3.dp,
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                            )
                            if (index < trip.itinerary.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(100.dp)
                                        .background(Color(0xFFE2E8F0))
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, bottom = 20.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "${item.date.format(DateTimeFormatter.ofPattern("MMM d"))} - ${item.time}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF667EEA),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = item.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A202C),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = item.location,
                                    fontSize = 14.sp,
                                    color = Color(0xFF64748B),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpensesTab(trip: Trip) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Budget Overview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A202C),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    BudgetRow("Total Budget:", "FCFA ${trip.budget}", Color(0xFF667EEA))
                    BudgetRow("Spent:", "FCFA 68000", Color(0xFFDC2626))
                    BudgetRow("Remaining:", "FCfA 72000", Color(0xFF059669))

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(8.dp)
                                .background(Color(0xFF667EEA), RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
        }

        items(listOf(
            ExpenseItem("Accommodation", "Safari Lodge (4 nights)", "FCFA 9600"),
            ExpenseItem("Transportation", "4x4 Vehicle rental", "FCFA 4800"),
            ExpenseItem("Activities", "Game drives & balloon safari", "FCFA 3200")
        )) { expense ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = expense.category,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A202C)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = expense.description,
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = expense.amount,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A202C)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(icon: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 16.sp,
            modifier = Modifier.padding(end = 12.dp)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
private fun TravelerItem(initials: String, name: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = name,
            fontSize = 14.sp,
            color = Color(0xFF1A202C)
        )
    }
}

@Composable
private fun BudgetRow(label: String, amount: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF1A202C)
        )
        Text(
            text = amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

private data class ExpenseItem(
    val category: String,
    val description: String,
    val amount: String
)