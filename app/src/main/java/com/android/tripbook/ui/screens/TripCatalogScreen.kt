
package com.android.tripbook.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items // For LazyRow items
import androidx.compose.foundation.lazy.itemsIndexed // For LazyColumn itemsIndexed
import androidx.compose.foundation.shape.CircleShape // For FAB
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add // For FAB
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.List // For View Toggle
import androidx.compose.material.icons.filled.Place // For View Toggle & My Location
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// import androidx.compose.ui.graphics.Color // Replaced with MaterialTheme colors
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.model.User
import com.android.tripbook.model.Trip
import com.android.tripbook.ui.components.* // MapView, LocationSearchBar, TripCard etc.
import com.android.tripbook.ui.components.MiniProfileTruncated
import com.android.tripbook.utils.LocationUtils
import com.android.tripbook.viewmodel.MapViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun mockUsersForTrip(tripId: Int): List<User> { // Keep or replace with real data
    return listOf(
        User("user1_for_trip_${tripId}", "https://randomuser.me/api/portraits/women/${tripId % 10}.jpg", "Alice T."),
        User("user2_for_trip_${tripId}", "https://randomuser.me/api/portraits/men/${tripId % 10}.jpg", "Bob M.")
    )
}

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@SuppressLint("MissingPermission")
@Composable
fun TripCatalogScreen(
    modifier: Modifier = Modifier,
    onTripClick: (Int) -> Unit,
    onNavigateToAddPlace: () -> Unit,
    mapViewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State for controlling AddPlaceScreen visibility
    var showAddScreen by remember { mutableStateOf(false) }

    // If showing add screen, display it instead of catalog
    if (showAddScreen) {
        AddPlaceScreen(
            onBack = { showAddScreen = false },
//            onSave = { newPlace ->
//                // Handle saving logic here
//                showAddScreen = false
//            }
        )
        return
    }

    // ViewModel states
    val currentAllTrips = mapViewModel.allTrips // Directly use the observable List<Trip>
    val filteredTrips by mapViewModel.filteredTrips // State<List<Trip>>
    val mapRegion by mapViewModel.mapRegion // State<MapRegion>
    val selectedTrip by mapViewModel.selectedTrip // State<Trip?>
    val isMapView by mapViewModel.isMapView // State<Boolean>
    val searchQueryFromViewModel by mapViewModel.searchQuery // State<String>
    val userLocationAddress by mapViewModel.userLocationAddress // State<String?>

    var searchFieldValue by remember(searchQueryFromViewModel) { // Sync with VM
        mutableStateOf(TextFieldValue(searchQueryFromViewModel))
    }
    var isLoading by remember { mutableStateOf(false) } // For list pagination
    var currentPage by remember { mutableIntStateOf(1) }
    val pageSize = 5
    var showAdvancedFilters by remember { mutableStateOf(false) }

    val locationUtils = remember { LocationUtils(context) }

    // These calculations are now based on `currentAllTrips` which is mapViewModel.allTrips
    // The ViewModel methods getPopularDestinations() and getTripsByRegion() use their internal _allTrips
    val popularDestinations = remember(currentAllTrips) {
        mapViewModel.getPopularDestinations() // Uses VM's internal list
            .take(5)
            .filter { it.first.isNotBlank() && it.second > 0 }
    }
    val regions = remember(currentAllTrips) {
        mapViewModel.getTripsByRegion().keys.toList().sorted() // Uses VM's internal list
    }

    var showRegionDropdown by remember { mutableStateOf(false) }
    var selectedRegionFilter by remember(searchQueryFromViewModel) { // Sync with ViewModel's query
        mutableStateOf(
            if (searchQueryFromViewModel.startsWith("region:")) {
                searchQueryFromViewModel.substringAfter("region:")
            } else {
                "All Regions"
            }
        )
    }

    val allSearchableLocations = remember(currentAllTrips) {
        val cities = currentAllTrips.map { "${it.city}, ${it.country}" }.distinct()
        val tripRegions = currentAllTrips.mapNotNull { it.region }.distinct()
        (cities + tripRegions).sorted()
    }

    val filteredSuggestions = remember(searchFieldValue.text, allSearchableLocations) {
        if (searchFieldValue.text.isEmpty()) {
            emptyList()
        } else {
            allSearchableLocations.filter {
                it.contains(searchFieldValue.text, ignoreCase = true)
            }.take(5)
        }
    }

    val displayedTripsForList by remember(filteredTrips, currentPage) {
        derivedStateOf {
            filteredTrips.take(currentPage * pageSize)
        }
    }

    LaunchedEffect(searchFieldValue.text) {
        delay(300) // Debounce
        if (searchFieldValue.text != searchQueryFromViewModel) {
            mapViewModel.updateSearchQuery(searchFieldValue.text)
            currentPage = 1
        }
    }

    LaunchedEffect(searchQueryFromViewModel) {
        if (searchFieldValue.text != searchQueryFromViewModel) {
            searchFieldValue = TextFieldValue(searchQueryFromViewModel)
        }
    }

    LocationPermissionHandler(
        onPermissionGranted = {
            coroutineScope.launch {
                val location: android.location.Location? = locationUtils.getCurrentLocation() // Ensure this returns Location?
                mapViewModel.updateUserLocation(location)
            }
        },
        onPermissionDenied = { /* Handle denial if necessary */ }
    ) { requestPermissionLambda, hasPermission ->

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(

                    onClick = { onNavigateToAddPlace() },
                    modifier = Modifier.padding(16.dp),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add place",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Softer elevation
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        LocationSearchBar(
                            searchQuery = searchFieldValue,
                            onSearchQueryChange = { searchFieldValue = it },
                            suggestions = filteredSuggestions,
                            onSuggestionClick = { suggestion ->
                                searchFieldValue = TextFieldValue(suggestion)
                                mapViewModel.updateSearchQuery(suggestion)
                                currentPage = 1
                                showAdvancedFilters = false
                            },
                            onClearClick = {
                                searchFieldValue = TextFieldValue("")
                                mapViewModel.resetFilters()
                                currentPage = 1
                                showAdvancedFilters = false
                            },
                            placeholder = "Search destinations, cities..."
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            Arrangement.spacedBy(8.dp),
                            Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { mapViewModel.toggleMapView() },
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(if (isMapView) Icons.Default.List else Icons.Default.Place, "", Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(if (isMapView) "List View" else "Map View", fontSize = 13.sp)
                            }
                            OutlinedButton(
                                onClick = {
                                    if (hasPermission) {
                                        coroutineScope.launch {
                                            val location = locationUtils.getCurrentLocation()
                                            mapViewModel.updateUserLocation(location)
                                            location?.let { androidLocation ->
                                                mapViewModel.moveMapToRegion(androidLocation.latitude, androidLocation.longitude, 12f)
                                                val nearMeUserQuery = "Trips near you"
                                                val nearMeInternalQuery = "near_me" // For VM logic
                                                searchFieldValue = TextFieldValue(nearMeUserQuery) // Update UI
                                                mapViewModel.updateSearchQuery(nearMeInternalQuery) // Trigger VM filter
                                                currentPage = 1
                                                showAdvancedFilters = false
                                            }
                                        }
                                    } else { requestPermissionLambda() }
                                },
                                Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Place, "My Location", Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Near Me", fontSize = 13.sp)
                            }
                        }
                        Row(
                            Modifier.fillMaxWidth().padding(top = 8.dp),
                            Arrangement.SpaceBetween, Alignment.CenterVertically
                        ) {
                            Text(
                                "${filteredTrips.size} trips found",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(if (userLocationAddress != null) 0.6f else 1f) // Adjust weight
                            )
                            userLocationAddress?.let { address ->
                                Text(
                                    "Current Area: $address",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.weight(0.4f), // Adjust weight
                                    maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Row(
                            Modifier.fillMaxWidth().padding(top = 8.dp),
                            Arrangement.SpaceBetween, Alignment.CenterVertically
                        ) {
                            Text("Filter Options", style = MaterialTheme.typography.titleSmall)
                            IconButton({ showAdvancedFilters = !showAdvancedFilters }) {
                                Icon(if (showAdvancedFilters) Icons.Default.ExpandLess else Icons.Default.ExpandMore, "Toggle Filters")
                            }
                        }
                        AnimatedVisibility(
                            showAdvancedFilters,
                            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                        ) {
                            Column(Modifier.fillMaxWidth().padding(top = 4.dp)) {
                                if (popularDestinations.isNotEmpty()) {
                                    Column(Modifier.padding(top = 8.dp)) {
                                        Text("Popular Destinations", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 4.dp))
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp), // Correct named parameter
                                            // contentPadding = PaddingValues(horizontal = 0.dp), // Optional, if you had it
                                            modifier = Modifier.fillMaxWidth() // Correct named parameter
                                        ) {
                                            items(popularDestinations) { (dest, count) ->
                                                AssistChip(
                                                    onClick = {
                                                        searchFieldValue = TextFieldValue(dest)
                                                        mapViewModel.updateSearchQuery(dest)
                                                        currentPage = 1
                                                        showAdvancedFilters = false
                                                    },
                                                    label = { Text("$dest ($count)") },
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                if (regions.isNotEmpty()) {
                                    Column(Modifier.padding(top = 12.dp)) { // Increased top padding
                                        Text("Filter by Region:", style = MaterialTheme.typography.labelMedium)
                                        Spacer(Modifier.height(4.dp))
                                        ExposedDropdownMenuBox(showRegionDropdown, { showRegionDropdown = !it }, Modifier.fillMaxWidth()) {
                                            OutlinedTextField(
                                                value = selectedRegionFilter,
                                                onValueChange = {}, readOnly = true,
                                                label = { Text("Region") },
                                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRegionDropdown) },
                                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            ExposedDropdownMenu(showRegionDropdown, { showRegionDropdown = false }) {
                                                DropdownMenuItem(
                                                    text = { Text("All Regions") },
                                                    onClick = {
                                                        selectedRegionFilter = "All Regions"
                                                        mapViewModel.resetFilters()
                                                        searchFieldValue = TextFieldValue("") // Clear search field
                                                        currentPage = 1; showRegionDropdown = false; showAdvancedFilters = false
                                                    }
                                                )
                                                regions.forEach { region ->
                                                    DropdownMenuItem(
                                                        text = { Text(region) },
                                                        onClick = {
                                                            val regionQuery = "region:$region"
                                                            selectedRegionFilter = region
                                                            searchFieldValue = TextFieldValue(regionQuery) // Update field
                                                            mapViewModel.updateSearchQuery(regionQuery)   // Update VM
                                                            currentPage = 1; showRegionDropdown = false; showAdvancedFilters = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Box(Modifier.fillMaxWidth().weight(1f)) { // Main content area for Map or List
                    if (isMapView) {
                        Card(
                            Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp), // Consistent padding
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            MapView(
                                trips = filteredTrips, // Pass the filtered list
                                mapRegion = mapRegion,
                                selectedTrip = selectedTrip,
                                onTripMarkerClick = { mapViewModel.selectTrip(it) },
                                onMapBoundsChange = { mapViewModel.updateMapRegion(it) },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        LazyColumn(
                            Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 72.dp) // Space for FAB
                        ) {
                            if (displayedTripsForList.isEmpty() && !isLoading && searchQueryFromViewModel.isNotEmpty()) {
                                item {
                                    Card(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                                        Column(
                                            Modifier.padding(24.dp).fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(Icons.Filled.Place, contentDescription = "No trips found icon", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.surfaceVariant)
                                            Spacer(Modifier.height(16.dp))
                                            Text("No trips match your search", style = MaterialTheme.typography.titleMedium)
                                            Text(
                                                "Try a different destination or adjust your filters.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(top=4.dp)
                                            )
                                            Spacer(Modifier.height(16.dp))
                                            Button({
                                                searchFieldValue = TextFieldValue("")
                                                mapViewModel.resetFilters()
                                                currentPage = 1
                                            }) {
                                                Text("Clear Search & Filters")
                                            }
                                        }
                                    }
                                }
                            } else if (displayedTripsForList.isEmpty() && !isLoading && searchQueryFromViewModel.isEmpty()) {
                                item {
                                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Filled.Place, contentDescription = "No trips available icon", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.surfaceVariant)
                                            Spacer(Modifier.height(16.dp))
                                            Text("No trips available yet.", style = MaterialTheme.typography.titleMedium)
                                            Text(
                                                "New destinations are added regularly!",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            } else {
                                itemsIndexed(displayedTripsForList) { index, trip ->
                                    TripCard(
                                        trip = trip,
                                        onClick = { onTripClick(trip.id) },
                                        miniProfileContent = { MiniProfileTruncated(users = mockUsersForTrip(trip.id)) }
                                    )
                                    // Pagination logic
                                    if (index == displayedTripsForList.lastIndex && !isLoading && displayedTripsForList.size < filteredTrips.size) {
                                        LaunchedEffect(index, filteredTrips.size) { // Keyed for safety
                                            isLoading = true; delay(1000); currentPage += 1; isLoading = false
                                        }
                                    }
                                }
                            }

                            if (isLoading) {
                                item {
                                    Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }

                    // Selected Trip Info Card (Overlay for Map View) - Appears at the bottom
                    androidx.compose.animation.AnimatedVisibility(
                        visible = selectedTrip != null && isMapView,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(), // Slide up from half height
                        exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut() // Slide down to half height
                    ) {
                        selectedTrip?.let { trip -> // Safe call, though visibility condition already checks for null
                            Card(
                                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), // Standard elevation
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(trip.title, style = MaterialTheme.typography.titleLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "${trip.city}, ${trip.country}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.height(8.dp))

                                    Spacer(Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { onTripClick(trip.id) },
                                            modifier = Modifier.weight(1f).height(48.dp)
                                        ) { Text("View Details") }
                                        OutlinedButton(
                                            onClick = { mapViewModel.selectTrip(null) }, // Deselect trip
                                            modifier = Modifier.weight(1f).height(48.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                                        ) { Text("Close") }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}