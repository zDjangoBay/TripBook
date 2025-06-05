// ui/components/TripMarker.kt
package com.android.tripbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
// import androidx.compose.material.icons.Icons // Not used in this version
// import androidx.compose.material.icons.filled.Place // Not used in this version
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// This TripMarker only takes price, so it doesn't directly know about the Trip object
// For it to be clickable and return a Trip, it would need the Trip object.
// I will assume for now the MapView will handle wrapping this with a Marker that has the onClick.
// OR, we modify this to take the full Trip.
// Given the MapView context, it's better if TripMarker knows about the trip for the onClick.
// Let's adjust it slightly to take the full trip and onClick.
import com.android.tripbook.model.Trip // Assuming Trip model has a 'price' field
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker // Import the actual Marker
import com.google.maps.android.compose.MarkerState // Import MarkerState
import com.google.maps.android.compose.rememberMarkerState


@Composable
fun TripMapPin( // Renamed to avoid confusion if you have another TripMarker component
    trip: Trip, // Now takes the full trip
    isSelected: Boolean = false,
    onClick: (Trip) -> Unit, // Callback with the trip
    modifier: Modifier = Modifier // Modifier for the Marker itself
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
    val borderColor = MaterialTheme.colorScheme.primary

    // The Google Maps Marker composable is the one that's actually placed on the map.
    // Your custom composable (Box with Text) cannot be directly used as a map marker icon
    // unless you convert it to a BitmapDescriptor.

    // For simplicity with custom content, Google Maps Compose offers adding a Composable inside Marker content.
    // However, your existing TripMarker is a Box, not a Marker.
    // Let's wrap your custom pin UI inside the Google Maps Marker's icon property
    // by converting your composable to a Bitmap. This is more advanced.

    // A simpler approach for now: Use the standard Google Maps Marker and pass your Trip data.
    // Your custom UI (the circle with price) can be an InfoWindow or a different UI element.
    // OR, if you intend the *entire marker icon* to be your composable,
    // you'd use the `Marker` composable with a custom `icon` generated from your `TripMapPinContent`.

    // Let's make this `TripMarker` the actual Google Maps `Marker`
    val markerState = rememberMarkerState(position = LatLng(trip.latitude, trip.longitude))

    Marker(
        state = markerState,
        title = trip.title,
        tag = trip.id, // Useful for identifying the marker
        onClick = {
            onClick(trip)
            true // Consume the click
        },
        // To make the icon look like your custom composable is complex.
        // The easiest way to customize beyond default is with BitmapDescriptorFactory.
        // For a fully custom Composable as a marker, it's more involved.
        // For now, let's use a default marker but colored based on selection.
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