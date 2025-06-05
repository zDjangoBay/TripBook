package com.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tripbook.models.Review
import com.tripbook.ui.components.ReviewCard
import com.tripbook.viewmodel.ReviewViewModel

@Composable
fun ReviewsScreen(
    tripId: String,
    viewModel: ReviewViewModel,
    onAddReview: () -> Unit,
    onBackPressed: () -> Unit
) {
    val reviews by viewModel.reviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.loadReviewsForTrip(tripId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip Reviews") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onAddReview) {
                        Icon(Icons.Default.Add, "Add Review")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = error ?: "An error occurred",
                            color = MaterialTheme.colors.error
                        )
                        Button(
                            onClick = { viewModel.loadReviewsForTrip(tripId) },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
                reviews.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No reviews yet")
                        Button(
                            onClick = onAddReview,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Be the first to review")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(reviews) { review ->
                            ReviewCard(
                                review = review,
                                onLikeClick = { viewModel.likeReview(review.id) },
                                onHelpfulClick = { viewModel.markReviewAsHelpful(review.id) }
                            )
                        }
                    }
                }
            }
        }
    }
} 