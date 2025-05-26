package com.android.tripbook.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.components.TripCard
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.TextFieldValue
import com.android.tripbook.model.Trip

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
    val allTrips = remember { sampleTrips() } // Replace with real fetch
    val context = LocalContext.current

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
                TripCard(trip = trip, onClick = { onTripClick(trip.id) })

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

fun sampleTrips(): List<Trip> = listOf(
    Trip(
        id = 1,
        title = "Yaounde Capital",
        description = "Explore the capital city.",
        imageUrl = "https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48="
    ),
    Trip(
        id = 2,
        title = "Buea Mountain",
        description = "Climb the scenic Mount Cameroon.",
        imageUrl = "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k="
    ),
    Trip(
        id = 3,
        title = "Kribi",
        description = "Beach town with waterfalls.",
        imageUrl = "https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48="
    ),
    Trip(
        id = 4,
        title = "Bamenda Town",
        description = "Highland city with great culture.",
        imageUrl = "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=",
    )
)
