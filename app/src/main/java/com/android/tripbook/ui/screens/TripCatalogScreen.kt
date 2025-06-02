package com.android.tripbook.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.components.TripCard
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.TextFieldValue
import com.android.tripbook.data.SampleTrips
import com.android.tripbook.model.User
import com.android.tripbook.ui.components.MiniProfileTruncated

/**
 * Main Trip Catalog Screen combining:
 * - Search bar (sticky)
 * - Scrollable, paginated trip list
 */
@SuppressLint("MissingPermission")
@Composable
fun TripCatalogScreen(
    modifier: Modifier = Modifier,
    onTripClick: (Int) -> Unit
) {
    val allTrips = remember { SampleTrips.get() } // Replace with real fetch

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val pageSize = 5
    var currentPage by remember { mutableStateOf(1) }

    // Filtered and paginated list of trips
    val displayedTrips = remember(searchQuery.text, currentPage) {
        allTrips
            .filter { trip ->
                trip.title.contains(searchQuery.text, ignoreCase = true)
                        || trip.description.contains(searchQuery.text, ignoreCase = true)
            }
            .take(currentPage * pageSize)
    }

    var isLoading by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        // Search Bar (sticky header)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                currentPage = 1
            },
            label = { Text("Search by location") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )

        // Results Section
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            itemsIndexed(displayedTrips) { index, trip ->
                // For demo: mock users for each trip (replace with real data)
                val users = listOf(
                    User("alice", "https://randomuser.me/api/portraits/women/1.jpg", "Alice"),
                    User("bob", "https://randomuser.me/api/portraits/men/2.jpg", "Bob"),
                    User("carol", "https://randomuser.me/api/portraits/women/3.jpg", "Carol"),
                    User("dan", "https://randomuser.me/api/portraits/men/4.jpg", "Dan"),
                    User("eve", "https://randomuser.me/api/portraits/women/5.jpg", "Eve")
                )
                TripCard(
                    trip = trip,
                    onClick = { onTripClick(trip.id) },
                    miniProfileContent = { MiniProfileTruncated(users = users) }
                )

                // Trigger pagination when reaching the end
                if (index == displayedTrips.lastIndex && !isLoading && displayedTrips.size < allTrips.size) {
                    LaunchedEffect(index) {
                        isLoading = true
                        delay(1000) // simulate network
                        currentPage += 1
                        isLoading = false
                    }
                }
            }

            if (isLoading) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}
