package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.Trip
import com.android.tripbook.ui.theme.TripBookTheme

class TripCatalogActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("Trip Catalog") })
                    }
                ) { innerPadding ->
                    TripCatalogScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TripCatalogScreen(modifier: Modifier = Modifier) {
    val trips = listOf(
        Trip(1, "Explore Yaounde", 250, "Yaounde", 4.5f),
        Trip(2, "Safari in Douala", 400, "Douala", 4.8f),
        Trip(3, "Beach Getaway", 150, "Garoua", 4.1f)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Available Trips", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(trips) { trip ->
                TripCard(trip)
            }
        }
    }
}

@Composable
fun TripCard(trip: Trip) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(trip.title, style = MaterialTheme.typography.titleMedium)
            Text("Location: ${trip.location}")
            Text("Price: ₦${trip.price}")
            Text("Rating: ${trip.rating}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripCatalogPreview() {
    TripBookTheme {
        TripCatalogScreen()
    }
}
