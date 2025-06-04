package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.util.Log
import com.android.tripbook.ui.components.ReviewCard
import com.android.tripbook.viewmodel.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllReviewsScreen(
    tripId: Int,
    onBack: () -> Unit,
    reviewViewModel: ReviewViewModel = viewModel(),
    onLikeClicked: (reviewId: Int) -> Unit = {},
    onFlagClicked: (reviewId: Int) -> Unit = {}
) {
    val reviews by reviewViewModel.reviews.collectAsState()
    val tripReviews = remember(reviews, tripId) {
        reviews.filter { it.tripId == tripId }
    }

    // Add LaunchedEffect here for logging
    LaunchedEffect(reviews, tripId) {
        Log.d("AllReviews", "All reviews count: ${reviews.size}")
        Log.d("AllReviews", "Reviews for trip $tripId: ${tripReviews.size}")
        reviews.forEach { review ->
            Log.d("AllReviews", "Review tripId: ${review.tripId}, current tripId: $tripId")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Reviews") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (tripReviews.isEmpty()) {
                item {
                    Text("No reviews to display.", modifier = Modifier.padding(8.dp))
                }
            } else {
                items(tripReviews) { review ->
                    ReviewCard(
                        review = review,
                        onLikeClicked = { onLikeClicked(review.id) },
                        onFlagClicked = { onFlagClicked(review.id) }
                    )
                }
            }
        }
    }
}