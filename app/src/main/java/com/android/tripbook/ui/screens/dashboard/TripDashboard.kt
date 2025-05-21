package com.android.tripbook.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDashboard(
    onCreateTrip: () -> Unit,
    onOpenItinerary: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Trips") },
                actions = {
                    IconButton(onClick = { /* Implement search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search trips")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateTrip) {
                Icon(Icons.Default.Add, contentDescription = "Create new trip")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sampleTrips) { trip ->
                    TripCard(
                        trip = trip,
                        onClick = { onOpenItinerary(trip.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search trips...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripCard(
    trip: Trip,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = trip.destination,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${trip.startDate} - ${trip.endDate}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = trip.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = trip.completionStatus,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

data class Trip(
    val id: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val description: String,
    val completionStatus: Float
)

// Sample data for preview
val sampleTrips = listOf(
    Trip(
        id = "1",
        destination = "Paris, France",
        startDate = "May 1, 2024",
        endDate = "May 7, 2024",
        description = "A week-long exploration of the City of Light",
        completionStatus = 0.8f
    ),
    Trip(
        id = "2",
        destination = "Tokyo, Japan",
        startDate = "June 15, 2024",
        endDate = "June 25, 2024",
        description = "Discovering Japanese culture and cuisine",
        completionStatus = 0.3f
    )
)