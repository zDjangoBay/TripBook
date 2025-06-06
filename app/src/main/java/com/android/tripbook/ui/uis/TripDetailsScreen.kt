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
import com.android.tripbook.model.ReviewType
import com.android.tripbook.viewmodel.ReviewViewModel
import com.android.tripbook.ui.components.* // Import all components, including EditActivityDialog
import com.android.tripbook.ui.theme.TripBookColors
import com.android.tripbook.viewmodel.TripDetailsViewModel
import com.android.tripbook.viewmodel.MapViewMode
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import androidx.compose.ui.res.painterResource
import com.android.tripbook.R

@Composable
fun TripDetailsScreen(
    trip: Trip,
    onBackClick: () -> Unit,
    onEditItineraryClick: () -> Unit, // This was originally for the main "Edit Itinerary" button
) {
    val viewModel: TripDetailsViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    // State for showing the Edit Activity Dialog
    var showEditActivityDialog by remember { mutableStateOf(false) }
    var activityToEdit by remember { mutableStateOf<ItineraryItem?>(null) }

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
                subtitle = "${currentTrip.startDate.format(DateTimeFormatter.ofPattern("MMM d"))} - ${currentTrip.endDate.format(DateTimeFormatter.ofPattern("MMM d,yyyy"))}",
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
                                onUpdateActivityClick = { item: ItineraryItem -> // Explicitly define type
                                    activityToEdit = item
                                    showEditActivityDialog = true
                                },
                                onDeleteActivityClick = { itemId: String -> // Explicitly define type
                                    viewModel.deleteItineraryItem(itemId)
                                }
                            )
                            "Map" -> EnhancedMapTab(
                                trip = currentTrip,
                                uiState = uiState,
                                viewModel = viewModel
                            )
                            "Weather" -> WeatherForecastTab(
                                trip = currentTrip,
                                viewModel = viewModel
                            )
                            "Reviews" -> ReviewsTab(
                                trip = currentTrip
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Activity Dialog (for adding new activities via the '+' button)
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

    // Edit Activity Dialog (for updating/deleting existing activities)
    if (showEditActivityDialog) {
        activityToEdit?.let { item ->
            EditActivityDialog(
                item = item,
                onDismiss = { showEditActivityDialog = false; activityToEdit = null },
                onSave = { updatedItem: ItineraryItem -> // Explicitly define type
                    viewModel.updateItineraryItem(updatedItem)
                    showEditActivityDialog = false
                    activityToEdit = null
                },
                onDelete = { itemId: String -> // Explicitly define type
                    viewModel.deleteItineraryItem(itemId)
                    showEditActivityDialog = false
                    activityToEdit = null
                }
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
fun WeatherForecastTab(trip: Trip, viewModel: TripDetailsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
    ) {
        // Location header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = TripBookColors.Primary,
                modifier = Modifier
                    .size(24.dp)
                    .background(TripBookColors.Primary.copy(alpha = 0.1f), CircleShape)
                    .padding(4.dp)
            )
            Text(
                text = trip.destination,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = TripBookColors.TextPrimary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        // Current weather
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "27°",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = TripBookColors.TextPrimary
                )
                Text(
                    text = "Partly Cloudy",
                    fontSize = 20.sp,
                    color = TripBookColors.TextSecondary
                )
                
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "High",
                        tint = TripBookColors.TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "28° /",
                        fontSize = 16.sp,
                        color = TripBookColors.TextSecondary
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Low",
                        tint = TripBookColors.TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = " 20°",
                        fontSize = 16.sp,
                        color = TripBookColors.TextSecondary
                    )
                }
                Text(
                    text = "Feels like 29°",
                    fontSize = 14.sp,
                    color = TripBookColors.TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            // Weather icon
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = "Partly Cloudy",
                tint = TripBookColors.Primary,
                modifier = Modifier.size(80.dp)
            )
        }
        
        // Weather alert
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F0FF))
        ) {
            Text(
                text = "Showers ending early. Low 20C.",
                fontSize = 14.sp,
                color = TripBookColors.TextPrimary,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        // Hourly forecast
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F0FF))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 5 PM
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "5 PM",
                            fontSize = 14.sp,
                            color = TripBookColors.TextSecondary
                        )
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = TripBookColors.Primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "26°",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TripBookColors.TextPrimary
                        )
                        Text(
                            text = "11%",
                            fontSize = 12.sp,
                            color = Color(0xFF4FC3F7)
                        )
                    }
                    
                    // 6 PM
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "6 PM",
                            fontSize = 14.sp,
                            color = TripBookColors.TextSecondary
                        )
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = TripBookColors.Primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "25°",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TripBookColors.TextPrimary
                        )
                        Text(
                            text = "9%",
                            fontSize = 12.sp,
                            color = Color(0xFF4FC3F7)
                        )
                    }
                    
                    // 6:23 PM - Sunset
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "6:23 PM",
                            fontSize = 14.sp,
                            color = TripBookColors.TextSecondary
                        )
                        Text(
                            text = "Sunset",
                            fontSize = 12.sp,
                            color = Color(0xFFFF9800)
                        )
                        Text(
                            text = "25°",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TripBookColors.TextPrimary
                        )
                        Text(
                            text = "8%",
                            fontSize = 12.sp,
                            color = Color(0xFF4FC3F7)
                        )
                    }
                    
                    // 7 PM
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "7 PM",
                            fontSize = 14.sp,
                            color = TripBookColors.TextSecondary
                        )
                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = null,
                            tint = TripBookColors.Primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "24°",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TripBookColors.TextPrimary
                        )
                        Text(
                            text = "7%",
                            fontSize = 12.sp,
                            color = Color(0xFF4FC3F7)
                        )
                    }
                    
                    // 8 PM
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "8 PM",
                            fontSize = 14.sp,
                            color = TripBookColors.TextSecondary
                        )
                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = null,
                            tint = TripBookColors.Primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "23°",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TripBookColors.TextPrimary
                        )
                        Text(
                            text = "9%",
                            fontSize = 12.sp,
                            color = Color(0xFF4FC3F7)
                        )
                    }
                }
                
                // Temperature line
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color.White)
                )
            }
        }
        
        // Rain forecast
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F0FF))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Rain",
                        tint = Color(0xFF4FC3F7),
                        modifier = Modifier.size(20.dp)
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = "Rain Coming",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TripBookColors.TextPrimary
                        )
                        Text(
                            text = "Rain possible this evening",
                            fontSize = 14.sp,
                            color = TripBookColors.TextSecondary
                        )
                    }
                }
                Text(
                    text = "34%",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TripBookColors.TextPrimary
                )
            }
        }
        
        // Daily forecast
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F0FF))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Yesterday",
                        fontSize = 16.sp,
                        color = TripBookColors.TextPrimary
                    )
                    Text(
                        text = "28° 20°",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TripBookColors.TextPrimary
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today",
                        fontSize = 16.sp,
                        color = TripBookColors.TextPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Rain",
                            tint = Color(0xFF4FC3F7),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "11%",
                            fontSize = 14.sp,
                            color = Color(0xFF4FC3F7),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "Partly Cloudy",
                            tint = TripBookColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "28° 20°",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TripBookColors.TextPrimary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HourlyForecastItem(
    time: String,
    temp: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    precipitation: Int
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = time,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "${temp}°",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Text(
            text = "${precipitation}%",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
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
        listOf("Overview", "Itinerary", "Map", "Weather", "Reviews").forEach { tab ->
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
    onUpdateActivityClick: (ItineraryItem) -> Unit, // New parameter
    onDeleteActivityClick: (String) -> Unit,       // New parameter
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
            horizontalArrangement = Arrangement.End, // Changed to End to align toggle to right
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
                        onAddActivityClick = viewModel::showAddActivityDialog,
                        onUpdateActivityClick = onUpdateActivityClick, // Pass update callback
                        onDeleteActivityClick = onDeleteActivityClick  // Pass delete callback
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

// The following composables were not part of the `TripDetailsScreen` in previous versions,
// but were included in the original `TripDetailsScreen.kt` file provided.
// They are kept here for completeness, assuming they are indeed top-level declarations
// in the original file, but they are not directly called by the `TripDetailsScreen` logic
// as modified. If they were meant to be part of a different screen or utility,
// they should be moved accordingly.

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

// These are composables that were potentially misplaced or were intended to be
// top-level. I'm moving them to the end of the file as top-level declarations
// to resolve the "Expecting a top level declaration" error if they were
// causing it by being inside another scope.
// ItineraryTab was previously included and is now moved to the end as a top-level.
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
            // This button is removed as it was causing navigation to ItineraryBuilderScreen for editing
            // and individual item editing is handled by the dialog.
            /*
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
            */
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
fun ReviewsTab(
    trip: Trip,
    modifier: Modifier = Modifier
) {
    val reviewViewModel: ReviewViewModel = viewModel()
    val uiState by reviewViewModel.uiState.collectAsState()

    // Load reviews when the tab is opened
    LaunchedEffect(trip.id) {
        if (trip.id.isNotEmpty()) {
            reviewViewModel.loadReviewsForTarget(
                reviewType = ReviewType.TRIP,
                targetId = trip.id,
                targetName = trip.name
            )
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Review Summary Card
            ReviewSummaryCard(
                reviewSummary = uiState.reviewSummary,
                onWriteReviewClick = {
                    if (trip.status == com.android.tripbook.model.TripStatus.COMPLETED) {
                        reviewViewModel.showReviewDialog()
                    }
                },
                onRateClick = {
                    if (trip.status == com.android.tripbook.model.TripStatus.COMPLETED) {
                        reviewViewModel.showRatingDialog()
                    }
                }
            )
        }

        // Show user's existing review if any
        uiState.userReview?.let { userReview ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Your Review",
                                tint = Color(0xFF667EEA),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Your Review",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF667EEA)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        ReviewCard(
                            review = userReview,
                            onHelpfulClick = { /* User can't mark their own review helpful */ }
                        )
                    }
                }
            }
        }

        // Reviews from other users
        if (uiState.reviews.isNotEmpty()) {
            item {
                Text(
                    text = "Community Reviews",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A202C),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(uiState.reviews.filter { it.userId != uiState.userReview?.userId }) { review ->
                ReviewCard(
                    review = review,
                    onHelpfulClick = { isHelpful ->
                        reviewViewModel.markReviewHelpful(review.id, isHelpful)
                    }
                )
            }
        } else if (!uiState.isLoading && uiState.userReview == null) {
            item {
                // No reviews state
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.RateReview,
                            contentDescription = "No reviews",
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No reviews yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6B7280)
                        )
                        Text(
                            text = "Be the first to share your experience!",
                            fontSize = 14.sp,
                            color = Color(0xFF9CA3AF),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Loading state
        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF667EEA)
                    )
                }
            }
        }

        // Error state
        uiState.error?.let { error ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Error loading reviews",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFDC2626)
                            )
                        }
                        Text(
                            text = error,
                            fontSize = 14.sp,
                            color = Color(0xFF7F1D1D),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { reviewViewModel.clearError() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDC2626)
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }

    // Review submission dialog
    ReviewSubmissionDialog(
        isVisible = uiState.showReviewDialog,
        targetName = trip.name,
        onDismiss = { reviewViewModel.hideReviewDialog() },
        onSubmit = { rating, title, content, pros, cons ->
            reviewViewModel.submitReview(
                reviewType = ReviewType.TRIP,
                targetId = trip.id,
                targetName = trip.name,
                rating = rating,
                title = title,
                content = content,
                pros = pros,
                cons = cons
            )
        },
        isSubmitting = uiState.isSubmittingReview
    )

    // Rating dialog
    RatingDialog(
        isVisible = uiState.showRatingDialog,
        targetName = trip.name,
        currentRating = uiState.userRating?.rating ?: 0f,
        onDismiss = { reviewViewModel.hideRatingDialog() },
        onSubmit = { rating ->
            reviewViewModel.submitRating(
                reviewType = ReviewType.TRIP,
                targetId = trip.id,
                rating = rating
            )
        },
        isSubmitting = uiState.isSubmittingRating
    )
}
