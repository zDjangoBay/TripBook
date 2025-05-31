// ui/screens/TripCatalogScreen.kt
package com.android.tripbook.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
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
 */
@SuppressLint("MissingPermission")
@Composable
fun TripCatalogScreen(
    modifier: Modifier = Modifier,
    onTripClick: (Int) -> Unit,
    mapViewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // ViewModel states
    val allTrips by mapViewModel.allTrips
    val filteredTrips by mapViewModel.filteredTrips
    val mapRegion by mapViewModel.mapRegion
    val selectedTrip by mapViewModel.selectedTrip
    val isMapView by mapViewModel.isMapView
    val searchQuery by mapViewModel.searchQuery

    // UI states
    var searchFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(1) }
    val pageSize = 5

    // Location utilities
    val locationUtils = remember { LocationUtils(context) }

    // Search suggestions based on available destinations
    val searchSuggestions = remember(allTrips) {
        val cities = allTrips.map { "${it.city}, ${it.country}" }.distinct()
        val regions = allTrips.mapNotNull { it.region }.distinct()
        (cities + regions).sorted()
    }

    // Filter suggestions based on current search
    val filteredSuggestions = remember(searchFieldValue.text, searchSuggestions) {
        if (searchFieldValue.text.isEmpty()) {
            emptyList()
        } else {
            searchSuggestions.filter {
                it.contains(searchFieldValue.text, ignoreCase = true)
            }.take(5)
        }
    }

    // Paginated trips for list view
    val displayedTrips = remember(filteredTrips, currentPage) {
        filteredTrips.take(currentPage * pageSize)
    }

    // Update search query when text field changes
    LaunchedEffect(searchFieldValue.text) {
        delay(300) // Debounce search
        mapViewModel.updateSearchQuery(searchFieldValue.text)
        currentPage = 1
    }

    LocationPermissionHandler(
        onPermissionGranted = {
            // Permission granted - can use location features
        },
        onPermissionDenied = {
            // Permission denied - show message or disable location features
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
                        onSearchQueryChange = { searchFieldValue = it },
                        suggestions = filteredSuggestions,
                        onSuggestionClick = { suggestion ->
                            searchFieldValue = TextFieldValue(suggestion)
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

                    // Results Summary
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    MapView(
                        trips = filteredTrips,
                        mapRegion = mapRegion,
                        selectedTrip = selectedTrip,
                        onTripMarkerClick = { trip ->
                            mapViewModel.selectTrip(trip)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Selected Trip Info Card (overlay on map)
                selectedTrip?.let { trip ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = trip.title,
                                style = MaterialTheme.typography.headlineSmall
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

                            Spacer(modifier = Modifier.height(8.dp))

                            Row {
                                Button(
                                    onClick = { onTripClick(trip.id) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("View Details")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                OutlinedButton(
                                    onClick = { mapViewModel.selectTrip(null) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Close")
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
                    itemsIndexed(displayedTrips) { index, trip ->
                        TripCard(
                            trip = trip,
                            onClick = { onTripClick(trip.id) }
                        )

                        // Pagination trigger
                        if (index == displayedTrips.lastIndex &&
                            !isLoading &&
                            displayedTrips.size < filteredTrips.size) {
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