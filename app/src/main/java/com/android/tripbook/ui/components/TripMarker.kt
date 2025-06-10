
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
    trip: Trip,
    isSelected: Boolean = false,
    onClick: (Any) -> Unit
) {
    if (isSelected) MaterialTheme.colorScheme.primary else Color.White
    if (isSelected) Color.White else MaterialTheme.colorScheme.primary
    MaterialTheme.colorScheme.primary

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


    @Composable
    fun TripMapPinContent(
        isSelected: Boolean = false,
        modifier: Modifier = Modifier
    ) {
        val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
        val borderColor = MaterialTheme.colorScheme.primary

        Box(
            modifier = modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .border(2.dp, borderColor, CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {

        }
    }
}