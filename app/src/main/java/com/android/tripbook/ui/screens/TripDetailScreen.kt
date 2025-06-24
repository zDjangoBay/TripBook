package com.android.tripbook.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.tripbook.viewmodel.MockReviewViewModel
import com.android.tripbook.viewmodel.MockTripViewModel
import com.android.tripbook.ui.components.ImageGallery
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
    val tripViewModel = remember { MockTripViewModel() }
    val trip = remember { tripViewModel.getTripById(tripId) }
    val reviewViewModel = remember { MockReviewViewModel() }
    val allReviews by reviewViewModel.reviews.collectAsState()
    val reviewsForTrip = allReviews.filter { it.tripId == tripId }

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
    ) { padding ->
        if (trip == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Trip not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                ImageGallery(
                    images = trip.imageUrl,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),

                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = trip.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = { onBookTrip(tripId) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text("Book Now")
                        }
                    }
                }

                // ⭐ Insert Star Rating Row here
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    repeat(trip.rating.coerceIn(0, 5)) {
                        Text(
                            text = "⭐",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    repeat(3 - trip.rating.coerceIn(0, 35)) {
                        Text(
                            text = "⭐",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }

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
                            ReviewCard(
                                review = review,
                                onClick = {
                                    navController.navigate("detailReview/${review.id}/$tripId")
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }


                    }
                }

                // 🔽 Review Input Section
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Add Your Review",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                var name by remember { mutableStateOf("") }
                var comment by remember { mutableStateOf("") }
                var userRating by remember { mutableStateOf(0) }

// ⭐ Interactive Star Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    repeat(5) { index ->
                        Text(
                            text = if (index < userRating) "⭐" else "☆",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (index < userRating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .clickable { userRating = index + 1 }
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Your Comment") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    maxLines = 4,
                    singleLine = false
                )

                Button(
                    onClick = {
                        // 🔽 Handle Review Submission Logic
                        // e.g., call reviewViewModel.addReview(...)
                    },
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .align(Alignment.End),
                    enabled = name.isNotBlank() && comment.isNotBlank() && userRating > 0
                ) {
                    Text("Submit Review")
                }

            }
        }
    }
}
