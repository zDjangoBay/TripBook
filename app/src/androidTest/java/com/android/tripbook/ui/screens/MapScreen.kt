package com.tripbook.ui.screens

import android.content.Context
import androidx.compose.runtime.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.google.firebase.database.*

@Composable
fun MapScreen(context: Context) {
    val dbRef = FirebaseDatabase.getInstance().getReference("locations")
    var markers by remember { mutableStateOf(listOf<MarkerOptions>()) }

    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 2f)
    }

    LaunchedEffect(Unit) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updated = mutableListOf<MarkerOptions>()
                snapshot.children.forEach {
                    val uid = it.key ?: return@forEach
                    val lat = it.child("latitude").getValue(Double::class.java) ?: 0.0
                    val lon = it.child("longitude").getValue(Double::class.java) ?: 0.0
                    updated.add(MarkerOptions().position(LatLng(lat, lon)).title(uid))
                }
                markers = updated
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    GoogleMap(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        cameraPositionState = cameraPosition
    ) {
        markers.forEach { addMarker(it) }
    }
}
