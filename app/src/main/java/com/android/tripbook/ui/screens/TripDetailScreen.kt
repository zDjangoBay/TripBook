package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import android.app.Application
// ✅ MIGRATION COMPLETE: Using Room Database for Persistent Storage
import com.android.tripbook.ViewModel.RoomReviewViewModel
import com.android.tripbook.ViewModel.RoomTripViewModel
import com.android.tripbook.ViewModel.TripBookViewModelFactory
import com.android.tripbook.ui.components.ImageCarousel
import com.android.tripbook.ui.components.ReviewCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    tripId: Int,
    onBack: () -> Unit,
    navController: NavHostController,
    onSeeAllReviews: (Int) -> Unit,
    onBookTrip: (Int) -> Unit = {}
) {
    // ✅ MIGRATION COMPLETE: Room Database for Persistent Storage
    val application = LocalContext.current.applicationContext as Application
    val factory = remember { TripBookViewModelFactory(application) }

    val tripViewModel: RoomTripViewModel = viewModel(factory = factory)
    val reviewViewModel: RoomReviewViewModel = viewModel(factory = factory)

    val allTrips by tripViewModel.trips.collectAsState()
    val trip = allTrips.find { it.id == tripId }
    val allReviews by reviewViewModel.reviews.collectAsState()
    val reviewsForTrip = allReviews.filter { it.tripId == tripId }

    if (trip == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Trip Not Found") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Sorry, this trip does not exist.")
            }
        }
        return
    }

    var reviewText by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip Details") },
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
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            ImageCarousel(
                images = trip.imageUrl,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = trip.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reviews",
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextButton(onClick = { onSeeAllReviews(tripId) }) {
                        Text("See All")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (reviewsForTrip.isEmpty()) {
                    Text(
                        text = "No reviews yet. Be the first to review!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    reviewsForTrip.take(3).forEach { review ->
                        ReviewCard(review = review)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.LightGray, thickness = 1.dp)

            Column(modifier = Modifier.padding(16.dp)) {
                Text("Add Your Review", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Your Comment") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    maxLines = 6,
                    singleLine = false,
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { /* Simulate image picker */ },
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                    contentPadding = PaddingValues(vertical = 14.dp, horizontal = 24.dp)
                ) {
                    Text("Upload Images")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { /* Submit Review */ },
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                        contentPadding = PaddingValues(vertical = 14.dp, horizontal = 24.dp)
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}
