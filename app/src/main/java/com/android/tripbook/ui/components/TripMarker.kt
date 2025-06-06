// ui/components/TripMarker.kt
package com.android.tripbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.Trip
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState


@Composable
fun TripMapPin(
    trip: Trip, //consuming full trip object
    isSelected: Boolean = false,
    onClick: (Trip) -> Unit, // Callback with the trip
    modifier: Modifier = Modifier // Modifier for the Marker itself
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
    val borderColor = MaterialTheme.colorScheme.primary

    val markerState = rememberMarkerState(position = LatLng(trip.latitude, trip.longitude))

    Marker(
        state = markerState,
        title = trip.title,
        tag = trip.id,
        onClick = {
            onClick(trip)
            true
        },
        icon = BitmapDescriptorFactory.defaultMarker(
            if (isSelected) BitmapDescriptorFactory.HUE_AZURE else BitmapDescriptorFactory.HUE_ROSE
        ),
        zIndex = if (isSelected) 1f else 0f
    )

    // If you want your custom Composable (the circle with price) to BE the marker icon,
    // it's more complex. You would need to:
    // 1. Create a Composable that renders your desired UI.
    // 2. Convert that Composable view into a Bitmap at runtime.
    // 3. Create a BitmapDescriptor from that Bitmap.
    // 4. Assign it to the Marker's icon property.
    // This is often done using a library or helper functions for "Composable to Bitmap".

    // For now, the TripMarker above is a standard Google Maps marker.
    // Your `TripMapPinContent` is separate.
}

// This is your visual pin content if you were to convert it to a Bitmap for the Marker's icon
@Composable
fun TripMapPinContent(
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
    val borderColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier // This modifier would be for the Box itself
            .size(50.dp) // Fixed size, might need to be dynamic if price string is long
            .clip(CircleShape)
            .background(backgroundColor)
            .border(2.dp, borderColor, CircleShape)
            .padding(4.dp), // Add some padding so text isn't at the very edge
        contentAlignment = Alignment.Center
    ) {

    }
}