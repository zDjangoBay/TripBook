// ui/screens/TripCatalogScreen.kt
package com.android.tripbook.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign // <-- REQUIRED IMPORT
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
@OptIn(ExperimentalMaterial3Api::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@SuppressLint("MissingPermission")
@Composable
fun TripCatalogScreen(
    modifier: Modifier = Modifier,
    onTripClick: (Int) -> Unit,
    onAddTripClick: () -> Unit, // Parameter for Add Trip FAB
    mapViewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // ViewModel states
    val allTrips = mapViewModel.allTrips // Corrected: No 'by' keyword needed for List<Trip>
    val filteredTrips by mapViewModel.filteredTrips
    val mapRegion by mapViewModel.mapRegion
    val selectedTrip by mapViewModel.selectedTrip
    val isMapView by mapViewModel.isMapView
    val searchQuery by mapViewModel.searchQuery
    val userLocationAddress by mapViewModel.userLocationAddress

    // UI states
    var searchFieldValue by remember { mutableStateOf(TextFieldValue(searchQuery)) }
    var isLoading by remember { mutableStateOf(false) }
    var currentPage by remember { mutableIntStateOf(1) }
    val pageSize = 5
    var showAdvancedFilters by remember { mutableStateOf(false) }

    val locationUtils = remember { LocationUtils(context) }

    val popularDestinations = remember(allTrips) {
        mapViewModel.getPopularDestinations()
            .take(5)
            .filter { it.first.isNotBlank() && it.second > 0 }
    }
    val regions = remember(allTrips) {
        mapViewModel.getTripsByRegion().keys.toList().sorted()
    }

    var showRegionDropdown by remember { mutableStateOf(false) }
    var selectedRegionFilter by remember(searchQuery) {
        mutableStateOf(if (mapViewModel.searchQuery.value.startsWith("region:")) {
            mapViewModel.searchQuery.value.substringAfter("region:")
        } else {
            "All Regions"
        })
    }

    val allSearchableLocations = remember(allTrips) {
        val cities = allTrips.map { "${it.city}, ${it.country}" }.distinct()
        val tripRegions = allTrips.mapNotNull { it.region }.distinct()
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
        delay(300)
        if (searchFieldValue.text != searchQuery) {
            mapViewModel.updateSearchQuery(searchFieldValue.text)
            currentPage = 1
        }
    }

    LaunchedEffect(searchQuery) {
        if (searchFieldValue.text != searchQuery) {
            searchFieldValue = TextFieldValue(searchQuery)
        }
    }

    LocationPermissionHandler(
        onPermissionGranted = {
            coroutineScope.launch {
                val location = locationUtils.getCurrentLocation()
                mapViewModel.updateUserLocation(location)
            }
        },
        onPermissionDenied = {
            // Optional: Show a SnackBar or dialog if permission is denied
        }
    ) { requestPermission, hasPermission ->

        Scaffold( // Main Scaffold for the entire screen
            floatingActionButton = {
                FloatingActionButton(onClick = onAddTripClick) { // FAB for Add Trip
                    Icon(Icons.Default.Add, contentDescription = "Add New Trip")
                }
            },
            floatingActionButtonPosition = FabPosition.End // Position FAB
        ) { paddingValues -> // Apply Scaffold's padding to the main content
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Apply Scaffold's padding
            ) {

                // --- REFINED: Top Controls Section ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp), // Card itself has padding now
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp) // <-- Rounded corners for the card
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp) // Inner padding for card content
                    ) {
                        // Search Bar with Location Suggestions
                        LocationSearchBar(
                            searchQuery = searchFieldValue,
                            onSearchQueryChange = { newTextFieldValue ->
                                searchFieldValue = newTextFieldValue
                                mapViewModel.updateSearchQuery(newTextFieldValue.text)
                                currentPage = 1
                            },
                            suggestions = filteredSuggestions,
                            onSuggestionClick = { suggestion ->
                                searchFieldValue = TextFieldValue(suggestion)
                                showAdvancedFilters = false // Auto-collapse filters on suggestion click
                            },
                            onClearClick = {
                                searchFieldValue = TextFieldValue("")
                                mapViewModel.resetFilters()
                                showAdvancedFilters = false // Auto-collapse filters on clear
                            },
                            placeholder = "Search destinations, cities, regions..."
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Control Buttons Row (View Toggle & My Location)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { mapViewModel.toggleMapView() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp) // Rounded button corners
                            ) {
                                Icon(
                                    imageVector = if (isMapView) Icons.Default.List else Icons.Default.Place,
                                    contentDescription = if (isMapView) "List View" else "Map View",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isMapView) "List View" else "Map View", fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    if (hasPermission) {
                                        coroutineScope.launch {
                                            val location = locationUtils.getCurrentLocation()
                                            mapViewModel.updateUserLocation(location)
                                            location?.let {
                                                mapViewModel.moveMapToRegion(
                                                    it.latitude,
                                                    it.longitude,
                                                    12f
                                                )
                                                mapViewModel.updateSearchQuery("near_me")
                                                showAdvancedFilters = false
                                            } ?: run {
                                                // Optional: Show a Snackbar if location can't be fetched
                                            }
                                        }
                                    } else {
                                        requestPermission()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = "My Location",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Near Me", fontSize = 13.sp)
                            }
                        }

                        // --- Location & Results Summary Row ---
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Results Summary
                            Text(
                                text = "${filteredTrips.size} trips found",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            userLocationAddress?.let { address ->
                                Text(
                                    text = "Your location: $address",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.End, // <-- FIXED: Use TextAlign.End
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }


                        // --- Collapsible Advanced Filters Section ---
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Filter Options",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(onClick = { showAdvancedFilters = !showAdvancedFilters }) {
                                Icon(
                                    imageVector = if (showAdvancedFilters) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (showAdvancedFilters) "Hide Filters" else "Show Filters"
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = showAdvancedFilters,
                            enter = expandVertically(expandFrom = Alignment.Top),
                            exit = shrinkVertically(shrinkTowards = Alignment.Top)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Popular Destinations Row
                                if (popularDestinations.isNotEmpty()) {
                                    Column(modifier = Modifier.padding(top = 8.dp)) {
                                        Text(
                                            text = "Popular Destinations",
                                            style = MaterialTheme.typography.labelMedium,
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
                                                        showAdvancedFilters = false
                                                    },
                                                    label = { Text("$destination ($count)") },
                                                    enabled = searchQuery != destination,
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Filter by Region Dropdown
                                if (regions.isNotEmpty()) {
                                    Column(modifier = Modifier.padding(top = 8.dp)) {
                                        Text(
                                            text = "Filter by Region:",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        ExposedDropdownMenuBox(
                                            expanded = showRegionDropdown,
                                            onExpandedChange = { showRegionDropdown = !showRegionDropdown },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            OutlinedTextField(
                                                value = selectedRegionFilter,
                                                onValueChange = {},
                                                readOnly = true,
                                                label = { Text("Region") },
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRegionDropdown)
                                                },
                                                modifier = Modifier
                                                    .menuAnchor()
                                                    .fillMaxWidth(),
                                                shape = RoundedCornerShape(8.dp)
                                            )

                                            ExposedDropdownMenu(
                                                expanded = showRegionDropdown,
                                                onDismissRequest = { showRegionDropdown = false }
                                            ) {
                                                DropdownMenuItem(
                                                    text = { Text("All Regions") },
                                                    onClick = {
                                                        selectedRegionFilter = "All Regions"
                                                        mapViewModel.resetFilters()
                                                        showRegionDropdown = false
                                                        showAdvancedFilters = false
                                                    }
                                                )
                                                regions.forEach { region ->
                                                    DropdownMenuItem(
                                                        text = { Text(region) },
                                                        onClick = {
                                                            selectedRegionFilter = region
                                                            mapViewModel.updateSearchQuery("region:$region")
                                                            showRegionDropdown = false
                                                            showAdvancedFilters = false
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
                // --- END REFINED TOP CONTROLS ---

                // Main Content Area
                // Use a Box to layer the Map and the Selected Trip Info Card (overlay)
                Box( // <-- This Box provides the BoxScope for Modifier.align
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Takes remaining vertical space
                ) {
                    // Map View (base layer)
                    if (isMapView) {
                        Card(
                            modifier = Modifier
                                .fillMaxSize() // Fills the parent Box
                                .padding(horizontal = 16.dp, vertical = 8.dp), // Padding around the map card
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            MapView(
                                trips = filteredTrips,
                                mapRegion = mapRegion,
                                selectedTrip = selectedTrip,
                                onTripMarkerClick = { trip ->
                                    mapViewModel.selectTrip(trip)
                                },
                                onCameraIdle = { newMapRegion ->
                                    mapViewModel.updateMapRegion(newMapRegion)
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        // List View
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            itemsIndexed(displayedTripsForList) { index, trip ->
                                TripCard(
                                    trip = trip,
                                    onClick = { onTripClick(trip.id) }
                                )

                                if (index == displayedTripsForList.lastIndex &&
                                    !isLoading &&
                                    displayedTripsForList.size < filteredTrips.size) {
                                    LaunchedEffect(index) {
                                        isLoading = true
                                        delay(1000)
                                        currentPage += 1
                                        isLoading = false
                                    }
                                }
                            }

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

                    // Selected Trip Info Card (overlay layer, only visible if map is showing)
                    androidx.compose.animation.AnimatedVisibility( // <-- This AnimatedVisibility is NOW a direct child of the Box
                        visible = selectedTrip != null && isMapView, // <-- COMBINE CONDITIONS HERE
                        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(), // Slide up from half height
                        exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(), // Slide down to half height
                        modifier = Modifier
                            .align(Alignment.BottomCenter) // This now correctly works inside the Box
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp) // Padding around the overlay card
                    ) {
                        selectedTrip?.let { trip ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = trip.title,
                                        style = MaterialTheme.typography.headlineSmall,
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

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = { onTripClick(trip.id) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp)
                                        ) {
                                            Text(
                                                text = "View Details",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                color = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }

                                        OutlinedButton(
                                            onClick = { mapViewModel.selectTrip(null) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = MaterialTheme.colorScheme.primary
                                            ),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                                        ) {
                                            Text(
                                                text = "Close",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } // End of AnimatedVisibility
                } // End of main Box
            } // End of LocationPermissionHandler lambda
        } // End of Scaffold content Column
    } // End of Scaffold
} // End of TripCatalogScreen Composable