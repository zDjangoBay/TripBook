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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryEditor(
    tripId: String?,
    onNavigateBack: () -> Unit
) {
    var showMapView by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Itinerary") },
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
            FloatingActionButton(onClick = { /* Add activity */ }) {
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
                days = sampleDays,
                selectedDay = selectedDay,
                onDaySelected = { selectedDay = it }
            )
            
            if (showMapView) {
                MapViewPlaceholder()
            } else {
                ActivityTimeline(
                    activities = sampleActivities,
                    onActivityClick = { /* Handle activity click */ }
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
    activities: List<Activity>,
    onActivityClick: (Activity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(activities) { activity ->
            ActivityCard(
                activity = activity,
                onClick = { onActivityClick(activity) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityCard(
    activity: Activity,
    onClick: () -> Unit
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
            Column {
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
            IconButton(onClick = { /* Handle drag */ }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Reorder")
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

data class Activity(
    val id: String,
    val time: String,
    val title: String,
    val location: String
)

// Sample data for preview
val sampleDays = listOf("Day 1", "Day 2", "Day 3", "Day 4", "Day 5")

val sampleActivities = listOf(
    Activity(
        id = "1",
        time = "09:00 AM",
        title = "Breakfast at Cafe Central",
        location = "1st District"
    ),
    Activity(
        id = "2",
        time = "11:00 AM",
        title = "Visit Schönbrunn Palace",
        location = "Schönbrunn"
    ),
    Activity(
        id = "3",
        time = "02:00 PM",
        title = "Lunch at Naschmarkt",
        location = "6th District"
    )
)