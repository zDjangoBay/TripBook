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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
<<<<<<< HEAD
import androidx.lifecycle.viewmodel.compose.viewModel
import android.util.Log
=======
import androidx.navigation.NavHostController
import com.android.tripbook.viewmodel.MockReviewViewModel
>>>>>>> d90952d8704d10f2091d5b680619905aba83bd29
import com.android.tripbook.viewmodel.MockTripViewModel
import com.android.tripbook.viewmodel.ReviewViewModel
import com.android.tripbook.ui.components.ImageGallery
import com.android.tripbook.ui.components.ReviewCard
import com.android.tripbook.ui.components.StarRatingInput
import com.android.tripbook.ui.components.StarRatingDisplay
import com.android.tripbook.ui.components.RatingSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    tripId: Int,
    onBack: () -> Unit,
    navController: NavHostController,
    onSeeAllReviews: (Int) -> Unit,
    reviewViewModel: ReviewViewModel = viewModel(),
    onBookTrip: (Int) -> Unit = {}
) {
    val tripViewModel = remember { MockTripViewModel() }
    val trip = remember { tripViewModel.getTripById(tripId) }
    val allReviews by reviewViewModel.reviews.collectAsState()
    val reviewsForTrip = remember(allReviews, tripId) {
        allReviews.filter { it.tripId == tripId }
    }

    // Add LaunchedEffect here for logging
    LaunchedEffect(reviewsForTrip) {
        Log.d("TripDetail", "Reviews for trip $tripId: ${reviewsForTrip.size}")
        reviewsForTrip.forEach { review ->
            Log.d("TripDetail", "Review: ${review.userName} - ${review.comment} - Rating: ${review.rating}")
        }
    }

    // Calculate rating statistics
    val averageRating = remember(reviewsForTrip) {
        if (reviewsForTrip.isNotEmpty()) {
            reviewsForTrip.map { it.rating.toDouble() }.average().toFloat()
        } else 0f
    }

    val ratingBreakdown = remember(reviewsForTrip) {
        (1..5).associateWith { ratingValue ->
            reviewsForTrip.count { review -> review.rating == ratingValue.toFloat() }
        }
    }

    // Review form state
    var reviewText by remember { mutableStateOf(TextFieldValue("")) }
    var userRating by remember { mutableFloatStateOf(0f) }
    var showRatingError by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }

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
                Text(
                    text = "Sorry, this trip does not exist.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        return
    }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Image Gallery
            ImageGallery(
                images = trip.imageUrl,
                modifier = Modifier.fillMaxWidth()
            )

            // Title and Book Button Row
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

            // Rating Display
            if (reviewsForTrip.isNotEmpty()) {
                Row(
<<<<<<< HEAD
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StarRatingDisplay(
                        rating = averageRating,
                        starSize = 24,
                        showRatingText = true
                    )
                    Text(
                        text = "(${reviewsForTrip.size} reviews)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Description
            Text(
                text = trip.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.LightGray, thickness = 1.dp)

            // Rating Summary Section
            if (reviewsForTrip.isNotEmpty()) {
                RatingSummary(
                    averageRating = averageRating,
                    totalReviews = reviewsForTrip.size,
                    ratingBreakdown = ratingBreakdown
                )
            }

            // Reviews Section
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
=======
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),

>>>>>>> d90952d8704d10f2091d5b680619905aba83bd29
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "User Reviews",
                        style = MaterialTheme.typography.titleLarge
                    )
                    if (reviewsForTrip.isNotEmpty()) {
                        TextButton(onClick = { onSeeAllReviews(tripId) }) {
                            Text(
                                text = "See All",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (reviewsForTrip.isEmpty()) {
                    Text(
                        text = "No reviews yet for this trip.",
                        color = Color.Gray
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(reviewsForTrip.take(5)) { review ->
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

<<<<<<< HEAD
            // Add Review Form
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Add Your Review",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                // User Name Input
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Your Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Rating Input Section
                Text(
                    text = "Rate this trip",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                StarRatingInput(
                    rating = userRating.toInt(),
                    onRatingChanged = {
                        userRating = it.toFloat()
                        showRatingError = false
                    },
                    starSize = 40
                )

                if (showRatingError) {
                    Text(
                        text = "Please select a rating",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Review Text Input
                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Your Comment") },
                    placeholder = { Text("Share your experience...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    maxLines = 6,
                    singleLine = false,
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* Simulate image picker */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add Photos")
                    }

                    Button(
                        onClick = {
                            if (userRating == 0f) {
                                showRatingError = true
                            } else if (userName.isNotBlank() && reviewText.text.isNotBlank()) {
                                Log.d("TripDetail", "Submitting review for trip $tripId")
                                // Submit review
                                reviewViewModel.addReview(
                                    tripId = tripId,
                                    userName = userName.trim(),
                                    comment = reviewText.text.trim(),
                                    rating = userRating
                                )
                                Log.d("TripDetail", "Review submitted, total reviews: ${reviewViewModel.reviews.value.size}")

                                // Reset form
                                reviewText = TextFieldValue("")
                                userRating = 0f
                                userName = ""
                                showRatingError = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = reviewText.text.isNotBlank() && userName.isNotBlank()
                    ) {
                        Text("Submit Review")
=======
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


>>>>>>> d90952d8704d10f2091d5b680619905aba83bd29
                    }
                }
            }
        }
    }
}