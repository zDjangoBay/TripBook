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
import com.android.tripbook.service.NominatimService
import com.android.tripbook.service.TravelAgencyService
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
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.CalendarToday
import com.android.tripbook.service.CalendarIntegrationService
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat
import com.android.tripbook.MainActivity

@Composable
fun TripDetailsScreen(
    trip: Trip,
    onBackClick: () -> Unit,
    onEditItineraryClick: () -> Unit
) {
    val viewModel: TripDetailsViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val scaffoldState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showCalendarPicker by remember { mutableStateOf(false) }
    var selectedCalendarId by remember { mutableStateOf<Long?>(null) }

    val activity = context as? Activity

    // Initialize with the provided trip
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
                                onEditItineraryClick = onEditItineraryClick,
                                showCalendarPicker = showCalendarPicker,
                                onShowCalendarPicker = { showCalendarPicker = it },
                                selectedCalendarId = selectedCalendarId,
                                onCalendarSelected = { selectedCalendarId = it }
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

    // Calendar picker dialog
    if (showCalendarPicker) {
        if (CalendarIntegrationService.hasCalendarPermission(context)) {
            CalendarPickerDialog(
                context = context,
                onCalendarSelected = { calendarId ->
                    selectedCalendarId = calendarId
                    val results = CalendarIntegrationService.syncTripToCalendar(
                        context,
                        currentTrip.itinerary,
                        calendarId
                    )
                    coroutineScope.launch {
                        scaffoldState.showSnackbar(
                            if (results.any { it != null }) "Trip synced to calendar"
                            else "Trip already synced"
                        )
                    }
                    showCalendarPicker = false
                },
                onDismiss = { showCalendarPicker = false }
            )
        } else {
            LaunchedEffect(Unit) {
                scaffoldState.showSnackbar("Calendar permission required")
            }
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
    showCalendarPicker: Boolean,
    onShowCalendarPicker: (Boolean) -> Unit,
    selectedCalendarId: Long?,
    onCalendarSelected: (Long) -> Unit
) {
    val scaffoldState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
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
            Button(
                onClick = {
                    if (CalendarIntegrationService.hasCalendarPermission(context)) {
                        if (selectedCalendarId != null) {
                            val results = CalendarIntegrationService.syncTripToCalendar(
                                context,
                                trip.itinerary,
                                selectedCalendarId
                            )
                            coroutineScope.launch {
                                scaffoldState.showSnackbar(
                                    if (results.any { it != null }) "Trip synced to calendar"
                                    else "Trip already synced"
                                )
                            }
                        } else {
                            onShowCalendarPicker(true)
                        }
                    } else {
                        val activity = context as? Activity
                        activity?.let {
                            ActivityCompat.requestPermissions(
                                it,
                                arrayOf(
                                    Manifest.permission.READ_CALENDAR,
                                    Manifest.permission.WRITE_CALENDAR
                                ),
                                MainActivity.CALENDAR_PERMISSION_REQUEST_CODE
                            )
                        }
                        coroutineScope.launch {
                            scaffoldState.showSnackbar("Calendar permission required")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Sync to Calendar",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sync",
                    fontSize = 14.sp
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
                // Wrap in a Box to ensure proper composable context
                Box(modifier = Modifier.fillMaxSize()) {
                    EnhancedMapTab(trip = trip, uiState = uiState, viewModel = viewModel)
                }
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
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp)),
            showRoute = true
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