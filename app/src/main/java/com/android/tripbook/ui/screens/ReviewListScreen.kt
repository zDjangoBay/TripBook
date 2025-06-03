// Create this as a NEW file
package com.android.tripbook.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tripbook.viewmodel.ReviewViewModel
import com.android.tripbook.model.Review
import com.android.tripbook.ui.components.ReviewCard // Use the shared component
import com.android.tripbook.ui.model.ReviewSortState
import com.android.tripbook.ui.model.SortCriterion
import com.android.tripbook.ui.model.SortOrder
import com.android.tripbook.ui.model.displayName
import com.android.tripbook.ui.components.StarRatingDisplay // For consistent star display
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun ReviewListScreen(
    tripId: Int,
    reviewViewModel: ReviewViewModel = viewModel()
) {
    val reviews by reviewViewModel.reviews.collectAsState()
    val isLoading by reviewViewModel.isLoading.collectAsState()
    var sortState by remember { mutableStateOf(ReviewSortState()) }
    var showSortOptions by remember { mutableStateOf(false) }

    // Define your date formatter. Adjust pattern to match your date string format.
    // Example: "yyyy-MM-dd" or "yyyy-MM-dd HH:mm:ss"
    // If using LocalDateTime, parse to LocalDateTime instead of LocalDate.
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }

    val tripReviews = remember(reviews, tripId, sortState) {
        val filteredReviews = reviews.filter { it.tripId == tripId }
        when (sortState.criterion) {
            SortCriterion.DATE -> {
                // Robust date sorting
                val sorted = filteredReviews.sortedWith(compareBy { review ->
                    try {
                        LocalDate.parse(review.date, dateFormatter)
                    } catch (e: DateTimeParseException) {
                        // Handle malformed dates, e.g., treat them as oldest or log error
                        // For simplicity, placing them at the end for ascending, beginning for descending
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
                    StarRatingDisplay(
                        rating = averageRating,
                        starSize = 20, // dp
                        showRatingText = true,
                        ratingTextStyle = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp),
                        starColor = MaterialTheme.colorScheme.secondary, // Use theme color
                        emptyStarColor = MaterialTheme.colorScheme.outline // Use theme color
                        )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reviews List
        // Sort options UI
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp), // Add some padding below sort options
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

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (tripReviews.isEmpty()) {
            Text(
                text = "No reviews yet. Be the first to review!",
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp) // Add padding at the bottom of the list
            ) {
                items(tripReviews, key = { review -> review.id }) { review -> // Assuming Review has a unique 'id'
                    ReviewCard(review = review)
                }
            }
        }
 }