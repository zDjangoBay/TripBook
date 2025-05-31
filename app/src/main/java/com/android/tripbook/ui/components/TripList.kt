package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.Model.Trip

@Composable
fun TripList(trips: List<Trip>, onItemClick: (Trip) -> Unit = {}) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        items(trips) { trip ->
            TripItem(trip = trip, onClick = { onItemClick(trip) })
            Spacer(modifier = Modifier.width(8.dp)) // Space between items
        }
    }
}
