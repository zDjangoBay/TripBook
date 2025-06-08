package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.tripbook.companycatalog.data.MockCompanyData
import com.android.tripbook.companycatalog.ui.components.NearbyCompanyCard
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Near Me", "Map View")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
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
            )
        )

        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        // Content based on selected tab
        when (selectedTab) {
            0 -> NearMeContent(navController)
            1 -> MapViewContent()
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

@Composable
private fun MapViewContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Map View Coming Soon",
            style = MaterialTheme.typography.bodyLarge
        )
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
