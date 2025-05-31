
// ui/components/MiniMap.kt
package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.android.tripbook.model.Trip

@Composable
fun MiniMap(
    trip: Trip,
    modifier: Modifier = Modifier,
    height: Int = 200
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                onCreate(null)
                onResume()
                getMapAsync { map ->
                    setupMiniMap(map, trip)
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(12.dp))
    )
}

private fun setupMiniMap(map: GoogleMap, trip: Trip) {
    val location = LatLng(trip.latitude, trip.longitude)

    map.apply {
        uiSettings.apply {
            isZoomControlsEnabled = false
            isCompassEnabled = false
            isMyLocationButtonEnabled = false
            isScrollGesturesEnabled = false
            isZoomGesturesEnabled = false
            isTiltGesturesEnabled = false
            isRotateGesturesEnabled = false
        }

        addMarker(
            MarkerOptions()
                .position(location)
                .title(trip.title)
                .snippet("${trip.city}, ${trip.country}")
        )

        moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
    }
}