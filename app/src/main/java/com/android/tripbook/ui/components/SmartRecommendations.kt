package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.Trip

/**
 * Displays personalized trip recommendations based on user preferences.
 *
 * Features:
 * - Filters trips matching simulated user preferences (beach/luxury/family)
 * - Shows horizontal scroll of up to 5 recommended trips
 * - Uses full-width TripCards for better visibility
 * - Only displays when recommendations are available
 *
 * @param trips List of all available trips
 * @param onTripClick Callback when a recommended trip is selected
 */

@Composable
fun SmartRecommendations(
    trips: List<Trip>,
    modifier: Modifier = Modifier,
    onTripClick: (Int) -> Unit
) {
    // Simulate user preferences based on:
    // - Previously viewed trips
    // - Bookmarked trips
    // - Demographic info
    val simulatedPreferences = remember {
        setOf("beach", "luxury", "family") // Simulated user preferences to be replaced with real preferences
    }

    val recommendedTrips by remember(trips, simulatedPreferences) {
        derivedStateOf {
            trips.filter { trip ->
                trip.tags.any { tag -> simulatedPreferences.contains(tag) }
            }.shuffled().take(5) // Take 5 random matches
        }
    }

    if (recommendedTrips.isNotEmpty()) {
        Column(modifier = modifier) {
            Text(
                text = "Recommended For You",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(recommendedTrips) { trip ->
                    TripCard(
                        trip = trip,
                        onClick = { onTripClick(trip.id) },
                        modifier = Modifier.width(280.dp)
                    )
                }
            }
        }
    }
}