package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.Place
import com.android.tripbook.ui.components.PlaceCard

@Composable
fun PlaceSelectorScreen(
    places: List<Place>,
    onPlaceSelected: (Place) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(places) { place ->
            PlaceCard(
                place = place,
                onClick = { onPlaceSelected(place) }
            )
        }
    }
}
