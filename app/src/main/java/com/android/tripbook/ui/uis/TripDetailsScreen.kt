package com.android.tripbook.ui.uis

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.model.Trip
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.ItineraryType
import com.android.tripbook.model.Location
import com.android.tripbook.ui.components.*
import com.android.tripbook.ui.theme.TripBookColors
import com.android.tripbook.viewmodel.TripDetailsViewModel
import com.android.tripbook.viewmodel.MapViewMode
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.google.android.gms.maps.model.BitmapDescriptorFactory

@Composable
fun TripDetailsScreen(
    trip: Trip,
    onBackClick: () -> Unit,
    onEditItineraryClick: () -> Unit
) {
    val viewModel: TripDetailsViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Initialize with the provided trip//
    LaunchedEffect(trip.id) {
        if (trip.id.isNotEmpty()) {
            viewModel.loadTripDetails(trip.id)
        }
    }

    // Use the trip from viewModel if available, otherwise use the provided trip
    val currentTrip = uiState.trip ?: trip

    TripBookGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp)
        ) {
            // Header with back button
            TripBookHeader(
                title = currentTrip.name,
                subtitle = "${currentTrip.startDate.format(DateTimeFormatter.ofPattern("MMM d"))} - ${currentTrip.endDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
                onBackClick = onBackClick
            )

            // Content card
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Enhanced Tabs
                    EnhancedTabRow(
                        selectedTab = uiState.selectedTab,
                        onTabSelected = viewModel::selectTab
                    )

                    HorizontalDivider(
                        color = TripBookColors.Divider,
                        thickness = 1.dp,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    // Tab content
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        when (uiState.selectedTab) {
                            "Overview" -> EnhancedOverviewTab(
                                trip = currentTrip,
                                statistics = viewModel.getTripStatistics()
                            )
                            "Itinerary" -> EnhancedItineraryTab(
                                trip = currentTrip,
                                uiState = uiState,
                                viewModel = viewModel,
                                onEditItineraryClick = onEditItineraryClick
                            )
                            "Map" -> EnhancedMapTab(
                                trip = currentTrip,
                                uiState = uiState,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Activity Dialog
    if (uiState.showAddActivityDialog) {
        uiState.selectedDate?.let { selectedDate ->
            AddActivityDialog(
                tripId = currentTrip.id,
                selectedDate = selectedDate,
                onDismiss = viewModel::hideAddActivityDialog,
                onActivityAdded = viewModel::addItineraryItem
            )
        }
    }

    // Error handling
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error message (you could use a Snackbar here)
            viewModel.clearError()
        }
    }
}

@Composable
fun EnhancedTabRow(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("Overview", "Itinerary", "Map").forEach { tab ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = tab,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selectedTab == tab) TripBookColors.Primary else TripBookColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (selectedTab == tab) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(3.dp)
                            .background(
                                TripBookColors.Primary,
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedOverviewTab(
    trip: Trip,
    statistics: com.android.tripbook.viewmodel.TripStatistics,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TripStatisticsCard(statistics = statistics)
        }

        item {
            TripSummaryCard(trip = trip)
        }

        item {
            TravelersCard(trip = trip)
        }
    }
}

@Composable
fun EnhancedItineraryTab(
    trip: Trip,
    uiState: com.android.tripbook.viewmodel.TripDetailsUiState,
    viewModel: TripDetailsViewModel,
    onEditItineraryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header with edit button and view toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.toggleMapViewMode() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.mapViewMode == MapViewMode.MAP)
                        TripBookColors.Primary else Color.White,
                    contentColor = if (uiState.mapViewMode == MapViewMode.MAP)
                        Color.White else TripBookColors.Primary
                ),
                border = if (uiState.mapViewMode == MapViewMode.LIST)
                    BorderStroke(1.dp, TripBookColors.Primary) else null,
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = if (uiState.mapViewMode == MapViewMode.MAP)
                        Icons.Default.List else Icons.Default.Map,
                    contentDescription = "Toggle View",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (uiState.mapViewMode == MapViewMode.MAP) "List View" else "Map View",
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = onEditItineraryClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TripBookColors.Primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Edit Itinerary",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        when (uiState.mapViewMode) {
            MapViewMode.LIST -> {
                // Date selector
                DateSelector(
                    dates = viewModel.getTripDates(),
                    selectedDate = uiState.selectedDate,
                    onDateSelected = viewModel::selectDate,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Day timeline view
                uiState.selectedDate?.let { selectedDate ->
                    val activitiesForDate = viewModel.getItineraryByDate(selectedDate)
                    DayTimelineView(
                        date = selectedDate,
                        activities = activitiesForDate,
                        onActivityClick = { /* Handle activity click */ },
                        onAddActivityClick = viewModel::showAddActivityDialog
                    )
                }
            }
            MapViewMode.MAP -> {
                EnhancedMapTab(trip = trip, uiState = uiState, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun EnhancedMapTab(
    trip: Trip,
    uiState: com.android.tripbook.viewmodel.TripDetailsUiState,
    viewModel: TripDetailsViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Date filter for map
        if (trip.itinerary.isNotEmpty()) {
            DateSelector(
                dates = viewModel.getTripDates(),
                selectedDate = uiState.selectedDate,
                onDateSelected = viewModel::selectDate,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Map view
        TripMapView(
            trip = trip,
            selectedDate = uiState.selectedDate,
            showRoutes = true,
            mapType = MapType.NORMAL,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
        )

        // Legend
        if (trip.itinerary.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            MapLegend()
        }
    }
}

@Composable
fun TripSummaryCard(
    trip: Trip,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                color = TripBookColors.TextPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val totalDays = java.time.temporal.ChronoUnit.DAYS.between(trip.startDate, trip.endDate).toInt() + 1
            DetailItem(icon = "📅", text = "$totalDays days")
            DetailItem(icon = "📍", text = trip.destination)
            DetailItem(icon = "👥", text = "${trip.travelers} travelers")
            DetailItem(icon = "💰", text = "FCFA ${trip.budget} budget")
            DetailItem(icon = "📝", text = trip.category.name.lowercase().replaceFirstChar { it.uppercase() })
            if (trip.description.isNotEmpty()) {
                DetailItem(icon = "📋", text = trip.description)
            }
        }
    }
}

@Composable
fun TravelersCard(
    trip: Trip,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                color = TripBookColors.TextPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (trip.companions.isEmpty()) {
                Text(
                    text = "Solo trip",
                    fontSize = 14.sp,
                    color = TripBookColors.TextSecondary
                )
            } else {
                trip.companions.forEachIndexed { index, companion ->
                    TravelerItem(
                        initials = companion.name.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString(""),
                        name = companion.name,
                        color = when (index % 3) {
                            0 -> TripBookColors.Primary
                            1 -> Color(0xFF00CC66)
                            else -> Color(0xFFE91E63)
                        }
                    )
                    if (index < trip.companions.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

// Rest of the file remains unchanged
@Composable
private fun MapTab(trip: Trip) {
    var showRoutes by remember { mutableStateOf(true) }
    var mapType by remember { mutableStateOf(MapType.NORMAL) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Map controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Route toggle
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                ;
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

            // Map type selector
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

        // Map view
        TripMapView(
            trip = trip,
            showRoutes = showRoutes,
            mapType = mapType,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
        )

        // Legend
        if (trip.itinerary.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            MapLegend()
        }
    }
}

@Composable
private fun TripMapView(
    trip: Trip,
    selectedDate: LocalDate? = null,
    showRoutes: Boolean = true,
    mapType: MapType = MapType.NORMAL,
    modifier: Modifier = Modifier
) {
    // Calculate map center based on trip data
    val mapCenter = remember(trip) {
        trip.destinationCoordinates?.let {
            LatLng(it.latitude, it.longitude)
        } ?: trip.itinerary.firstOrNull()?.coordinates?.let {
            LatLng(it.latitude, it.longitude)
        } ?: LatLng(3.848, 11.502) // Default to Yaoundé, Cameroon
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapCenter, 12f)
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
        // Add destination marker if available
        trip.destinationCoordinates?.let { destination ->
            Marker(
                state = MarkerState(
                    position = LatLng(destination.latitude, destination.longitude)
                ),
                title = trip.destination,
                snippet = "Main Destination",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )
        }

        // Add markers for itinerary items (filtered by selected date if provided)
        val itemsToShow = if (selectedDate != null) {
            trip.itinerary.filter { it.date == selectedDate }
        } else {
            trip.itinerary
        }

        itemsToShow.forEachIndexed { index, item ->
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

        // Add route polylines if enabled and route data exists
        if (showRoutes) {
            trip.itinerary.forEachIndexed { index, item ->
                item.routeToNext?.let { route ->
                    if (route.polyline.isNotEmpty()) {
                        if (index < trip.itinerary.size - 1) {
                            val currentCoords = item.coordinates
                            val nextCoords = trip.itinerary[index + 1].coordinates

                            if (currentCoords != null && nextCoords != null) {
                                Polyline(
                                    points = listOf(
                                        LatLng(currentCoords.latitude, currentCoords.longitude),
                                        LatLng(nextCoords.latitude, nextCoords.longitude)
                                    ),
                                    color = Color(0xFF667EEA),
                                    width = 5f
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
                    color = Color(0xFF00CC66),
                    label = "Hotels",
                    modifier = Modifier.weight(1f)
                )
                LegendItem(
                    color = Color(0xFFFF9500),
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
private fun OverviewTab(trip: Trip) {
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
                        name = "John Doe (Trip Leader)",
                        color = Color(0xFF667EEA)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TravelerItem(
                        initials = "JS",
                        name = "Jane Smith",
                        color = Color(0xFF764BA2)
                    )
                }
            }
        }
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
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = item.type.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (item.type) {
                                        ItineraryType.ACTIVITY -> Color(0xFF667EEA)
                                        ItineraryType.ACCOMMODATION -> Color(0xFFE91E63)
                                        ItineraryType.TRANSPORTATION -> Color(0xFF00CC66)
                                    },
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                if (item.notes.isNotEmpty()) {
                                    Text(
                                        text = item.notes,
                                        fontSize = 14.sp,
                                        color = Color(0xFF64748B)
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
private fun TripBookGradientBackground(content: @Composable () -> Unit) {
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
        content()
    }
}