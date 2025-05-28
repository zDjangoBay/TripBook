package com.android.tripbook.ui.screens.itinerary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.tripbook.data.ActivityRepository
import com.android.tripbook.data.TripActivity
import com.android.tripbook.data.TripRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryEditor(
    tripId: String?,
    onNavigateBack: () -> Unit
) {
    // State variables
    var showMapView by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf(0) }
    var showActivityDialog by remember { mutableStateOf(false) }
    var selectedActivity by remember { mutableStateOf<TripActivity?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    
    // Get trip information
    val trip = tripId?.let { TripRepository.getTripById(it) }
    
    // Calculate number of days in trip
    val numberOfDays = remember(trip) {
        if (trip != null) {
            // In a real app, calculate days between start and end date
            // For now, use a placeholder of 5 days
            5
        } else {
            0
        }
    }
    
    // Generate day labels
    val dayLabels = remember(numberOfDays) {
        List(numberOfDays) { index -> "Day ${index + 1}" }
    }
    
    // Get activities for the selected day
    val activities = remember(tripId, selectedDay, ActivityRepository.activities) {
        tripId?.let {
            // Initialize with sample data if empty
            if (ActivityRepository.activities.isEmpty() && tripId.isNotEmpty()) {
                ActivityRepository.addSampleActivities(tripId)
            }
            ActivityRepository.getActivitiesForTripAndDay(tripId, selectedDay)
        } ?: emptyList()
    }
    
    // Activity dialog
    if (showActivityDialog) {
        ActivityDialog(
            activity = selectedActivity,
            day = selectedDay,
            tripId = tripId ?: "",
            onDismiss = {
                showActivityDialog = false
                selectedActivity = null
            },
            onSave = { time, title, location ->
                if (selectedActivity != null) {
                    // Update existing activity
                    val updatedActivity = selectedActivity!!.copy(
                        time = time,
                        title = title,
                        location = location
                    )
                    ActivityRepository.updateActivity(updatedActivity)
                } else {
                    // Add new activity
                    tripId?.let {
                        ActivityRepository.addActivity(
                            tripId = it,
                            day = selectedDay,
                            time = time,
                            title = title,
                            location = location
                        )
                    }
                }
                showActivityDialog = false
                selectedActivity = null
            }
        )
    }
    
    // Delete confirmation dialog
    if (showDeleteConfirmation && selectedActivity != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
                selectedActivity = null
            },
            title = { Text("Delete Activity") },
            text = { Text("Are you sure you want to delete this activity?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedActivity?.let {
                            ActivityRepository.deleteActivity(it.id)
                        }
                        showDeleteConfirmation = false
                        selectedActivity = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        selectedActivity = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Itinerary: ${trip?.destination ?: ""}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Navigate back")
                    }
                },
                actions = {
                    IconButton(onClick = { showMapView = !showMapView }) {
                        Icon(
                            if (showMapView) Icons.Default.List else Icons.Default.Place,
                            contentDescription = "Toggle view"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedActivity = null
                    showActivityDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add activity")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            DaySelector(
                days = dayLabels,
                selectedDay = selectedDay,
                onDaySelected = { selectedDay = it }
            )
            
            if (showMapView) {
                MapViewPlaceholder()
            } else {
                ActivityTimeline(
                    activities = activities,
                    onActivityClick = { activity ->
                        selectedActivity = activity
                        showActivityDialog = true
                    },
                    onDeleteActivity = { activity ->
                        selectedActivity = activity
                        showDeleteConfirmation = true
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaySelector(
    days: List<String>,
    selectedDay: Int,
    onDaySelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedDay,
        modifier = Modifier.fillMaxWidth()
    ) {
        days.forEachIndexed { index, day ->
            Tab(
                selected = selectedDay == index,
                onClick = { onDaySelected(index) },
                text = { Text(day) }
            )
        }
    }
}

@Composable
fun ActivityTimeline(
    activities: List<TripActivity>,
    onActivityClick: (TripActivity) -> Unit,
    onDeleteActivity: (TripActivity) -> Unit
) {
    if (activities.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No activities for this day. Add one using the + button.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(activities) { activity ->
                ActivityCard(
                    activity = activity,
                    onClick = { onActivityClick(activity) },
                    onDelete = { onDeleteActivity(activity) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityCard(
    activity: TripActivity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.time,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = activity.location,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row {
                IconButton(onClick = onClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit activity")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete activity")
                }
            }
        }
    }
}

@Composable
fun MapViewPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Text("Map View Coming Soon")
    }
}
