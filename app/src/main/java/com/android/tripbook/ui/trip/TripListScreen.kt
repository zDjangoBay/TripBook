package com.android.tripbook.ui.trip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.Trip
import com.android.tripbook.viewmodel.TripViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripListScreen(
    tripViewModel: TripViewModel,
    onNavigateToTripDetail: (String) -> Unit,
    onNavigateToCreateTrip: () -> Unit
) {
    val trips by tripViewModel.allTrips.observeAsState(initial = emptyList())
    val currentTrips = trips

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Trips") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateTrip) {
                Icon(Icons.Filled.Add, contentDescription = "Create Trip")
            }
        }
    ) { paddingValues ->
        if (currentTrips.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No trips yet. Create one!")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(currentTrips) { trip ->
                    TripListItem(trip = trip, onClick = { onNavigateToTripDetail(trip.id) })
                }
            }
        }
    }
}

@Composable
fun TripListItem(trip: Trip, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = trip.destination, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            val startDate = trip.startDate?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it) } ?: "N/A"
            val endDate = trip.endDate?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it) } ?: "N/A"
            Text(text = "$startDate - $endDate", style = MaterialTheme.typography.bodySmall)
        }
    }
} 