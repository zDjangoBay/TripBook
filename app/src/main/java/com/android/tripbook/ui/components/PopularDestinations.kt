package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.Trip

/**
 * Displays a horizontal list of top 5 most popular trip destinations.
 *
 * Features:
 * - Shows location name and trip count for each destination
 * - Clickable cards that navigate to location-specific trips
 * - Responsive design with lazy loading for optimal performance
 * - Automatically sorts and filters top destinations from trip data
 *
 * @param trips List of all trips to analyze for popularity
 * @param onLocationClick Callback when a destination card is clicked
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopularDestinations(
    trips: List<Trip>,
    modifier: Modifier = Modifier,
    onLocationClick: (String) -> Unit
) {
    // Group trips by location and count
    val popularDestinations by remember(trips) {
        derivedStateOf {
            trips.groupBy { it.title } 
                .mapValues { it.value.size }
                .toList()
                .sortedByDescending { it.second }
                .take(5)
        }
    }

    Column(modifier = modifier) {
        Text(
            text = "Popular Destinations",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(popularDestinations) { (location, count) ->
                Card(
                    onClick = { onLocationClick(location) },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.width(180.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = location,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$count ${if (count == 1) "trip" else "trips"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}