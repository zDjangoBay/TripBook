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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.model.User
import com.android.tripbook.ui.components.*
import com.android.tripbook.ui.components.MiniProfileTruncated
import com.android.tripbook.utils.LocationUtils
import com.android.tripbook.viewmodel.MapViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun mockUsersForTrip(tripId: Int): List<User> {
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

    // to refresh data on screen resume
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                mapViewModel.refreshTrips()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var showAddScreen by remember { mutableStateOf(false) }


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

    //  my ViewModel states
    val currentAllTrips = mapViewModel.allTrips
    val filteredTrips by mapViewModel.filteredTrips
    val mapRegion by mapViewModel.mapRegion
    val selectedTrip by mapViewModel.selectedTrip
    val isMapView by mapViewModel.isMapView
    val userLocationAddress by mapViewModel.userLocationAddress


    var searchFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    var isLoading by remember { mutableStateOf(false) }
    var currentPage by remember { mutableIntStateOf(1) }
    val pageSize = 5
    var showAdvancedFilters by remember { mutableStateOf(false) }

    val locationUtils = remember { LocationUtils(context) }

    val popularDestinations = remember(currentAllTrips) {
        mapViewModel.getPopularDestinations()
            .take(5)
            .filter { it.first.isNotBlank() && it.second > 0 }
    }
    val regions = remember(currentAllTrips) {
        mapViewModel.getTripsByRegion().keys.toList().sorted()
    }

    var showRegionDropdown by remember { mutableStateOf(false) }
    // This state is now also locally controlled
    var selectedRegionFilter by remember { mutableStateOf("All Regions") }


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

        if (searchFieldValue.text != mapViewModel.searchQuery.value) {
            delay(300) // Debounce
        }

        mapViewModel.updateSearchQuery(searchFieldValue.text)
        currentPage = 1
    }


    LocationPermissionHandler(
        onPermissionGranted = {
            coroutineScope.launch {
                val location: android.location.Location? = locationUtils.getCurrentLocation()
                mapViewModel.updateUserLocation(location)
            }
        },
        onPermissionDenied = {  }
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        LocationSearchBar(
                            searchQuery = searchFieldValue,
                            onSearchQueryChange = { searchFieldValue = it },
                            suggestions = filteredSuggestions,
                            onSuggestionClick = { suggestion ->

                                searchFieldValue = TextFieldValue(suggestion)
                                showAdvancedFilters = false
                            },
                            onClearClick = {
                                searchFieldValue = TextFieldValue("")
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

                                                searchFieldValue = TextFieldValue("near_me")
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
                                modifier = Modifier.weight(if (userLocationAddress != null) 0.6f else 1f)
                            )
                            userLocationAddress?.let { address ->
                                Text(
                                    "Current Area: $address",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.weight(0.4f),
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
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(popularDestinations) { (dest, count) ->
                                                AssistChip(
                                                    onClick = {

                                                        searchFieldValue = TextFieldValue(dest)
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
                                    Column(Modifier.padding(top = 12.dp)) {
                                        Text("Filter by Region:", style = MaterialTheme.typography.labelMedium)
                                        Spacer(Modifier.height(4.dp))
                                        // FIX 1: The onExpandedChange lambda is the proper way to control the dropdown's visibility.
                                        ExposedDropdownMenuBox(
                                            expanded = showRegionDropdown,
                                            onExpandedChange = { showRegionDropdown = !showRegionDropdown },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            // FIX 2: Added a modifier to the TextField to make it clickable and open the menu.
                                            OutlinedTextField(
                                                value = selectedRegionFilter,
                                                onValueChange = {}, // Stays empty as the dropdown items handle changes
                                                readOnly = true,
                                                label = { Text("Region") },
                                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRegionDropdown) },
                                                // The menuAnchor modifier is essential for positioning the dropdown.
                                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            ExposedDropdownMenu(
                                                expanded = showRegionDropdown,
                                                // This is called when the user clicks outside the menu or presses back
                                                onDismissRequest = { showRegionDropdown = false }
                                            ) {
                                                DropdownMenuItem(
                                                    text = { Text("All Regions") },
                                                    onClick = {
                                                        selectedRegionFilter = "All Regions" // Update the text field's display text
                                                        searchFieldValue = TextFieldValue("") // Clear the actual search query
                                                        showRegionDropdown = false
                                                        showAdvancedFilters = false
                                                    }
                                                )
                                                regions.forEach { region ->
                                                    DropdownMenuItem(
                                                        text = { Text(region) },
                                                        onClick = {
                                                            selectedRegionFilter = region // Update the text field's display text
                                                            searchFieldValue = TextFieldValue("region:$region") // Set the actual search query
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


                Box(Modifier.fillMaxWidth().weight(1f)) {
                    if (isMapView) {
                        Card(
                            Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            MapView(
                                trips = filteredTrips,
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
                            contentPadding = PaddingValues(top = 8.dp, bottom = 72.dp)
                        ) {
                            if (displayedTripsForList.isEmpty() && !isLoading && mapViewModel.searchQuery.value.isNotEmpty()) {
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
                            } else if (displayedTripsForList.isEmpty() && !isLoading && mapViewModel.searchQuery.value.isEmpty()) {
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
                                        LaunchedEffect(index, filteredTrips.size) {
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

                    androidx.compose.animation.AnimatedVisibility(
                        visible = selectedTrip != null && isMapView,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
                    ) {
                        selectedTrip?.let { trip ->
                            Card(
                                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
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
                                            onClick = { mapViewModel.selectTrip(null) },
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