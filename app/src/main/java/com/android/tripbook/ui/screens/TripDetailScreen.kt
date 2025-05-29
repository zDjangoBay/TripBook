package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.android.tripbook.viewModel.MockReviewViewModel
import com.android.tripbook.viewModel.MockTripViewModel
import com.android.tripbook.ui.components.ImageCarousel
import com.android.tripbook.ui.components.ReviewCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(tripId: Int, onBack: () -> Unit, onSeeAllReviews: (Int) -> Unit) {
    val tripViewModel = remember {MockTripViewModel()}
    val trip = remember { tripViewModel.getTripById(tripId) }
    val reviewViewModel = remember { MockReviewViewModel() }
    val allReviews by reviewViewModel.reviews.collectAsState()
    val reviewsForTrip = allReviews.filter { it.tripId == tripId }

    if (trip == null) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    ImageCarousel(images = trip.imageUrl, modifier = Modifier.fillMaxSize())
                }
            }

            item {
                Text(trip.title, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(trip.description, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.LightGray, thickness = 1.dp)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("User Reviews", style = MaterialTheme.typography.titleLarge)
                    TextButton(onClick = { onSeeAllReviews(tripId) }) {
                        Text("See All", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (reviewsForTrip.isEmpty()) {
                    Text("No reviews yet for this trip.", color = Color.Gray)
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(reviewsForTrip.take(5)) { review -> // limit to preview size
                            ReviewCard(
                                review = review,
                                modifier = Modifier
                                    .width(280.dp)
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.LightGray, thickness = 1.dp)
            }


            item {
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
