// file: com.android.tripbook/ui/components/MapView.kt
package com.android.tripbook.ui.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.android.tripbook.model.MapRegion // YOUR MapRegion model
import com.android.tripbook.model.Trip    // Your unified Trip model
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*

private const val MAP_VIEW_TAG = "MapView"

@Composable
fun MapView(
    trips: List<Trip>,
    mapRegion: MapRegion, // YOUR MapRegion from ViewModel
    selectedTrip: Trip?,
    onTripMarkerClick: (Trip) -> Unit,
    onMapBoundsChange: (MapRegion) -> Unit, // Callback with YOUR MapRegion
    modifier: Modifier = Modifier
) {
    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = true,
            compassEnabled = true,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = false,
            rotationGesturesEnabled = true,
            scrollGesturesEnabled = true,
            tiltGesturesEnabled = true,
            zoomGesturesEnabled = true
        )
    }

    val mapProperties = remember {
        MapProperties(mapType = MapType.NORMAL, isTrafficEnabled = false)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(mapRegion.centerLatitude, mapRegion.centerLongitude),
            mapRegion.zoomLevel
        )
    }

    // Effect to react to changes in mapRegion from the ViewModel
    LaunchedEffect(mapRegion) {
        Log.d(MAP_VIEW_TAG, "MapRegion Prop Changed: Center=(${mapRegion.centerLatitude}, ${mapRegion.centerLongitude}), Zoom=${mapRegion.zoomLevel}")
        val newCameraPosition = CameraPosition.fromLatLngZoom(
            LatLng(mapRegion.centerLatitude, mapRegion.centerLongitude),
            mapRegion.zoomLevel
        )
        // Check if camera actually needs to move to avoid redundant animations
        if (cameraPositionState.position.target.latitude != newCameraPosition.target.latitude ||
            cameraPositionState.position.target.longitude != newCameraPosition.target.longitude ||
            cameraPositionState.position.zoom != newCameraPosition.zoom) {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(newCameraPosition),
                750 // Animation duration
            )
        }
    }

    // Effect to react to a trip being selected
    LaunchedEffect(selectedTrip) {
        selectedTrip?.let { trip ->
            Log.d(MAP_VIEW_TAG, "SelectedTrip Prop Changed: ${trip.title}")
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(LatLng(trip.latitude, trip.longitude), 14f) // Zoom closer
                ),
                750
            )
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = uiSettings,
        onMapLoaded = {
            Log.d(MAP_VIEW_TAG, "Map successfully loaded.")
            // Trigger initial bounds change once map is loaded and camera is set
            val currentPosition = cameraPositionState.position
            val visibleRegion = cameraPositionState.projection?.visibleRegion
            val newCenter = currentPosition.target
            val newZoom = currentPosition.zoom
            val bounds = visibleRegion?.latLngBounds

            val newMapRegion = MapRegion(
                centerLatitude = newCenter.latitude,
                centerLongitude = newCenter.longitude,
                latitudeDelta = bounds?.let { it.northeast.latitude - it.southwest.latitude } ?: mapRegion.latitudeDelta,
                longitudeDelta = bounds?.let { it.northeast.longitude - it.southwest.longitude } ?: mapRegion.longitudeDelta,
                zoomLevel = newZoom
            )
            onMapBoundsChange(newMapRegion)
        }
    ) {
        LaunchedEffect(cameraPositionState.isMoving) {
            if (!cameraPositionState.isMoving) {
                val currentPosition = cameraPositionState.position
                val visibleRegion = cameraPositionState.projection?.visibleRegion
                val newCenter = currentPosition.target
                val newZoom = currentPosition.zoom
                val bounds = visibleRegion?.latLngBounds

                Log.d(MAP_VIEW_TAG, "Camera Idle: Center=$newCenter, Zoom=$newZoom, Bounds=$bounds")

                val latDelta = bounds?.let { Math.abs(it.northeast.latitude - it.southwest.latitude) } ?: 0.0
                val lngDelta = bounds?.let { Math.abs(it.northeast.longitude - it.southwest.longitude) } ?: 0.0

                val newMapRegion = MapRegion(
                    centerLatitude = newCenter.latitude,
                    centerLongitude = newCenter.longitude,
                    latitudeDelta = if (latDelta == 0.0) mapRegion.latitudeDelta else latDelta, // Use old delta if new is 0
                    longitudeDelta = if (lngDelta == 0.0) mapRegion.longitudeDelta else lngDelta, // Use old delta if new is 0
                    zoomLevel = newZoom
                )
                onMapBoundsChange(newMapRegion)
            }
        }

        trips.forEach { trip ->
            // Using the corrected TripMapPin which is now a Google Maps Marker
            TripMapPin( // This is the Composable Marker we defined
                trip = trip,
                isSelected = (trip.id == selectedTrip?.id),
                onClick = { clickedTrip -> // Renamed for clarity from onMarkerClick
                    Log.d(MAP_VIEW_TAG, "Marker clicked: ${clickedTrip.title}")
                    onTripMarkerClick(clickedTrip)
                }
            )
        }
    }
}