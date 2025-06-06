package com.tripbook.ui.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.tripbook.data.model.Traveler
import com.tripbook.repository.LocationRepository
import com.tripbook.util.checkAndRequestLocationPermission

@Composable
fun NearbyTravelersScreen() {
    val context = LocalContext.current
    val activity = context as Activity
    val fusedLocationProvider = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationRepo = remember { LocationRepository() }

    var travelers by remember { mutableStateOf<List<Traveler>>(emptyList()) }

    LaunchedEffect(Unit) {
        if (checkAndRequestLocationPermission(activity)) {
            fusedLocationProvider.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val lat = it.latitude
                    val lon = it.longitude
                    locationRepo.updateUserLocation(lat, lon)

                    // CoroutineScope already available in LaunchedEffect
                    LaunchedEffect(true) {
                        travelers = locationRepo.fetchNearbyTravelers(lat, lon)
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Nearby Travelers", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.height(12.dp))

        travelers.forEach {
            Text("Traveler ID: ${it.id}\nLat: ${it.latitude}, Lon: ${it.longitude}")
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
