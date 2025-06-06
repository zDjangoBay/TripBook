package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.android.tripbook.model.Location
import com.android.tripbook.model.Trip
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun LocationMarker(
    location: Location?,
    onClick: () -> Unit
) {
    location?.let {
        Marker(
            state = MarkerState(position = LatLng(it.latitude, it.longitude)),
            title = it.name,
            snippet = it.address,
            onInfoWindowClick = { onClick() }
        )
    }
}

@Composable
fun MiniMap(
    location: Location?,
    modifier: Modifier = Modifier
) {
    var isMapLoaded by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0), 15f)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLoaded = { isMapLoaded = true },
        uiSettings = MapUiSettings(zoomControlsEnabled = false)
    ) {
        if (location != null) {
            Marker(
                state = MarkerState(position = LatLng(location.latitude, location.longitude)),
                title = location.name
            )
        }
    }
}

@Composable
fun TripMapView(
    trip: Trip?,
    modifier: Modifier = Modifier
) {
    val locations = trip?.locations ?: emptyList()
    val startLocation = locations.firstOrNull()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(startLocation?.latitude ?: 0.0, startLocation?.longitude ?: 0.0), 10f)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        locations.forEach { location ->
            Marker(
                state = MarkerState(position = LatLng(location.latitude, location.longitude)),
                title = location.name,
                snippet = location.address
            )
        }
        if (locations.size > 1) {
            Polyline(
                points = locations.map { LatLng(it.latitude, it.longitude) }
            )
        }
    }
} 