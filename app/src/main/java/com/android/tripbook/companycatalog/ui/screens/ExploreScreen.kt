package com.android.tripbook.companycatalog.ui.screens

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.tripbook.companycatalog.data.MockCompanyData
import com.android.tripbook.companycatalog.ui.components.NearbyCompanyCard
import kotlin.math.roundToInt
import org.osmdroid.util.GeoPoint
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionStatus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ExploreScreen(navController: NavHostController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Explore",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    Button(
                        onClick = { navController.navigate("map") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "View Map",
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Map View",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NearMeContent(navController)
        }
    }
}

@Composable
private fun NearMeContent(navController: NavHostController) {
    // Mock current location (in a real app, this would come from location services)
    val currentLocation = Pair(0.0, 0.0)
    
    // Calculate distances and sort companies
    val nearbyCompanies = MockCompanyData.companies
        .map { company ->
            val distance = calculateDistance(
                currentLocation.first,
                currentLocation.second,
                company.location.first,
                company.location.second
            )
            company to distance
        }
        .sortedBy { it.second }
        .map { (company, distance) ->
            company to formatDistance(distance)
        }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (nearbyCompanies.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No companies found nearby",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(nearbyCompanies) { (company, distance) ->
                    NearbyCompanyCard(
                        company = company,
                        distance = distance,
                        onClick = { /* TODO: Navigate to company details */ }
                    )
                }
            }
        }
    }
}

// Helper function to calculate distance between two points using Haversine formula
private fun calculateDistance(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
): Double {
    val R = 6371.0 // Earth's radius in kilometers
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return R * c
}

// Helper function to format distance
private fun formatDistance(distanceInKm: Double): String {
    return when {
        distanceInKm < 1 -> "${(distanceInKm * 1000).roundToInt()}m"
        else -> "${distanceInKm.roundToInt()}km"
    }
}
