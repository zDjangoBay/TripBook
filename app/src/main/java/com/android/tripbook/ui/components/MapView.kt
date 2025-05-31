// ui/components/MapView.kt
package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.android.tripbook.model.Trip
import com.android.tripbook.model.MapRegion

@Composable
fun MapView(
    trips: List<Trip>,
    mapRegion: MapRegion,
    selectedTrip: Trip?,
    onTripMarkerClick: (Trip) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                onCreate(null)
                onResume()
                getMapAsync { map ->
                    googleMap = map
                    setupMap(map, trips, onTripMarkerClick)
                    updateMapRegion(map, mapRegion)
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { mapView ->
        googleMap?.let { map ->
            updateMapRegion(map, mapRegion)
            updateMarkers(map, trips, selectedTrip, onTripMarkerClick)
        }
    }
}

private fun setupMap(
    map: GoogleMap,
    trips: List<Trip>,
    onTripMarkerClick: (Trip) -> Unit
) {
    map.uiSettings.apply {
        isZoomControlsEnabled = true
        isCompassEnabled = true
        isMyLocationButtonEnabled = false // We'll add our own
    }
}

private fun updateMapRegion(map: GoogleMap, region: MapRegion) {
    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
        LatLng(region.centerLatitude, region.centerLongitude),
        region.zoomLevel
    )
    map.animateCamera(cameraUpdate)
}

private fun updateMarkers(
    map: GoogleMap,
    trips: List<Trip>,
    selectedTrip: Trip?,
    onTripMarkerClick: (Trip) -> Unit
) {
    map.clear()

    trips.forEach { trip ->
        val marker = map.addMarker(
            MarkerOptions()
                .position(LatLng(trip.latitude, trip.longitude))
                .title(trip.title)
                .snippet("${trip.city}, ${trip.country} - ${trip.price}")
        )

        marker?.tag = trip
    }

    map.setOnMarkerClickListener { marker ->
        val trip = marker.tag as? Trip
        trip?.let { onTripMarkerClick(it) }
        true
    }
}



