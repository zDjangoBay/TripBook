kotlin
package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.android.tripbook.model.Trip
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun TripMapView(
    trip: Trip,
    modifier: Modifier = Modifier,
    showRoute: Boolean = true
) {
    // Use destination or first itinerary item as map center
    val defaultCenter = LatLng(0.0, 0.0)
    val mapCenter = trip.destination?.let {
        LatLng(it.latitude, it.longitude)
    } ?: trip.itinerary.firstOrNull()?.let {
        LatLng(it.location.latitude, it.location.longitude)
    } ?: defaultCenter

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapCenter, 12f)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            compassEnabled = true,
            myLocationButtonEnabled = true
        )
    ) {
        // Add markers for itinerary items
        trip.itinerary.forEach { item ->
            Marker(
                state = MarkerState(
                    position = LatLng(item.location.latitude, item.location.longitude)
                ),
                title = item.title,
                snippet = "${item.time} - ${item.location.address}"
            )
        }

        // Add destination marker if available
        trip.destination?.let { destination ->
            Marker(
                state = MarkerState(
                    position = LatLng(destination.latitude, destination.longitude)
                ),
                title = "Trip Destination",
                snippet = destination.address
            )
        }

        // Add route polylines if requested (requires polyline points in your model)
        // Uncomment and implement if you have route polylines
        /*
        if (showRoute) {
            trip.itinerary.forEach { item ->
                item.routePolyline?.let { polylinePoints: List<LatLng> ->
                    Polyline(
                        points = polylinePoints,
                        color = Color.Blue,
                        width = 6f
                    )
                }
            }
        }
        */
    }
}