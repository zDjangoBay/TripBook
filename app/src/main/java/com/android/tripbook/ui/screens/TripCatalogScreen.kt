package com.android.tripbook.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.Trip
import com.android.tripbook.ui.components.TripCard

/**
 * Displays a list of available trips using cards.
 */
@Composable
fun TripCatalogScreen(
    modifier: Modifier = Modifier,
    onTripClick: (Int) -> Unit
) {
    val trips = listOf(
        Trip(
            id = 1,
            title = "Yaounde capital",
            description = "Explore the romantic city of lights and its charming streets.",
            imageUrl = "https://media.gettyimages.com/id/1933195714/fr/photo/yaounde-cameroon-a-monument-in-a-central-square-of-yaounde-is-seen-on-january-14-2024-in.jpg?s=1024x1024&w=gi&k=20&c=eov_LCEGoma18MdC_FLgyFgKduyTCvReFGA4asPB_Ik="
        ),
        Trip(
            id = 2,
            title = "Buea Mountain",
            description = "Experience the vibrant culture and futuristic skyline.",
            imageUrl = "https://media.gettyimages.com/id/953837336/photo/cameroon-politics-unrest-police.jpg?s=2048x2048&w=gi&k=20&c=FB3Qus6FKDhoijiBjUfBNKEoBNmmdjaDntYX2ZxRSlA="
        ),
        Trip(
            id = 3,
            title = "Kribi",
            description = "Witness the Big Five in their natural habitat on an unforgettable safari.",
            imageUrl = "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k="
        ),
        Trip(
            id = 4,
            title = "Bamenda Town",
            description = "The city that never sleeps awaits you with endless possibilities.",
            imageUrl = "https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48="
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Explore Trips",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
            items(trips) { trip ->
                TripCard(trip, onClick = { onTripClick(trip.id) })
            }
        }
    }
}
