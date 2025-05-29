package com.android.tripbook.ui.components

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

@SuppressLint("MissingPermission") // For demo purposes, permission should be handled properly
@Composable
fun LocationSelector(
    modifier: Modifier = Modifier,
    onLocationSelected: (LatLng) -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var locationText by remember { mutableStateOf("No location selected") }

    Button(
        modifier = modifier,
        onClick = {
            // ... other code
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        onLocationSelected(latLng)
                        locationText = "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
                    } else {
                        locationText = "Location not found"
                    }
                }
                .addOnFailureListener { e -> // Or any name you prefer, like 'ex' or 'throwable'
                    locationText = "Failed to get location"
                    Log.e("LocationSelector", "Error getting location", e) // Use the name you defined
                }
// ... other code
        }
    ) {
        Text("Select Current Location")
    }

    Spacer(modifier = Modifier.height(8.dp))
    Text(locationText)
}
