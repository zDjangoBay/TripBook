package com.android.tripbook.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.util.Log
import com.android.tripbook.model.Review
import com.android.tripbook.ui.model.ReviewSortState
import com.android.tripbook.ui.model.SortCriterion
import com.android.tripbook.ui.model.SortOrder
import com.android.tripbook.ui.model.displayName
import com.android.tripbook.ui.components.ReviewCard
import com.android.tripbook.viewmodel.ReviewViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllReviewsScreen(
    tripId: Int,
    onBack: () -> Unit,
    reviewViewModel: ReviewViewModel = viewModel()
) {
    val reviews by reviewViewModel.reviews.collectAsState()
    var sortState by remember { mutableStateOf(ReviewSortState()) }
    var showSortOptions by remember { mutableStateOf(false) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") } // Adjust as needed

    val tripReviews = remember(reviews, tripId, sortState) {
        val filteredReviews = reviews.filter { it.tripId == tripId }
        when (sortState.criterion) {
            SortCriterion.DATE -> {
                val sorted = filteredReviews.sortedWith(compareBy { review ->
                    try {
                        LocalDate.parse(review.date, dateFormatter)
                    } catch (e: DateTimeParseException) {
                        // Handle malformed dates
                        if (sortState.order == SortOrder.ASCENDING) LocalDate.MAX else LocalDate.MIN
                    }
                })
                if (sortState.order == SortOrder.DESCENDING) sorted.reversed() else sorted
            }
            SortCriterion.RATING -> {
                if (sortState.order == SortOrder.DESCENDING) {
                    filteredReviews.sortedByDescending { it.rating }
                } else {
                    filteredReviews.sortedBy { it.rating }
                }
            }
        }
    }

    // Add LaunchedEffect here for logging
    LaunchedEffect(reviews, tripId, tripReviews) {
        Log.d("AllReviews", "All reviews count: ${reviews.size}")
        Log.d("AllReviews", "Reviews for trip $tripId: ${tripReviews.size}")
        Log.d("AllReviews", "Current sort: ${sortState.criterion} ${sortState.order}")
        tripReviews.take(5).forEach { review -> // Log first 5 sorted reviews
            Log.d("AllReviews", "Sorted Review: ${review.userName} - Date: ${review.date} - Rating: ${review.rating}")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Sort options UI
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Sort by: ${sortState.criterion.displayName()}",
                    style = MaterialTheme.typography.titleSmall
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { showSortOptions = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort options")
                    }
                    DropdownMenu(
                        expanded = showSortOptions,
                        onDismissRequest = { showSortOptions = false }
                    ) {
                        SortCriterion.values().forEach { criterion ->
                            DropdownMenuItem(
                                text = { Text(criterion.displayName()) },
                                onClick = {
                                    sortState = sortState.copy(criterion = criterion)
                                    showSortOptions = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = {
                        sortState = sortState.copy(
                            order = if (sortState.order == SortOrder.ASCENDING) SortOrder.DESCENDING else SortOrder.ASCENDING
                        )
                    }) {
                        Icon(
                            imageVector = if (sortState.order == SortOrder.ASCENDING) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = if (sortState.order == SortOrder.ASCENDING) "Ascending" else "Descending"
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp), // Add padding for top and bottom
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (tripReviews.isEmpty()) {
                    item {
                        Text("No reviews to display.", modifier = Modifier.padding(top = 16.dp, start = 8.dp))
                    }
                } else {
                    items(tripReviews, key = { review -> review.id }) { review -> // Assuming Review has a unique 'id'
                        ReviewCard(review = review)
                    }
                }
            }
        }
    }
}
            if (tripReviews.isEmpty()) {
                item {
                    Text("No reviews to display.", modifier = Modifier.padding(8.dp))
                }
            } else {
                items(tripReviews) { review ->
                    ReviewCard(review = review)
                }
            }
        }
    }
}