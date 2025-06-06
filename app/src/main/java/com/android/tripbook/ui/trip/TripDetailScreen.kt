package com.android.tripbook.ui.trip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.viewmodel.TripViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    tripViewModel: TripViewModel,
    tripId: String,
    onNavigateToBudget: (String) -> Unit
) {
    val trip by tripViewModel.getTripById(tripId).observeAsState()
    val currentTrip = trip

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentTrip?.destination ?: "Trip Detail") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToBudget(tripId) }) {
                Icon(Icons.Filled.Add, contentDescription = "Go to Budget")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (currentTrip == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Start Date: ${currentTrip.startDate?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it) }}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("End Date: ${currentTrip.endDate?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it) }}")
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
} 