package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.tripbook.ViewModel.MainViewModel
import com.android.tripbook.ui.components.TripItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val upcomingTrips by mainViewModel.upcomingTrips.observeAsState(emptyList())
    val isLoading by mainViewModel.isLoadingUpcoming.observeAsState(true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Trip Schedule",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(upcomingTrips) { trip ->
                    TripItem(trip = trip)
                }
            }
        }
    }
}
