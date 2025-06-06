package com.android.tripbook.ui.trip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.Trip
import com.android.tripbook.model.TripStatus
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

    // Add sample data if no trips exist
    LaunchedEffect(currentTrips) {
        if (currentTrips.isEmpty()) {
            // Create sample trips for Cameroonian destinations
            val sampleTrip1 = Trip(
                id = "sample_trip_123",
                destination = "Kribi Beach, Littoral Region",
                startDate = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L, // 7 days from now
                endDate = System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000L, // 14 days from now
                budget = 150000 // 150,000 CFA Franc (~$250 USD)
            )
            val sampleTrip2 = Trip(
                id = "sample_trip_456", 
                destination = "Waza National Park, Far North",
                startDate = System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L, // 30 days from now
                endDate = System.currentTimeMillis() + 45 * 24 * 60 * 60 * 1000L, // 45 days from now
                budget = 200000 // 200,000 CFA Franc (~$330 USD)
            )
            val sampleTrip3 = Trip(
                id = "sample_trip_789",
                destination = "Douala Business District",
                startDate = System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000L, // 3 days from now
                endDate = System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000L, // 5 days from now
                budget = 75000 // 75,000 CFA Franc (~$125 USD)
            )
            tripViewModel.insert(sampleTrip1)
            tripViewModel.insert(sampleTrip2)
            tripViewModel.insert(sampleTrip3)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Trips") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateTrip) {
                Icon(Icons.Filled.Add, contentDescription = "Test Budget Tracker")
            }
        }
    ) { paddingValues ->
        if (currentTrips.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading sample Cameroonian destinations...")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tip: Click the + button to test Budget Tracker!")
                }
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
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val startDate = dateFormat.format(Date(trip.startDate))
            val endDate = dateFormat.format(Date(trip.endDate))
            Text(text = "$startDate - $endDate", style = MaterialTheme.typography.bodySmall)
        }
    }
} 