// ui/screens/TripCatalogScreen.kt
package com.android.tripbook.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.ui.components.*
import com.android.tripbook.viewmodel.MapViewModel
import com.android.tripbook.utils.LocationUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Enhanced Trip Catalog Screen with Maps Integration
 * Features:
 * - Toggle between Map and List view
 * - Location-based search
 * - Interactive map with trip markers
 * - Search suggestions
 * - Current location support
 * - Popular Destinations quick filters
 * - Filter by Region dropdown
 */
@OptIn(ExperimentalMaterial3Api::class) // For ExposedDropdownMenuBox
@SuppressLint("MissingPermission")
@Composable
fun TripCatalogScreen(
    modifier: Modifier = Modifier,
    onTripClick: (Int) -> Unit,
    mapViewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // ViewModel states (Correctly observed)
    val allTrips by mapViewModel.allTrips // The full list of all trips
    val filteredTrips by mapViewModel.filteredTrips // The list filtered by current search/map interactions
    val mapRegion by mapViewModel.mapRegion
    val selectedTrip by mapViewModel.selectedTrip
    val isMapView by mapViewModel.isMapView
    val searchQuery by mapViewModel.searchQuery // The search query from the ViewModel

    // UI states (local to this composable)
    // searchFieldValue controls the TextField, keeps it in sync with ViewModel's searchQuery
    var searchFieldValue by remember { mutableStateOf(TextFieldValue(searchQuery)) } // Initialize with ViewModel's query

    var isLoading by remember { mutableStateOf(false) }
    var currentPage by remember { mutableIntStateOf(1) } // For manual pagination in list view
    val pageSize = 5 // Still used for pagination

    // Location utilities
    val locationUtils = remember { LocationUtils(context) }

    // --- Data for Popular Destinations & Regions (Correct) ---
    val popularDestinations = remember(allTrips) {
        mapViewModel.getPopularDestinations()
            .take(5)
            .filter { it.first.isNotBlank() && it.second > 0 }
    }
    val regions = remember(allTrips) {
        mapViewModel.getTripsByRegion().keys.toList().sorted()
    }

    var showRegionDropdown by remember { mutableStateOf(false) }
    var selectedRegionFilter by remember(searchQuery) { // Reset selected region when search query changes
        mutableStateOf(if (mapViewModel.searchQuery.value.startsWith("region:")) {
            mapViewModel.searchQuery.value.substringAfter("region:")
        } else {
            "All Regions"
        })
    }
    // --- END Data ---

    // Search suggestions based on available destinations (Corrected)
    // This now correctly takes from allTrips, which should be the full dataset
    val allSearchableLocations = remember(allTrips) {
        val cities = allTrips.map { "${it.city}, ${it.country}" }.distinct()
        val tripRegions = allTrips.mapNotNull { it.region }.distinct()
        (cities + tripRegions).sorted()
    }

    // Filter suggestions based on current search field value (Corrected)
    val filteredSuggestions = remember(searchFieldValue.text, allSearchableLocations) {
        if (searchFieldValue.text.isEmpty()) {
            emptyList()
        } else {
            allSearchableLocations.filter {
                it.contains(searchFieldValue.text, ignoreCase = true)
            }.take(5)
        }
    }

    // Paginated trips for list view (Corrected to use filteredTrips from ViewModel)
    // This is the list that LazyColumn will display
    val displayedTripsForList by remember(filteredTrips, currentPage) {
        derivedStateOf {
            filteredTrips.take(currentPage * pageSize)
        }
    }

    // Update ViewModel's search query from local TextField changes
    LaunchedEffect(searchFieldValue.text) {
        delay(300) // Debounce search input
        // Only update if the text field's value is truly different from what ViewModel already holds
        // This prevents infinite loops if ViewModel updates searchQuery and then this LaunchedEffect reacts.
        if (searchFieldValue.text != searchQuery) {
            mapViewModel.updateSearchQuery(searchFieldValue.text)
            currentPage = 1 // Reset pagination on new search
        }
    }

    // Reset UI search field when ViewModel's search query changes (e.g., from chip click)
    // This is important to ensure the text field displays the query set by chips/dropdowns
    LaunchedEffect(searchQuery) {
        if (searchFieldValue.text != searchQuery) {
            searchFieldValue = TextFieldValue(searchQuery)
        }
    }

    LocationPermissionHandler(
        onPermissionGranted = {
            // Permission granted - can use location features
        },
        onPermissionDenied = {
            // Permission denied - show message or disable location features
            // You might want a SnackBar here to inform the user
        }
    ) { requestPermission, hasPermission ->

        Column(modifier = modifier.fillMaxSize()) {

            // Top Controls Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Search Bar with Location Suggestions
                    LocationSearchBar(
                        searchQuery = searchFieldValue,
                        onSearchQueryChange = { newTextFieldValue ->
                            searchFieldValue = newTextFieldValue // Update local state
                            mapViewModel.updateSearchQuery(newTextFieldValue.text) // Update ViewModel
                            currentPage = 1 // Reset pagination
                        },
                        suggestions = filteredSuggestions, // Correctly using the derived state
                        onSuggestionClick = { suggestion ->
                            searchFieldValue = TextFieldValue(suggestion)
                            // This also triggers the LaunchedEffect to update ViewModel
                        },
                        onClearClick = {
                            searchFieldValue = TextFieldValue("")
                            mapViewModel.resetFilters()
                        },
                        placeholder = "Search destinations, cities, regions..."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Control Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // View Toggle Button
                        OutlinedButton(
                            onClick = { mapViewModel.toggleMapView() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isMapView) Icons.Default.List else Icons.Default.Place,
                                contentDescription = if (isMapView) "List View" else "Map View"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isMapView) "List View" else "Map View")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // My Location Button
                        OutlinedButton(
                            onClick = {
                                if (hasPermission) {
                                    coroutineScope.launch {
                                        val location = locationUtils.getCurrentLocation()
                                        location?.let {
                                            mapViewModel.moveMapToRegion(
                                                it.latitude,
                                                it.longitude,
                                                12f
                                            )
                                            // Optionally, clear search query to show all trips near location
                                            mapViewModel.updateSearchQuery("")
                                            // searchFieldValue will be updated by LaunchedEffect(searchQuery)
                                        }
                                    }
                                } else {
                                    requestPermission()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "My Location"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Near Me")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- Popular Destinations Row ---
                    if (popularDestinations.isNotEmpty()) {
                        Column {
                            Text(
                                text = "Popular Destinations",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 0.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(popularDestinations) { (destination, count) ->
                                    AssistChip(
                                        onClick = {
                                            mapViewModel.updateSearchQuery(destination)
                                            // searchFieldValue will be updated by LaunchedEffect(searchQuery)
                                        },
                                        label = { Text("$destination ($count)") },
                                        enabled = searchQuery != destination
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // --- Filter by Region Dropdown ---
                    if (regions.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Filter by Region:", style = MaterialTheme.typography.labelLarge)
                            Spacer(modifier = Modifier.width(8.dp))
                            ExposedDropdownMenuBox(
                                expanded = showRegionDropdown,
                                onExpandedChange = { showRegionDropdown = !showRegionDropdown },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = selectedRegionFilter,
                                    onValueChange = {}, // Read-only
                                    readOnly = true,
                                    label = { Text("Region") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRegionDropdown)
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = showRegionDropdown,
                                    onDismissRequest = { showRegionDropdown = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("All Regions") },
                                        onClick = {
                                            selectedRegionFilter = "All Regions"
                                            mapViewModel.resetFilters() // Reset any region filter
                                            showRegionDropdown = false
                                        }
                                    )
                                    regions.forEach { region ->
                                        DropdownMenuItem(
                                            text = { Text(region) },
                                            onClick = {
                                                selectedRegionFilter = region
                                                // Prepend "region:" to search query for MapViewModel to handle
                                                mapViewModel.updateSearchQuery("region:$region")
                                                showRegionDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Results Summary (moved after new filters)
                    Text(
                        text = "${filteredTrips.size} trips found",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Main Content Area
            if (isMapView) {
                // Map View
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    MapView(
                        trips = filteredTrips,
                        mapRegion = mapRegion,
                        selectedTrip = selectedTrip,
                        onTripMarkerClick = { trip ->
                            mapViewModel.selectTrip(trip)
                        },
                        onCameraIdle = { newMapRegion -> // <-- NEW CALL HERE
                            mapViewModel.updateMapRegion(newMapRegion)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Selected Trip Info Card (overlay on map)
                selectedTrip?.let { trip ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp) // Padding for the card from screen edges
                            .wrapContentHeight(), // Make card height adapt to content
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp) // Inner padding for card content
                        ) {
                            Text(
                                text = trip.title,
                                style = MaterialTheme.typography.headlineSmall,
                                // Optional: Ensure text doesn't overflow and push buttons down
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${trip.city}, ${trip.country}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = trip.price,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(12.dp)) // Space before buttons

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp), // Consistent spacing between buttons
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { onTripClick(trip.id) },
                                    modifier = Modifier
                                        .weight(1f) // Buttons take equal weight
                                        .height(48.dp) // Fixed height to ensure they are visible
                                ) {
                                    Text(
                                        text = "View Details",
                                        fontSize = 12.sp, // <-- Explicitly set a smaller font size
                                        fontWeight = FontWeight.Medium, // Makes text a bit bolder
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onPrimary // Ensure text color contrasts with button background
                                    )
                                }

                                // Removed the Spacer here because Arrangement.spacedBy handles it

                                OutlinedButton(
                                    onClick = { mapViewModel.selectTrip(null) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp), // Fixed height
                                    colors = ButtonDefaults.outlinedButtonColors( // Ensure OutlinedButton colors are defined
                                        contentColor = MaterialTheme.colorScheme.primary // Text and icon color
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary) // Ensure border is visible
                                ) {
                                    Text(
                                        text = "Close",
                                        fontSize = 12.sp, // <-- Explicitly set a smaller font size
                                        fontWeight = FontWeight.Medium, // Makes text a bit bolder
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.primary // Ensure text color contrasts with background
                                    )
                                }
                            }
                        }
                    }
                }

            } else {
                // List View
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    itemsIndexed(displayedTripsForList) { index, trip -> // <-- Use displayedTripsForList here
                        TripCard(
                            trip = trip,
                            onClick = { onTripClick(trip.id) }
                        )

                        // Pagination trigger
                        if (index == displayedTripsForList.lastIndex && // <-- Use displayedTripsForList here
                            !isLoading &&
                            displayedTripsForList.size < filteredTrips.size) { // <-- Use displayedTripsForList here
                            LaunchedEffect(index) {
                                isLoading = true
                                delay(1000) // Simulate network delay
                                currentPage += 1
                                isLoading = false
                            }
                        }
                    }

                    // Loading indicator
                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    // No results message
                    if (filteredTrips.isEmpty() && searchFieldValue.text.isNotEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "No trips found",
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Text(
                                        text = "Try searching for a different destination",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            searchFieldValue = TextFieldValue("")
                                            mapViewModel.resetFilters()
                                        }
                                    ) {
                                        Text("Clear Search")
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