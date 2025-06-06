package com.android.tripbook.ui.components


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.android.tripbook.Model.Place

@Composable
fun PlaceList(places: List<Place>, onPlaceClick: (Place) -> Unit) {
    LazyColumn {
        items(places) { place ->
            PlaceItem(place = place, onClick = { onPlaceClick(place) })
        }
    }
}
