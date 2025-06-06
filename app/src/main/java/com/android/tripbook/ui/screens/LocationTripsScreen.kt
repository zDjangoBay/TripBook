package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.Trip
import com.android.tripbook.ui.components.TripCard

/**
 * Displays a list of trips filtered by location(title) in a scrollable list.
 *
 * Features:
 * - Top app bar with back navigation and location title
 * - Shows empty state message if no trips found
 * - Scrollable list of TripCards for the selected location
 * - Handles trip selection via onTripClick callback
 *
 * @param location The location name to filter trips by
 * @param trips List of trips to display (filtered by location)
 * @param onBack Callback when back button is pressed
 * @param onTripClick Callback when a trip card is clicked (passes trip ID)
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationTripsScreen(
    location: String,
    trips: List<Trip>,
    onBack: () -> Unit,
    onTripClick: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Trips in $location") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (trips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No trips found for this location")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(trips) { trip ->
                    TripCard(
                        trip = trip,
                        onClick = { onTripClick(trip.id) },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}