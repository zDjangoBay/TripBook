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
import com.android.tripbook.data.User
import com.android.tripbook.ui.components.MiniProfileTruncated

import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState

/**
 * Main Trip Catalog Screen combining:
 * - Search bar (sticky)
 * - Scrollable, paginated trip list
 * -Add a place button
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun TripCatalogScreen(
    modifier: Modifier = Modifier,
    onTripClick: (Int) -> Unit,
    onAddClick: () -> Unit = {}
) {
    // State for controlling AddPlaceScreen visibility
    var showAddScreen by remember { mutableStateOf(false) }

    // If showing add screen, display it instead of catalog
    if (showAddScreen) {
        AddPlaceScreen(
            onBack = { showAddScreen = false },
//            onSave = { newPlace ->
//                // Handle saving logic here
//                showAddScreen = false
//            }
        )
        return
    }

    val allTrips = remember { SampleTrips.get() } // Replace with real fetch

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val pageSize = 5
    var currentPage by remember { mutableStateOf(1) }

    // press detects Add place
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

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

    Box(modifier = modifier.fillMaxSize()) {
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
                        User(
                            "1",
                            "Alice",
                            "Paris",
                            "https://randomuser.me/api/portraits/women/1.jpg"
                        ),
                        User("2", "Bob", "London", "https://randomuser.me/api/portraits/men/2.jpg"),
                        User(
                            "3",
                            "Carol",
                            "Tokyo",
                            "https://randomuser.me/api/portraits/women/3.jpg"
                        ),
                        User("4", "Dan", "Berlin", "https://randomuser.me/api/portraits/men/4.jpg"),
                        User("5", "Eve", "Rome", "https://randomuser.me/api/portraits/women/5.jpg")
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
        FloatingActionButton(
            onClick = {
                onAddClick()
                showAddScreen = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .graphicsLayer {
                    scaleX = if (isPressed) 0.9f else 1f
                    scaleY = if (isPressed) 0.9f else 1f
                },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add place",
                tint = Color.White
            )
        }
    }
}
