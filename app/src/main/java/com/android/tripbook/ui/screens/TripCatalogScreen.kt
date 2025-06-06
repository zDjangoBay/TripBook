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
import androidx.navigation.NavHostController
import com.android.tripbook.data.SampleTrips
import com.android.tripbook.ui.components.PopularDestinations

/**
 * Main Trip Catalog Screen combining:
 * - Search bar (sticky)
 * - Scrollable, paginated trip list
 */
@SuppressLint("MissingPermission")
@Composable
fun TripCatalogScreen(
    modifier: Modifier = Modifier,
    onTripClick: (Int) -> Unit,
    navController: NavHostController
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

             item {
                 PopularDestinations(
                     trips = allTrips,
                     onLocationClick = { location ->
                         navController.navigate("location/${location}")
                     },
                     modifier = Modifier.padding(top = 16.dp)
                 )
                Divider(modifier = Modifier.padding(vertical = 16.dp))
            }

            itemsIndexed(displayedTrips) { index, trip ->
                TripCard(trip = trip, onClick = { onTripClick(trip.id) },
                 modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
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
