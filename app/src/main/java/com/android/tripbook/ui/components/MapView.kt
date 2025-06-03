// ui/components/MapView.kt
package com.android.tripbook.ui.components

import android.annotation.SuppressLint
import android.location.Location // <-- Ensure this import is present for Location.distanceBetween
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
import com.android.tripbook.model.MapRegion
import com.google.android.gms.maps.model.BitmapDescriptorFactory

@SuppressLint("PotentialBehaviorOverride")
@Composable
fun MapView(
    trips: List<Trip>,
    mapRegion: MapRegion,
    selectedTrip: Trip?,
    onTripMarkerClick: (Trip) -> Unit,
    onCameraIdle: (MapRegion) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    // Use rememberUpdatedState for callbacks and states to ensure they always reference the latest lambda/value
    val currentOnTripMarkerClick by rememberUpdatedState(onTripMarkerClick)
    val currentOnCameraIdle by rememberUpdatedState(onCameraIdle)
    val currentSelectedTrip by rememberUpdatedState(selectedTrip)

    // Ensure the lifecycle of the MapView is tied to the Composable's lifecycle
    DisposableEffect(mapView) {
        mapView.onCreate(null)
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDestroy()
            // Important: map should be cleared if the MapView is being reused across composables
            // For single-instance MapView, this is less critical but good practice.
            // map.getMapAsync { it.clear() } // You might want to do this on dispose
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize(),
        update = { map -> // This update block is called on every recomposition with new parameters
            map.getMapAsync { googleMap ->
                // --- Configure UI Settings (apply only once) ---
                // Only set these settings once, using a LaunchedEffect or `mapView.tag`
                // to prevent repeated configuration. However, for simplicity here,
                // if they are idempotent, it's less critical.
                if (map.tag == null) { // Simple flag to ensure one-time setup
                    googleMap.uiSettings.apply {
                        isZoomControlsEnabled = false
                        isCompassEnabled = false
                        isMyLocationButtonEnabled = false
                        isScrollGesturesEnabled = true // Allow user interaction
                        isZoomGesturesEnabled = true
                        isTiltGesturesEnabled = true
                        isRotateGesturesEnabled = true
                    }

                    // --- Set OnCameraIdleListener (apply only once) ---
                    googleMap.setOnCameraIdleListener {
                        val cameraPosition = googleMap.cameraPosition
                        val projection = googleMap.projection
                        val visibleRegion = projection.visibleRegion.latLngBounds

                        val newMapRegion = MapRegion(
                            centerLatitude = cameraPosition.target.latitude,
                            centerLongitude = cameraPosition.target.longitude,
                            latitudeDelta = visibleRegion.northeast.latitude - visibleRegion.southwest.latitude,
                            longitudeDelta = visibleRegion.northeast.longitude - visibleRegion.southwest.longitude,
                            zoomLevel = cameraPosition.zoom
                        )
                        currentOnCameraIdle(newMapRegion) // Use the current updated lambda
                    }

                    // --- Set OnMarkerClickListener (apply only once) ---
                    googleMap.setOnMarkerClickListener { marker ->
                        val clickedTripId = marker.tag as? Int // Retrieve the trip ID from the tag
                        // Find the trip from the *current* list of trips
                        val clickedTrip = trips.find { it.id == clickedTripId }
                        clickedTrip?.let {
                            currentOnTripMarkerClick(it) // Use the current updated lambda
                            marker.showInfoWindow() // Show info window on click
                            googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.position)) // Center map on clicked marker
                        }
                        true // Return true to indicate that we've consumed the event
                    }
                    map.tag = "configured" // Mark as configured
                }


                // --- Update Map Camera (on every recomposition if mapRegion changes) ---
                val targetLatLng = LatLng(mapRegion.centerLatitude, mapRegion.centerLongitude)
                val currentCameraPosition = googleMap.cameraPosition

                // Define a threshold for movement to prevent constant re-centering if user is interacting
                val distanceThreshold = 50.0 // meters - reduce from 100 for more responsiveness
                val zoomThreshold = 0.2f // zoom levels - reduce for more responsiveness

                val distance = FloatArray(1)
                Location.distanceBetween( // <-- CORRECTED: Directly use Location.distanceBetween
                    currentCameraPosition.target.latitude, currentCameraPosition.target.longitude,
                    targetLatLng.latitude, targetLatLng.longitude,
                    distance
                )

                val shouldMoveCamera =
                    distance[0] > distanceThreshold || // If target is far from current center
                            Math.abs(currentCameraPosition.zoom - mapRegion.zoomLevel) > zoomThreshold // If zoom is significantly different

                if (shouldMoveCamera) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetLatLng, mapRegion.zoomLevel))
                }


                // --- Update Markers (on every recomposition if 'trips' or 'selectedTrip' changes) ---
                googleMap.clear() // Clear existing markers

                trips.forEach { trip ->
                    val markerOptions = MarkerOptions()
                        .position(LatLng(trip.latitude, trip.longitude))
                        .title(trip.title)
                        .snippet("${trip.city}, ${trip.country}")
                        .icon(
                            if (trip.id == currentSelectedTrip?.id) { // Use currentSelectedTrip here
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE) // Highlight selected
                            } else {
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) // Default color
                            }
                        )

                    val marker = googleMap.addMarker(markerOptions)
                    marker?.tag = trip.id // Store trip ID in tag for click listener
                }
            }
        }
    )
}