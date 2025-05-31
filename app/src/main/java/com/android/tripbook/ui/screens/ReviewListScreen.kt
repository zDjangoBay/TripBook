// Create this as a NEW file
package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.viewmodel.ReviewViewModel
import com.android.tripbook.model.Review
import com.android.tripbook.ui.components.ReviewCard

@Composable
fun ReviewListScreen(
    tripId: Int,
    reviewViewModel: ReviewViewModel = viewModel()
) {
    // ... (the complete ReviewListScreen code from the artifact)
    val reviews by reviewViewModel.reviews.collectAsState()
    val isLoading by reviewViewModel.isLoading.collectAsState()

    val tripReviews = remember(reviews, tripId) {
        reviews.filter { it.tripId == tripId }
    }

    val averageRating = remember(tripReviews) {
        if (tripReviews.isNotEmpty()) {
            tripReviews.map { it.rating }.average().toFloat()
        } else 0f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Summary Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Reviews (${tripReviews.size})",
                    style = MaterialTheme.typography.headlineSmall
                )
                if (tripReviews.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < averageRating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = null,
                                tint = if (index < averageRating) Color(0xFFFFD700) else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = String.format("%.1f", averageRating),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reviews List
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (tripReviews.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No reviews yet. Be the first to review!",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            tripReviews.forEach { review ->
                ReviewCard(review = review)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
}

@Composable
fun ReviewCard(review: Review) {
    // ... (the complete ReviewCard code from the artifact)
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = review.userName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = review.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < review.rating) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        tint = if (index < review.rating) Color(0xFFFFD700) else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}}