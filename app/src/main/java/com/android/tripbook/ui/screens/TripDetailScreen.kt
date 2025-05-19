package com.android.tripbook.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.tripbook.model.Trip

// Temporary placeholder list (should be replaced by ViewModel or repository)
val sampleTrips = listOf(
    Trip(1, "Paris Getaway", "Explore the romantic city...", "https://source.unsplash.com/400x300/?paris"),
    Trip(2, "Tokyo Adventure", "Experience the vibrant culture...", "https://source.unsplash.com/400x300/?tokyo"),
    Trip(3, "Safari in Kenya", "Witness the Big Five...", "https://source.unsplash.com/400x300/?safari,kenya"),
    Trip(4, "New York City Tour", "The city that never sleeps...", "https://source.unsplash.com/400x300/?newyork")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    tripId: Int,
    onBack: () -> Unit
) {
    val trip = sampleTrips.find { it.id == tripId }

    if (trip == null) {
        Text("Trip not found", modifier = Modifier.padding(16.dp))
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(trip.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(trip.imageUrl),
                contentDescription = trip.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(MaterialTheme.shapes.medium)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = trip.description,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
