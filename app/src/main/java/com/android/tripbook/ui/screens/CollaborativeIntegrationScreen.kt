package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.data.SampleData
import com.android.tripbook.ui.components.IntegratedTripCard

@Composable
fun CollaborativeIntegrationScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "TripBook Catalog",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Trips with embedded organizer profiles",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Trip Catalog using your integrated component
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleTripsWithIntegration()) { tripData ->
                IntegratedTripCard(
                    tripTitle = tripData.title,
                    tripDescription = tripData.description,
                    tripDestination = tripData.destination,
                    organizer = tripData.organizer
                )
            }
        }
    }
}

// Sample trip data for integration demonstration
private fun sampleTripsWithIntegration() = listOf(
    TripData(
        title = "Mount Cameroon Adventure",
        description = "Conquer West Africa's highest peak! Experience volcanic landscapes and breathtaking sunrise views from 4,095 meters above sea level.",
        destination = "Mount Cameroon, Southwest Region",
        organizer = SampleData.sampleUsers[1] // Paul Essomba
    ),
    TripData(
        title = "Waza National Park Safari",
        description = "Discover Cameroon's premier wildlife destination. Spot elephants, lions, giraffes, and over 300 bird species in their natural habitat.",
        destination = "Waza National Park, Far North",
        organizer = SampleData.sampleUsers[2] // Fatima Abba
    ),
    TripData(
        title = "Kribi Beach Paradise",
        description = "Relax on Cameroon's most beautiful coastline! Visit the famous Lobé Waterfalls flowing directly into the ocean.",
        destination = "Kribi, South Region",
        organizer = SampleData.sampleUsers[3] // Jean Baptiste
    ),
    TripData(
        title = "Yaoundé Cultural Heritage Tour",
        description = "Explore Cameroon's vibrant capital city! Visit museums, markets, and experience authentic Cameroonian cuisine.",
        destination = "Yaoundé, Centre Region",
        organizer = SampleData.sampleUsers[0] // Marie Nkomo
    )
)

private data class TripData(
    val title: String,
    val description: String,
    val destination: String,
    val organizer: com.android.tripbook.data.User
)